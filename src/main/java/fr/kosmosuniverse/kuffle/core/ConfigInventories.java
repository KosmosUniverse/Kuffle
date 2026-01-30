package fr.kosmosuniverse.kuffle.core;

import fr.kosmosuniverse.kuffle.utils.Function0arity;
import fr.kosmosuniverse.kuffle.utils.ItemMaker;
import fr.kosmosuniverse.kuffle.utils.ItemsUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author KosmosUniverse
 */
public class ConfigInventories {
    private final Map<String, Inventory> invs;
    private final Map<String, Function0arity> createInvMethods;

    public ConfigInventories() {
        invs = new HashMap<>();
        createInvMethods = new HashMap<>();
    }

    public Inventory getMainInv() {
        return getInv("Config Main Board");
    }

    public Inventory getInv(String invname) {
        if (invname != null && invs.containsKey(invname)) {
            return invs.get(invname);
        }

        return null;
    }

    public void reloadInv(String invName) {
        createInvMethods.get(invName).apply();
    }

    public void clear() {
        invs.clear();
        createInvMethods.clear();
    }

    private void setupFirstRow(Inventory inv, String curInv, String prevInv) {
        for (int i = 0; i < 9; i++) {
            if (i == 0) {
                inv.setItem(i, prevInv != null ? ItemMaker.newItem(ItemsUtils.getBackPane()).addTag("invname", prevInv).getItem() : ItemsUtils.getQuitPane());
            } else if (i == 8) {
                inv.setItem(i, ItemMaker.newItem(Material.MAGENTA_STAINED_GLASS_PANE).addName("Reload Inventory").addTag("reload", curInv).getItem());
            } else {
                inv.setItem(i, ItemsUtils.getLimitPane());
            }
        }
    }

    public void createInventories() {
        createMainInv();
        createSystemInv();
        createGameInv();

        createInvMethods.put("Config Main Board", this::createMainInv);
        createInvMethods.put("System Config Board", this::createSystemInv);
        createInvMethods.put("Game Config Board", this::createGameInv);
    }

    private void createMainInv() {
        Inventory mainInv = Bukkit.createInventory(null, 18, "Config Main Board");

        setupFirstRow(mainInv, "Config Main Board", null);

        mainInv.setItem(9, ItemMaker.newItem(Material.BARRIER).addName("System Config").addTag("invname", "System Config Board").getItem());
        mainInv.setItem(10, ItemMaker.newItem(Material.BELL).addName("Game Config").addTag("invname", "Game Config Board").getItem());
        mainInv.setItem(17, ItemMaker.newItem(Material.CHEST).addName("Save Config").getItem());

        invs.put("Config Main Board", mainInv);
    }

    private void createSystemInv() {
        Inventory systemInv = Bukkit.createInventory(null, 18, "System Config Board");

        setupFirstRow(systemInv, "System Config Board", "Config Main Board");

        systemInv.setItem(9, ConfigInvItems.getStartTypeItem());
        systemInv.setItem(10, ConfigInvItems.getLogGameResultsItem());

        ConfigInvTrigger.addTrigger("startType", ConfigInvTrigger::startTypeTrigger);
        ConfigInvTrigger.addTrigger("logResult", ConfigInvTrigger::logResultTrigger);

        invs.put("System Config Board", systemInv);
    }

    private void createGameInv() {
        Inventory systemInv = Bukkit.createInventory(null, 45, "Game Config Board");

        setupFirstRow(systemInv, "Game Config Board", "Config Main Board");

        systemInv.setItem(9, ConfigInvItems.getCustomCraftItem());
        systemInv.setItem(10, ItemMaker.newItem(Material.ENDER_CHEST).addName("Skip").addTag("invname", "Skip Board").getItem());
        systemInv.setItem(11, ItemMaker.newItem(Material.COBBLESTONE).addName("Targets per Age").addTag("invname", "Target Board").getItem());
        systemInv.setItem(18, ItemMaker.newItem(Material.ENDER_PEARL).addName("Spreadplayers").addTag("invname", "Spreadplayer Board").getItem());
        systemInv.setItem(19, ItemMaker.newItem(Material.CLOCK).addName("Time").addTag("invname", "Time Board").getItem());
        systemInv.setItem(20, ItemMaker.newItem(Material.WHITE_BANNER).addName("Passive").addTag("invname", "Passive Board").getItem());
        systemInv.setItem(27, ConfigInvItems.getSaturationItem());
        systemInv.setItem(28, ItemMaker.newItem(Material.PLAYER_HEAD).addName("Player Config").addTag("invname", "Player Config Board").getItem());
        systemInv.setItem(29, ItemMaker.newItem(Material.EXPERIENCE_BOTTLE).addName("XP Costs").addTag("invname", "XP Costs Board").getItem());
        systemInv.setItem(36, ConfigInvItems.getLevelItem());
        systemInv.setItem(37, ConfigInvItems.getRewardItem());

        systemInv.setItem(14, ConfigInvItems.getPrintPlayerItem());
        systemInv.setItem(23, ConfigInvItems.getEndWhenLastItem());
        systemInv.setItem(32, ConfigInvItems.getLastAgeItem());

        systemInv.setItem(17, ItemMaker.newItem(Material.NETHER_STAR).addName("Team Option").addTag("invname", "Team Option Board").getItem());
        systemInv.setItem(26, ConfigInvItems.getSameOptionItem());
        systemInv.setItem(35, ConfigInvItems.getDoubleOptionItem());
        systemInv.setItem(44, ItemMaker.newItem(Material.BONE).addName("SBTT Option").addTag("invname", "SBTT Option Board").getItem());

        ConfigInvTrigger.addTrigger("customCrafts", ConfigInvTrigger::customCraftTrigger);
        ConfigInvTrigger.addTrigger("saturation", ConfigInvTrigger::saturationTrigger);
        ConfigInvTrigger.addTrigger("penaltyLevel", ConfigInvTrigger::penaltyLevelTrigger);
        ConfigInvTrigger.addTrigger("rewards", ConfigInvTrigger::rewardTrigger);
        ConfigInvTrigger.addTrigger("printPlayerScore", ConfigInvTrigger::printPlayerScoreTrigger);
        ConfigInvTrigger.addTrigger("endWhenLast", ConfigInvTrigger::endWhenLastTrigger);
        ConfigInvTrigger.addTrigger("lastAge", ConfigInvTrigger::lastAgeTrigger);
        ConfigInvTrigger.addTrigger("sameOption", ConfigInvTrigger::sameOptionTrigger);
        ConfigInvTrigger.addTrigger("doubleOption", ConfigInvTrigger::doubleOptionTrigger);

        invs.put("Game Config Board", systemInv);

        createSkipInv();

        createPlusMinusInv("Target", "Game Config Board", "Target", ConfigInvItems.getTargetPerAgeItem());
        ConfigInvTrigger.addTrigger("minusTarget", ConfigInvTrigger::minusTargetTrigger);
        ConfigInvTrigger.addTrigger("plusTarget", ConfigInvTrigger::plusTargetTrigger);

        createSpreadplayerInv();
        createTimeInv();
        createPassiveInv();
        createPlayerConfigInv();
        createXpCostsInv();
        createTeamOptionInv();
        createSbttOptionInv();

        createInvMethods.put("Skip Board", this::createSkipInv);
        createInvMethods.put("Target Board", () -> createPlusMinusInv("Target", "Game Config Board", "Target", ConfigInvItems.getTargetPerAgeItem()));
        createInvMethods.put("Spreadplayer Board", this::createSpreadplayerInv);
        createInvMethods.put("Time Board", this::createTimeInv);
        createInvMethods.put("Passive Board", this::createPassiveInv);
        createInvMethods.put("Player Config Board", this::createPlayerConfigInv);
        createInvMethods.put("XP Costs Board", this::createXpCostsInv);
        createInvMethods.put("Team Option Board", this::createTeamOptionInv);
        createInvMethods.put("SBTT Option Board", this::createSbttOptionInv);
    }

    private void createSkipInv() {
        Inventory skipInv = Bukkit.createInventory(null, 18, "Skip Board");

        setupFirstRow(skipInv, "Skip Board", "Game Config Board");

        skipInv.setItem(11, ConfigInvItems.getSkipItem());
        skipInv.setItem(15, ConfigInvItems.getSkipAgeItem());

        ConfigInvTrigger.addTrigger("skip", ConfigInvTrigger::skipTrigger);
        ConfigInvTrigger.addTrigger("skipAge", ConfigInvTrigger::skipAgeTrigger);

        invs.put("Skip Board", skipInv);
    }

    public void createPlusMinusInv(String invname, String prevInvName, String triggerName, ItemStack countingItem) {
        Inventory plusMinusInv = Bukkit.createInventory(null, 18, invname + " Board");

        setupFirstRow(plusMinusInv, invname + " Board", prevInvName);

        plusMinusInv.setItem(11, ConfigInvItems.getMinusItem(triggerName));
        plusMinusInv.setItem(13, countingItem);
        plusMinusInv.setItem(15, ConfigInvItems.getPlusItem(triggerName));

        ConfigInvTrigger.addTrigger("skipAge", ConfigInvTrigger::skipAgeTrigger);

        invs.put(invname + " Board", plusMinusInv);
    }

    public void createSpreadplayerInv() {
        Inventory spreadInv = Bukkit.createInventory(null, 18, "Spreadplayer Board");

        setupFirstRow(spreadInv, "Spreadplayer Board", "Game Config Board");

        spreadInv.setItem(11, ConfigInvItems.getSpreadplayerItem());
        spreadInv.setItem(13, ItemMaker.newItem(Material.STICK).addName("Spreadplayer Distance").addTag("invname", "Spreadplayer Distance Board").getItem());
        spreadInv.setItem(15, ItemMaker.newItem(Material.HEART_OF_THE_SEA).addName("Spreadplayer Radius").addTag("invname", "Spreadplayer Radius Board").getItem());

        ConfigInvTrigger.addTrigger("spreadplayer", ConfigInvTrigger::spreadplayerTrigger);

        invs.put("Spreadplayer Board", spreadInv);

        createPlusMinusInv("Spreadplayer Distance", "Spreadplayer Board", "SpreadDistance", ConfigInvItems.getSpreadDistanceItem());
        createPlusMinusInv("Spreadplayer Radius", "Spreadplayer Board", "SpreadRadius", ConfigInvItems.getSpreadRadiusItem());

        ConfigInvTrigger.addTrigger("minusSpreadDistance", ConfigInvTrigger::minusSpreadDistanceTrigger);
        ConfigInvTrigger.addTrigger("plusSpreadDistance", ConfigInvTrigger::plusSpreadDistanceTrigger);
        ConfigInvTrigger.addTrigger("minusSpreadRadius", ConfigInvTrigger::minusSpreadRadiusTrigger);
        ConfigInvTrigger.addTrigger("plusSpreadRadius", ConfigInvTrigger::plusSpreadRadiusTrigger);

        createInvMethods.put("Spreadplayer Distance Board", () -> createPlusMinusInv("Spreadplayer Distance", "Spreadplayer Board", "SpreadDistance", ConfigInvItems.getSpreadDistanceItem()));
        createInvMethods.put("Spreadplayer Radius Board", () -> createPlusMinusInv("Spreadplayer Radius", "Spreadplayer Board", "SpreadRadius", ConfigInvItems.getSpreadRadiusItem()));
    }

    public void createTimeInv() {
        Inventory timeInv = Bukkit.createInventory(null, 18, "Time Board");

        setupFirstRow(timeInv, "Time Board", "Game Config Board");

        timeInv.setItem(11, ItemMaker.newItem(Material.COMPASS).addName("Start Time").addTag("invname", "Start Time Board").getItem());
        timeInv.setItem(15, ItemMaker.newItem(Material.CLOCK).addName("Added Time").addTag("invname", "Added Time Board").getItem());

        invs.put("Time Board", timeInv);

        createPlusMinusInv("Start Time", "Time Board", "StartTime", ConfigInvItems.getStartTimeItem());
        createPlusMinusInv("Added Time", "Time Board", "AddedTime", ConfigInvItems.getAddedTimeItem());

        ConfigInvTrigger.addTrigger("minusStartTime", ConfigInvTrigger::minusStartTimeTrigger);
        ConfigInvTrigger.addTrigger("plusStartTime", ConfigInvTrigger::plusStartTimeTrigger);
        ConfigInvTrigger.addTrigger("minusAddedTime", ConfigInvTrigger::minusAddedTimeTrigger);
        ConfigInvTrigger.addTrigger("plusAddedTime", ConfigInvTrigger::plusAddedTimeTrigger);

        createInvMethods.put("Start Time Board", () -> createPlusMinusInv("Start Time", "Time Board", "StartTime", ConfigInvItems.getStartTimeItem()));
        createInvMethods.put("Added Time Board", () -> createPlusMinusInv("Added Time", "Time Board", "AddedTime", ConfigInvItems.getAddedTimeItem()));
    }

    public void createPassiveInv() {
        Inventory passiveInv = Bukkit.createInventory(null, 18, "Passive Board");

        setupFirstRow(passiveInv, "Passive Board", "Game Config Board");

        passiveInv.setItem(11, ConfigInvItems.getPassiveAllItem());
        passiveInv.setItem(15, ConfigInvItems.getPassiveTeamItem());

        ConfigInvTrigger.addTrigger("passiveAll", ConfigInvTrigger::passiveAllTrigger);
        ConfigInvTrigger.addTrigger("passiveTeam", ConfigInvTrigger::passiveTeamTrigger);

        invs.put("Passive Board", passiveInv);
    }

    public void createPlayerConfigInv() {
        Inventory playerConfigInv = Bukkit.createInventory(null, 18, "Player Config Board");

        setupFirstRow(playerConfigInv, "Player Config Board", "Game Config Board");

        playerConfigInv.setItem(11, ConfigInvItems.getPlayerTipsItem());
        playerConfigInv.setItem(15, ConfigInvItems.getPlayerLangItem());

        ConfigInvTrigger.addTrigger("playerTips", ConfigInvTrigger::playerTipsTrigger);
        ConfigInvTrigger.addTrigger("playerLang", ConfigInvTrigger::playerLangTrigger);

        invs.put("Player Config Board", playerConfigInv);
    }

    public void createXpCostsInv() {
        Inventory xpCostsInv = Bukkit.createInventory(null, 18, "XP Costs Board");

        setupFirstRow(xpCostsInv, "XP Costs Board", "Game Config Board");

        xpCostsInv.setItem(11, ItemMaker.newItem(Material.END_PORTAL_FRAME).addName("End Teleporter").addTag("invname", "End Teleporter Board").getItem());
        xpCostsInv.setItem(13, ItemMaker.newItem(Material.GRASS_BLOCK).addName("Overworld Teleporter").addTag("invname", "Overworld Teleporter Board").getItem());
        xpCostsInv.setItem(15, ItemMaker.newItem(Material.FIRE_CORAL).addName("Coral Compass").addTag("invname", "Coral Compass Board").getItem());

        invs.put("XP Costs Board", xpCostsInv);

        createPlusMinusInv("End Teleporter", "XP Costs Board", "EndTeleporter", ConfigInvItems.getEndTeleporterItem());
        createPlusMinusInv("Overworld Teleporter", "XP Costs Board", "OverworldTeleporter", ConfigInvItems.getOverworldTeleporterItem());
        createPlusMinusInv("Coral Compass", "XP Costs Board", "CoralCompass", ConfigInvItems.getCoralCompassItem());

        ConfigInvTrigger.addTrigger("minusEndTeleporter", ConfigInvTrigger::minusEndTeleporterTrigger);
        ConfigInvTrigger.addTrigger("plusEndTeleporter", ConfigInvTrigger::plusEndTeleporterTrigger);
        ConfigInvTrigger.addTrigger("minusOverworldTeleporter", ConfigInvTrigger::minusOverworldTeleporterTrigger);
        ConfigInvTrigger.addTrigger("plusOverworldTeleporter", ConfigInvTrigger::plusOverworldTeleporterTrigger);
        ConfigInvTrigger.addTrigger("minusCoralCompass", ConfigInvTrigger::minusCoralCompassTrigger);
        ConfigInvTrigger.addTrigger("plusCoralCompass", ConfigInvTrigger::plusCoralCompassTrigger);

        createInvMethods.put("End Teleporter Board", () -> createPlusMinusInv("End Teleporter", "XP Costs Board", "EndTeleporter", ConfigInvItems.getEndTeleporterItem()));
        createInvMethods.put("Overworld Teleporter Board", () -> createPlusMinusInv("Overworld Teleporter", "XP Costs Board", "OverworldTeleporter", ConfigInvItems.getOverworldTeleporterItem()));
        createInvMethods.put("Coral Compass Board", () -> createPlusMinusInv("Coral Compass", "XP Costs Board", "CoralCompass", ConfigInvItems.getCoralCompassItem()));
    }

    public void createTeamOptionInv() {
        Inventory teamOptionInv = Bukkit.createInventory(null, 18, "Team Option Board");

        setupFirstRow(teamOptionInv, "Team Option Board", "Game Config Board");

        teamOptionInv.setItem(11, ConfigInvItems.getTeamItem());
        teamOptionInv.setItem(13, ItemMaker.newItem(Material.PRISMARINE_CRYSTALS).addName("Team Size").addTag("invname", "Team Size Board").getItem());
        teamOptionInv.setItem(15, ItemMaker.newItem(Material.CHEST).addName("Team Inventory").addTag("invname", "Team Inventory Board").getItem());

        ConfigInvTrigger.addTrigger("teamOption", ConfigInvTrigger::teamOptionTrigger);

        invs.put("Team Option Board", teamOptionInv);

        createPlusMinusInv("Team Size", "Team Option Board", "TeamSize", ConfigInvItems.getTeamSizeItem());

        ConfigInvTrigger.addTrigger("minusTeamSize", ConfigInvTrigger::minusTeamSizeTrigger);
        ConfigInvTrigger.addTrigger("plusTeamSize", ConfigInvTrigger::plusTeamSizeTrigger);

        createTeamInvInv();

        createInvMethods.put("Team Size Board", () -> createPlusMinusInv("Team Size", "Team Option Board", "TeamSize", ConfigInvItems.getTeamSizeItem()));
        createInvMethods.put("Team Inventory Board", this::createTeamInvInv);
    }

    public void createTeamInvInv() {
        Inventory teamInvInv = Bukkit.createInventory(null, 18, "Team Inventory Board");

        setupFirstRow(teamInvInv, "Team Inventory Board", "Team Option Board");

        teamInvInv.setItem(11, ConfigInvItems.getTeamInvItem());
        teamInvInv.setItem(15, ItemMaker.newItem(Material.PRISMARINE_CRYSTALS).addName("Team Inventory Size").addTag("invname", "Team Inventory Size Board").getItem());

        ConfigInvTrigger.addTrigger("teamInventory", ConfigInvTrigger::teamInventoryTrigger);

        invs.put("Team Inventory Board", teamInvInv);

        createPlusMinusInv("Team Inventory Size", "Team Inventory Board", "TeamInvSize", ConfigInvItems.getTeamInvSizeItem());

        ConfigInvTrigger.addTrigger("minusTeamInvSize", ConfigInvTrigger::minusTeamInvSizeTrigger);
        ConfigInvTrigger.addTrigger("plusTeamInvSize", ConfigInvTrigger::plusTeamInvSizeTrigger);

        createInvMethods.put("Team Inventory Size Board", () -> createPlusMinusInv("Team Inventory Size", "Team Inventory Board", "TeamInvSize", ConfigInvItems.getTeamInvSizeItem()));
    }

    public void createSbttOptionInv() {
        Inventory sbttOptionInv = Bukkit.createInventory(null, 18, "SBTT Option Board");

        setupFirstRow(sbttOptionInv, "SBTT Option Board", "Game Config Board");

        sbttOptionInv.setItem(11, ConfigInvItems.getSbttItem());
        sbttOptionInv.setItem(15, ItemMaker.newItem(Material.PRISMARINE_CRYSTALS).addName("SBTT Size").addTag("invname", "SBTT Size Board").getItem());

        ConfigInvTrigger.addTrigger("sbtt", ConfigInvTrigger::sbttTrigger);

        invs.put("SBTT Option Board", sbttOptionInv);

        createPlusMinusInv("SBTT Size", "SBTT Option Board", "SbttSize", ConfigInvItems.getSbttSizeItem());

        ConfigInvTrigger.addTrigger("minusSbttSize", ConfigInvTrigger::minusSbttSizeTrigger);
        ConfigInvTrigger.addTrigger("plusSbttSize", ConfigInvTrigger::plusSbttSizeTrigger);

        createInvMethods.put("SBTT Size Board", () -> createPlusMinusInv("SBTT Size", "SBTT Option Board", "SbttSize", ConfigInvItems.getSbttSizeItem()));
    }
}
