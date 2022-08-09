 package main.fr.kosmosuniverse.kuffle.core;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.json.simple.JSONObject;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleConfigException;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class Config {
	private static final String CONFIG_DEFAULT = "CONFIG_DEFAULT";
	
	private ConfigHolder configValues;
	private boolean setRet;
	private String error;

	private Map<String, Consumer<Object>> configElems = null;

	/**
	 * Constructor
	 * 
	 * @param configFile	configuration file used to setup config values
	 */
	public Config(FileConfiguration configFile) {
		configValues = new ConfigHolder();
		configElems = new HashMap<>();

		configElems.put("SATURATION", (Object b) -> setSaturation((Boolean) b));
		configElems.put("SPREADPLAYERS", (Object b) -> setSpreadplayers((Boolean) b));
		configElems.put("REWARDS", (Object b) -> setRewards((Boolean) b));
		configElems.put("SKIP", (Object b) -> setSkip((Boolean) b));
		configElems.put("CUSTOM_CRAFTS", (Object b) -> setCrafts((Boolean) b));
		configElems.put("TEAM", (Object b) -> setTeam((Boolean) b));
		configElems.put("SAME_MODE", (Object b) -> setSame((Boolean) b));
		configElems.put("DOUBLE_MODE", (Object b) -> setDoubleMode((Boolean) b));
		configElems.put("SBTT_MODE", (Object b) -> setSbttMode((Boolean) b));
		configElems.put("AUTO_DETECT_END", (Object b) -> setGameEnd((Boolean) b));
		configElems.put("END_WHEN_ONE", (Object b) -> setEndOne((Boolean) b));
		configElems.put("PASSIVE", (Object b) -> setPassive((Boolean) b));
		configElems.put("SPREAD_MIN_DISTANCE", (Object i) -> setSpreadDistance((Integer) i));
		configElems.put("SPREAD_MIN_RADIUS", (Object i) -> setSpreadRadius((Integer) i));
		configElems.put("TARGET_PER_AGE", (Object i) -> setItemAge((Integer) i));
		configElems.put("START_DURATION", (Object i) -> setStartTime((Integer) i));
		configElems.put("ADDED_DURATION", (Object i) -> setAddedTime((Integer) i));
		configElems.put("TEAMSIZE", (Object i) -> setTeamSize((Integer) i));
		configElems.put("SBTT_AMOUNT", (Object i) -> setSbttAmount((Integer) i));
		configElems.put("XP_END_TELEPORTER", (Object i) -> setXpEnd((Integer) i));
		configElems.put("XP_OVERWORLD_TELEPORTER", (Object i) -> setXpOverworld((Integer) i));
		configElems.put("XP_CORAL_COMPASS", (Object i) -> setXpCoral((Integer) i));
		configElems.put("LAST_AGE", (Object i) -> setLastAge((String) i));
		configElems.put("FIRST_AGE_SKIP", (Object i) -> setFirstSkip((String) i));
		configElems.put("LEVEL", (Object i) -> setLevel((String) i));
		configElems.put("LANG", (Object i) -> setLang((String) i));
		
		setupConfig(configFile);
	}
	
	/**
	 * Searches is a specific key exists in configElems
	 * 
	 * @param key	The key to search for
	 * 
	 * @return True if key exists, False instead
	 */
	public boolean hasKey(String key) {
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
	public void setElem(String key, Object elem) throws KuffleConfigException {
		setRet = false;
		error = "";

		try {
			configElems.get(key).accept(elem);
		} catch (Exception e) {
			setRet = false;
			error = "Invalid Parameter type for this config element !";
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
	private void setupConfig(FileConfiguration configFile) {
		if (!configFile.contains("game_settings.lang")
				|| !KuffleMain.langs.contains(configFile.getString("game_settings.lang"))) {
			configValues.lang = "en";
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "lang"));
			configFile.set("game_settings.lang", "en");
		} else {
			configValues.lang = configFile.getString("game_settings.lang");
		}
		
		checkFileSpread(configFile);
		checkFileModes(configFile);
		checkFileStart(configFile);
		checkFileOther(configFile);
		checkFileEnd(configFile);
		
		setValues(configFile);
	}
	
	/**
	 * Check spread values in config file to ensure they exists and are conform
	 * 
	 * @param configFile	configuration file used to setup config values
	 */
	private void checkFileSpread(FileConfiguration configFile) {
		if (!configFile.contains("game_settings.spreadplayers.enable")) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "enabling spreadplayers"));
			configFile.set("game_settings.spreadplayers.enable", false);
		}
		
		if (!configFile.contains("game_settings.spreadplayers.minimum_distance")
				|| configFile.getInt("game_settings.spreadplayers.minimum_distance") < 1) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "spreadplayers minimum distance"));
			configFile.set("game_settings.spreadplayers.minimum_distance", 500);
		}

		if (!configFile.contains("game_settings.spreadplayers.minimum_radius")
				|| configFile.getInt("game_settings.spreadplayers.minimum_radius") < configFile
						.getInt("game_settings.spreadplayers.minimum_distance")) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "spreadplayers minimum radius"));
			configFile.set("game_settings.spreadplayers.minimum_radius", 1000);
		}
	}
	
	/**
	 * Check modes values in config file to ensure they exists and are conform
	 * 
	 * @param configFile	configuration file used to setup config values
	 */
	private void checkFileModes(FileConfiguration configFile) {
		if (!configFile.contains("game_settings.team.enable")) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "enabling team"));
			configFile.set("game_settings.team.enable", false);
		}

		if (!configFile.contains("game_settings.team.size") || configFile.getInt("game_settings.team.size") < 2
				|| configFile.getInt("game_settings.team.size") > 10) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "max team size"));
			configFile.set("game_settings.team.size", 2);
		}
		
		if (!configFile.contains("game_settings.same_mode")) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "enabling same mode"));
			configFile.set("game_settings.same_mode", false);
		}
		
		if (!configFile.contains("game_settings.sbtt_mode.enable")) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "SBTT mode"));
			configFile.set("game_settings.sbtt_mode.enable", false);
		}
		
		if (!configFile.contains("game_settings.sbtt_mode.amount") ||
				configFile.getInt("game_settings.sbtt_mode.amount") < 1 ||
				configFile.getInt("game_settings.sbtt_mode.amount") > 9) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "SBTT item amount"));
			configFile.set("game_settings.sbtt_mode.amount", 4);
		}
		
		if (!configFile.contains("game_settings.double_mode")) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "Double mode"));
			configFile.set("game_settings.double_mode", false);
		}
		
		if (!configFile.contains("game_settings.passive")) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "Passive mode"));
			configFile.set("game_settings.passive", false);
		}
	}
	
	/**
	 * Check basic values in config file to ensure they exists and are conform
	 * 
	 * @param configFile	configuration file used to setup config values
	 */
	private void checkFileStart(FileConfiguration configFile) {
		if (!configFile.contains("game_settings.target_per_age")
				|| configFile.getInt("game_settings.target_per_age") < 1) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "item per age"));
			configFile.set("game_settings.target_per_age", 5);
		}
		
		if (!configFile.contains("game_settings.start_time") || configFile.getInt("game_settings.start_time") < 1) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "start time"));
			configFile.set("game_settings.start_time", 4);
		}

		if (!configFile.contains("game_settings.time_added") || configFile.getInt("game_settings.time_added") < 1) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "time added"));
			configFile.set("game_settings.time_added", 2);
		}

		if (!configFile.contains("game_settings.last_age") || configFile.getInt("game_settings.last_age") < 1 || configFile.getInt("game_settings.last_age") > AgeManager.getLastAgeIndex()) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "max ages"));
			configFile.set("game_settings.last_age", AgeManager.getLastAgeIndex());
		}

		if (!configFile.contains("game_settings.level") || configFile.getInt("game_settings.level") < 0
				|| configFile.getInt("game_settings.level") > 3) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "level"));
			configFile.set("game_settings.level", 1);
		}
	}
	
	/**
	 * Check other values in config file to ensure they exists and are conform
	 * 
	 * @param configFile	configuration file used to setup config values
	 */
	private void checkFileOther(FileConfiguration configFile) {
		if (!configFile.contains("game_settings.skip.enable")) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "enabling skip"));
			configFile.set("game_settings.skip.enable", true);
		}

		if (!configFile.contains("game_settings.skip.age") || configFile.getInt("game_settings.skip.age") < 1) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "skip age"));
			configFile.set("game_settings.skip.age", 2);
		}

		if (!configFile.contains("game_settings.custom_crafts")) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "enabling custom crafts"));
			configFile.set("game_settings.custom_crafts", true);
		}
		
		if (!configFile.contains("game_settings.xp_max.end_teleporter")) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "xp max EndTeleporter"));
			configFile.set("game_settings.xp_max.end_teleporter", 5);
		}
		
		if (!configFile.contains("game_settings.xp_max.overworld_teleporter")) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "xp max OverworldTeleporter"));
			configFile.set("game_settings.xp_max.overworld_teleporter", 10);
		}
		
		if (!configFile.contains("game_settings.xp_max.coral_compass")) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "xp max CoralCompass"));
			configFile.set("game_settings.xp_max.coral_compass", 20);
		}
	}
	
	/**
	 * Check end values in config file to ensure they exists and are conform
	 * 
	 * @param configFile	configuration file used to setup config values
	 */
	private void checkFileEnd(FileConfiguration configFile) {
		if (!configFile.contains("game_settings.auto_detect_game_end.enable")) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "enabling auto detect game end"));
			configFile.set("game_settings.auto_detect_game_end.enable", false);
		}
		
		configValues.gameEnd = configFile.getBoolean("game_settings.auto_detect_game_end.enable");
		
		if (!configFile.contains("game_settings.auto_detect_game_end.end_when_one") ||
				(!configValues.gameEnd && configFile.getBoolean("game_settings.auto_detect_game_end.end_when_one"))) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "game end when one"));
			configFile.set("game_settings.auto_detect_game_end.end_when_one", false);
		}
	}

	/**
	 * Setup config values from config file
	 * 
	 * @param configFile	file that contains all config values
	 */
	private void setValues(FileConfiguration configFile) {
		configValues.saturation = configFile.getBoolean("game_settings.saturation");
		configValues.spread = configFile.getBoolean("game_settings.spreadplayers.enable");
		configValues.rewards = configFile.getBoolean("game_settings.rewards");
		configValues.skip = configFile.getBoolean("game_settings.skip.enable");
		configValues.crafts = configFile.getBoolean("game_settings.custom_crafts");
		configValues.team = configFile.getBoolean("game_settings.team.enable");
		configValues.same = configFile.getBoolean("game_settings.same_mode");
		configValues.endOne = configFile.getBoolean("game_settings.auto_detect_game_end.end_when_one");
		configValues.duoMode = configFile.getBoolean("game_settings.double_mode");
		configValues.sbttMode = configFile.getBoolean("game_settings.sbtt_mode.enable");
		configValues.passive = configFile.getBoolean("game_settings.passive");
		
		configValues.spreadDistance = configFile.getInt("game_settings.spreadplayers.minimum_distance");
		configValues.spreadRadius = configFile.getInt("game_settings.spreadplayers.minimum_radius");
		configValues.targetPerAge = configFile.getInt("game_settings.target_per_age");
		configValues.skipAge = configFile.getInt("game_settings.skip.age");
		configValues.lastAge = configFile.getInt("game_settings.last_age");
		configValues.startTime = configFile.getInt("game_settings.start_time");
		configValues.addedTime = configFile.getInt("game_settings.time_added");
		configValues.level = configFile.getInt("game_settings.level");
		configValues.teamSize = configFile.getInt("game_settings.team.size");
		configValues.sbttAmount = configFile.getInt("game_settings.sbtt_mode.amount");
		configValues.xpEnd = configFile.getInt("game_settings.xp_max.end_teleporter");
		configValues.xpOverworld = configFile.getInt("game_settings.xp_max.overworld_teleporter");
		configValues.xpCoral = configFile.getInt("game_settings.xp_max.coral_compass");
	}
	
	/**
	 * Construct a String from config values to be display
	 * 
	 * @return the string
	 */
	public String displayConfig() {
		StringBuilder sb = new StringBuilder();

		sb.append("Configuration:").append("\n");
		sb.append("  " + ChatColor.BLUE).append("Saturation: " + ChatColor.GOLD).append(configValues.saturation).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Spreadplayers: " + ChatColor.GOLD).append(configValues.spread).append("\n");
		sb.append("  " + ChatColor.BLUE).append("  - Spreadplayer min distance: " + ChatColor.GOLD).append(configValues.spreadDistance).append("\n");
		sb.append("  " + ChatColor.BLUE).append("  - Spreadplayer min radius: " + ChatColor.GOLD).append(configValues.spreadRadius).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Rewards: " + ChatColor.GOLD).append(configValues.rewards).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Skip: " + ChatColor.GOLD).append(configValues.skip).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Crafts: " + ChatColor.GOLD).append(configValues.crafts).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Nb item per age: " + ChatColor.GOLD).append(configValues.targetPerAge).append("\n");
		sb.append("  " + ChatColor.BLUE).append("First Age for Skipping: " + ChatColor.GOLD).append(configValues.skipAge).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Max age: " + ChatColor.GOLD).append(configValues.lastAge).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Start duration: " + ChatColor.GOLD).append(configValues.startTime).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Added duration: " + ChatColor.GOLD).append(configValues.addedTime).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Lang: " + ChatColor.GOLD).append(configValues.lang).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Level: " + ChatColor.GOLD).append(LevelManager.getLevelByNumber(configValues.level).name).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Detect Game End: " + ChatColor.GOLD).append(configValues.gameEnd).append("\n");
		sb.append("  " + ChatColor.BLUE).append("End game at one: " + ChatColor.GOLD).append(configValues.endOne).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Passive: " + ChatColor.GOLD).append(configValues.passive).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Team: " + ChatColor.GOLD).append(configValues.team).append("\n");
		sb.append("  " + ChatColor.BLUE).append("  - Team Size: " + ChatColor.GOLD).append(configValues.teamSize).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Same mode: " + ChatColor.GOLD).append(configValues.same).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Double mode: " + ChatColor.GOLD).append(configValues.duoMode).append("\n");
		sb.append("  " + ChatColor.BLUE).append("SBTT mode: " + ChatColor.GOLD).append(configValues.sbttMode).append("\n");
		sb.append("  " + ChatColor.BLUE).append("  - SBTT amount: " + ChatColor.GOLD).append(configValues.sbttAmount).append("\n");
		sb.append("  " + ChatColor.BLUE).append("XP Max: ").append("\n");
		sb.append("  " + ChatColor.BLUE).append("  - EndTeleporter: " + ChatColor.GOLD).append(configValues.xpEnd).append("\n");
		sb.append("  " + ChatColor.BLUE).append("  - OverworldTeleporter: " + ChatColor.GOLD).append(configValues.xpOverworld).append("\n");
		sb.append("  " + ChatColor.BLUE).append("  - CoralCompass: " + ChatColor.GOLD).append(configValues.xpCoral).append("\n");

		return sb.toString();
	}
	
	/**
	 * Clears the configElems map
	 */
	public void clear() {
		configElems.clear();
	}
	
	/**
	 * Creates a JSONObject to save config values
	 * 
	 * @return the JSONObject
	 */
	@SuppressWarnings("unchecked")
	public JSONObject saveConfig() {
		JSONObject configObj = new JSONObject();
		
		configObj.put("saturation", configValues.saturation);
		configObj.put("spread", configValues.spread);
		configObj.put("rewards", configValues.rewards);
		configObj.put("skip", configValues.skip);
		configObj.put("crafts", configValues.crafts);
		configObj.put("team", configValues.team);
		configObj.put("same", configValues.same);
		configObj.put("duoMode", configValues.duoMode);
		configObj.put("sbttMode", configValues.sbttMode);
		configObj.put("gameEnd", configValues.gameEnd);
		configObj.put("endOne", configValues.endOne);
		configObj.put("sbttAmount", configValues.sbttAmount);
		configObj.put("teamSize", configValues.teamSize);
		configObj.put("spreadMin", configValues.spreadDistance);
		configObj.put("spreadMax", configValues.spreadRadius);
		configObj.put("itemPerAge", configValues.targetPerAge);
		configObj.put("skipAge", configValues.skipAge);
		configObj.put("maxAges", configValues.lastAge);
		configObj.put("startTime", configValues.startTime);
		configObj.put("addedTime", configValues.addedTime);
		configObj.put("level", configValues.level);
		configObj.put("lang", configValues.lang);
		configObj.put("xpEnd", configValues.xpEnd);
		configObj.put("xpOverworld", configValues.xpOverworld);
		configObj.put("xpCoral", configValues.xpCoral);
		
		return configObj;
	}
	
	/**
	 * Setup config values from JSONObject
	 * 
	 * @param configObj	JSONObject that contains config to load
	 */
	public void loadConfig(JSONObject configObj) {
		configValues.saturation = (boolean) configObj.get("saturation");
		configValues.spread = (boolean) configObj.get("spread");
		configValues.rewards = (boolean) configObj.get("rewards");
		configValues.skip = (boolean) configObj.get("skip");
		configValues.crafts = (boolean) configObj.get("crafts");
		configValues.team = (boolean) configObj.get("team");
		configValues.same = (boolean) configObj.get("same");
		configValues.duoMode = (boolean) configObj.get("duoMode");
		configValues.sbttMode = (boolean) configObj.get("sbttMode");
		configValues.gameEnd = (boolean) configObj.get("gameEnd");
		configValues.endOne = (boolean) configObj.get("endOne");
		
		configValues.sbttAmount = Integer.parseInt(configObj.get("sbttAmount").toString());
		configValues.teamSize = Integer.parseInt(configObj.get("teamSize").toString());
		configValues.spreadDistance = Integer.parseInt(configObj.get("spreadMin").toString());
		configValues.spreadRadius = Integer.parseInt(configObj.get("spreadMax").toString());
		configValues.targetPerAge = Integer.parseInt(configObj.get("itemPerAge").toString());
		configValues.skipAge = Integer.parseInt(configObj.get("skipAge").toString());
		configValues.lastAge = Integer.parseInt(configObj.get("maxAges").toString());
		configValues.startTime = Integer.parseInt(configObj.get("startTime").toString());
		configValues.addedTime = Integer.parseInt(configObj.get("addedTime").toString());
		configValues.level = Integer.parseInt(configObj.get("level").toString());
		configValues.xpEnd = Integer.parseInt(configObj.get("xpEnd").toString());
		configValues.xpOverworld = Integer.parseInt(configObj.get("xpOverworld").toString());
		configValues.xpCoral = Integer.parseInt(configObj.get("xpCoral").toString());
		
		configValues.lang = configObj.get("lang").toString();
	}

	/**
	 * Get saturation enable value
	 * 
	 * @return if saturation is enabled
	 */
	public boolean getSaturation() {
		return configValues.saturation;
	}

	/**
	 * Get spread enable value
	 * 
	 * @return if spread is enabled
	 */
	public boolean getSpread() {
		return configValues.spread;
	}

	/**
	 * Get rewards enable value
	 * 
	 * @return if rewards are enabled
	 */
	public boolean getRewards() {
		return configValues.rewards;
	}

	/**
	 * Get skip enable value
	 * 
	 * @return if skip is enabled
	 */
	public boolean getSkip() {
		return configValues.skip;
	}

	/**
	 * Get craft enable value
	 * 
	 * @return if craft are enabled
	 */
	public boolean getCrafts() {
		return configValues.crafts;
	}

	/**
	 * Get team enable value
	 * 
	 * @return if team mode is enabled
	 */
	public boolean getTeam() {
		return configValues.team;
	}
	
	/**
	 * Get same enable value
	 * 
	 * @return if same mode is enabled
	 */
	public boolean getSame() {
		return configValues.same;
	}
	
	/**
	 * Get double enable value
	 * 
	 * @return if double mode is enabled
	 */
	public boolean getDouble() {
		return configValues.duoMode;
	}
	
	/**
	 * Get sbtt enable value
	 * 
	 * @return if sbtt mode is enabled
	 */
	public boolean getSBTT() {
		return configValues.sbttMode;
	}
	
	/**
	 * Get game end enable value
	 * 
	 * @return if game end is enabled
	 */
	public boolean getGameEnd() {
		return configValues.gameEnd;
	}
	
	/**
	 * Get end one enable value
	 * 
	 * @return if end one is enabled
	 */
	public boolean getEndOne() {
		return configValues.endOne;
	}
	
	/**
	 * Get passive enable value
	 * 
	 * @return if passive is enabled
	 */
	public boolean getPassive() {
		return configValues.passive;
	}

	/**
	 * Get team size value
	 * 
	 * @return the team size
	 */
	public int getTeamSize() {
		return configValues.teamSize;
	}

	/**
	 * Get target per age value
	 * 
	 * @return the amount of target per age
	 */
	public int getTargetPerAge() {
		return configValues.targetPerAge;
	}

	/**
	 * Get start time value
	 * 
	 * @return the start time
	 */
	public int getStartTime() {
		return configValues.startTime;
	}

	/**
	 * Get added time value
	 * 
	 * @return the added time
	 */
	public int getAddedTime() {
		return configValues.addedTime;
	}

	/**
	 * Get spread distance value
	 * 
	 * @return the spread distance
	 */
	public int getSpreadDistance() {
		return configValues.spreadDistance;
	}

	/**
	 * Get spread radius value
	 * 
	 * @return the spread radius
	 */
	public int getSpreadRadius() {
		return configValues.spreadRadius;
	}
	
	/**
	 * Get sbtt amount value
	 * 
	 * @return the sbtt amount
	 */
	public int getSBTTAmount() {
		return configValues.sbttAmount;
	}
	
	/**
	 * Get xp end value
	 * 
	 * @return the end teleporter xp amount
	 */
	public int getXpEnd() {
		return configValues.xpEnd;
	}
	
	/**
	 * Get xp overworld value
	 * 
	 * @return the overworld xp amount
	 */
	public int getXpOverworld() {
		return configValues.xpOverworld;
	}
	
	/**
	 * Get xp coral value
	 * 
	 * @return the coral xp amount
	 */
	public int getXpCoral() {
		return configValues.xpCoral;
	}
	
	/**
	 * Get the first skip age value
	 * 
	 * @return the first skip age number
	 */
	public Age getSkipAge() {
		return AgeManager.getAgeByNumber(configValues.skipAge);
	}

	/**
	 * Get last age value
	 * 
	 * @return the last age number
	 */
	public Age getLastAge() {
		return AgeManager.getAgeByNumber(configValues.lastAge);
	}

	/**
	 * Get Level value
	 * 
	 * @return the level
	 */
	public Level getLevel() {
		return LevelManager.getLevelByNumber(configValues.level);
	}

	/**
	 * Get lang value
	 * 
	 * @return the lang
	 */
	public String getLang() {
		return configValues.lang;
	}

	/**
	 * Set saturation value
	 * 
	 * @param configSaturation	value used to set saturation
	 */
	public void setSaturation(boolean configSaturation) {
		configValues.saturation = configSaturation;
		setRet = true;
	}

	/**
	 * Set spread player value
	 * 
	 * @param configSpread	value used to set spread player
	 */
	public void setSpreadplayers(boolean configSpread) {
		configValues.spread = configSpread;
		setRet = true;
	}

	/**
	 * Set reward value
	 * 
	 * @param configRewards	value used to set reward
	 */
	public void setRewards(boolean configRewards) {
		configValues.rewards = configRewards;
		setRet = true;
	}

	/**
	 * Set skip value
	 * 
	 * @param configSkip	value used to set skip
	 */
	public void setSkip(boolean configSkip) {
		configValues.skip = configSkip;
		setRet = true;
	}

	/**
	 * Set craft value
	 * 
	 * @param configCrafts	value used to set craft
	 */
	public void setCrafts(boolean configCrafts) {
		configValues.crafts = configCrafts;
		setRet = true;
	}

	/**
	 * Set team value
	 * 
	 * @param configTeam	value used to set team
	 */
	public void setTeam(boolean configTeam) {
		if (KuffleMain.gameStarted) {
			error = "Game is already running !";
			setRet = false;
		} else {
			configValues.team = configTeam;
			setRet = true;			
		}
	}
	
	/**
	 * Set same value
	 * 
	 * @param configSame	value used to set same
	 */
	public void setSame(boolean configSame) {
		if (KuffleMain.gameStarted) {
			error = "Game is already running !";
			setRet = false;
		} else {		
			configValues.same = configSame;
			setRet = true;
		}
	}
	
	/**
	 * Set double value
	 * 
	 * @param configDuoMode	value used to set double mode
	 */
	public void setDoubleMode(boolean configDuoMode) {
		configValues.duoMode = configDuoMode;
		setRet = true;
	}
	
	/**
	 * Set sbtt value
	 * 
	 * @param configSbttMode	value used to set sbtt
	 */
	public void setSbttMode(boolean configSbttMode) {
		configValues.sbttMode = configSbttMode;
		setRet = true;
	}
	
	/**
	 * Set game end value
	 * 
	 * @param configGameEnd	value used to set game end
	 */
	public void setGameEnd(boolean configGameEnd) {
		configValues.gameEnd = configGameEnd;
		
		if (!configValues.gameEnd) {
			configValues.endOne = false;
		}
		
		setRet = true;
	}
	
	/**
	 * Set end one value
	 * 
	 * @param configEndOne	value used to set end one
	 */
	public void setEndOne(boolean configEndOne) {
		if (!configValues.gameEnd) {
			setRet = false;
		} else {
			configValues.endOne = configEndOne;
			setRet = true;
		}
	}
	
	/**
	 * Set passive value
	 * 
	 * @param configPassive	value used to set passive
	 */
	public void setPassive(boolean configPassive) {
		configValues.passive = configPassive;
		setRet = true;
	}

	/**
	 * Set team size value
	 * 
	 * @param configTeamSize	value used to set team size
	 */
 	public void setTeamSize(int configTeamSize) {
 		if (KuffleMain.gameStarted) {
 			error = "Game is already running !";
			setRet = false;
		}
 		
 		if (setRet && configTeamSize < 1) {
 			error = "Cannot set team size under 1 !";
 			setRet = false;
 		}
 		
		if (setRet && configValues.team && KuffleMain.teams.getTeams().size() > 0 && KuffleMain.teams.getMaxTeamSize() > configTeamSize) {
			error = "Cannot set team size less than a current team size !";
			setRet = false;
		}

		if (setRet) {
			configValues.teamSize = configTeamSize;
			setRet = true;
		}
	}

 	/**
	 * Set spread distance value
	 * 
	 * @param configSpreadDistance	value used to set spread distance
	 */
	public void setSpreadDistance(int configSpreadDistance) {
		configValues.spreadDistance = configSpreadDistance;
		setRet = true;
	}

	/**
	 * Set spread radius value
	 * 
	 * @param configSpreadRadius	value used to set spread radius
	 */
	public void setSpreadRadius(int configSpreadRadius) {
		if (configSpreadRadius < configValues.spreadDistance) {
			error = "Cannot set spread radius less than spread distance !";
			setRet = false;
		} else {		
			configValues.spreadRadius = configSpreadRadius;
			setRet = true;
		}
	}

	/**
	 * Set target per age value
	 * 
	 * @param configTargetPerAge	value used to set target per age
	 */
	public void setItemAge(int configTargetPerAge) {
		if (configTargetPerAge < 1) {
			error = "Cannot have less than one target per Age !";
			setRet = false;
		} else {
			configValues.targetPerAge = configTargetPerAge;
			setRet = true;
		}
	}

	/**
	 * Set start time value
	 * 
	 * @param configStartTime	value used to set start time
	 */
	public void setStartTime(int configStartTime) {
		if (configStartTime < 1) {
			error = "Cannot set added time less than 1";
			setRet = false;
		} else {
			configValues.startTime = configStartTime;
			setRet = true;
		}
	}

	/**
	 * Set added time value
	 * 
	 * @param configAddedTime	value used to set added time
	 */
	public void setAddedTime(int configAddedTime) {
		if (configAddedTime < 1) {
			error = "Cannot set added time less than 1";
			setRet = false;
		} else {
			configValues.addedTime = configAddedTime;
			setRet = true;
		}
	}
	
	/**
	 * Set sbtt amount value
	 * 
	 * @param configSbttAmount	value used to set sbtt amount
	 */
	public void setSbttAmount(int configSbttAmount) {
		if (configSbttAmount < 1 || configSbttAmount > 9) {
			error = "Cannot set out out of 1 to 9 range !";
			setRet = false;
		}
		
		configValues.sbttAmount = configSbttAmount;
		
		setRet = true;
	}
	
	/**
	 * Set xp end value
	 * 
	 * @param configXpEnd	value used to set xp end
	 */
	public void setXpEnd(int configXpEnd) {
		if (configXpEnd < 1 || configXpEnd > 10) {
			error = "Cannot set out of 1 to 10 range !";
			setRet = false;
		} else {
			configValues.xpEnd = configXpEnd;
			setRet = true;
		}
	}
	
	/**
	 * Set xp overworld value
	 * 
	 * @param configXpOverworld	value used to set xp overworld
	 */
	public void setXpOverworld(int configXpOverworld) {
		if (configXpOverworld < 1 || configXpOverworld > 20) {
			error = "Cannot set out of 1 to 20 range !";
			setRet = false;
		} else {
			configValues.xpOverworld = configXpOverworld;
			setRet = true;
		}
	}
	
	/**
	 * Set xp coral value
	 * 
	 * @param configXpCoral	value used to set xp coral
	 */
	public void setXpCoral(int configXpCoral) {
		if (configXpCoral < 1 || configXpCoral > 30) {
			error = "Cannot set out of 1 to 30 range !";
			setRet = false;
		} else {
			configValues.xpCoral = configXpCoral;
			setRet = true;
		}
	}
	
	/**
	 * Set last age value
	 * 
	 * @param configLastAge	value used to set last age
	 */
	public void setLastAge(String configLastAge) {
		if (!AgeManager.ageExists(configLastAge)) {
			error = "Unknown Age !";
			setRet = false;
		} else {
			configValues.lastAge = AgeManager.getAgeByName(configLastAge).number;
			setRet = true;
		}
	}
	
	/**
	 * Set skip age value
	 * 
	 * @param configSkipAge	value used to set skip age
	 */
	public void setFirstSkip(String configSkipAge) {
		if (!AgeManager.ageExists(configSkipAge)) {
			error = "Unknown Age !";
			setRet = false;
		} else if (AgeManager.getAgeByName(configSkipAge).number > configValues.lastAge) {
			error = "Cannot set the first age for skipping after the last age !";
			setRet = false;
		} else {
			configValues.skipAge = AgeManager.getAgeByName(configSkipAge).number;
			setRet = true;
		}
	}

	/**
	 * Set level value
	 * 
	 * @param configLevel	value used to set level
	 */
	public void setLevel(String configLevel) {
		if (!LevelManager.levelExists(configLevel)) {
			error = "Unknown level !";
			setRet = false;
		} else {
			configValues.level = LevelManager.getLevelByName(configLevel).number;
			setRet = true;
		}
	}

	/**
	 * Set lang value
	 * 
	 * @param configLang	value used to set lang
	 */
	public void setLang(String configLang) {
		if (!KuffleMain.langs.contains(configLang)) {
			error = "Unknown lang !";
			setRet = false;
		} else {
			configValues.lang = configLang;
			setRet = true;			
		}
	}
}
