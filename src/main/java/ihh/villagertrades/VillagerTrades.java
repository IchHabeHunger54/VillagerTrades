package ihh.villagertrades;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades.ITrade;
import net.minecraftforge.common.VillagerTradingManager;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

@Mod("villagertrades")
public class VillagerTrades {
    public static final Logger LOGGER = LogManager.getLogger();

    public VillagerTrades() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    }

    private void setup(final FMLLoadCompleteEvent e) {
        Map<VillagerProfession, Int2ObjectMap<ITrade[]>> villager = Config.getVillagerTrades();
        if (!villager.isEmpty()) {
            try {
                Field field = VillagerTradingManager.class.getDeclaredField("VANILLA_TRADES");
                field.setAccessible(true);
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                field.set(null, villager);
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }
            net.minecraft.entity.merchant.villager.VillagerTrades.VILLAGER_DEFAULT_TRADES.putAll(villager);
        }
        Int2ObjectMap<ITrade[]> trades = Config.getTraderTrades();
        if (trades.get(1).length > 0 && trades.get(2).length > 0) {
            try {
                Field field = VillagerTradingManager.class.getDeclaredField("WANDERER_TRADES");
                field.setAccessible(true);
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                field.set(null, trades);
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }
            net.minecraft.entity.merchant.villager.VillagerTrades.field_221240_b.putAll(trades);
        }
    }
}
