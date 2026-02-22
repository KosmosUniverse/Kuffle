package fr.kosmosuniverse.kuffle.core;

import fr.kosmosuniverse.kuffle.utils.ItemEnchant;
import fr.kosmosuniverse.kuffle.utils.ItemMaker;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * @author KosmosUniverse
 */
public class ConfigInvItems {
    public static ItemStack getSaveItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.CHEST)
                .addName("Save Config")
                .addTag("trigger", "saveConfig")
                .addLores("Save actual config.");

        return itemBuilder.getItem();
    }

    public static ItemStack getResetItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.CLOCK)
                .addName("Reset Config")
                .addTag("trigger", "resetConfig")
                .addLores("Discard all config to reload default plugin config.");

        return itemBuilder.getItem();
    }

    public static ItemStack getStartTypeItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.BOOK)
                .addName("Start Type")
                .addTag("trigger", "startType")
                .addLores("Type:" + Config.getStartType(),
                        "Game type loaded at plugin start.",
                        "Values : NO_TYPE, ITEMS, BLOCKS.",
                        "Default : NO_TYPE");

        return itemBuilder.getItem();
    }

    public static ItemStack getLogGameResultsItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.PAPER)
                .addTag("trigger", "logResult")
                .addLores("Defines if game results are logged at game end.",
                        "Values : true, false.",
                        "Default : false");

        if (Config.getLogGameResult()) {
            itemBuilder.addName("Log Game Results ENABLED");
            itemBuilder.addEnchants(Collections.singletonList(new ItemEnchant(Enchantment.MENDING, 1)));
        } else {
            itemBuilder.addName("Log Game Results DISABLED");
        }

        return itemBuilder.getItem();
    }

    public static ItemStack getCustomCraftItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.CRAFTING_TABLE)
                .addTag("trigger", "customCrafts")
                .addLores("Defines if not mandatory custom crafts will be loaded for the game.",
                        "Values : true, false.",
                        "Default : true");

        if (Config.getCrafts()) {
            itemBuilder.addName("Custom Crafts ENABLED");
            itemBuilder.addEnchants(Collections.singletonList(new ItemEnchant(Enchantment.MENDING, 1)));
        } else {
            itemBuilder.addName("Custom Crafts DISABLED");
        }

        return itemBuilder.getItem();
    }

    public static ItemStack getSaturationItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.COOKED_BEEF)
                .addTag("trigger", "saturation")
                .addLores("Defines if player will have constant saturation.",
                        "Values : true, false.",
                        "Default : true");

        if (Config.getSaturation()) {
            itemBuilder.addName("Saturation ENABLED");
            itemBuilder.addEnchants(Collections.singletonList(new ItemEnchant(Enchantment.MENDING, 1)));
        } else {
            itemBuilder.addName("Saturation DISABLED");
        }

        return itemBuilder.getItem();
    }

    public static ItemStack getLevelItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.REPEATER)
                .addName("Penalty Level")
                .addTag("trigger", "penaltyLevel")
                .addLores("Current Level:" + Config.getLevel().getName(),
                        "Level used for the game.",
                        "Values : " + LevelManager.getInstance()
                                .getLevels()
                                .stream()
                                .sorted(Comparator.comparingInt(Level::getNumber))
                                .map(Level::getName)
                                .collect(Collectors.joining(", ")) + ".",
                        "Default : EASY");

        return itemBuilder.getItem();
    }

    public static ItemStack getRewardItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.MAGENTA_SHULKER_BOX)
                .addTag("trigger", "rewards")
                .addLores("Defines if player will receive reward at each Age end.",
                        "Values : true, false.",
                        "Default : true");

        if (Config.getRewards()) {
            itemBuilder.addName("Rewards ENABLED");
            itemBuilder.addEnchants(Collections.singletonList(new ItemEnchant(Enchantment.MENDING, 1)));
        } else {
            itemBuilder.addName("Rewards DISABLED");
        }

        return itemBuilder.getItem();
    }

    public static ItemStack getPrintPlayerItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.PAPER)
                .addTag("trigger", "printPlayerScore")
                .addLores("Defines if player will have his stats displayed at game end.",
                        "Values : true, false.",
                        "Default : false");

        if (Config.getPrintTab()) {
            itemBuilder.addName("Print Player Score ENABLED");
            itemBuilder.addEnchants(Collections.singletonList(new ItemEnchant(Enchantment.MENDING, 1)));
        } else {
            itemBuilder.addName("Print Player Score DISABLED");
        }

        return itemBuilder.getItem();
    }

    public static ItemStack getEndWhenLastItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.WOODEN_PICKAXE)
                .addTag("trigger", "endWhenLast")
                .addLores("Defines if game will end when only one player left.",
                        "Values : true, false.",
                        "Default : false");

        if (Config.getEndOne()) {
            itemBuilder.addName("End When Last ENABLED");
            itemBuilder.addEnchants(Collections.singletonList(new ItemEnchant(Enchantment.MENDING, 1)));
        } else {
            itemBuilder.addName("End When Last DISABLED");
        }

        return itemBuilder.getItem();
    }

    public static ItemStack getLastAgeItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Config.getLastAge().getBox())
                .addName("Last Age")
                .addTag("trigger", "lastAge")
                .addLores("Last Age:" + Config.getLastAge().getName(),
                        "Defines which Age will be the last of the game.",
                        "Values : " + AgeManager.getAges().stream()
                                .filter(age -> age.getNumber() != -1)
                                .sorted(Comparator.comparingInt(Age::getNumber))
                                .map(age -> age.getColor() + age.getName().replace("_Age", ""))
                                .collect(Collectors.joining(ChatColor.RESET + ", ")) + ".",
                        "Default : Mythic");

        return itemBuilder.getItem();
    }

    public static ItemStack getCoopOptionItem() {
        ItemMaker itemBuilder = Config.getCoop() ? ItemMaker.newItem(Material.LIME_TERRACOTTA).addName("Coop ENABLED") : ItemMaker.newItem(Material.RED_TERRACOTTA).addName("Coop DISABLED");

        itemBuilder.addTag("trigger", "coopOption")
                .addLores("Defines if game will have the \"Coop\" option enabled.",
                        "Values : true, false.",
                        "Default : false");

        return itemBuilder.getItem();
    }

    public static ItemStack getCoopSkipItem() {
        ItemMaker itemBuilder = Config.getCoopSkip() ? ItemMaker.newItem(Material.LIME_TERRACOTTA).addName("Coop Skip ENABLED") : ItemMaker.newItem(Material.RED_TERRACOTTA).addName("Coop Skip DISABLED");

        itemBuilder.addTag("trigger", "coopSkip")
                .addLores("Defines if skip will impact \"Coop\" Option timer.",
                        "Values : true, false.",
                        "Default : false");

        return itemBuilder.getItem();
    }

    public static ItemStack getCoopBaseItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.EMERALD)
                .addName("Coop Base Time")
                .addLores("Time (in mins):" + Config.getCoopBase(),
                        "Represent the amount of time the game will start with.",
                        "Increase or Decreased by 5.",
                        "Values : greater or equal to 1.",
                        "Default : 15");

        return itemBuilder.getItem();
    }

    public static ItemStack getCoopUpdatedItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.EMERALD)
                .addName("Coop Updated Time")
                .addLores("Time (in mins):" + Config.getCoopUpdated(),
                        "Represent the amount of time added to the timer wen a target is validated.",
                        "Same amount of time will decrease the timer if coop skip is enabled.",
                        "Increase or Decreased by 1.",
                        "Values : greater or equal to 1.",
                        "Default : 1");

        return itemBuilder.getItem();
    }

    public static ItemStack getSameOptionItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.SLIME_BALL)
                .addTag("trigger", "sameOption")
                .addLores("Defines if game will have the \"Same\" option enabled.",
                        "Values : true, false.",
                        "Default : false");

        if (Config.getSame()) {
            itemBuilder.addName("Same Option ENABLED");
            itemBuilder.addEnchants(Collections.singletonList(new ItemEnchant(Enchantment.MENDING, 1)));
        } else {
            itemBuilder.addName("Same Option DISABLED");
        }

        return itemBuilder.getItem();
    }

    public static ItemStack getDoubleOptionItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.PRISMARINE_CRYSTALS)
                .addTag("trigger", "doubleOption")
                .addLores("Defines if game will have the \"Double\" option enabled.",
                        "Values : true, false.",
                        "Default : false");

        if (Config.getDouble()) {
            itemBuilder.addName("Double Option ENABLED");
            itemBuilder.addEnchants(Collections.singletonList(new ItemEnchant(Enchantment.MENDING, 1)));
        } else {
            itemBuilder.addName("Double Option DISABLED");
        }

        return itemBuilder.getItem();
    }

    public static ItemStack getSkipItem() {
        ItemMaker itemBuilder = Config.getSkip() ? ItemMaker.newItem(Material.LIME_TERRACOTTA).addName("Skip ENABLED") : ItemMaker.newItem(Material.RED_TERRACOTTA).addName("Skip DISABLED");

        itemBuilder.addTag("trigger", "skip")
                .addLores("Defines if player will be authorized to skip targets.",
                        "Values : true, false.",
                        "Default : true");

        return itemBuilder.getItem();
    }

    public static ItemStack getSkipAgeItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Config.getSkipAge().getBox())
                .addName("Skip Age")
                .addTag("trigger", "skipAge")
                .addLores("Age:" + Config.getSkipAge().getName(),
                        "Defines player will be able to skip starting from this Age.",
                        "Values : " + AgeManager.getAges().stream()
                                .filter(age -> age.getNumber() != -1)
                                .sorted(Comparator.comparingInt(Age::getNumber))
                                .map(age -> age.getColor() + age.getName().replace("_Age", ""))
                                .collect(Collectors.joining(ChatColor.RESET + ", ")) + ".",
                        "Default : Classic");

        return itemBuilder.getItem();
    }

    public static ItemStack getMinusItem(String triggerName) {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.STONE_BUTTON)
                .addName("-")
                .addTag("trigger", "minus" + triggerName)
                .addLores("Decrease value.",
                        "See central item to see amount decreased");

        return itemBuilder.getItem();
    }

    public static ItemStack getTargetPerAgeItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.EMERALD)
                .addName("Target Per Age")
                .addLores("Target amount:" + Config.getTargetPerAge(),
                        "Represent the amount of target to validate in an Age.",
                        "Increase or Decreased by 1.",
                        "Values : greater or equal to 1.",
                        "Default : 5");

        return itemBuilder.getItem();
    }

    public static ItemStack getPlusItem(String triggerName) {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.NETHER_STAR)
                .addName("+")
                .addTag("trigger", "plus" + triggerName)
                .addLores("Increase value.",
                        "See central item to see amount increase");

        return itemBuilder.getItem();
    }

    public static ItemStack getSpreadplayerItem() {
        ItemMaker itemBuilder = Config.getSpread() ? ItemMaker.newItem(Material.LIME_TERRACOTTA).addName("Spreadplayer ENABLED") : ItemMaker.newItem(Material.RED_TERRACOTTA).addName("Spreadplayer DISABLED");

        itemBuilder.addTag("trigger", "spreadplayer")
                .addLores("Defines if players will be teleported at game start.",
                        "Values : true, false.",
                        "Default : false");

        return itemBuilder.getItem();
    }

    public static ItemStack getSpreadDistanceItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.EMERALD)
                .addName("Spread Distance")
                .addLores("Distance:" + Config.getSpreadDistance(),
                        "Represent the min distance between players when spread.",
                        "Increase or Decreased by 100.",
                        "Values : greater than 100 and lower than Spread Radius.",
                        "Default : 500");

        return itemBuilder.getItem();
    }

    public static ItemStack getSpreadRadiusItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.EMERALD)
                .addName("Spread Radius")
                .addLores("Radius:" + Config.getSpreadRadius(),
                        "Represent the max radius when spread.",
                        "Increase or Decreased by 100.",
                        "Values : greater than Spread Distance.",
                        "Default : 1000");

        return itemBuilder.getItem();
    }

    public static ItemStack getStartTimeItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.EMERALD)
                .addName("Start Time")
                .addLores("Time (in min):" + Config.getStartTime(),
                        "Define the time amount for the first Age targets.",
                        "Increase or Decreased by 1.",
                        "Values : greater or equal to 1.",
                        "Default : 4");

        return itemBuilder.getItem();
    }

    public static ItemStack getAddedTimeItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.EMERALD)
                .addName("Added Time")
                .addLores("Time (in min):" + Config.getAddedTime(),
                        "Define the time amount added each Age for the targets.",
                        "Increase or Decreased by 1.",
                        "Values : greater or equal to 1.",
                        "Default : 2");

        return itemBuilder.getItem();
    }

    public static ItemStack getPassiveAllItem() {
        ItemMaker itemBuilder = Config.getPassiveAll() ? ItemMaker.newItem(Material.LIME_TERRACOTTA).addName("Passive All ENABLED") : ItemMaker.newItem(Material.RED_TERRACOTTA).addName("Passive All DISABLED");

        itemBuilder.addTag("trigger", "passiveAll")
                .addLores("Defines if players interaction will be prevented.",
                        "Values : true, false.",
                        "Default : false");

        return itemBuilder.getItem();
    }

    public static ItemStack getPassiveTeamItem() {
        ItemMaker itemBuilder = Config.getPassiveTeam() ? ItemMaker.newItem(Material.LIME_TERRACOTTA).addName("Passive Team ENABLED") : ItemMaker.newItem(Material.RED_TERRACOTTA).addName("Passive Team DISABLED");

        itemBuilder.addTag("trigger", "passiveTeam")
                .addLores("Defines if players interaction, outside of teams, will be prevented.",
                        "Values : true, false.",
                        "Default : false");

        return itemBuilder.getItem();
    }

    public static ItemStack getPlayerTipsItem() {
        ItemMaker itemBuilder = Config.getTips() ? ItemMaker.newItem(Material.LIME_TERRACOTTA).addName("Tips ENABLED") : ItemMaker.newItem(Material.RED_TERRACOTTA).addName("Tips DISABLED");

        itemBuilder.addTag("trigger", "playerTips")
                .addLores("Defines if players will have tips at each Age.",
                        "Values : true, false.",
                        "Default : false");

        return itemBuilder.getItem();
    }

    public static ItemStack getPlayerLangItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.BOOK)
                .addName("Lang")
                .addTag("trigger", "playerLang")
                .addLores("lang:" + Config.getLang(),
                        "Defines the lang used for plugin display",
                        "Values : " + String.join(", ", LangManager.getLangs()) + ".",
                        "Default : en");

        return itemBuilder.getItem();
    }

    public static ItemStack getEndTeleporterItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.EMERALD)
                .addName("End Teleporter XP Cost")
                .addLores("Cost (in level):" + Config.getXpEnd(),
                        "Defines base the cost in xp levels to use this teleporter.",
                        "Values : Between 1 and 10 included.",
                        "Default : 5");

        return itemBuilder.getItem();
    }

    public static ItemStack getOverworldTeleporterItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.EMERALD)
                .addName("Overworld Teleporter XP Cost")
                .addLores("Cost (in level):" + Config.getXpOverworld(),
                        "Defines base the cost in xp levels to use this teleporter.",
                        "Values : Between 1 and 20 included.",
                        "Default : 10");

        return itemBuilder.getItem();
    }

    public static ItemStack getCoralCompassItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.EMERALD)
                .addName("Coral Compass XP Cost")
                .addLores("Cost (in level):" + Config.getXpCoral(),
                        "Defines base the cost in xp levels to use this teleporter.",
                        "Values : Between 1 and 30 included.",
                        "Default : 20");

        return itemBuilder.getItem();
    }

    public static ItemStack getTeamItem() {
        ItemMaker itemBuilder = Config.getTeam() ? ItemMaker.newItem(Material.LIME_TERRACOTTA).addName("Team Option ENABLED") : ItemMaker.newItem(Material.RED_TERRACOTTA).addName("Team Option DISABLED");

        itemBuilder.addTag("trigger", "teamOption")
                .addLores("Defines if \"Team\" option is enabled.",
                        "Values : true, false.",
                        "Default : false");

        return itemBuilder.getItem();
    }

    public static ItemStack getTeamSizeItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.EMERALD)
                .addName("Team Size")
                .addLores("Max player in Team:" + Config.getTeamSize(),
                        "Defines the max size of teams.",
                        "Values : Above 1.",
                        "Default : 2");

        return itemBuilder.getItem();
    }

    public static ItemStack getTeamInvItem() {
        ItemMaker itemBuilder = Config.getTeamInv() ? ItemMaker.newItem(Material.LIME_TERRACOTTA).addName("Team Inventory ENABLED") : ItemMaker.newItem(Material.RED_TERRACOTTA).addName("Team Inventory DISABLED");

        itemBuilder.addTag("trigger", "teamInventory")
                .addLores("Defines if \"Team Inventory\" option is enabled.",
                        "Values : true, false.",
                        "Default : false");

        return itemBuilder.getItem();
    }

    public static ItemStack getTeamInvSizeItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.EMERALD)
                .addName("Team Inventory Size")
                .addLores("Size (as lines):" + Config.getTeamInvSize(),
                        "Defines the size of team inventory.",
                        "Values : Between 1 and 6 included (inventory lines)",
                        "Default : 1");

        return itemBuilder.getItem();
    }

    public static ItemStack getSbttItem() {
        ItemMaker itemBuilder = Config.getSBTT() ? ItemMaker.newItem(Material.LIME_TERRACOTTA).addName("SBTT ENABLED") : ItemMaker.newItem(Material.RED_TERRACOTTA).addName("SBTT DISABLED");

        itemBuilder.addTag("trigger", "sbtt")
                .addLores("Defines if \"SBTT\" option is enabled.",
                        "Values : true, false.",
                        "Default : false");

        return itemBuilder.getItem();
    }

    public static ItemStack getSbttSizeItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.EMERALD)
                .addName("SBTT Size")
                .addLores("Amount of target per SBTT:" + Config.getSBTTAmount(),
                        "Defines the amount of target used to create template.",
                        "Values : Between 1 and 9 included",
                        "Default : 4");

        return itemBuilder.getItem();
    }
}
