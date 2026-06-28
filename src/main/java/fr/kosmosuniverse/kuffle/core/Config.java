package fr.kosmosuniverse.kuffle.core;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import fr.kosmosuniverse.kuffle.KuffleMain;
import fr.kosmosuniverse.kuffle.datamanagers.LangManager;
import fr.kosmosuniverse.kuffle.datamanagers.age.Age;
import fr.kosmosuniverse.kuffle.datamanagers.age.AgeManager;
import fr.kosmosuniverse.kuffle.datamanagers.level.Level;
import fr.kosmosuniverse.kuffle.datamanagers.level.LevelManager;
import fr.kosmosuniverse.kuffle.exceptions.KuffleConfigException;
import fr.kosmosuniverse.kuffle.mode.Mode;
import fr.kosmosuniverse.kuffle.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
	 * Config Default message
	 */
	private static final String CONFIG_DEFAULT = "CONFIG_DEFAULT";
	
	private static ConfigHolder configValues;

	/**
	 * Error flag
	 */
	public static boolean setRet;
	
	/**
	 * Error message
	 */
	public static String error;

	/**
	 * Map of config value and set method
	 */
	private static Map<String, Consumer<String>> configElems = null;

	private static ConfigInventories configInvs;

	/**
	 * Constructor
	 * 
	 * @param configFile	configuration file used to set up config values
	 */
	public static void setupConfig(FileConfiguration configFile) {
		configValues = new ConfigHolder();
		configElems = new HashMap<>();

		configElems.put("LOG_RESULTS", (String b) -> setLogResults(Boolean.parseBoolean(b)));
		configElems.put("START_MODE", Config::setStartMode);

		configElems.put("TIPS", (String b) -> setTips(Boolean.parseBoolean(b)));
		configElems.put("SATURATION", (String b) -> setSaturation(Boolean.parseBoolean(b)));
		configElems.put("SPREADPLAYERS", (String b) -> setSpreadplayers(Boolean.parseBoolean(b)));
		configElems.put("REWARDS", (String b) -> setRewards(Boolean.parseBoolean(b)));
		configElems.put("SKIP", (String b) -> setSkip(Boolean.parseBoolean(b)));
		configElems.put("CUSTOM_CRAFTS", (String b) -> setCrafts(Boolean.parseBoolean(b)));
		configElems.put("TEAM", (String b) -> setTeam(Boolean.parseBoolean(b)));
		configElems.put("TEAM_INV", (String b) -> setTeamInv(Boolean.parseBoolean(b)));
		configElems.put("COOP", (String b) -> setCoop(Boolean.parseBoolean(b)));
		configElems.put("COOP_SKIP", (String b) -> setCoopSkip(Boolean.parseBoolean(b)));
		configElems.put("SAME_MODE", (String b) -> setSame(Boolean.parseBoolean(b)));
		configElems.put("DOUBLE_MODE", (String b) -> setDoubleMode(Boolean.parseBoolean(b)));
		configElems.put("SBTT_MODE", (String b) -> setSbttMode(Boolean.parseBoolean(b)));
		configElems.put("PRINT_TAB", (String b) -> setPrintTab(Boolean.parseBoolean(b)));
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
		configElems.put("COOP_BASE", (String i) -> setCoopBase(Integer.parseInt(i)));
		configElems.put("COOP_UPDATED", (String i) -> setCoopUpdated(Integer.parseInt(i)));
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
	 * @param configFile	configuration file used to set up config values
	 */
	private static void checkAndSetConfig(FileConfiguration configFile) {
		setRet = false;

		checkFileSystem(configFile);
		checkFilePersonal(configFile);
		checkFileSpread(configFile);
		checkFileModes(configFile);
		checkFileStart(configFile);
		checkFileOther(configFile);
		checkFileEnd(configFile);

		if (setRet) {
			KuffleMain.getInstance().saveConfig();
		}

		setValues(configFile);
	}

	private static void checkFileSystem(FileConfiguration configFile) {
		if (!configFile.contains(ConfigPaths.SYS_START_MODE.getPath()) ||
				!Mode.hasMode(configFile.getString(ConfigPaths.SYS_START_MODE.getPath()))) {
			configValues.setStartMode("NO_MODE");
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "start mode"));
			configFile.set(ConfigPaths.SYS_START_MODE.getPath(), "NO_MODE");
			setRet = true;
		}

		if (!configFile.contains(ConfigPaths.SYS_LOG_RESULT.getPath())) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "log game results"));
			configFile.set(ConfigPaths.SYS_LOG_RESULT.getPath(), false);
			setRet = true;
		}
	}
	
	private static void checkFilePersonal(FileConfiguration configFile) {
		if (!configFile.contains(ConfigPaths.GAME_PERS_LANG.getPath())
				|| !LangManager.hasLang(configFile.getString(ConfigPaths.GAME_PERS_LANG.getPath()))) {
			configValues.setLang("en");
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "lang"));
			configFile.set(ConfigPaths.GAME_PERS_LANG.getPath(), "en");
			setRet = true;
		} else {
			configValues.setLang(configFile.getString(ConfigPaths.GAME_PERS_LANG.getPath()));
		}
		
		if (!configFile.contains(ConfigPaths.GAME_PERS_TIPS.getPath())) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "enabling tips"));
			configFile.set(ConfigPaths.GAME_PERS_TIPS.getPath(), false);
			setRet = true;
		}
	}
	
	/**
	 * Check spread values in config file to ensure they exist and are conform
	 * 
	 * @param configFile	configuration file used to set up config values
	 */
	private static void checkFileSpread(FileConfiguration configFile) {
		if (!configFile.contains(ConfigPaths.GAME_SPREAD.getPath())) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "enabling spreadplayers"));
			configFile.set(ConfigPaths.GAME_SPREAD.getPath(), false);
			setRet = true;
		}
		
		if (!configFile.contains(ConfigPaths.GAME_SPREAD_DIST.getPath())
				|| configFile.getInt(ConfigPaths.GAME_SPREAD_DIST.getPath()) < 1) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "spreadplayers minimum distance"));
			configFile.set(ConfigPaths.GAME_SPREAD_DIST.getPath(), 500);
			setRet = true;
		}

		if (!configFile.contains(ConfigPaths.GAME_SPREAD_RAD.getPath())
				|| configFile.getInt(ConfigPaths.GAME_SPREAD_RAD.getPath()) < configFile.getInt(ConfigPaths.GAME_SPREAD_DIST.getPath())) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "spreadplayers minimum radius"));
			configFile.set(ConfigPaths.GAME_SPREAD_RAD.getPath(), 1000);
			setRet = true;
		}
	}
	
	/**
	 * Check modes values in config file to ensure they exist and are conform
	 * 
	 * @param configFile	configuration file used to set up config values
	 */
	private static void checkFileModes(FileConfiguration configFile) {
		if (!configFile.contains(ConfigPaths.GAME_TEAM.getPath())) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "enabling team"));
			configFile.set(ConfigPaths.GAME_TEAM.getPath(), false);
			setRet = true;
		}

		if (!configFile.contains(ConfigPaths.GAME_MODE_COOP.getPath())) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "enabling coop"));
			configFile.set(ConfigPaths.GAME_MODE_COOP.getPath(), false);
			setRet = true;
		}

		if (!configFile.contains(ConfigPaths.GAME_MODE_COOP_SKIP.getPath())) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "enabling coop skip"));
			configFile.set(ConfigPaths.GAME_MODE_COOP_SKIP.getPath(), false);
			setRet = true;
		}

		if (!configFile.contains(ConfigPaths.GAME_MODE_COOP_BASE.getPath()) &&
				configFile.getInt(ConfigPaths.GAME_MODE_COOP_BASE.getPath()) < 1) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "coop base time"));
			configFile.set(ConfigPaths.GAME_MODE_COOP_BASE.getPath(), 15);
			setRet = true;
		}

		if (!configFile.contains(ConfigPaths.GAME_MODE_COOP_ADDED.getPath()) &&
				configFile.getInt(ConfigPaths.GAME_MODE_COOP_ADDED.getPath()) < 1) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "coop updated time"));
			configFile.set(ConfigPaths.GAME_MODE_COOP_ADDED.getPath(), 1);
			setRet = true;
		}

		if (!configFile.contains(ConfigPaths.GAME_TEAM_SIZE.getPath()) || configFile.getInt(ConfigPaths.GAME_TEAM_SIZE.getPath()) < 2
				|| configFile.getInt(ConfigPaths.GAME_TEAM_SIZE.getPath()) > 10) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "max team size"));
			configFile.set(ConfigPaths.GAME_TEAM_SIZE.getPath(), 2);
			setRet = true;
		}
		
		if (!configFile.contains(ConfigPaths.GAME_TEAM_INV.getPath())) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "enabling team inv"));
			configFile.set(ConfigPaths.GAME_TEAM_INV.getPath(), false);
			setRet = true;
		}
		
		if (!configFile.contains(ConfigPaths.GAME_TEAM_INV_SIZE.getPath()) || configFile.getInt(ConfigPaths.GAME_TEAM_INV_SIZE.getPath()) < 1
				|| configFile.getInt(ConfigPaths.GAME_TEAM_INV_SIZE.getPath()) > 6) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "team inv size"));
			configFile.set(ConfigPaths.GAME_TEAM_INV_SIZE.getPath(), 1);
			setRet = true;
		}
		
		if (!configFile.contains(ConfigPaths.GAME_MODE_SAME.getPath())) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "enabling same mode"));
			configFile.set(ConfigPaths.GAME_MODE_SAME.getPath(), false);
			setRet = true;
		}
		
		if (!configFile.contains(ConfigPaths.GAME_MODE_SBTT.getPath())) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "SBTT mode"));
			configFile.set(ConfigPaths.GAME_MODE_SBTT.getPath(), false);
			setRet = true;
		}
		
		if (!configFile.contains(ConfigPaths.GAME_MODE_SBTT_AMNT.getPath()) ||
				configFile.getInt(ConfigPaths.GAME_MODE_SBTT_AMNT.getPath()) < 1 ||
				configFile.getInt(ConfigPaths.GAME_MODE_SBTT_AMNT.getPath()) > 9) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "SBTT amount"));
			configFile.set(ConfigPaths.GAME_MODE_SBTT_AMNT.getPath(), 4);
			setRet = true;
		}
		
		if (!configFile.contains(ConfigPaths.GAME_MODE_DOUBLE.getPath())) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "Double mode"));
			configFile.set(ConfigPaths.GAME_MODE_DOUBLE.getPath(), false);
			setRet = true;
		}
		
		if (!configFile.contains(ConfigPaths.GAME_PASS_ALL.getPath())) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "Passive mode"));
			configFile.set(ConfigPaths.GAME_PASS_ALL.getPath(), false);
			setRet = true;
		}
		
		if (!configFile.contains(ConfigPaths.GAME_PASS_TEAM.getPath())) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "Passive mode"));
			configFile.set(ConfigPaths.GAME_PASS_TEAM.getPath(), false);
			setRet = true;
		}
	}
	
	/**
	 * Check basic values in config file to ensure they exist and are conform
	 * 
	 * @param configFile	configuration file used to set up config values
	 */
	private static void checkFileStart(FileConfiguration configFile) {
		if (!configFile.contains(ConfigPaths.GAME_NB_TARGET.getPath())
				|| configFile.getInt(ConfigPaths.GAME_NB_TARGET.getPath()) < 1) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "item per age"));
			configFile.set(ConfigPaths.GAME_NB_TARGET.getPath(), 5);
			setRet = true;
		}
		
		if (!configFile.contains(ConfigPaths.GAME_TIME_START.getPath()) || configFile.getInt(ConfigPaths.GAME_TIME_START.getPath()) < 1) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "start time"));
			configFile.set(ConfigPaths.GAME_TIME_START.getPath(), 4);
			setRet = true;
		}

		if (!configFile.contains(ConfigPaths.GAME_TIME_ADD.getPath()) || configFile.getInt(ConfigPaths.GAME_TIME_ADD.getPath()) < 1) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "time added"));
			configFile.set(ConfigPaths.GAME_TIME_ADD.getPath(), 2);
			setRet = true;
		}

		if (!configFile.contains(ConfigPaths.GAME_LAST_AGE.getPath()) ||
				AgeManager.getAgeByName(configFile.getString(ConfigPaths.GAME_LAST_AGE.getPath())) == null ||
				AgeManager.getAgeByName(configFile.getString(ConfigPaths.GAME_LAST_AGE.getPath())).getNumber() == -1) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "max ages"));
			configFile.set(ConfigPaths.GAME_LAST_AGE.getPath(), AgeManager.getLastAge().getName());
			setRet = true;
		}

		if (!configFile.contains(ConfigPaths.GAME_LEVEL.getPath()) ||
				LevelManager.getInstance().levelNotExists(configFile.getString(ConfigPaths.GAME_LEVEL.getPath()))) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "levels"));
			configFile.set(ConfigPaths.GAME_LEVEL.getPath(), LevelManager.getInstance().getFirstLevel().getName());
			setRet = true;
		}
	}
	
	/**
	 * Check other values in config file to ensure they exist and are conform
	 * 
	 * @param configFile	configuration file used to set up config values
	 */
	private static void checkFileOther(FileConfiguration configFile) {
		if (!configFile.contains(ConfigPaths.GAME_SKIP.getPath())) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "enabling skip"));
			configFile.set(ConfigPaths.GAME_SKIP.getPath(), true);
			setRet = true;
		}

		if (!configFile.contains(ConfigPaths.GAME_SKIP_AGE.getPath()) ||
				AgeManager.getAgeByName(configFile.getString(ConfigPaths.GAME_SKIP_AGE.getPath())) == null ||
				AgeManager.getAgeByName(configFile.getString(ConfigPaths.GAME_SKIP_AGE.getPath())).getNumber() == -1) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "skip age"));
			configFile.set(ConfigPaths.GAME_SKIP_AGE.getPath(), AgeManager.getFirstAge().getName());
			setRet = true;
		}

		if (!configFile.contains(ConfigPaths.GAME_CRAFTS.getPath())) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "enabling custom crafts"));
			configFile.set(ConfigPaths.GAME_CRAFTS.getPath(), true);
			setRet = true;
		}
		
		if (!configFile.contains(ConfigPaths.GAME_XP_END.getPath())) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "xp max EndTeleporter"));
			configFile.set(ConfigPaths.GAME_XP_END.getPath(), 5);
			setRet = true;
		}
		
		if (!configFile.contains(ConfigPaths.GAME_XP_OVERWORLD.getPath())) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "xp max OverworldTeleporter"));
			configFile.set(ConfigPaths.GAME_XP_OVERWORLD.getPath(), 10);
		}
		
		if (!configFile.contains(ConfigPaths.GAME_XP_CORAL.getPath())) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "xp max CoralCompass"));
			configFile.set(ConfigPaths.GAME_XP_CORAL.getPath(), 20);
			setRet = true;
		}
	}
	
	/**
	 * Check end values in config file to ensure they exist and are conform
	 * 
	 * @param configFile	configuration file used to set up config values
	 */
	private static void checkFileEnd(FileConfiguration configFile) {
		if (!configFile.contains(ConfigPaths.GAME_PRINT.getPath())) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "enabling game end tab display"));
			configFile.set(ConfigPaths.GAME_PRINT.getPath(), false);
			setRet = true;
		}
		
		if (!configFile.contains(ConfigPaths.GAME_END_ONE.getPath())) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(CONFIG_DEFAULT, configValues.getLang()).replace("<#>", "game end when one"));
			configFile.set(ConfigPaths.GAME_END_ONE.getPath(), false);
			setRet = true;
		}
	}

	/**
	 * Setup config values from config file
	 * 
	 * @param configFile	file that contains all config values
	 */
	private static void setValues(FileConfiguration configFile) {
		configValues.setStartMode(configFile.getString(ConfigPaths.SYS_START_MODE.getPath()));
		configValues.setLogResults(configFile.getBoolean(ConfigPaths.SYS_LOG_RESULT.getPath()));

		configValues.setTips(configFile.getBoolean(ConfigPaths.GAME_PERS_TIPS.getPath()));
		
		configValues.setSaturation(configFile.getBoolean(ConfigPaths.GAME_SAT.getPath()));
		configValues.setSpread(configFile.getBoolean(ConfigPaths.GAME_SPREAD.getPath()));
		configValues.setRewards(configFile.getBoolean(ConfigPaths.GAME_REWARD.getPath()));
		configValues.setSkip(configFile.getBoolean(ConfigPaths.GAME_SKIP.getPath()));
		configValues.setCrafts(configFile.getBoolean(ConfigPaths.GAME_CRAFTS.getPath()));
		configValues.setTeam(configFile.getBoolean(ConfigPaths.GAME_TEAM.getPath()));
		configValues.setCoop(configFile.getBoolean(ConfigPaths.GAME_MODE_COOP.getPath()));
		configValues.setCoopSkip(configFile.getBoolean(ConfigPaths.GAME_MODE_COOP_SKIP.getPath()));
		configValues.setTeamInv(configFile.getBoolean(ConfigPaths.GAME_TEAM_INV.getPath()));
		configValues.setSame(configFile.getBoolean(ConfigPaths.GAME_MODE_SAME.getPath()));
		configValues.setPrintTab(configFile.getBoolean(ConfigPaths.GAME_PRINT.getPath()));
		configValues.setEndOne(configFile.getBoolean(ConfigPaths.GAME_END_ONE.getPath()));
		configValues.setDuoMode(configFile.getBoolean(ConfigPaths.GAME_MODE_DOUBLE.getPath()));
		configValues.setSbttMode(configFile.getBoolean(ConfigPaths.GAME_MODE_SBTT.getPath()));
		configValues.setPassiveAll(configFile.getBoolean(ConfigPaths.GAME_PASS_ALL.getPath()));
		configValues.setPassiveTeam(configFile.getBoolean(ConfigPaths.GAME_PASS_TEAM.getPath()));
		
		configValues.setSpreadDistance(configFile.getInt(ConfigPaths.GAME_SPREAD_DIST.getPath()));
		configValues.setSpreadRadius(configFile.getInt(ConfigPaths.GAME_SPREAD_RAD.getPath()));
		configValues.setTargetPerAge(configFile.getInt(ConfigPaths.GAME_NB_TARGET.getPath()));
		configValues.setStartTime(configFile.getInt(ConfigPaths.GAME_TIME_START.getPath()));
		configValues.setAddedTime(configFile.getInt(ConfigPaths.GAME_TIME_ADD.getPath()));
		configValues.setTeamSize(configFile.getInt(ConfigPaths.GAME_TEAM_SIZE.getPath()));
		configValues.setTeamInvSize(configFile.getInt(ConfigPaths.GAME_TEAM_INV_SIZE.getPath()));
		configValues.setCoopBase(configFile.getInt(ConfigPaths.GAME_MODE_COOP_BASE.getPath()));
		configValues.setCoopUpdate(configFile.getInt(ConfigPaths.GAME_MODE_COOP_ADDED.getPath()));
		configValues.setSbttAmount(configFile.getInt(ConfigPaths.GAME_MODE_SBTT_AMNT.getPath()));
		configValues.setXpEnd(configFile.getInt(ConfigPaths.GAME_XP_END.getPath()));
		configValues.setXpOverworld(configFile.getInt(ConfigPaths.GAME_XP_OVERWORLD.getPath()));
		configValues.setXpCoral(configFile.getInt(ConfigPaths.GAME_XP_CORAL.getPath()));
		
		configValues.setLastAge(AgeManager.getAgeByName(configFile.getString(ConfigPaths.GAME_LAST_AGE.getPath())).getNumber());
		configValues.setLevel(LevelManager.getInstance().getLevelByName(configFile.getString(ConfigPaths.GAME_LEVEL.getPath())).getNumber());
		configValues.setSkipAge(AgeManager.getAgeByName(configFile.getString(ConfigPaths.GAME_SKIP_AGE.getPath())).getNumber());

		configInvs = new ConfigInventories();
		configInvs.createInventories();
	}

	public static void saveValues() {
		FileConfiguration config = KuffleMain.getInstance().getConfig();
		
		config.set(ConfigPaths.SYS_START_MODE.getPath(), configValues.getStartMode());
		config.set(ConfigPaths.SYS_LOG_RESULT.getPath(), configValues.isLogResults());
		config.set(ConfigPaths.GAME_PRINT.getPath(), configValues.isPrintTab());
		config.set(ConfigPaths.GAME_END_ONE.getPath(), configValues.isEndOne());
		config.set(ConfigPaths.GAME_SPREAD.getPath(), configValues.isSpread());
		config.set(ConfigPaths.GAME_SPREAD_DIST.getPath(), configValues.getSpreadDistance());
		config.set(ConfigPaths.GAME_SPREAD_RAD.getPath(), configValues.getSpreadRadius());
		config.set(ConfigPaths.GAME_SAT.getPath(), configValues.isSaturation());
		config.set(ConfigPaths.GAME_REWARD.getPath(), configValues.isRewards());
		config.set(ConfigPaths.GAME_PASS_ALL.getPath(), configValues.isPassiveAll());
		config.set(ConfigPaths.GAME_PASS_TEAM.getPath(), configValues.isPassiveTeam());
		config.set(ConfigPaths.GAME_TIME_START.getPath(), configValues.getStartTime());
		config.set(ConfigPaths.GAME_TIME_ADD.getPath(), configValues.getAddedTime());
		config.set(ConfigPaths.GAME_LAST_AGE.getPath(), AgeManager.getAgeByNumber(configValues.getLastAge()).getName());
		config.set(ConfigPaths.GAME_NB_TARGET.getPath(), configValues.getTargetPerAge());
		config.set(ConfigPaths.GAME_PERS_LANG.getPath(), configValues.getLang());
		config.set(ConfigPaths.GAME_PERS_TIPS.getPath(), configValues.isTips());
		config.set(ConfigPaths.GAME_LEVEL.getPath(), LevelManager.getInstance().getLevelByNumber(configValues.getLevel()).getName());
		config.set(ConfigPaths.GAME_SKIP.getPath(), configValues.isSkip());
		config.set(ConfigPaths.GAME_SKIP_AGE.getPath(), AgeManager.getAgeByNumber(configValues.getSkipAge()).getName());
		config.set(ConfigPaths.GAME_CRAFTS.getPath(), configValues.isCrafts());
		config.set(ConfigPaths.GAME_TEAM.getPath(), configValues.isTeam());
		config.set(ConfigPaths.GAME_MODE_COOP.getPath(), configValues.isCoop());
		config.set(ConfigPaths.GAME_MODE_COOP_SKIP.getPath(), configValues.isCoopSkip());
		config.set(ConfigPaths.GAME_MODE_COOP_BASE.getPath(), configValues.getCoopBase());
		config.set(ConfigPaths.GAME_MODE_COOP_ADDED.getPath(), configValues.getCoopUpdate());
		config.set(ConfigPaths.GAME_TEAM_SIZE.getPath(), configValues.getTeamSize());
		config.set(ConfigPaths.GAME_TEAM_INV.getPath(), configValues.isTeamInv());
		config.set(ConfigPaths.GAME_TEAM_INV_SIZE.getPath(), configValues.getTeamInvSize());
		config.set(ConfigPaths.GAME_MODE_SAME.getPath(), configValues.isSame());
		config.set(ConfigPaths.GAME_MODE_DOUBLE.getPath(), configValues.isDuoMode());
		config.set(ConfigPaths.GAME_MODE_SBTT.getPath(), configValues.isSbttMode());
		config.set(ConfigPaths.GAME_MODE_SBTT_AMNT.getPath(), configValues.getSbttAmount());
		config.set(ConfigPaths.GAME_XP_END.getPath(), configValues.getXpEnd());
		config.set(ConfigPaths.GAME_XP_OVERWORLD.getPath(), configValues.getXpOverworld());
		config.set(ConfigPaths.GAME_XP_CORAL.getPath(), configValues.getXpCoral());

		KuffleMain.getInstance().saveConfig();
	}

	public static void resetConfig() {
		File configFile = new File(KuffleMain.getInstance().getDataFolder(), "config.yml");

		if (!configFile.delete()) {
			LogManager.getInstanceSystem().logSystemMsg("Cannot delete config file");
		}

		KuffleMain.getInstance().saveDefaultConfig();
		KuffleMain.getInstance().reloadConfig();
		setValues(KuffleMain.getInstance().getConfig());
		configInvs.clear();
		configInvs.createInventories();
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
				"System : " + "\n"
				+ ChatColor.BLUE + "  - Start Mode: " + ChatColor.GOLD + configValues.getStartMode() + "\n"
				+ ChatColor.BLUE + "  - Log Result: " + ChatColor.GOLD + configValues.isLogResults() + "\n"
				+ ChatColor.BLUE + "Saturation: " + ChatColor.GOLD + configValues.isSaturation() + "\n"
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
				+ ChatColor.BLUE + "Personals: " + "\n"
				+ ChatColor.BLUE + "  - Lang: " + ChatColor.GOLD + configValues.getLang() + "\n"
				+ ChatColor.BLUE + "  - Tips: " + ChatColor.GOLD + configValues.isTips() + "\n"
				+ ChatColor.BLUE + "Level: " + ChatColor.GOLD + LevelManager.getInstance().getLevelByNumber(configValues.getLevel()).getName() + "\n"
				+ ChatColor.BLUE + "Print tab at game end: " + ChatColor.GOLD + configValues.isPrintTab() + "\n"
				+ ChatColor.BLUE + "Game ends when remains one: " + ChatColor.GOLD + configValues.isEndOne() + "\n"
				+ ChatColor.BLUE + "Passive: " + "\n"
				+ ChatColor.BLUE + "  - All: " + ChatColor.GOLD + configValues.isPassiveAll() + "\n"
				+ ChatColor.BLUE + "  - Team: " + ChatColor.GOLD + configValues.isPassiveTeam() + "\n"
				+ ChatColor.BLUE + "Team: " + ChatColor.GOLD + configValues.isTeam() + "\n"
				+ ChatColor.BLUE + "  - Team Size: " + ChatColor.GOLD + configValues.getTeamSize() + "\n"
				+ ChatColor.BLUE + "  - Team Inv: " + ChatColor.GOLD + configValues.isTeamInv() + "\n"
				+ ChatColor.BLUE + "    - Team Inv Size: " + ChatColor.GOLD + configValues.getTeamInvSize() + "\n"
				+ ChatColor.BLUE + "Modes: " + "\n"
				+ ChatColor.BLUE + "  - Coop: " + ChatColor.GOLD + configValues.isCoop() + "\n"
				+ ChatColor.BLUE + "    - base: " + ChatColor.GOLD + configValues.getCoopBase() + "\n"
				+ ChatColor.BLUE + "    - updated: " + ChatColor.GOLD + configValues.getCoopUpdate() + "\n"
				+ ChatColor.BLUE + "    - skip: " + ChatColor.GOLD + configValues.isCoopSkip() + "\n"
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
		ConfigInvTrigger.clear();
		configInvs.clear();
	}

	public static boolean hasInv(String invName) {
		return configInvs.getInv(invName) != null;
	}

	public static Inventory getMainInv() {
		return configInvs.getMainInv();
	}

	public static Inventory getInv(String invName) {
		return configInvs.getInv(invName);
	}

	public static void invTrigger(Player player, Inventory inv, ItemStack item, String trigger) {
		setRet = true;
		error = "";

		ConfigInvTrigger.apply(trigger, player, inv, item);
	}

	public static void reloadInv(String invName) {
		configInvs.reloadInv(invName);
	}
	
	/**
	 * Loads config
	 * 
	 * @param config	config read from file
	 */
	public static void loadConfig(ConfigHolder config) {
		configValues = new ConfigHolder(config);
	}

	public static Mode getStartMode() {
		return Mode.valueOf(configValues.getStartMode());
	}

	public static boolean getLogGameResult() {
		return configValues.isLogResults();
	}

	public static boolean getTips() {
		return configValues.isTips();
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
	 * Get coop enable value
	 *
	 * @return if coop mode is enabled
	 */
	public static boolean getCoop() {
		return configValues.isCoop();
	}

	/**
	 * Get coop enable value
	 *
	 * @return if coop mode is enabled
	 */
	public static int getCoopBase() {
		return configValues.getCoopBase();
	}

	/**
	 * Get coop enable value
	 *
	 * @return if coop mode is enabled
	 */
	public static int getCoopUpdated() {
		return configValues.getCoopUpdate();
	}

	/**
	 * Get coop enable value
	 *
	 * @return if coop mode is enabled
	 */
	public static boolean getCoopSkip() {
		return configValues.isCoopSkip();
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
	 * @return if passive is enabled for teammates
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
	
	public static void setStartMode(String startMode) {
		if (Mode.hasMode(startMode)) {
			configValues.setStartMode(startMode);
			setRet = true;
		} else {
			error = "This mode does not exists !";
			setRet = false;
		}
	}
	
	public static void  setLogResults(boolean logResults) {
		configValues.setLogResults(logResults);
		setRet = true;
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
	public static void setSaturation(boolean configSaturation) {
		configValues.setSaturation(configSaturation);
		setRet = true;
	}

	/**
	 * Set spread player value
	 * 
	 * @param configSpread	value used to set spread player
	 */
	public static void setSpreadplayers(boolean configSpread) {
		configValues.setSpread(configSpread);
		setRet = true;
	}

	/**
	 * Set reward value
	 * 
	 * @param configRewards	value used to set reward
	 */
	public static void setRewards(boolean configRewards) {
		configValues.setRewards(configRewards);
		setRet = true;
	}

	/**
	 * Set skip value
	 * 
	 * @param configSkip	value used to set skip
	 */
	public static void setSkip(boolean configSkip) {
		configValues.setSkip(configSkip);
		setRet = true;
	}

	/**
	 * Set craft value
	 * 
	 * @param configCrafts	value used to set craft
	 */
	public static void setCrafts(boolean configCrafts) {
		configValues.setCrafts(configCrafts);
		setRet = true;
	}

	/**
	 * Set team value
	 * 
	 * @param configTeam	value used to set team
	 */
	public static void setTeam(boolean configTeam) {
		if (PartyTmp.getInstance().checkConfigUpdatability()) {
			error = "Cannot set Team while game is running !";
			setRet = false;
		} else {
			configValues.setTeam(configTeam);
			setRet = true;			
		}
	}

	/**
	 * Set Coop value
	 *
	 * @param configCoop	value used to set coop
	 */
	public static void setCoop(boolean configCoop) {
		if (PartyTmp.getInstance().checkConfigUpdatability()) {
			error = "Cannot set Coop while game is running !";
			setRet = false;
		} else {
			configValues.setCoop(configCoop);
			setRet = true;
		}
	}

	/**
	 * Set Coop skip value
	 *
	 * @param configCoop	value used to set coop skip
	 */
	public static void setCoopSkip(boolean configCoop) {
		if (PartyTmp.getInstance().checkConfigUpdatability()) {
			error = "Cannot set Coop Skip while game is running !";
			setRet = false;
		} else {
			configValues.setCoopSkip(configCoop);
			setRet = true;
		}
	}
	
	/**
	 * Set team inv value
	 * 
	 * @param configTeamInv	value used to set team inv
	 */
	public static void setTeamInv(boolean configTeamInv) {
		if (PartyTmp.getInstance().checkConfigUpdatability()) {
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
	public static void setSame(boolean configSame) {
		if (PartyTmp.getInstance().checkConfigUpdatability()) {
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
	public static void setDoubleMode(boolean configDuoMode) {
		configValues.setDuoMode(configDuoMode);
		setRet = true;
	}
	
	/**
	 * Set sbtt value
	 * 
	 * @param configSbttMode	value used to set sbtt
	 */
	public static void setSbttMode(boolean configSbttMode) {
		configValues.setSbttMode(configSbttMode);
		setRet = true;
	}
	
	/**
	 * Set print tab value
	 * 
	 * @param configPrintTab	value used to set printTab
	 */
	public static void setPrintTab(boolean configPrintTab) {
		configValues.setPrintTab(configPrintTab);
		setRet = true;
	}
	
	/**
	 * Set end one value
	 * 
	 * @param configEndOne	value used to set end one
	 */
	public static void setEndOne(boolean configEndOne) {
		configValues.setEndOne(configEndOne);
		setRet = true;
	}
	
	/**
	 * Set passive All value
	 * 
	 * @param configPassive	value used to set passive all
	 */
	public static void setPassiveAll(boolean configPassive) {
		configValues.setPassiveAll(configPassive);
		setRet = true;
	}

	/**
	 * Set passive Team value
	 * 
	 * @param configPassive	value used to set passive team
	 */
	public static void setPassiveTeam(boolean configPassive) {
		configValues.setPassiveTeam(configPassive);
		setRet = true;
	}
	
	/**
	 * Set team size value
	 * 
	 * @param configTeamSize	value used to set team size
	 */
	public static void setTeamSize(int configTeamSize) {
		if (PartyTmp.getInstance().checkConfigUpdatability()) {
 			error = "Cannot change team size when game is running !";
			setRet = false;
		}
 		
 		if (setRet && configTeamSize <= 1) {
 			error = "Cannot set team size under 1 !";
 			setRet = false;
 		}
 		
		if (setRet && configValues.isTeam() && !TeamManager.getInstance().getTeams().isEmpty() && TeamManager.getInstance().getMaxTeamSize() > configTeamSize) {
			error = "Cannot set team size less than a current min team size !";
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
	public static void setTeamInvSize(int configTeamIntSize) {
		if (PartyTmp.getInstance().checkConfigUpdatability()) {
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
	 * Set Coop base time (in mins)
	 *
	 * @param configCoopBase value used to set coop base time
	 */
	public static void setCoopBase(int configCoopBase) {
		if (PartyTmp.getInstance().checkConfigUpdatability()) {
			error = "Cannot change coop base when game is running !";
			setRet = false;
		}

		if (setRet && configCoopBase < 1) {
			error = "Cannot set coop base time below 1!";
			setRet = false;
		}

		if (setRet) {
			configValues.setCoopBase(configCoopBase);
			setRet = true;
		}
	}

	/**
	 * Set Coop updating time (in mins)
	 *
	 * @param configCoopUpdate value used to set coop updating time
	 */
	public static void setCoopUpdated(int configCoopUpdate) {
		if (PartyTmp.getInstance().checkConfigUpdatability()) {
			error = "Cannot change coop updating when game is running !";
			setRet = false;
		}

		if (setRet && configCoopUpdate < 1) {
			error = "Cannot set coop updating time below 1!";
			setRet = false;
		}

		if (setRet) {
			configValues.setCoopUpdate(configCoopUpdate);
			setRet = true;
		}
	}

 	/**
	 * Set spread distance value
	 * 
	 * @param configSpreadDistance	value used to set spread distance
	 */
	public static void setSpreadDistance(int configSpreadDistance) {
		if (configSpreadDistance < 100) {
			setRet = false;
			error = "Cannot set Spread distance less than 100";
		} else if (configSpreadDistance > configValues.getSpreadRadius()) {
			setRet = false;
			error = "Cannot set Spread distance more than Spread Radius";
		} else {
			configValues.setSpreadDistance(configSpreadDistance);
			setRet = true;
		}
	}

	/**
	 * Set spread radius value
	 * 
	 * @param configSpreadRadius	value used to set spread radius
	 */
	public static void setSpreadRadius(int configSpreadRadius) {
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
	public static void setTargetAge(int configTargetPerAge) {
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
	public static void setStartTime(int configStartTime) {
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
	public static void setAddedTime(int configAddedTime) {
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
	public static void setSbttAmount(int configSbttAmount) {
		if (configSbttAmount < 1 || configSbttAmount > 9) {
			error = "Cannot set out of 1 to 9 range !";
			setRet = false;
		} else {
			configValues.setSbttAmount(configSbttAmount);
			setRet = true;
		}
	}
	
	/**
	 * Set xp end value
	 * 
	 * @param configXpEnd	value used to set xp end
	 */
	public static void setXpEnd(int configXpEnd) {
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
	public static void setXpOverworld(int configXpOverworld) {
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
	public static void setXpCoral(int configXpCoral) {
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
	public static void setLastAge(String configLastAge) {
		if (PartyTmp.getInstance().checkConfigUpdatability()) {
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
	public static void setFirstSkip(String configSkipAge) {
		if (PartyTmp.getInstance().checkConfigUpdatability()) {
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
	public static void setLevel(String configLevel) {
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
	public static void setLang(String configLang) {
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
