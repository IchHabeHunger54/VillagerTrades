package ihh.villagertrades;

import javafx.util.Pair;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.entity.villager.VillagerType;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.storage.MapDecoration;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ihh.villagertrades.VillagerTrades.LOGGER;

public class TradeUtils {
    public static void addNormalTrade(List<VillagerTrades.ITrade> trades, int uses, int xp, float price, List<String> s) {
        Item buy = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s.get(0)));
        int buyCount = Integer.parseInt(s.get(1));
        if (s.size() == 4) {
            Item sell = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s.get(2)));
            int sellCount = Integer.parseInt(s.get(3));
            trades.add(new Trade(uses, xp, price, buy, buyCount, sell, sellCount));
            LOGGER.debug("Added new trade with " + uses + " uses, " + xp + " villager xp, a price multiplier of " + price + " and trading the following items: " + sellCount + " " + sell + " for " + buyCount + " " + buy);
        } else if (s.size() == 6) {
            Item buySecond = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s.get(2)));
            int buySecondCount = Integer.parseInt(s.get(3));
            Item sell = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s.get(4)));
            int sellCount = Integer.parseInt(s.get(5));
            trades.add(new Trade(uses, xp, price, buy, buyCount, buySecond, buySecondCount, sell, sellCount));
            LOGGER.debug("Added new trade with " + uses + " uses, " + xp + " villager xp, a price multiplier of " + price + " and trading the following items: " + sellCount + " " + sell + " for " + buyCount + " " + buy + " and " + buySecondCount + " " + buySecond);
        }
    }

    public static void addDyedTrade(List<VillagerTrades.ITrade> trades, int uses, int xp, float price, List<String> s) {
        Item buy = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s.get(0)));
        int buyCount = Integer.parseInt(s.get(1));
        if (s.size() == 3) {
            Item sell = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s.get(2)));
            trades.add(new Trade.DyedItemTrade(uses, xp, price, buy, buyCount, sell));
            LOGGER.debug("Added new trade with " + uses + " uses, " + xp + " villager xp, a price multiplier of " + price + " and trading the following items: 1 " + sell + " (randomly dyed) for " + buyCount + " " + buy);
        } else if (s.size() == 5) {
            Item buySecond = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s.get(2)));
            int buySecondCount = Integer.parseInt(s.get(3));
            Item sell = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s.get(4)));
            trades.add(new Trade.DyedItemTrade(uses, xp, price, buy, buyCount, buySecond, buySecondCount, sell));
            LOGGER.debug("Added new trade with " + uses + " uses, " + xp + " villager xp, a price multiplier of " + price + " and trading the following items: 1 " + sell + " (randomly dyed) for " + buyCount + " " + buy + " and " + buySecondCount + " " + buySecond);
        }
    }

    public static void addMapTrade(List<VillagerTrades.ITrade> trades, int uses, int xp, float price, List<String> s) {
        Item buy = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s.get(0)));
        int buyCount = Integer.parseInt(s.get(1));
        if (s.size() == 4) {
            Structure<?> structure = ForgeRegistries.STRUCTURE_FEATURES.getValue(new ResourceLocation(s.get(2)));
            MapDecoration.Type mapDecoration = MapDecoration.Type.valueOf(s.get(3).toUpperCase());
            trades.add(new Trade.MapTrade(uses, xp, price, buy, buyCount, structure, mapDecoration));
            LOGGER.debug("Added new trade with " + uses + " uses, " + xp + " villager xp, a price multiplier of " + price + " and trading the following items: 1 minecraft:filled_map that leads to structure " + structure + " for " + buyCount + " " + buy);
        } else if (s.size() == 6) {
            Item buySecond = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s.get(2)));
            int buySecondCount = Integer.parseInt(s.get(3));
            Structure<?> structure = ForgeRegistries.STRUCTURE_FEATURES.getValue(new ResourceLocation(s.get(4)));
            MapDecoration.Type mapDecoration = MapDecoration.Type.valueOf(s.get(5).toUpperCase());
            trades.add(new Trade.MapTrade(uses, xp, price, buy, buyCount, buySecond, buySecondCount, structure, mapDecoration));
            LOGGER.debug("Added new trade with " + uses + " uses, " + xp + " villager xp, a price multiplier of " + price + " and trading the following items: 1 minecraft:filled_map that leads to structure " + structure + " for " + buyCount + " " + buy + " and " + buySecondCount + " " + buySecond);
        }
    }

    public static void addBiomeTrade(List<VillagerTrades.ITrade> trades, int uses, int xp, float price, List<String> s) {
        Map<VillagerType, Pair<Pair<Item, Integer>, Pair<Item, Integer>>> m = new HashMap<>();
        for (int i = 0; i < s.size(); i += 5)
            if (getVillagerType(s.get(i)) != null) try {
                m.put(getVillagerType(s.get(0)), new Pair<>(new Pair<>(ForgeRegistries.ITEMS.getValue(new ResourceLocation(s.get(1))), Integer.parseInt(s.get(2))), new Pair<>(ForgeRegistries.ITEMS.getValue(new ResourceLocation(s.get(3))), Integer.parseInt(s.get(4)))));
            } catch (RuntimeException e) {
                e.printStackTrace();
                return;
            }
            else return;
        trades.add(new Trade.BiomeTrade(uses, xp, price, m));
    }

    public static void addEnchantedBookTrade(List<VillagerTrades.ITrade> trades, int uses, int xp, float price, List<String> s) {
        Item buy = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s.get(0)));
        if (s.size() == 1) {
            trades.add(new Trade.EnchantedBookTrade(uses, xp, price, buy));
            LOGGER.debug("Added new trade with " + uses + " uses, " + xp + " villager xp, a price multiplier of " + price + " and trading the following items: 1 randomly enchanted minecraft:enchanted_book for a predefined amount of " + buy);
        } else if (s.size() == 3) {
            Item buySecond = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s.get(1)));
            int buySecondCount = Integer.parseInt(s.get(2));
            trades.add(new Trade.EnchantedBookTrade(uses, xp, price, buy, buySecond, buySecondCount));
            LOGGER.debug("Added new trade with " + uses + " uses, " + xp + " villager xp, a price multiplier of " + price + " and trading the following items: 1 randomly enchanted minecraft:enchanted_book for a predefined amount of " + buy + " and " + buySecondCount + " " + buySecond);
        }
    }

    public static void addEnchantedItemTrade(List<VillagerTrades.ITrade> trades, int uses, int xp, float price, List<String> s) {
        Item buy = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s.get(0)));
        if (s.size() == 2) {
            Item sell = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s.get(1)));
            trades.add(new Trade.EnchantedItemTrade(uses, xp, price, buy, sell));
            LOGGER.debug("Added new trade with " + uses + " uses, " + xp + " villager xp, a price multiplier of " + price + " and trading the following items: 1 randomly enchanted " + sell + " for a predefined amount of " + buy);
        } else if (s.size() == 4) {
            Item buySecond = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s.get(1)));
            int buySecondCount = Integer.parseInt(s.get(2));
            Item sell = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s.get(3)));
            trades.add(new Trade.EnchantedItemTrade(uses, xp, price, buy, buySecond, buySecondCount, sell));
            LOGGER.debug("Added new trade with " + uses + " uses, " + xp + " villager xp, a price multiplier of " + price + " and trading the following items: 1 randomly enchanted " + sell + " for a predefined amount of " + buy + " and " + buySecondCount + " " + buySecond);
        }
    }

    public static void addPotionTrade(List<VillagerTrades.ITrade> trades, int uses, int xp, float price, List<String> s) {
        Item buy = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s.get(0)));
        int buyCount = Integer.parseInt(s.get(1));
        if (s.size() == 3) {
            Item sell = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s.get(2)));
            trades.add(new Trade.PotionItemTrade(uses, xp, price, buy, buyCount, sell));
            LOGGER.debug("Added new trade with " + uses + " uses, " + xp + " villager xp, a price multiplier of " + price + " and trading the following items: 1 " + sell + " with a random potion applied to it for " + buyCount + " " + buy);
        } else if (s.size() == 5) {
            Item buySecond = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s.get(2)));
            int buySecondCount = Integer.parseInt(s.get(3));
            Item sell = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s.get(4)));
            trades.add(new Trade.PotionItemTrade(uses, xp, price, buy, buyCount, buySecond, buySecondCount, sell));
            LOGGER.debug("Added new trade with " + uses + " uses, " + xp + " villager xp, a price multiplier of " + price + " and trading the following items: 1 " + sell + " with a random potion applied to it for " + buyCount + " " + buy + " and " + buySecondCount + " " + buySecond);
        }
    }

    public static void addStewTrade(List<VillagerTrades.ITrade> trades, int uses, int xp, float price, List<String> s) {
        Item buy = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s.get(0)));
        int buyCount = Integer.parseInt(s.get(1));
        if (s.size() == 4) {
            Effect effect = ForgeRegistries.POTIONS.getValue(new ResourceLocation(s.get(2)));
            int duration = Integer.parseInt(s.get(3));
            trades.add(new Trade.StewTrade(uses, xp, price, buy, buyCount, effect, duration));
            LOGGER.debug("Added new trade with " + uses + " uses, " + xp + " villager xp, a price multiplier of " + price + " and trading the following items: 1 minecraft:suspicious_stew with " + effect + " (" + (duration / 20) + " s) applied to it for " + buyCount + " " + buy);
        } else if (s.size() == 6) {
            Item buySecond = ForgeRegistries.ITEMS.getValue(new ResourceLocation(s.get(2)));
            int buySecondCount = Integer.parseInt(s.get(3));
            Effect effect = ForgeRegistries.POTIONS.getValue(new ResourceLocation(s.get(4)));
            int duration = Integer.parseInt(s.get(5));
            trades.add(new Trade.StewTrade(uses, xp, price, buy, buyCount, buySecond, buySecondCount, effect, duration));
            LOGGER.debug("Added new trade with " + uses + " uses, " + xp + " villager xp, a price multiplier of " + price + " and trading the following items: 1 minecraft:suspicious_stew with " + effect + " (" + (duration / 20) + " s) applied to it for " + buyCount + " " + buy + " and " + buySecondCount + " " + buySecond);
        }
    }

    public static VillagerType getVillagerType(String s) {
        switch (s) {
            case "desert":
                return VillagerType.DESERT;
            case "jungle":
                return VillagerType.JUNGLE;
            case "savanna":
                return VillagerType.SAVANNA;
            case "snow":
                return VillagerType.SNOW;
            case "swamp":
                return VillagerType.SWAMP;
            case "taiga":
                return VillagerType.TAIGA;
            case "plains":
                return VillagerType.PLAINS;
            default:
                return null;
        }
    }
}
