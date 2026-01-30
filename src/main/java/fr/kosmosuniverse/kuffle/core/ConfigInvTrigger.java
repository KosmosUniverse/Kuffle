package fr.kosmosuniverse.kuffle.core;

import fr.kosmosuniverse.kuffle.utils.Function3arity;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author KosmosUniverse
 */
public class ConfigInvTrigger {
    private static final Map<String, Function3arity<Player, Inventory, ItemStack>> triggers = new HashMap<>();
    private static final List<String> types = Arrays.asList("NO_TYPE", "ITEMS", "BLOCKS");
    private static final List<String> levels = LevelManager.getInstance().getLevels().stream().sorted(Comparator.comparingInt(Level::getNumber)).map(Level::getName).collect(Collectors.toList());
    private static final List<String> ages = AgeManager.getOrderedAgesNameList();
    private static final List<String> langs = LangManager.getLangs();

    public static void clear() {
        triggers.clear();
    }

    public static void addTrigger(String triggerName, Function3arity<Player, Inventory, ItemStack> function) {
        if (!triggers.containsKey(triggerName)) {
            triggers.put(triggerName, function);
        }
    }

    public static void apply(String triggerName, Player player, Inventory inv, ItemStack item) {
        if (!triggers.containsKey(triggerName)) {
            throw new IllegalArgumentException("no trigger for [" + triggerName + "]");
        }

        triggers.get(triggerName).apply(player, inv, item);
    }

    public static void startTypeTrigger(Player player, Inventory inv, ItemStack item) {
        String type = item.getItemMeta().getLore().get(0).split(":")[1];
        int idx = types.indexOf(type);
        idx++;

        if (idx == types.size()) {
            idx = 0;
        }

        Config.setStartType(types.get(idx));

        inv.setItem(9, ConfigInvItems.getStartTypeItem());
    }

    public static void logResultTrigger(Player player, Inventory inv, ItemStack item) {
        boolean isEnabled = item.getItemMeta().hasEnchants();

        Config.setLogResults(!isEnabled);

        inv.setItem(10, ConfigInvItems.getLogGameResultsItem());
    }

    public static void customCraftTrigger(Player player, Inventory inv, ItemStack item) {
        boolean isEnabled = item.getItemMeta().hasEnchants();

        Config.setCrafts(!isEnabled);

        inv.setItem(9, ConfigInvItems.getCustomCraftItem());
    }

    public static void saturationTrigger(Player player, Inventory inv, ItemStack item) {
        boolean isEnabled = item.getItemMeta().hasEnchants();

        Config.setSaturation(!isEnabled);

        inv.setItem(27, ConfigInvItems.getSaturationItem());
    }

    public static void penaltyLevelTrigger(Player player, Inventory inv, ItemStack item) {
        String level = item.getItemMeta().getLore().get(0).split(":")[1];
        int idx = levels.indexOf(level);
        idx++;

        if (idx == levels.size()) {
            idx = 0;
        }

        Config.setLevel(levels.get(idx));

        inv.setItem(36, ConfigInvItems.getLevelItem());
    }

    public static void rewardTrigger(Player player, Inventory inv, ItemStack item) {
        boolean isEnabled = item.getItemMeta().hasEnchants();

        Config.setRewards(!isEnabled);

        inv.setItem(37, ConfigInvItems.getRewardItem());
    }

    public static void printPlayerScoreTrigger(Player player, Inventory inv, ItemStack item) {
        boolean isEnabled = item.getItemMeta().hasEnchants();

        Config.setPrintTab(!isEnabled);

        inv.setItem(14, ConfigInvItems.getPrintPlayerItem());
    }

    public static void endWhenLastTrigger(Player player, Inventory inv, ItemStack item) {
        boolean isEnabled = item.getItemMeta().hasEnchants();

        Config.setEndOne(!isEnabled);

        inv.setItem(23, ConfigInvItems.getEndWhenLastItem());
    }

    public static void lastAgeTrigger(Player player, Inventory inv, ItemStack item) {
        String age = item.getItemMeta().getLore().get(0).split(":")[1];
        int idx = ages.indexOf(age);
        idx++;

        if (idx == ages.size()) {
            idx = 0;
        }

        Config.setLastAge(ages.get(idx));

        inv.setItem(32, ConfigInvItems.getLastAgeItem());
    }

    public static void sameOptionTrigger(Player player, Inventory inv, ItemStack item) {
        boolean isEnabled = item.getItemMeta().hasEnchants();

        Config.setSame(!isEnabled);

        inv.setItem(26, ConfigInvItems.getSameOptionItem());
    }

    public static void doubleOptionTrigger(Player player, Inventory inv, ItemStack item) {
        boolean isEnabled = item.getItemMeta().hasEnchants();

        Config.setDoubleMode(!isEnabled);

        inv.setItem(35, ConfigInvItems.getDoubleOptionItem());
    }

    public static void skipTrigger(Player player, Inventory inv, ItemStack item) {
        boolean isEnabled = item.getType() == Material.LIME_TERRACOTTA;

        Config.setSkip(!isEnabled);

        inv.setItem(11, ConfigInvItems.getSkipItem());
    }

    public static void skipAgeTrigger(Player player, Inventory inv, ItemStack item) {
        String skipAge = item.getItemMeta().getLore().get(0).split(":")[1];
        int lastAge = ages.indexOf(Config.getLastAge().getName());
        int idx = ages.indexOf(skipAge);
        idx++;

        if (idx == ages.size() || idx == lastAge + 1) {
            idx = 0;
        }

        Config.setFirstSkip(ages.get(idx));

        inv.setItem(15, ConfigInvItems.getSkipAgeItem());
    }

    public static void minusTargetTrigger(Player player, Inventory inv, ItemStack item) {
        int amount = Integer.parseInt(inv.getItem(13).getItemMeta().getLore().get(0).split(":")[1]);

        targetTrigger(player, inv, --amount);
    }

    public static void plusTargetTrigger(Player player, Inventory inv, ItemStack item) {
        int amount = Integer.parseInt(inv.getItem(13).getItemMeta().getLore().get(0).split(":")[1]);

        targetTrigger(player, inv, ++amount);
    }

    private static void targetTrigger(Player player, Inventory inv, int amount) {
        Config.setTargetAge(amount);

        if (!Config.setRet) {
            player.sendMessage(Config.error);
        } else {
            inv.setItem(13, ConfigInvItems.getTargetPerAgeItem());
        }
    }

    public static void spreadplayerTrigger(Player player, Inventory inv, ItemStack item) {
        boolean isEnabled = item.getType() == Material.LIME_TERRACOTTA;

        Config.setSpreadplayers(!isEnabled);

        inv.setItem(11, ConfigInvItems.getSpreadplayerItem());
    }

    public static void minusSpreadDistanceTrigger(Player player, Inventory inv, ItemStack item) {
        int amount = Integer.parseInt(inv.getItem(13).getItemMeta().getLore().get(0).split(":")[1]);

        spreadDistanceTrigger(player, inv, amount - 100);
    }

    public static void plusSpreadDistanceTrigger(Player player, Inventory inv, ItemStack item) {
        int amount = Integer.parseInt(inv.getItem(13).getItemMeta().getLore().get(0).split(":")[1]);

        spreadDistanceTrigger(player, inv, amount + 100);
    }

    private static void spreadDistanceTrigger(Player player, Inventory inv, int amount) {
        Config.setSpreadDistance(amount);

        if (!Config.setRet) {
            player.sendMessage(Config.error);
        } else {
            inv.setItem(13, ConfigInvItems.getSpreadDistanceItem());
        }
    }

    public static void minusSpreadRadiusTrigger(Player player, Inventory inv, ItemStack item) {
        int amount = Integer.parseInt(inv.getItem(13).getItemMeta().getLore().get(0).split(":")[1]);

        spreadRadiusTrigger(player, inv, amount - 100);
    }

    public static void plusSpreadRadiusTrigger(Player player, Inventory inv, ItemStack item) {
        int amount = Integer.parseInt(inv.getItem(13).getItemMeta().getLore().get(0).split(":")[1]);

        spreadRadiusTrigger(player, inv, amount + 100);
    }

    private static void spreadRadiusTrigger(Player player, Inventory inv, int amount) {
        Config.setSpreadRadius(amount);

        if (!Config.setRet) {
            player.sendMessage(Config.error);
        } else {
            inv.setItem(13, ConfigInvItems.getSpreadRadiusItem());
        }
    }

    public static void minusStartTimeTrigger(Player player, Inventory inv, ItemStack item) {
        int amount = Integer.parseInt(inv.getItem(13).getItemMeta().getLore().get(0).split(":")[1]);

        startTimeTrigger(player, inv, amount - 1);
    }

    public static void plusStartTimeTrigger(Player player, Inventory inv, ItemStack item) {
        int amount = Integer.parseInt(inv.getItem(13).getItemMeta().getLore().get(0).split(":")[1]);

        startTimeTrigger(player, inv, amount + 1);
    }

    private static void startTimeTrigger(Player player, Inventory inv, int amount) {
        Config.setStartTime(amount);

        if (!Config.setRet) {
            player.sendMessage(Config.error);
        } else {
            inv.setItem(13, ConfigInvItems.getStartTimeItem());
        }
    }

    public static void minusAddedTimeTrigger(Player player, Inventory inv, ItemStack item) {
        int amount = Integer.parseInt(inv.getItem(13).getItemMeta().getLore().get(0).split(":")[1]);

        addedTimeTrigger(player, inv, amount - 1);
    }

    public static void plusAddedTimeTrigger(Player player, Inventory inv, ItemStack item) {
        int amount = Integer.parseInt(inv.getItem(13).getItemMeta().getLore().get(0).split(":")[1]);

        addedTimeTrigger(player, inv, amount + 1);
    }

    private static void addedTimeTrigger(Player player, Inventory inv, int amount) {
        Config.setAddedTime(amount);

        if (!Config.setRet) {
            player.sendMessage(Config.error);
        } else {
            inv.setItem(13, ConfigInvItems.getAddedTimeItem());
        }
    }

    public static void passiveAllTrigger(Player player, Inventory inv, ItemStack item) {
        boolean isEnabled = item.getType() == Material.LIME_TERRACOTTA;

        Config.setPassiveAll(!isEnabled);

        inv.setItem(11, ConfigInvItems.getPassiveAllItem());
    }

    public static void passiveTeamTrigger(Player player, Inventory inv, ItemStack item) {
        boolean isEnabled = item.getType() == Material.LIME_TERRACOTTA;

        Config.setPassiveTeam(!isEnabled);

        inv.setItem(15, ConfigInvItems.getPassiveTeamItem());
    }

    public static void playerTipsTrigger(Player player, Inventory inv, ItemStack item) {
        boolean isEnabled = item.getType() == Material.LIME_TERRACOTTA;

        Config.setTips(!isEnabled);

        inv.setItem(11, ConfigInvItems.getPlayerTipsItem());
    }

    public static void playerLangTrigger(Player player, Inventory inv, ItemStack item) {
        String age = item.getItemMeta().getLore().get(0).split(":")[1];
        int idx = langs.indexOf(age);
        idx++;

        if (idx == langs.size()) {
            idx = 0;
        }

        Config.setLang(langs.get(idx));

        inv.setItem(15, ConfigInvItems.getPlayerLangItem());
    }

    public static void minusEndTeleporterTrigger(Player player, Inventory inv, ItemStack item) {
        int amount = Integer.parseInt(inv.getItem(13).getItemMeta().getLore().get(0).split(":")[1]);

        endTeleporterTrigger(player, inv, --amount);
    }

    public static void plusEndTeleporterTrigger(Player player, Inventory inv, ItemStack item) {
        int amount = Integer.parseInt(inv.getItem(13).getItemMeta().getLore().get(0).split(":")[1]);

        endTeleporterTrigger(player, inv, ++amount);
    }

    private static void endTeleporterTrigger(Player player, Inventory inv, int amount) {
        Config.setXpEnd(amount);

        if (!Config.setRet) {
            player.sendMessage(Config.error);
        } else {
            inv.setItem(13, ConfigInvItems.getEndTeleporterItem());
        }
    }

    public static void minusOverworldTeleporterTrigger(Player player, Inventory inv, ItemStack item) {
        int amount = Integer.parseInt(inv.getItem(13).getItemMeta().getLore().get(0).split(":")[1]);

        overworldTeleporterTrigger(player, inv, amount - 2);
    }

    public static void plusOverworldTeleporterTrigger(Player player, Inventory inv, ItemStack item) {
        int amount = Integer.parseInt(inv.getItem(13).getItemMeta().getLore().get(0).split(":")[1]);

        overworldTeleporterTrigger(player, inv, amount + 2);
    }

    private static void overworldTeleporterTrigger(Player player, Inventory inv, int amount) {
        Config.setXpOverworld(amount);

        if (!Config.setRet) {
            player.sendMessage(Config.error);
        } else {
            inv.setItem(13, ConfigInvItems.getOverworldTeleporterItem());
        }
    }

    public static void minusCoralCompassTrigger(Player player, Inventory inv, ItemStack item) {
        int amount = Integer.parseInt(inv.getItem(13).getItemMeta().getLore().get(0).split(":")[1]);

        coralCompassTrigger(player, inv, amount - 5);
    }

    public static void plusCoralCompassTrigger(Player player, Inventory inv, ItemStack item) {
        int amount = Integer.parseInt(inv.getItem(13).getItemMeta().getLore().get(0).split(":")[1]);

        coralCompassTrigger(player, inv, amount + 5);
    }

    private static void coralCompassTrigger(Player player, Inventory inv, int amount) {
        Config.setXpCoral(amount);

        if (!Config.setRet) {
            player.sendMessage(Config.error);
        } else {
            inv.setItem(13, ConfigInvItems.getCoralCompassItem());
        }
    }

    public static void teamOptionTrigger(Player player, Inventory inv, ItemStack item) {
        boolean isEnabled = item.getType() == Material.LIME_TERRACOTTA;

        Config.setTeam(!isEnabled);

        inv.setItem(11, ConfigInvItems.getTeamItem());
    }

    public static void minusTeamSizeTrigger(Player player, Inventory inv, ItemStack item) {
        int amount = Integer.parseInt(inv.getItem(13).getItemMeta().getLore().get(0).split(":")[1]);

        teamSizeTrigger(player, inv, --amount);
    }

    public static void plusTeamSizeTrigger(Player player, Inventory inv, ItemStack item) {
        int amount = Integer.parseInt(inv.getItem(13).getItemMeta().getLore().get(0).split(":")[1]);

        teamSizeTrigger(player, inv, ++amount);
    }

    private static void teamSizeTrigger(Player player, Inventory inv, int amount) {
        Config.setTeamSize(amount);

        if (!Config.setRet) {
            player.sendMessage(Config.error);
        } else {
            inv.setItem(13, ConfigInvItems.getTeamSizeItem());
        }
    }

    public static void teamInventoryTrigger(Player player, Inventory inv, ItemStack item) {
        boolean isEnabled = item.getType() == Material.LIME_TERRACOTTA;

        Config.setTeamInv(!isEnabled);

        inv.setItem(11, ConfigInvItems.getTeamInvItem());
    }

    public static void minusTeamInvSizeTrigger(Player player, Inventory inv, ItemStack item) {
        int amount = Integer.parseInt(inv.getItem(13).getItemMeta().getLore().get(0).split(":")[1]);

        teamInvSizeTrigger(player, inv, --amount);
    }

    public static void plusTeamInvSizeTrigger(Player player, Inventory inv, ItemStack item) {
        int amount = Integer.parseInt(inv.getItem(13).getItemMeta().getLore().get(0).split(":")[1]);

        teamInvSizeTrigger(player, inv, ++amount);
    }

    private static void teamInvSizeTrigger(Player player, Inventory inv, int amount) {
        Config.setTeamInvSize(amount);

        if (!Config.setRet) {
            player.sendMessage(Config.error);
        } else {
            inv.setItem(13, ConfigInvItems.getTeamInvSizeItem());
        }
    }

    public static void sbttTrigger(Player player, Inventory inv, ItemStack item) {
        boolean isEnabled = item.getType() == Material.LIME_TERRACOTTA;

        Config.setSbttMode(!isEnabled);

        inv.setItem(11, ConfigInvItems.getSbttItem());
    }

    public static void minusSbttSizeTrigger(Player player, Inventory inv, ItemStack item) {
        int amount = Integer.parseInt(inv.getItem(13).getItemMeta().getLore().get(0).split(":")[1]);

        sbttSizeTrigger(player, inv, --amount);
    }

    public static void plusSbttSizeTrigger(Player player, Inventory inv, ItemStack item) {
        int amount = Integer.parseInt(inv.getItem(13).getItemMeta().getLore().get(0).split(":")[1]);

        sbttSizeTrigger(player, inv, ++amount);
    }

    private static void sbttSizeTrigger(Player player, Inventory inv, int amount) {
        Config.setSbttAmount(amount);

        if (!Config.setRet) {
            player.sendMessage(Config.error);
        } else {
            inv.setItem(13, ConfigInvItems.getSbttSizeItem());
        }
    }
}
