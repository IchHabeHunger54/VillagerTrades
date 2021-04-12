package ihh.villagertrades;

import javafx.util.Pair;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.entity.villager.IVillagerDataHolder;
import net.minecraft.entity.villager.VillagerType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionBrewing;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Trade implements net.minecraft.entity.merchant.villager.VillagerTrades.ITrade {
    protected final int maxUses;
    protected final int xpValue;
    protected final float priceMultiplier;
    protected final Item buy;
    protected final Item buySecond;
    protected final Item sell;
    protected final int buyCount;
    protected final int buySecondCount;
    protected final int sellCount;
    protected Random rand;
    protected Entity trader;

    public Trade(int maxUsesIn, int xpValueIn, float priceMultiplierIn, Item buyIn, int buyCountIn, Item sellIn, int sellCountIn) {
        this(maxUsesIn, xpValueIn, priceMultiplierIn, buyIn, buyCountIn, null, 0, sellIn, sellCountIn);
    }

    public Trade(int maxUsesIn, int xpValueIn, float priceMultiplierIn, Item buyIn, int buyCountIn, Item buySecondIn, int buySecondCountIn, Item sellIn, int sellCountIn) {
        maxUses = maxUsesIn;
        xpValue = xpValueIn;
        priceMultiplier = priceMultiplierIn;
        buy = buyIn;
        buySecond = buySecondIn;
        sell = sellIn;
        buyCount = buyCountIn;
        buySecondCount = buySecondCountIn;
        sellCount = sellCountIn;
    }

    @Override
    public MerchantOffer getOffer(@Nonnull Entity trader, @Nonnull Random rand) {
        this.rand = rand;
        this.trader = trader;
        return new MerchantOffer(getBuyItem(), getSecondBuyItem(), getSellItem(), 0, maxUses, xpValue, priceMultiplier);
    }

    protected ItemStack getBuyItem() {
        return buy == null ? ItemStack.EMPTY : new ItemStack(buy, buyCount);
    }

    protected ItemStack getSecondBuyItem() {
        return buySecond == null ? ItemStack.EMPTY : new ItemStack(buySecond, buySecondCount);
    }

    protected ItemStack getSellItem() {
        return sell == null ? ItemStack.EMPTY : new ItemStack(sell, sellCount);
    }

    public static final class DyedItemTrade extends Trade {
        public DyedItemTrade(int maxUsesIn, int xpValueIn, float priceMultiplierIn, Item buyIn, int buyCountIn, Item sellIn) {
            super(maxUsesIn, xpValueIn, priceMultiplierIn, buyIn, buyCountIn, sellIn, 1);
        }

        public DyedItemTrade(int maxUsesIn, int xpValueIn, float priceMultiplierIn, Item buyIn, int buyCountIn, Item buySecondIn, int buyCountSecondIn, Item sellIn) {
            super(maxUsesIn, xpValueIn, priceMultiplierIn, buyIn, buyCountIn, buySecondIn, buyCountSecondIn, sellIn, 1);
        }

        @Override
        protected ItemStack getSellItem() {
            if (sell instanceof DyeableArmorItem) {
                List<DyeItem> list = new ArrayList<>();
                list.add(DyeItem.getItem(DyeColor.byId(rand.nextInt(16))));
                if (rand.nextFloat() > 0.7F) list.add(DyeItem.getItem(DyeColor.byId(rand.nextInt(16))));
                if (rand.nextFloat() > 0.8F) list.add(DyeItem.getItem(DyeColor.byId(rand.nextInt(16))));
                return IDyeableArmorItem.dyeItem(new ItemStack(sell), list);
            }
            return new ItemStack(sell);
        }
    }

    public static final class MapTrade extends Trade {
        private final Structure<?> structure;
        private final MapDecoration.Type mapDecoration;

        public MapTrade(int maxUsesIn, int xpValueIn, float priceMultiplierIn, Item buyIn, int buyCountIn, Structure<?> structureIn, MapDecoration.Type mapDecorationIn) {
            super(maxUsesIn, xpValueIn, priceMultiplierIn, buyIn, buyCountIn, Items.MAP, 1);
            structure = structureIn;
            mapDecoration = mapDecorationIn;
        }

        public MapTrade(int maxUsesIn, int xpValueIn, float priceMultiplierIn, Item buyIn, int buyCountIn, Item buySecondIn, int buyCountSecondIn, Structure<?> structureIn, MapDecoration.Type mapDecorationIn) {
            super(maxUsesIn, xpValueIn, priceMultiplierIn, buyIn, buyCountIn, buySecondIn, buyCountSecondIn, Items.MAP, 1);
            structure = structureIn;
            mapDecoration = mapDecorationIn;
        }

        @Override
        protected ItemStack getSellItem() {
            if (trader.world instanceof ServerWorld) {
                ServerWorld w = (ServerWorld) trader.world;
                BlockPos p = w.func_241117_a_(structure, trader.getPosition(), 100, true);
                if (p != null) {
                    ItemStack i = FilledMapItem.setupNewMap(w, p.getX(), p.getZ(), (byte) 2, true, true);
                    FilledMapItem.func_226642_a_(w, i);
                    MapData.addTargetDecoration(i, p, "+", mapDecoration);
                    i.setDisplayName(new TranslationTextComponent("filled_map." + structure.getStructureName().toLowerCase(Locale.ROOT)));
                    return i;
                }
            }
            return super.getSellItem();
        }
    }

    public static final class BiomeTrade extends Trade {
        private final Map<VillagerType, Pair<Pair<Item, Integer>, Pair<Item, Integer>>> items;

        public BiomeTrade(int maxUsesIn, int xpValueIn, float priceMultiplierIn, Map<VillagerType, Pair<Pair<Item, Integer>, Pair<Item, Integer>>> items) {
            super(maxUsesIn, xpValueIn, priceMultiplierIn, null, 1, null, 1);
            this.items = items;
        }

        @Override
        protected ItemStack getBuyItem() {
            if (trader instanceof IVillagerDataHolder)
                return new ItemStack(items.get(((IVillagerDataHolder) trader).getVillagerData().getType()).getKey().getKey(), items.get(((IVillagerDataHolder) trader).getVillagerData().getType()).getKey().getValue());
            return ItemStack.EMPTY;
        }

        @Override
        protected ItemStack getSellItem() {
            if (trader instanceof IVillagerDataHolder)
                return new ItemStack(items.get(((IVillagerDataHolder) trader).getVillagerData().getType()).getValue().getKey(), items.get(((IVillagerDataHolder) trader).getVillagerData().getType()).getValue().getValue());
            return ItemStack.EMPTY;
        }
    }

    public static final class EnchantedBookTrade extends Trade {
        public EnchantedBookTrade(int maxUsesIn, int xpValueIn, float priceMultiplierIn, Item buyIn) {
            super(maxUsesIn, xpValueIn, priceMultiplierIn, buyIn, 1, Items.ENCHANTED_BOOK, 1);
        }

        public EnchantedBookTrade(int maxUsesIn, int xpValueIn, float priceMultiplierIn, Item buyIn, Item buySecondIn, int buySecondCount) {
            super(maxUsesIn, xpValueIn, priceMultiplierIn, buyIn, 1, buySecondIn, buySecondCount, Items.ENCHANTED_BOOK, 1);
        }

        @Override
        public MerchantOffer getOffer(@Nonnull Entity trader, @Nonnull Random rand) {
            List<Enchantment> l = StreamSupport.stream(ForgeRegistries.ENCHANTMENTS.spliterator(), false).filter(Enchantment::canVillagerTrade).collect(Collectors.toList());
            Enchantment e = l.get(rand.nextInt(l.size()));
            int i = MathHelper.nextInt(rand, e.getMinLevel(), e.getMaxLevel());
            ItemStack s = EnchantedBookItem.getEnchantedItemStack(new EnchantmentData(e, i));
            int j = 2 + rand.nextInt(5 + i * 10) + 3 * i;
            if (e.isTreasureEnchantment()) j *= 2;
            if (j > 64) j = 64;
            return new MerchantOffer(new ItemStack(buy, j), new ItemStack(buySecond, buySecondCount), s, maxUses, xpValue, priceMultiplier);
        }
    }

    public static final class EnchantedItemTrade extends Trade {
        public EnchantedItemTrade(int maxUsesIn, int xpValueIn, float priceMultiplierIn, Item buyIn, Item sellIn) {
            super(maxUsesIn, xpValueIn, priceMultiplierIn, buyIn, 1, sellIn, 1);
        }

        public EnchantedItemTrade(int maxUsesIn, int xpValueIn, float priceMultiplierIn, Item buyIn, Item buySecondIn, int buySecondCount, Item sellIn) {
            super(maxUsesIn, xpValueIn, priceMultiplierIn, buyIn, 1, buySecondIn, buySecondCount, sellIn, 1);
        }

        @Override
        public MerchantOffer getOffer(@Nonnull Entity trader, @Nonnull Random rand) {
            int e = 5 + rand.nextInt(15);
            ItemStack i = new ItemStack(buy, e);
            ItemStack j = EnchantmentHelper.addRandomEnchantment(rand, new ItemStack(sell), e, false);
            return new MerchantOffer(i, new ItemStack(buySecond, buySecondCount), j, maxUses, xpValue, priceMultiplier);
        }
    }

    public static final class PotionItemTrade extends Trade {
        public PotionItemTrade(int maxUsesIn, int xpValueIn, float priceMultiplierIn, Item buyIn, int buyCountIn, Item sellIn) {
            super(maxUsesIn, xpValueIn, priceMultiplierIn, buyIn, buyCountIn, sellIn, 1);
        }

        public PotionItemTrade(int maxUsesIn, int xpValueIn, float priceMultiplierIn, Item buyIn, int buyCountIn, Item buySecondIn, int buyCountSecondIn, Item sellIn) {
            super(maxUsesIn, xpValueIn, priceMultiplierIn, buyIn, buyCountIn, buySecondIn, buyCountSecondIn, sellIn, 1);
        }

        @Override
        protected ItemStack getSellItem() {
            List<Potion> l = StreamSupport.stream(ForgeRegistries.POTION_TYPES.spliterator(), false).filter(potion -> !potion.getEffects().isEmpty() && PotionBrewing.isBrewablePotion(potion)).collect(Collectors.toList());
            return PotionUtils.addPotionToItemStack(new ItemStack(sell, sellCount), l.get(rand.nextInt(l.size())));
        }
    }

    public static final class StewTrade extends Trade {
        private final Effect effect;
        private final int duration;

        public StewTrade(int maxUsesIn, int xpValueIn, float priceMultiplierIn, Item buyIn, int buyCountIn, Effect effectIn, int durationIn) {
            super(maxUsesIn, xpValueIn, priceMultiplierIn, buyIn, buyCountIn, Items.SUSPICIOUS_STEW, 1);
            effect = effectIn;
            duration = durationIn;
        }

        public StewTrade(int maxUsesIn, int xpValueIn, float priceMultiplierIn, Item buyIn, int buyCountIn, Item buySecondIn, int buyCountSecondIn, Effect effectIn, int durationIn) {
            super(maxUsesIn, xpValueIn, priceMultiplierIn, buyIn, buyCountIn, buySecondIn, buyCountSecondIn, Items.SUSPICIOUS_STEW, 1);
            effect = effectIn;
            duration = durationIn;
        }

        @Override
        protected ItemStack getSellItem() {
            ItemStack i = new ItemStack(sell, sellCount);
            SuspiciousStewItem.addEffect(i, effect, duration);
            return i;
        }
    }
}
