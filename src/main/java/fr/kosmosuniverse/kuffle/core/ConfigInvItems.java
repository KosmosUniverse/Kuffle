package fr.kosmosuniverse.kuffle.core;

import fr.kosmosuniverse.kuffle.utils.ItemEnchant;
import fr.kosmosuniverse.kuffle.utils.ItemMaker;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

/**
 * @author KosmosUniverse
 */
public class ConfigInvItems {
    public static ItemStack getStartTypeItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.BOOK).addName("Start Type").addLore("Type:" + Config.getStartType()).addTag("trigger", "startType");

        return itemBuilder.getItem();
    }

    public static ItemStack getLogGameResultsItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.PAPER).addTag("trigger", "logResult");

        if (Config.getLogGameResult()) {
            itemBuilder.addName("Log Game Results ENABLED");
            itemBuilder.addEnchants(Collections.singletonList(new ItemEnchant(Enchantment.MENDING, 1)));
        } else {
            itemBuilder.addName("Log Game Results DISABLED");
        }

        return itemBuilder.getItem();
    }

    public static ItemStack getCustomCraftItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.CRAFTING_TABLE).addTag("trigger", "customCrafts");

        if (Config.getCrafts()) {
            itemBuilder.addName("Custom Crafts ENABLED");
            itemBuilder.addEnchants(Collections.singletonList(new ItemEnchant(Enchantment.MENDING, 1)));
        } else {
            itemBuilder.addName("Custom Crafts DISABLED");
        }

        return itemBuilder.getItem();
    }

    public static ItemStack getSaturationItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.COOKED_BEEF).addTag("trigger", "saturation");

        if (Config.getSaturation()) {
            itemBuilder.addName("Saturation ENABLED");
            itemBuilder.addEnchants(Collections.singletonList(new ItemEnchant(Enchantment.MENDING, 1)));
        } else {
            itemBuilder.addName("Saturation DISABLED");
        }

        return itemBuilder.getItem();
    }

    public static ItemStack getLevelItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.REPEATER).addName("Penalty Level").addLore("Current Level:" + Config.getLevel().getName()).addTag("trigger", "penaltyLevel");

        return itemBuilder.getItem();
    }

    public static ItemStack getRewardItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.MAGENTA_SHULKER_BOX).addTag("trigger", "rewards");

        if (Config.getRewards()) {
            itemBuilder.addName("Rewards ENABLED");
            itemBuilder.addEnchants(Collections.singletonList(new ItemEnchant(Enchantment.MENDING, 1)));
        } else {
            itemBuilder.addName("Rewards DISABLED");
        }

        return itemBuilder.getItem();
    }

    public static ItemStack getPrintPlayerItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.PAPER).addTag("trigger", "printPlayerScore");

        if (Config.getPrintTab()) {
            itemBuilder.addName("Print Player Score ENABLED");
            itemBuilder.addEnchants(Collections.singletonList(new ItemEnchant(Enchantment.MENDING, 1)));
        } else {
            itemBuilder.addName("Print Player Score DISABLED");
        }

        return itemBuilder.getItem();
    }

    public static ItemStack getEndWhenLastItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.WOODEN_PICKAXE).addTag("trigger", "endWhenLast");

        if (Config.getEndOne()) {
            itemBuilder.addName("End When Last ENABLED");
            itemBuilder.addEnchants(Collections.singletonList(new ItemEnchant(Enchantment.MENDING, 1)));
        } else {
            itemBuilder.addName("End When Last DISABLED");
        }

        return itemBuilder.getItem();
    }

    public static ItemStack getLastAgeItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Config.getLastAge().getBox()).addName("Last Age").addLore("Last Age:" + Config.getLastAge().getName()).addTag("trigger", "lastAge");

        return itemBuilder.getItem();
    }

    public static ItemStack getSameOptionItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.SLIME_BALL).addTag("trigger", "sameOption");

        if (Config.getSame()) {
            itemBuilder.addName("Same Option ENABLED");
            itemBuilder.addEnchants(Collections.singletonList(new ItemEnchant(Enchantment.MENDING, 1)));
        } else {
            itemBuilder.addName("Same Option DISABLED");
        }

        return itemBuilder.getItem();
    }

    public static ItemStack getDoubleOptionItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.PRISMARINE_CRYSTALS).addTag("trigger", "doubleOption");

        if (Config.getDouble()) {
            itemBuilder.addName("Double Option ENABLED");
            itemBuilder.addEnchants(Collections.singletonList(new ItemEnchant(Enchantment.MENDING, 1)));
        } else {
            itemBuilder.addName("Double Option DISABLED");
        }

        return itemBuilder.getItem();
    }

    public static ItemStack getSkipItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.LIME_TERRACOTTA).addTag("trigger", "skip");

        if (Config.getSkip()) {
            itemBuilder.addName("Skip ENABLED");
            itemBuilder.addEnchants(Collections.singletonList(new ItemEnchant(Enchantment.MENDING, 1)));
        } else {
            itemBuilder.addName("Skip DISABLED");
        }

        return itemBuilder.getItem();
    }

    public static ItemStack getSkipAgeItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Config.getSkipAge().getBox()).addName("Skip Age").addLore("Age:" + Config.getSkipAge().getName()).addTag("trigger", "skipAge");

        return itemBuilder.getItem();
    }

    public static ItemStack getMinusItem(String triggerName) {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.STONE_BUTTON).addName("-").addTag("trigger", "minus" + triggerName);

        return itemBuilder.getItem();
    }

    public static ItemStack getTargetPerAgeItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.EMERALD).addName("Target Per Age").addLore("Target amount:" + Config.getTargetPerAge());

        return itemBuilder.getItem();
    }

    public static ItemStack getPlusItem(String triggerName) {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.NETHER_STAR).addName("+").addTag("trigger", "plus" + triggerName);

        return itemBuilder.getItem();
    }

    public static ItemStack getSpreadplayerItem() {
        if (Config.getSpread()) {
            return ItemMaker.newItem(Material.LIME_TERRACOTTA).addName("Spreadplayer ENABLED").addTag("trigger", "spreadplayer").getItem();
        } else {
            return ItemMaker.newItem(Material.RED_TERRACOTTA).addName("Spreadplayer DISABLED").addTag("trigger", "spreadplayer").getItem();
        }
    }

    public static ItemStack getSpreadDistanceItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.EMERALD).addName("Spread Distance").addLore("Distance:" + Config.getSpreadDistance());

        return itemBuilder.getItem();
    }

    public static ItemStack getSpreadRadiusItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.EMERALD).addName("Spread Radius").addLore("Radius:" + Config.getSpreadRadius());

        return itemBuilder.getItem();
    }

    public static ItemStack getStartTimeItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.EMERALD).addName("Start Time").addLore("Time (in min):" + Config.getStartTime());

        return itemBuilder.getItem();
    }

    public static ItemStack getAddedTimeItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.EMERALD).addName("Added Time").addLore("Time (in min):" + Config.getStartTime());

        return itemBuilder.getItem();
    }

    public static ItemStack getPassiveAllItem() {
        if (Config.getPassiveAll()) {
            return ItemMaker.newItem(Material.LIME_TERRACOTTA).addName("Passive All ENABLED").addTag("trigger", "passiveAll").getItem();
        } else {
            return ItemMaker.newItem(Material.RED_TERRACOTTA).addName("Passive All DISABLED").addTag("trigger", "passiveAll").getItem();
        }
    }

    public static ItemStack getPassiveTeamItem() {
        if (Config.getPassiveTeam()) {
            return ItemMaker.newItem(Material.LIME_TERRACOTTA).addName("Passive Team ENABLED").addTag("trigger", "passiveTeam").getItem();
        } else {
            return ItemMaker.newItem(Material.RED_TERRACOTTA).addName("Passive Team DISABLED").addTag("trigger", "passiveTeam").getItem();
        }
    }

    public static ItemStack getPlayerTipsItem() {
        if (Config.getTips()) {
            return ItemMaker.newItem(Material.LIME_TERRACOTTA).addName("Tips ENABLED").addTag("trigger", "playerTips").getItem();
        } else {
            return ItemMaker.newItem(Material.RED_TERRACOTTA).addName("Tips DISABLED").addTag("trigger", "playerTips").getItem();
        }
    }

    public static ItemStack getPlayerLangItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.BOOK).addName("Lang").addLore("lang:" + Config.getLang()).addTag("trigger", "playerLang");

        return itemBuilder.getItem();
    }

    public static ItemStack getEndTeleporterItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.EMERALD).addName("End Teleporter XP Cost").addLore("Cost (in level):" + Config.getXpEnd());

        return itemBuilder.getItem();
    }

    public static ItemStack getOverworldTeleporterItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.EMERALD).addName("Overworld Teleporter XP Cost").addLore("Cost (in level):" + Config.getXpOverworld());

        return itemBuilder.getItem();
    }

    public static ItemStack getCoralCompassItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.EMERALD).addName("Coral Compass XP Cost").addLore("Cost (in level):" + Config.getXpCoral());

        return itemBuilder.getItem();
    }

    public static ItemStack getTeamItem() {
        if (Config.getTeam()) {
            return ItemMaker.newItem(Material.LIME_TERRACOTTA).addName("Team Option ENABLED").addTag("trigger", "teamOption").getItem();
        } else {
            return ItemMaker.newItem(Material.RED_TERRACOTTA).addName("Team Option DISABLED").addTag("trigger", "teamOption").getItem();
        }
    }

    public static ItemStack getTeamSizeItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.EMERALD).addName("Team Size").addLore("Max player in Team:" + Config.getTeamSize());

        return itemBuilder.getItem();
    }

    public static ItemStack getTeamInvItem() {
        if (Config.getTeamInv()) {
            return ItemMaker.newItem(Material.LIME_TERRACOTTA).addName("Team Inventory ENABLED").addTag("trigger", "teamInventory").getItem();
        } else {
            return ItemMaker.newItem(Material.RED_TERRACOTTA).addName("Team Inventory DISABLED").addTag("trigger", "teamInventory").getItem();
        }
    }

    public static ItemStack getTeamInvSizeItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.EMERALD).addName("Team Inventory Size").addLore("Size (as lines):" + Config.getTeamInvSize());

        return itemBuilder.getItem();
    }

    public static ItemStack getSbttItem() {
        if (Config.getSBTT()) {
            return ItemMaker.newItem(Material.LIME_TERRACOTTA).addName("SBTT ENABLED").addTag("trigger", "sbtt").getItem();
        } else {
            return ItemMaker.newItem(Material.RED_TERRACOTTA).addName("SBTT DISABLED").addTag("trigger", "sbtt").getItem();
        }
    }

    public static ItemStack getSbttSizeItem() {
        ItemMaker itemBuilder = ItemMaker.newItem(Material.EMERALD).addName("SBTT Size").addLore("Amount of target per SBTT:" + Config.getSBTTAmount());

        return itemBuilder.getItem();
    }
}
