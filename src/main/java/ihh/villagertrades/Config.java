package ihh.villagertrades;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Config {
    public static ForgeConfigSpec SPEC;
    private static ForgeConfigSpec.ConfigValue<List<String>> VILLAGER_1_TRADES;
    private static ForgeConfigSpec.ConfigValue<List<String>> VILLAGER_2_TRADES;
    private static ForgeConfigSpec.ConfigValue<List<String>> VILLAGER_3_TRADES;
    private static ForgeConfigSpec.ConfigValue<List<String>> VILLAGER_4_TRADES;
    private static ForgeConfigSpec.ConfigValue<List<String>> VILLAGER_5_TRADES;
    private static ForgeConfigSpec.ConfigValue<List<String>> TRADER_NORMAL_TRADES;
    private static ForgeConfigSpec.ConfigValue<List<String>> TRADER_LAST_TRADES;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        VILLAGER_1_TRADES = builder.comment("Adds new villager trades. villager_x_trades defines the villager level.\nFormat is \"profession;uses;xp;pricemultiplier;tradetype;tradetype-specific-arguments\"\nProfession: villager profession (e. g. \"minecraft:librarian\")\nLevel: villager level at which this trade is offered (1-5)\nUses: How often the trade can be traded before it is locked. This is 12 or 16 for most trades, for enchanted items, it is 3\nXp: The amount of villager xp given to the villager\nPrice multiplier: Every time you trade with a villager, the selling price is multiplied with (1 - this value). For example, a value of 0.1 means that every time you use the trade, the price is lowered by 10%. Vanilla default for most items is 0.05, for tools, armor or enchanted books, it is 0.2\nTradetype and corresponding tradetype-specific args can take the following values (values in [] are optional):\n  \"normal\": \"buyItem1;buyItemCount1;[buyItem2];[buyItemCount2];sellItem;sellItemCount\" - a normal trade that takes 1 or 2 stacks in and gives 1 stack out. The items are item ids (e. g. \"minecraft:emerald\"), the item counts are numbers between 1 and 64\n  \"dyed\": \"buyItem1;buyItemCount1;[buyItem2];[buyItemCount2];sellItem\" - the sell item (e. g. leather armor) will be randomly dyed\n  \"map\": \"buyItem1;buyItemCount1;[buyItem2];[buyItemCount2];structure;mapdecoration\" - structure is a structure id (e. g. \"minecraft:stronghold\"), mapdecoration can be one of the following: \"player\", \"frame\", \"red_marker\", \"blue_marker\", \"target_x\", \"target_point\", \"player_off_map\", \"player_off_limits\", \"mansion\", \"monument\", \"red_x\", \"banner_black\", \"banner_blue\", \"banner_brown\", \"banner_cyan\", \"banner_gray\", \"banner_green\", \"banner_light_blue\", \"banner_light_gray\", \"banner_lime\", \"banner_magenta\", \"banner_orange\", \"banner_pink\", \"banner_purple\", \"banner_red\", \"banner_white\", \"banner_yellow\"\n  \"enchantedbook\": \"buyItem1;[buyItem2];[buyItemCount2]\" - outputs a randomly enchanted book, at which rarity the quantity of buyItem1 is scaled - so if buyItem1 was minecraft:emerald, a sharpness 5 book costs approx. 5 times more emeralds than a sharpness 1 book\n  \"enchanteditem\": \"buyItem1;[buyItem2];[buyItemCount2];sellItem\" - the sell item is randomly enchanted, and the quantity of buyItem1 is scaled at the enchantment rarity, similar to the enchanted book trade type\n  \"potion\": \"buyItem1;buyItemCount1;[buyItem2];[buyItemCount2];sellItem\" - a random potion is applied to the sell item, so unless you have additional potion-like items added by other mods, this should only be \"minecraft:potion\", \"minecraft:splash_potion\", \"minecraft:lingering_potion\" or \"minecraft:tipped_arrow\"\n  \"stew\": \"buyItem1;buyItemCount1;[buyItem2];[buyItemCount2];effect;duration\" - effect is an effect id (e. g. \"minecraft:strength\\n, duration is an integer that resembles the ticks the effects last\nNote that as there is no other way to add trades than replacing all existing ones, you can only add either trades for all villagers or not any trades at all.").define("villager_1_trades", new ArrayList<>());
        VILLAGER_2_TRADES = builder.define("villager_2_trades", new ArrayList<>());
        VILLAGER_3_TRADES = builder.define("villager_3_trades", new ArrayList<>());
        VILLAGER_4_TRADES = builder.define("villager_4_trades", new ArrayList<>());
        VILLAGER_5_TRADES = builder.define("villager_5_trades", new ArrayList<>());
        TRADER_NORMAL_TRADES = builder.comment("Adds new wandering trader trades. Due to how Minecraft handles the wandering trader, there are two lists: normal and last trades. When the trader spawns, five normal trades and one last trade are each randomly chosen from their corresponding lists. Note that as soon as you add anything in one of the lists, it removes all other trades, and only this mod's added trades remain.\nFormat is \"uses;pricemultiplier;tradetype;tradetype-specific-args\"\nUses: How often the trade can be traded before it is locked. This is 12 or 16 for most trades, for enchanted items, it is 3\nPrice multiplier: Every time you trade with a villager, the selling price is multiplied with (1 - this value). For example, a value of 0.1 means that every time you use the trade, the price is lowered by 10%. Vanilla default for most items is 0.05, for tools, armor, enchanted books and maps, it is 0.2\nTradetype and corresponding tradetype-specific args can take the following values (values in [] are optional):\n  \"normal\": \"buyItem1;buyItemCount1;[buyItem2];[buyItemCount2];sellItem;sellItemCount\" - a normal trade that takes 1 or 2 stacks in and gives 1 stack out. The items are item ids (e. g. \"minecraft:emerald\"), the item counts are numbers between 1 and 64\n  \"dyed\": \"buyItem1;buyItemCount1;[buyItem2];[buyItemCount2];sellItem\" - the sell item (e. g. leather armor) will be randomly dyed\n  \"map\": \"buyItem1;buyItemCount1;[buyItem2];[buyItemCount2];structure;mapdecoration\" - structure is a structure id (e. g. \"minecraft:stronghold\"), mapdecoration can be one of the following: \"player\", \"frame\", \"red_marker\", \"blue_marker\", \"target_x\", \"target_point\", \"player_off_map\", \"player_off_limits\", \"mansion\", \"monument\", \"red_x\", \"banner_black\", \"banner_blue\", \"banner_brown\", \"banner_cyan\", \"banner_gray\", \"banner_green\", \"banner_light_blue\", \"banner_light_gray\", \"banner_lime\", \"banner_magenta\", \"banner_orange\", \"banner_pink\", \"banner_purple\", \"banner_red\", \"banner_white\", \"banner_yellow\"\n  \"enchantedbook\": \"buyItem1;buyItem2;buyItemCount2\" - outputs a randomly enchanted book, at which rarity the quantity of buyItem1 is scaled - so if buyItem1 was minecraft:emerald, a sharpness 5 book costs approx. 5 times more emeralds than a sharpness 1 book\n  \"enchanteditem\": \"buyItem1;buyItem2;buyItemCount2;sellItem\" - the sell item is randomly enchanted, and the quantity of buyItem1 is scaled at the enchantment rarity, similar to the enchanted book trade type\n  \"potion\": \"buyItem1;buyItemCount1;[buyItem2];[buyItemCount2];sellItem\" - a random potion is applied to sellItem, so unless you have additional potion-like items added by other mods, this should only be \"minecraft:potion\", \"minecraft:splash_potion\", \"minecraft:lingering_potion\" or \"minecraft:tipped_arrow\"\n  \"stew\": \"buyItem1;buyItemCount1;[buyItem2];[buyItemCount2];effect;duration\" - effect is an effect id (e. g. \"minecraft:strength\"), duration is an integer that resembles the ticks the effects last ").define("trader_normal_trades", new ArrayList<>());
        TRADER_LAST_TRADES = builder.define("trader_last_trades", new ArrayList<>());
        SPEC = builder.build();
    }

    public static Map<VillagerProfession, Int2ObjectMap<VillagerTrades.ITrade[]>> getVillagerTrades() {
        Map<VillagerProfession, Int2ObjectMap<VillagerTrades.ITrade[]>> result = new HashMap<>();
        for (VillagerProfession p : ForgeRegistries.PROFESSIONS.getValues()) {
            List<List<String>> strings = new ArrayList<>();
            Int2ObjectMap<VillagerTrades.ITrade[]> map = new Int2ObjectOpenHashMap<>();
            for (String s : VILLAGER_1_TRADES.get()) strings.add(Arrays.asList(s.split(";")));
            strings = strings.stream().filter(e -> e.size() > 4).filter(e -> ForgeRegistries.PROFESSIONS.getValue(new ResourceLocation(e.get(0))) == p).collect(Collectors.toList());
            map.put(1, getVillagerTrades(strings));
            strings.clear();
            for (String s : VILLAGER_2_TRADES.get()) strings.add(Arrays.asList(s.split(";")));
            strings = strings.stream().filter(e -> e.size() > 4).filter(e -> ForgeRegistries.PROFESSIONS.getValue(new ResourceLocation(e.get(0))) == p).collect(Collectors.toList());
            map.put(2, getVillagerTrades(strings));
            strings.clear();
            for (String s : VILLAGER_3_TRADES.get()) strings.add(Arrays.asList(s.split(";")));
            strings = strings.stream().filter(e -> e.size() > 4).filter(e -> ForgeRegistries.PROFESSIONS.getValue(new ResourceLocation(e.get(0))) == p).collect(Collectors.toList());
            map.put(3, getVillagerTrades(strings));
            strings.clear();
            for (String s : VILLAGER_4_TRADES.get()) strings.add(Arrays.asList(s.split(";")));
            strings = strings.stream().filter(e -> e.size() > 4).filter(e -> ForgeRegistries.PROFESSIONS.getValue(new ResourceLocation(e.get(0))) == p).collect(Collectors.toList());
            map.put(4, getVillagerTrades(strings));
            strings.clear();
            for (String s : VILLAGER_5_TRADES.get()) strings.add(Arrays.asList(s.split(";")));
            strings = strings.stream().filter(e -> e.size() > 4).filter(e -> ForgeRegistries.PROFESSIONS.getValue(new ResourceLocation(e.get(0))) == p).collect(Collectors.toList());
            map.put(5, getVillagerTrades(strings));
            result.put(p, map);
        }
        return result;
    }

    private static VillagerTrades.ITrade[] getVillagerTrades(List<List<String>> s) {
        List<VillagerTrades.ITrade> result = new ArrayList<>();
        for (List<String> l : s)
            try {
                int uses = Integer.parseInt(l.get(1));
                int xp = Integer.parseInt(l.get(2));
                float price = Float.parseFloat(l.get(3));
                if (uses < 1 || xp < 1 || price < 0 || price >= 1) continue;
                if (l.get(4).equals("normal"))
                    TradeUtils.addNormalTrade(result, uses, xp, price, l.subList(5, l.size()));
                if (l.get(4).equals("dyed"))
                    TradeUtils.addDyedTrade(result, uses, xp, price, l.subList(5, l.size()));
                if (l.get(4).equals("map"))
                    TradeUtils.addMapTrade(result, uses, xp, price, l.subList(5, l.size()));
                if (l.get(4).equals("biome"))
                    TradeUtils.addBiomeTrade(result, uses, xp, price, l.subList(5, l.size()));
                if (l.get(4).equals("enchantedbook"))
                    TradeUtils.addEnchantedBookTrade(result, uses, xp, price, l.subList(5, l.size()));
                if (l.get(4).equals("enchanteditem"))
                    TradeUtils.addEnchantedItemTrade(result, uses, xp, price, l.subList(5, l.size()));
                if (l.get(4).equals("potion"))
                    TradeUtils.addPotionTrade(result, uses, xp, price, l.subList(5, l.size()));
                if (l.get(4).equals("stew"))
                    TradeUtils.addStewTrade(result, uses, xp, price, l.subList(5, l.size()));
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        return result.toArray(new VillagerTrades.ITrade[0]);
    }

    public static Int2ObjectMap<VillagerTrades.ITrade[]> getTraderTrades() {
        Int2ObjectMap<VillagerTrades.ITrade[]> m = new Int2ObjectOpenHashMap<>();
        m.put(1, getTraderTrades(TRADER_NORMAL_TRADES.get()));
        m.put(2, getTraderTrades(TRADER_LAST_TRADES.get()));
        return m;
    }

    private static VillagerTrades.ITrade[] getTraderTrades(List<String> list) {
        List<List<String>> strings = new ArrayList<>();
        for (String s : list) strings.add(Arrays.asList(s.split(";")));
        strings = strings.stream().filter(e -> e.size() > 2).collect(Collectors.toList());
        List<VillagerTrades.ITrade> trades = new ArrayList<>();
        for (List<String> s : strings)
            try {
                int uses = Integer.parseInt(s.get(0));
                float price = Float.parseFloat(s.get(1));
                switch (s.get(2)) {
                    case "normal":
                        TradeUtils.addNormalTrade(trades, uses, 1, price, s.subList(3, s.size()));
                        break;
                    case "dyed":
                        TradeUtils.addDyedTrade(trades, uses, 1, price, s.subList(3, s.size()));
                        break;
                    case "map":
                        TradeUtils.addMapTrade(trades, uses, 1, price, s.subList(3, s.size()));
                        break;
                    case "enchantedbook":
                        TradeUtils.addEnchantedBookTrade(trades, uses, 1, price, s.subList(3, s.size()));
                        break;
                    case "enchanteditem":
                        TradeUtils.addEnchantedItemTrade(trades, uses, 1, price, s.subList(3, s.size()));
                        break;
                    case "potion":
                        TradeUtils.addPotionTrade(trades, uses, 1, price, s.subList(3, s.size()));
                        break;
                    case "stew":
                        TradeUtils.addStewTrade(trades, uses, 1, price, s.subList(3, s.size()));
                        break;
                }
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        return trades.toArray(new VillagerTrades.ITrade[0]);
    }
}
