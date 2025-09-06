package fr.kosmosuniverse.kuffle.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import fr.kosmosuniverse.kuffle.KuffleMain;
import fr.kosmosuniverse.kuffle.exceptions.KuffleConfigException;
import fr.kosmosuniverse.kuffle.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class Config implements Serializable {
	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Config Default messgage
	 */
	private static final String CONFIG_DEFAULT = "CONFIG_DEFAULT";
	
	private static ConfigHolder configValues;

	/**
	 * Error flag
	 */
	private static boolean setRet;
	
	/**
	 * Error messgae
	 */
	private static String error;

	/**
	 * Map of config value and set method
	 */
	private static Map<String, Consumer<String>> configElems = null;

	/**
	 * Constructor
	 * 
	 * @param configFile	configuration file used to setup config values
	 */
	public static void setupConfig(FileConfiguration configFile) {
		configValues = new ConfigHolder();
		configElems = new HashMap<>();

		configElems.put("TIPS", (String b) -> setTips(Boolean.parseBoolean(b)));
		configElems.put("SATURATION", (String b) -> setSaturation(Boolean.parseBoolean(b)));
		configElems.put("SPREADPLAYERS", (String b) -> setSpreadplayers(Boolean.parseBoolean(b)));
		configElems.put("REWARDS", (String b) -> setRewards(Boolean.parseBoolean(b)));
		configElems.put("SKIP", (String b) -> setSkip(Boolean.parseBoolean(b)));
		configElems.put("CUSTOM_CRAFTS", (String b) -> setCrafts(Boolean.parseBoolean(b)));
		configElems.put("TEAM", (String b) -> setTeam(Boolean.parseBoolean(b)));
		configElems.put("TEAM_INV", (String b) -> setTeamInv(Boolean.parseBoolean(b)));
		configElems.put("SAME_MODE", (String b) -> setSame(Boolean.parseBoolean(b)));
		configElems.put("DOUBLE_MODE", (String b) -> setDoubleMode(Boolean.parseBoolean(b)));
		configElems.put("SBTT_MODE", (String b) -> setSbttMode(Boolean.parseBoolean(b)));
		configElems.put("PRINT_TAB", (String b) -> setPrintTab(Boolean.parseBoolean(b)));
		configElems.put("PRINT_TAB_ALL", (String b) -> setPrintTabAll(Boolean.parseBoolean(b)));
		configElems.put("END_WHEN_ONE", (String b) -> setEndOne(Boolean.parseBoolean(b)));
		configElems.put("PASSIVE_ALL", (String b) -> setPassiveAll(Boolean.parseBoolean(b)));
		configElems.put("PASSIVE_TEAM", (String b) -> setPassiveTeam(Boolean.parseBoolean(b)));
		configElems.put("SPREAD_MIN_DISTANCE", (String i) -> setSpreadDistance(Integer.parseInt(i)));
		configElems.put("SPREAD_MIN_RADIUS", (String i) -> setSpreadRadius(Integer.parseInt(i)));
		configElems.put("TARGET_PER_AGE", (String i) -> setTargetAge(Integer.parseInt(i)));
		configElems.put("START_DURATION", (String i) -> setStartTime(Integer.parseInt(i)));
		configElems.put("ADDED_DURATION", (String i) -> setAddedTime(Integer.parseInt(i)));
		configElems.put("TEAM_SIZE", (String i) -> setTeamSize(Integer.parseInt(i)));
		configElems.put("TEAM_INV_SIZE", (String i) -> setTeamInvSize(Integer.parseInt(i)));
		configElems.put("SBTT_AMOUNT", (String i) -> setSbttAmount(Integer.parseInt(i)));
		configElems.put("XP_END_TELEPORTER", (String i) -> setXpEnd(Integer.parseInt(i)));
		configElems.put("XP_OVERWORLD_TELEPORTER", (String i) -> setXpOverworld(Integer.parseInt(i)));
		configElems.put("XP_CORAL_COMPASS", (String i) -> setXpCoral(Integer.parseInt(i)));
		configElems.put("LAST_AGE", Config::setLastAge);
		configElems.put("FIRST_AGE_SKIP", Config::setFirstSkip);
		configElems.put("LEVEL", Config::setLevel);
		configElems.put("LANG", Config::setLang);
		
		checkAndSetConfig(configFile);
	}
	
	/**
	 * Searches is a specific key exists in configElems
	 * 
	 * @param key	The key to search for
	 * 
	 * @return True if key exists, False instead
	 */
	public static boolean hasKey(String key) {
		return configElems.containsKey(key);
	}
	
	/**
	 * Set the value of a config element by name
	 * 
	 * @param key	The config element name
	 * @param elem	The value to apply for this element
	 * 
	 * @throws KuffleConfigException if Set throws or return False
	 */
	public static void setElem(String key, String elem) throws KuffleConfigException {
		setRet = true;
		error = "";

		try {
			configElems.get(key).accept(elem);
		} catch (Exception e) {
			setRet = false;
			error = "Invalid Parameter type for this config element !";
			Utils.logException(e);
		}
		
		if (!setRet) {
			throw new KuffleConfigException(error);
		}
	}

	/**
	 * Setup all config values
	 * 
	 * @param configFile	configuration file used to setup config values
	 */
	private static void checkAndSetConfig(FileConfiguration configFile) {
		checkFilePersonnal(configFile);
		checkFileSpread(configFile);
		checkFileModes(configFile);
		checkFileStart(configFile);
		checkFileOther(configFile);
		checkFileEnd(configFile);
		
		setValues(configFile);
	}
	
	private static void checkFilePersonnal(FileConfiguration configFile) {
		String langConfig = "game_settings.personals.lang";
		String tipsConfig = "game_settings.personals.tips";
		
		if (!configFile.contains(langConfig)
				|| !LangManager.hasLang(configFile.getString(langConfig))) {
			configValues.setLang("en");
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "lang"));
			configFile.set(langConfig, "en");
		} else {
			configValues.setLang(configFile.getString(langConfig));
		}
		
		if (!configFile.contains(tipsConfig)) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "enabling tips"));
			configFile.set(tipsConfig, false);
		}
	}
	
	/**
	 * Check spread values in config file to ensure they exists and are conform
	 * 
	 * @param configFile	configuration file used to setup config values
	 */
	private static void checkFileSpread(FileConfiguration configFile) {
		String spreadConfig = "game_settings.spreadplayers.enable";
		String spreadMinConfig = "game_settings.spreadplayers.minimum_distance";
		String spreadMaxConfig = "game_settings.spreadplayers.minimum_radius";
		
		if (!configFile.contains(spreadConfig)) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "enabling spreadplayers"));
			configFile.set(spreadConfig, false);
		}
		
		if (!configFile.contains(spreadMinConfig)
				|| configFile.getInt(spreadMinConfig) < 1) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "spreadplayers minimum distance"));
			configFile.set(spreadMinConfig, 500);
		}

		if (!configFile.contains(spreadMaxConfig)
				|| configFile.getInt(spreadMaxConfig) < configFile
						.getInt(spreadMinConfig)) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "spreadplayers minimum radius"));
			configFile.set(spreadMaxConfig, 1000);
		}
	}
	
	/**
	 * Check modes values in config file to ensure they exists and are conform
	 * 
	 * @param configFile	configuration file used to setup config values
	 */
	private static void checkFileModes(FileConfiguration configFile) {
		String teamConfig = "game_settings.team.enable";
		String teamSizeConfig = "game_settings.team.size";
		String teamInvConfig = "game_settings.team.inv.enable";
		String teamInvSizeConfig = "game_settings.team.inv.size";
		String sameConfig = "game_settings.modes.same";
		String sbttConfig = "game_settings.modes.sbtt.enable";
		String sbttAmountConfig = "game_settings.modes.sbtt.amount";
		String doubleConfig = "game_settings.modes.double";
		String passiveAllConfig = "game_settings.passive.all";
		String passiveTeamConfig = "game_settings.passive.team";
		
		if (!configFile.contains(teamConfig)) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "enabling team"));
			configFile.set(teamConfig, false);
		}

		if (!configFile.contains(teamSizeConfig) || configFile.getInt(teamSizeConfig) < 2
				|| configFile.getInt(teamSizeConfig) > 10) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "max team size"));
			configFile.set(teamSizeConfig, 2);
		}
		
		if (!configFile.contains(teamInvConfig)) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "enabling team inv"));
			configFile.set(teamConfig, false);
		}
		
		if (!configFile.contains(teamInvSizeConfig) || configFile.getInt(teamInvSizeConfig) < 1
				|| configFile.getInt(teamInvSizeConfig) > 6) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "team inv size"));
			configFile.set(teamSizeConfig, 1);
		}
		
		if (!configFile.contains(sameConfig)) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "enabling same mode"));
			configFile.set(sameConfig, false);
		}
		
		if (!configFile.contains(sbttConfig)) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "SBTT mode"));
			configFile.set(sbttConfig, false);
		}
		
		if (!configFile.contains(sbttAmountConfig) ||
				configFile.getInt(sbttAmountConfig) < 1 ||
				configFile.getInt(sbttAmountConfig) > 9) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "SBTT amount"));
			configFile.set(sbttAmountConfig, 4);
		}
		
		if (!configFile.contains(doubleConfig)) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "Double mode"));
			configFile.set(doubleConfig, false);
		}
		
		if (!configFile.contains(passiveAllConfig)) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "Passive mode"));
			configFile.set(passiveAllConfig, false);
		}
		
		if (!configFile.contains(passiveTeamConfig)) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "Passive mode"));
			configFile.set(passiveTeamConfig, false);
		}
	}
	
	/**
	 * Check basic values in config file to ensure they exists and are conform
	 * 
	 * @param configFile	configuration file used to setup config values
	 */
	private static void checkFileStart(FileConfiguration configFile) {
		String targetConfig = "game_settings.target_per_age";
		String timeStartConfig = "game_settings.time.start";
		String timeAddConfig = "game_settings.time.added";
		String lastAgeConfig = "game_settings.last_age";
		String levelConfig = "game_settings.level";
		
		if (!configFile.contains(targetConfig)
				|| configFile.getInt(targetConfig) < 1) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "item per age"));
			configFile.set(targetConfig, 5);
		}
		
		if (!configFile.contains(timeStartConfig) || configFile.getInt(timeStartConfig) < 1) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "start time"));
			configFile.set(timeStartConfig, 4);
		}

		if (!configFile.contains(timeAddConfig) || configFile.getInt(timeAddConfig) < 1) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "time added"));
			configFile.set(timeAddConfig, 2);
		}

		if (!configFile.contains(lastAgeConfig) || AgeManager.getAgeByName(configFile.getString(lastAgeConfig)) == null || AgeManager.getAgeByName(configFile.getString(lastAgeConfig)).getNumber() == -1) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "max ages"));
			configFile.set(lastAgeConfig, AgeManager.getLastAge().getName());
		}

		if (!configFile.contains(levelConfig) ||
				LevelManager.getInstance().levelNotExists(configFile.getString(levelConfig))) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "levels"));
			configFile.set(levelConfig, LevelManager.getInstance().getFirstLevel().getName());
		}
	}
	
	/**
	 * Check other values in config file to ensure they exists and are conform
	 * 
	 * @param configFile	configuration file used to setup config values
	 */
	private static void checkFileOther(FileConfiguration configFile) {
		String skipConfig = "game_settings.skip.enable";
		String skipAgeConfig = "game_settings.skip.age";
		String craftConfig = "game_settings.custom_crafts";
		String xpEndConfig = "game_settings.xp_max.end_teleporter";
		String xpOverConfig = "game_settings.xp_max.overworld_teleporter";
		String xpCoralConfig = "game_settings.xp_max.coral_compass";
		
		if (!configFile.contains(skipConfig)) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "enabling skip"));
			configFile.set(skipConfig, true);
		}

		if (!configFile.contains(skipAgeConfig) || AgeManager.getAgeByName(configFile.getString(skipAgeConfig)) == null || AgeManager.getAgeByName(configFile.getString(skipAgeConfig)).getNumber() == -1) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "skip age"));
			configFile.set(skipAgeConfig, AgeManager.getFirstAge().getName());
		}

		if (!configFile.contains(craftConfig)) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "enabling custom crafts"));
			configFile.set(craftConfig, true);
		}
		
		if (!configFile.contains(xpEndConfig)) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "xp max EndTeleporter"));
			configFile.set(xpEndConfig, 5);
		}
		
		if (!configFile.contains(xpOverConfig)) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "xp max OverworldTeleporter"));
			configFile.set(xpOverConfig, 10);
		}
		
		if (!configFile.contains(xpCoralConfig)) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "xp max CoralCompass"));
			configFile.set(xpCoralConfig, 20);
		}
	}
	
	/**
	 * Check end values in config file to ensure they exists and are conform
	 * 
	 * @param configFile	configuration file used to setup config values
	 */
	private static void checkFileEnd(FileConfiguration configFile) {
		String persoTabConfig = "game_settings.print_player_tab.for_you";
		String everyTabConfig = "game_settings.print_player_tab.for_all_players";
		String endOneConfig = "game_settings.end_game_when_one_remains";
		
		if (!configFile.contains(persoTabConfig)) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "enabling game end tab display"));
			configFile.set(persoTabConfig, true);
		}
		
		if (!configFile.contains(everyTabConfig) &&
				configFile.getBoolean(everyTabConfig)) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "enabling game end tab for all display"));
			configFile.set(everyTabConfig, true);
		}
		
		if (!configFile.contains(endOneConfig)) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "game end when one"));
			configFile.set(endOneConfig, false);
		}
	}

	/**
	 * Setup config values from config file
	 * 
	 * @param configFile	file that contains all config values
	 */
	private static void setValues(FileConfiguration configFile) {
		configValues.setTips(configFile.getBoolean("game_settings.personals.tips"));
		
		configValues.setSaturation(configFile.getBoolean("game_settings.saturation"));
		configValues.setSpread(configFile.getBoolean("game_settings.spreadplayers.enable"));
		configValues.setRewards(configFile.getBoolean("game_settings.rewards"));
		configValues.setSkip(configFile.getBoolean("game_settings.skip.enable"));
		configValues.setCrafts(configFile.getBoolean("game_settings.custom_crafts"));
		configValues.setTeam(configFile.getBoolean("game_settings.team.enable"));
		configValues.setTeamInv(configFile.getBoolean("game_settings.team.inv.enable"));
		configValues.setSame(configFile.getBoolean("game_settings.modes.same"));
		configValues.setPrintTab(configFile.getBoolean("game_settings.print_player_tab.for_you"));
		configValues.setPrintTabAll(configFile.getBoolean("game_settings.print_player_tab.for_all_players"));
		configValues.setEndOne(configFile.getBoolean("game_settings.end_game_when_one_remains"));
		configValues.setDuoMode(configFile.getBoolean("game_settings.modes.double"));
		configValues.setSbttMode(configFile.getBoolean("game_settings.modes.sbtt.enable"));
		configValues.setPassiveAll(configFile.getBoolean("game_settings.passive.all"));
		configValues.setPassiveTeam(configFile.getBoolean("game_settings.passive.team"));
		
		configValues.setSpreadDistance(configFile.getInt("game_settings.spreadplayers.minimum_distance"));
		configValues.setSpreadRadius(configFile.getInt("game_settings.spreadplayers.minimum_radius"));
		configValues.setTargetPerAge(configFile.getInt("game_settings.target_per_age"));
		configValues.setStartTime(configFile.getInt("game_settings.time.start"));
		configValues.setAddedTime(configFile.getInt("game_settings.time.added"));
		configValues.setTeamSize(configFile.getInt("game_settings.team.size"));
		configValues.setTeamInvSize(configFile.getInt("game_settings.team.inv.size"));
		configValues.setSbttAmount(configFile.getInt("game_settings.modes.sbtt.amount"));
		configValues.setXpEnd(configFile.getInt("game_settings.xp_max.end_teleporter"));
		configValues.setXpOverworld(configFile.getInt("game_settings.xp_max.overworld_teleporter"));
		configValues.setXpCoral(configFile.getInt("game_settings.xp_max.coral_compass"));
		
		configValues.setLastAge(AgeManager.getAgeByName(configFile.getString("game_settings.last_age")).getNumber());
		configValues.setLevel(LevelManager.getInstance().getLevelByName(configFile.getString("game_settings.level")).getNumber());
		configValues.setSkipAge(AgeManager.getAgeByName(configFile.getString("game_settings.skip.age")).getNumber());
	}
	
	/**
	 * Construct a String from config values to be display
	 * 
	 * @return the string
	 */
	public static String displayConfig() {
		String dash = "-------------------------------\n";

		return ChatColor.BLUE + dash +
				"-      Configuration Kuffle v" + KuffleMain.getInstance().getVersion() + "      -\n" +
				dash +
				"Saturation: " + ChatColor.GOLD + configValues.isSaturation() + "\n"
				+ ChatColor.BLUE + "Spreadplayers: " + ChatColor.GOLD + configValues.isSpread() + "\n"
				+ ChatColor.BLUE + "  - Spreadplayer min distance: " + ChatColor.GOLD + configValues.getSpreadDistance() + "\n" + ChatColor.BLUE + "  - Spreadplayer min radius: " + ChatColor.GOLD + configValues.getSpreadRadius() + "\n"
				+ ChatColor.BLUE + "Rewards: " + ChatColor.GOLD + configValues.isRewards() + "\n"
				+ ChatColor.BLUE + "Skip: " + ChatColor.GOLD + configValues.isSkip() + "\n"
				+ ChatColor.BLUE + "  - Not before : " + ChatColor.GOLD + AgeManager.getAgeByNumber(configValues.getSkipAge()).getName() + "\n"
				+ ChatColor.BLUE + "Crafts: " + ChatColor.GOLD + configValues.isCrafts() + "\n"
				+ ChatColor.BLUE + "Nb target per age: " + ChatColor.GOLD + configValues.getTargetPerAge() + "\n"
				+ ChatColor.BLUE + "Last age: " + ChatColor.GOLD + AgeManager.getAgeByNumber(configValues.getLastAge()).getName() + "\n"
				+ ChatColor.BLUE + "Start duration: " + ChatColor.GOLD + configValues.getStartTime() + "\n"
				+ ChatColor.BLUE + "Added duration: " + ChatColor.GOLD + configValues.getAddedTime() + "\n"
				+ ChatColor.BLUE + "Personnals: " + "\n"
				+ ChatColor.BLUE + "  - Lang: " + ChatColor.GOLD + configValues.getLang() + "\n"
				+ ChatColor.BLUE + "  - Tips: " + ChatColor.GOLD + configValues.isTips() + "\n"
				+ ChatColor.BLUE + "Level: " + ChatColor.GOLD + LevelManager.getInstance().getLevelByNumber(configValues.getLevel()).getName() + "\n"
				+ ChatColor.BLUE + "Print tab at game end: " + ChatColor.GOLD + configValues.isPrintTab() + "\n"
				+ ChatColor.BLUE + "Print tab for all players at game end: " + ChatColor.GOLD + configValues.isPrintTabAll() + "\n"
				+ ChatColor.BLUE + "Game ends when remains one: " + ChatColor.GOLD + configValues.isEndOne() + "\n"
				+ ChatColor.BLUE + "Passive: " + "\n"
				+ ChatColor.BLUE + "  - All: " + ChatColor.GOLD + configValues.isPassiveAll() + "\n"
				+ ChatColor.BLUE + "  - Team: " + ChatColor.GOLD + configValues.isPassiveTeam() + "\n"
				+ ChatColor.BLUE + "Team: " + ChatColor.GOLD + configValues.isTeam() + "\n"
				+ ChatColor.BLUE + "  - Team Size: " + ChatColor.GOLD + configValues.getTeamSize() + "\n"
				+ ChatColor.BLUE + "  - Team Inv: " + ChatColor.GOLD + configValues.isTeamInv() + "\n"
				+ ChatColor.BLUE + "    - Team Inv Size: " + ChatColor.GOLD + configValues.getTeamInvSize() + "\n"
				+ ChatColor.BLUE + "Modes: " + "\n"
				+ ChatColor.BLUE + "  - Same: " + ChatColor.GOLD + configValues.isSame() + "\n"
				+ ChatColor.BLUE + "  - Double: " + ChatColor.GOLD + configValues.isDuoMode() + "\n"
				+ ChatColor.BLUE + "  - SBTT: " + ChatColor.GOLD + configValues.isSbttMode() + "\n"
				+ ChatColor.BLUE + "    - amount: " + ChatColor.GOLD + configValues.getSbttAmount() + "\n"
				+ ChatColor.BLUE + "XP Max: " + "\n"
				+ ChatColor.BLUE + "  - EndTeleporter: " + ChatColor.GOLD + configValues.getXpEnd() + "\n"
				+ ChatColor.BLUE + "  - OverworldTeleporter: " + ChatColor.GOLD + configValues.getXpOverworld() + "\n"
				+ ChatColor.BLUE + "  - CoralCompass: " + ChatColor.GOLD + configValues.getXpCoral() + "\n"
				+ ChatColor.BLUE + dash + "-      Configuration Kuffle v" + KuffleMain.getInstance().getVersion() + "      -\n"
				+ dash + ChatColor.RESET;
	}
	
	/**
	 * Clears the configElems map
	 */
	public static void clear() {
		configElems.clear();
	}
	
	/**
	 * Loads config
	 * 
	 * @param config	config read from file
	 */
	public static void loadConfig(ConfigHolder config) {
		configValues = new ConfigHolder(config);
	}
	
	/**
	 * Get saturation enable value
	 * 
	 * @return if saturation is enabled
	 */
	public static boolean getSaturation() {
		return configValues.isSaturation();
	}

	/**
	 * Get spread enable value
	 * 
	 * @return if spread is enabled
	 */
	public static boolean getSpread() {
		return configValues.isSpread();
	}

	/**
	 * Get rewards enable value
	 * 
	 * @return if rewards are enabled
	 */
	public static boolean getRewards() {
		return configValues.isRewards();
	}

	/**
	 * Get skip enable value
	 * 
	 * @return if skip is enabled
	 */
	public static boolean getSkip() {
		return configValues.isSkip();
	}

	/**
	 * Get craft enable value
	 * 
	 * @return if craft are enabled
	 */
	public static boolean getCrafts() {
		return configValues.isCrafts();
	}

	/**
	 * Get team enable value
	 * 
	 * @return if team mode is enabled
	 */
	public static boolean getTeam() {
		return configValues.isTeam();
	}
	
	/**
	 * Get team inventory enable value
	 * 
	 * @return if team inventory is enabled
	 */
	public static boolean getTeamInv() {
		return configValues.isTeamInv();
	}
	
	/**
	 * Get same enable value
	 * 
	 * @return if same mode is enabled
	 */
	public static boolean getSame() {
		return configValues.isSame();
	}
	
	/**
	 * Get double enable value
	 * 
	 * @return if double mode is enabled
	 */
	public static boolean getDouble() {
		return configValues.isDuoMode();
	}
	
	/**
	 * Get sbtt enable value
	 * 
	 * @return if sbtt mode is enabled
	 */
	public static boolean getSBTT() {
		return configValues.isSbttMode();
	}
	
	/**
	 * Get print end game tab enable value
	 * 
	 * @return if print tab is enabled
	 */
	public static boolean getPrintTab() {
		return configValues.isPrintTab();
	}
	
	/**
	 * Get print end game tab for all players enable value
	 * 
	 * @return if print tab all is enabled
	 */
	public static boolean getPrintTabAll() {
		return configValues.isPrintTabAll();
	}
	
	/**
	 * Get end one enable value
	 * 
	 * @return if end one is enabled
	 */
	public static boolean getEndOne() {
		return configValues.isEndOne();
	}
	
	/**
	 * Get passive all enable value
	 * 
	 * @return if passive is enabled
	 */
	public static boolean getPassiveAll() {
		return configValues.isPassiveAll();
	}
	
	/**
	 * Get passive team enable value
	 * 
	 * @return if passive is enabled for team mates
	 */
	public static boolean getPassiveTeam() {
		return configValues.isPassiveTeam();
	}
	
	/**
	 * Get tips value
	 * 
	 * @return if tips is enable
	 */
	public static boolean hasTips() {
		return configValues.isTips();
	}

	/**
	 * Get team size value
	 * 
	 * @return the team size
	 */
	public static int getTeamSize() {
		return configValues.getTeamSize();
	}

	/**
	 * Get team inventory size value
	 * 
	 * @return the team inventory size
	 */
	public static int getTeamInvSize() {
		return configValues.getTeamInvSize();
	}
	
	/**
	 * Get target per age value
	 * 
	 * @return the amount of target per age
	 */
	public static int getTargetPerAge() {
		return configValues.getTargetPerAge();
	}

	/**
	 * Get start time value
	 * 
	 * @return the start time
	 */
	public static int getStartTime() {
		return configValues.getStartTime();
	}

	/**
	 * Get added time value
	 * 
	 * @return the added time
	 */
	public static int getAddedTime() {
		return configValues.getAddedTime();
	}

	/**
	 * Get spread distance value
	 * 
	 * @return the spread distance
	 */
	public static int getSpreadDistance() {
		return configValues.getSpreadDistance();
	}

	/**
	 * Get spread radius value
	 * 
	 * @return the spread radius
	 */
	public static int getSpreadRadius() {
		return configValues.getSpreadRadius();
	}
	
	/**
	 * Get sbtt amount value
	 * 
	 * @return the sbtt amount
	 */
	public static int getSBTTAmount() {
		return configValues.getSbttAmount();
	}
	
	/**
	 * Get xp end value
	 * 
	 * @return the end teleporter xp amount
	 */
	public static int getXpEnd() {
		return configValues.getXpEnd();
	}
	
	/**
	 * Get xp overworld value
	 * 
	 * @return the overworld xp amount
	 */
	public static int getXpOverworld() {
		return configValues.getXpOverworld();
	}
	
	/**
	 * Get xp coral value
	 * 
	 * @return the coral xp amount
	 */
	public static int getXpCoral() {
		return configValues.getXpCoral();
	}
	
	/**
	 * Get the first skip age value
	 * 
	 * @return the first skip age number
	 */
	public static Age getSkipAge() {
		return AgeManager.getAgeByNumber(configValues.getSkipAge());
	}

	/**
	 * Get last age value
	 * 
	 * @return the last age number
	 */
	public static Age getLastAge() {
		return AgeManager.getAgeByNumber(configValues.getLastAge());
	}

	/**
	 * Get Level value
	 * 
	 * @return the level
	 */
	public static Level getLevel() {
		return LevelManager.getInstance().getLevelByNumber(configValues.getLevel());
	}

	/**
	 * Get lang value
	 * 
	 * @return the lang
	 */
	public static String getLang() {
		if (configValues == null) {
			return "en";
		}
		
		return configValues.getLang();
	}
	
	/**
	 * Set tips value
	 * 
	 * @param configTips	value used to set tips
	 */
	public static void setTips(boolean configTips) {
		configValues.setTips(configTips);
		setRet = true;
	}

	/**
	 * Set saturation value
	 * 
	 * @param configSaturation	value used to set saturation
	 */
	private static void setSaturation(boolean configSaturation) {
		configValues.setSaturation(configSaturation);
		setRet = true;
	}

	/**
	 * Set spread player value
	 * 
	 * @param configSpread	value used to set spread player
	 */
	private static void setSpreadplayers(boolean configSpread) {
		configValues.setSpread(configSpread);
		setRet = true;
	}

	/**
	 * Set reward value
	 * 
	 * @param configRewards	value used to set reward
	 */
	private static void setRewards(boolean configRewards) {
		configValues.setRewards(configRewards);
		setRet = true;
	}

	/**
	 * Set skip value
	 * 
	 * @param configSkip	value used to set skip
	 */
	private static void setSkip(boolean configSkip) {
		configValues.setSkip(configSkip);
		setRet = true;
	}

	/**
	 * Set craft value
	 * 
	 * @param configCrafts	value used to set craft
	 */
	private static void setCrafts(boolean configCrafts) {
		configValues.setCrafts(configCrafts);
		setRet = true;
	}

	/**
	 * Set team value
	 * 
	 * @param configTeam	value used to set team
	 */
	private static void setTeam(boolean configTeam) {
		if (Party.getInstance().getStatus() != GameStatus.NOT_RUNNING) {
			error = "Cannot set Team while game is running !";
			setRet = false;
		} else {
			configValues.setTeam(configTeam);
			setRet = true;			
		}
	}
	
	/**
	 * Set team inv value
	 * 
	 * @param configTeamInv	value used to set team inv
	 */
	private static void setTeamInv(boolean configTeamInv) {
		if (Party.getInstance().getStatus() != GameStatus.NOT_RUNNING) {
			error = "Cannot set Team Inv while game is running !";
			setRet = false;
		} else {
			configValues.setTeamInv(configTeamInv);
			setRet = true;			
		}
	}
	
	/**
	 * Set same value
	 * 
	 * @param configSame	value used to set same
	 */
	private static void setSame(boolean configSame) {
		if (Party.getInstance().getStatus() != GameStatus.NOT_RUNNING) {
			error = "Cannot change mode when game is running !";
			setRet = false;
		} else {		
			configValues.setSame(configSame);
			setRet = true;
		}
	}
	
	/**
	 * Set double value
	 * 
	 * @param configDuoMode	value used to set double mode
	 */
	private static void setDoubleMode(boolean configDuoMode) {
		configValues.setDuoMode(configDuoMode);
		setRet = true;
	}
	
	/**
	 * Set sbtt value
	 * 
	 * @param configSbttMode	value used to set sbtt
	 */
	private static void setSbttMode(boolean configSbttMode) {
		configValues.setSbttMode(configSbttMode);
		setRet = true;
	}
	
	/**
	 * Set print tab value
	 * 
	 * @param configPrintTab	value used to set printTab
	 */
	private static void setPrintTab(boolean configPrintTab) {
		configValues.setPrintTab(configPrintTab);
		setRet = true;
	}
	
	/**
	 * Set print tab all value
	 * 
	 * @param configPrintTabAll	value used to set printTabAll
	 */
	private static void setPrintTabAll(boolean configPrintTabAll) {
		configValues.setPrintTabAll(configPrintTabAll);
		setRet = true;
	}
	
	/**
	 * Set end one value
	 * 
	 * @param configEndOne	value used to set end one
	 */
	private static void setEndOne(boolean configEndOne) {
		configValues.setEndOne(configEndOne);
		setRet = true;
	}
	
	/**
	 * Set passive All value
	 * 
	 * @param configPassive	value used to set passive all
	 */
	private static void setPassiveAll(boolean configPassive) {
		configValues.setPassiveAll(configPassive);
		setRet = true;
	}

	/**
	 * Set passive Team value
	 * 
	 * @param configPassive	value used to set passive team
	 */
	private static void setPassiveTeam(boolean configPassive) {
		configValues.setPassiveTeam(configPassive);
		setRet = true;
	}
	
	/**
	 * Set team size value
	 * 
	 * @param configTeamSize	value used to set team size
	 */
 	private static void setTeamSize(int configTeamSize) {
 		if (Party.getInstance().getStatus() != GameStatus.NOT_RUNNING) {
 			error = "Cannot change team siez when game is running !";
			setRet = false;
		}
 		
 		if (setRet && configTeamSize < 1) {
 			error = "Cannot set team size under 1 !";
 			setRet = false;
 		}
 		
		if (setRet && configValues.isTeam() && TeamManager.getInstance().getTeams().size() > 0 && TeamManager.getInstance().getMaxTeamSize() > configTeamSize) {
			error = "Cannot set team size less than a current team size !";
			setRet = false;
		}

		if (setRet) {
			configValues.setTeamSize(configTeamSize);
			setRet = true;
		}
	}
 	
 	/**
 	 * Set team in size
 	 * 
 	 * @param configTeamIntSize	value used to set team inv size
 	 */
 	private static void setTeamInvSize(int configTeamIntSize) {
 		if (Party.getInstance().getStatus() != GameStatus.NOT_RUNNING) {
 			error = "Cannot change team inv size when game is running !";
			setRet = false;
		}
 		
 		if (setRet && configTeamIntSize < 1 || configTeamIntSize > 6) {
 			error = "Cannot set team inv size, out of 1 to 6 bounds !";
 			setRet = false;
 		}
 		
 		if (setRet) {
			configValues.setTeamInvSize(configTeamIntSize);
			setRet = true;
		}
 	}

 	/**
	 * Set spread distance value
	 * 
	 * @param configSpreadDistance	value used to set spread distance
	 */
	private static void setSpreadDistance(int configSpreadDistance) {
		configValues.setSpreadDistance(configSpreadDistance);
		setRet = true;
	}

	/**
	 * Set spread radius value
	 * 
	 * @param configSpreadRadius	value used to set spread radius
	 */
	private static void setSpreadRadius(int configSpreadRadius) {
		if (configSpreadRadius < configValues.getSpreadDistance()) {
			error = "Cannot set spread radius less than spread distance !";
			setRet = false;
		} else {		
			configValues.setSpreadRadius(configSpreadRadius);
			setRet = true;
		}
	}

	/**
	 * Set target per age value
	 * 
	 * @param configTargetPerAge	value used to set target per age
	 */
	private static void setTargetAge(int configTargetPerAge) {
		if (configTargetPerAge < 1) {
			error = "Cannot have less than one target per Age !";
			setRet = false;
		} else {
			configValues.setTargetPerAge(configTargetPerAge);
			setRet = true;
		}
	}

	/**
	 * Set start time value
	 * 
	 * @param configStartTime	value used to set start time
	 */
	private static void setStartTime(int configStartTime) {
		if (configStartTime < 1) {
			error = "Cannot set added time less than 1";
			setRet = false;
		} else {
			configValues.setStartTime(configStartTime);
			setRet = true;
		}
	}

	/**
	 * Set added time value
	 * 
	 * @param configAddedTime	value used to set added time
	 */
	private static void setAddedTime(int configAddedTime) {
		if (configAddedTime < 1) {
			error = "Cannot set added time less than 1";
			setRet = false;
		} else {
			configValues.setAddedTime(configAddedTime);
			setRet = true;
		}
	}
	
	/**
	 * Set sbtt amount value
	 * 
	 * @param configSbttAmount	value used to set sbtt amount
	 */
	private static void setSbttAmount(int configSbttAmount) {
		if (configSbttAmount < 1 || configSbttAmount > 9) {
			error = "Cannot set out out of 1 to 9 range !";
			setRet = false;
		}
		
		configValues.setSbttAmount(configSbttAmount);
		setRet = true;
	}
	
	/**
	 * Set xp end value
	 * 
	 * @param configXpEnd	value used to set xp end
	 */
	private static void setXpEnd(int configXpEnd) {
		if (configXpEnd < 1 || configXpEnd > 10) {
			error = "Cannot set out of 1 to 10 range !";
			setRet = false;
		} else {
			configValues.setXpEnd(configXpEnd);
			setRet = true;
		}
	}
	
	/**
	 * Set xp overworld value
	 * 
	 * @param configXpOverworld	value used to set xp overworld
	 */
	private static void setXpOverworld(int configXpOverworld) {
		if (configXpOverworld < 1 || configXpOverworld > 20) {
			error = "Cannot set out of 1 to 20 range !";
			setRet = false;
		} else {
			configValues.setXpOverworld(configXpOverworld);
			setRet = true;
		}
	}
	
	/**
	 * Set xp coral value
	 * 
	 * @param configXpCoral	value used to set xp coral
	 */
	private static void setXpCoral(int configXpCoral) {
		if (configXpCoral < 1 || configXpCoral > 30) {
			error = "Cannot set out of 1 to 30 range !";
			setRet = false;
		} else {
			configValues.setXpCoral(configXpCoral);
			setRet = true;
		}
	}
	
	/**
	 * Set last age value
	 * 
	 * @param configLastAge	value used to set last age
	 */
	private static void setLastAge(String configLastAge) {
		if (Party.getInstance().getStatus() != GameStatus.NOT_RUNNING) {
			error = "Game already started, you cannot modify last Age";
			setRet = false;
		} else if (!AgeManager.ageExists(configLastAge)) {
			error = "Unknown Age !";
			setRet = false;
		} else {
			configValues.setLastAge(AgeManager.getAgeByName(configLastAge).getNumber());
			setRet = true;
		}
	}
	
	/**
	 * Set skip age value
	 * 
	 * @param configSkipAge	value used to set skip age
	 */
	private static void setFirstSkip(String configSkipAge) {
		if (Party.getInstance().getStatus() != GameStatus.NOT_RUNNING) {
			error = "Game already started, you cannot modify skip Age";
			setRet = false;
		} else if (!AgeManager.ageExists(configSkipAge)) {
			error = "Unknown Age !";
			setRet = false;
		} else if (AgeManager.getAgeByName(configSkipAge).getNumber() > configValues.getLastAge()) {
			error = "Cannot set the first age for skipping after the last age !";
			setRet = false;
		} else {
			configValues.setSkipAge(AgeManager.getAgeByName(configSkipAge).getNumber());
			setRet = true;
		}
	}

	/**
	 * Set level value
	 * 
	 * @param configLevel	value used to set level
	 */
	private static void setLevel(String configLevel) {
		if (LevelManager.getInstance().levelNotExists(configLevel)) {
			error = "Unknown level !";
			setRet = false;
		} else {
			configValues.setLevel(LevelManager.getInstance().getLevelByName(configLevel).getNumber());
			setRet = true;
		}
	}

	/**
	 * Set lang value
	 * 
	 * @param configLang	value used to set lang
	 */
	private static void setLang(String configLang) {
		if (!LangManager.hasLang(configLang)) {
			error = "Unknown lang !";
			setRet = false;
		} else {
			configValues.setLang(configLang);
			setRet = true;
		}
	}
	
	/**
	 * Gets the ConfigHolder object that contains all config values
	 * 
	 * @return the whole config values
	 */
	public static ConfigHolder getHolder() {
		return configValues;
	}
}
