package fr.kosmosuniverse.kuffle.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.kosmosuniverse.kuffle.KuffleMain;
import fr.kosmosuniverse.kuffle.core.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.json.JSONObject;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class FilesConformity {
	private static final String NBR_STR = "Number";
	
	/**
	 * Private FilesConformity constructor
	 * This class is utility class and must not be instantiated
	 * 
	 * @throws IllegalStateException if somebody try to instantiate
	 */
	private FilesConformity() {
		throw new IllegalStateException("Utility class");
	}
	
	/**
	 * Get a file content
	 * 
	 * @param file			The file of whom we will get the content
	 * 
	 * @return the file content as String, null if file is null
	 */
	public static String getContent(String file) {
		if (file.contains("%v")) {
			file = Utils.findFileExistVersion(file);
			
			if (file == null) {
				return null;
			}
		}
		
		String content = getFromFile(file);

		if (content == null || !checkContent(file, content)) {
			content = getFromResource(file);
			
			if (content != null)  {
				LogManager.getInstanceSystem().logMsg(KuffleMain.getInstance().getName(), LangManager.getMsgLang("FC_LOAD_RESOURCE", Config.getLang()).replace("%s", file));
			} else {
				LogManager.getInstanceSystem().logMsg(KuffleMain.getInstance().getName(), LangManager.getMsgLang("FC_LOAD_FAIL", Config.getLang()).replace("%s", file));
			}
		} else {
			LogManager.getInstanceSystem().logMsg(KuffleMain.getInstance().getName(), LangManager.getMsgLang("FC_LOAD_FILE", Config.getLang()).replace("%s", file));
		}
		
		return content;
	}
	
	/**
	 * Gets file content without checking it and without logging
	 * 
	 * @param file	The file from which it will get the content
	 * 
	 * @return the JSON file content as string
	 */
	public static String getRawContent(String file) {
		String content = getFromFile(file);
		
		if (content == null) {
			content = getFromResource(file);
		}
		
		return content;
	}
	
	/**
	 * Get content of a file that is in directory
	 * 
	 * @param file	The file of whom we will get the content
	 * 
	 * @return the file content as String, null if file does not exist and has been just created
	 */
	private static String getFromFile(String file) {
		if (fileExistsInPluginVersionDirectory(KuffleMain.getInstance().getDataFolder().getPath(), file)) {
			try (FileReader reader = new FileReader(KuffleMain.getInstance().getDataFolder().getPath() + File.separator + KuffleMain.getInstance().getDescription().getVersion() + File.separator  + file)) {
				return FileUtils.readJSONFileObject(file).toString();
			} catch (IOException e) {
				Utils.logException(e);
			}
		} else {
			createFromResource(file);
		}
		
		return null;
	}
	
	/**
	 * Create file in folder, with plugin version name, from Resource
	 * 
	 * @param fileName	The file name that will be created
	 */
	private static void createFromResource(String fileName) {
		String path = KuffleMain.getInstance().getDataFolder().getPath() + File.separator + KuffleMain.getInstance().getDescription().getVersion();
		directoryExistsOrCreate(path);		
		
		try (FileWriter writer = new FileWriter(path + File.separator + fileName)) {
			InputStream in = KuffleMain.getInstance().getResource(fileName);

			writer.write(Utils.readFileContent(in));
			Objects.requireNonNull(in).close();
		} catch (IOException e) {
			Utils.logException(e);
		}
	}
	
	/**
	 * Get content of a file that is in plugin resources
	 * 
	 * @param file	The file of whom we will get the content
	 * 
	 * @return the file content as String, null if IOException or ParseException was raised
	 */
	private static String getFromResource(String file) {
		String result = null;

		try (InputStream in = KuffleMain.getInstance().getResource(file)) {
			String content = Utils.readFileContent(in);
			JSONObject mainObject = FileUtils.readJSONObjectFromContent(content);
			result = mainObject.toString();
			mainObject.clear();
		} catch (IOException e) {
			Utils.logException(e);
		}
		
		return result;
	}
	
	/**
	 * Checks file conformity for every plugin file type
	 * 
	 * @param file		The file name to check
	 * @param content	The file content that will be checked
	 * 
	 * @return True if content is conform, False instead
	 */
	private static boolean checkContent(String file, String content) {
		boolean ret = !content.equals(getFromResource(file));

		if (ret && file.equals("ages.json")) {
			ret = ageConformity(content);
		} else if (ret && file.equals("msgs_langs.json")) {
			ret = langConformity(content);
		} else if (ret && file.equals("levels.json")) {
			ret = levelsConformity(content);
		} else if (ret && file.equals("targets.json")) {
			ret = targetsConformity(content);
		} else if (ret && file.equals("rewards.json")) {
			ret = rewardsConformity(content);
		}
		
		return ret;
	}
	
	/**
	 * Checks ages.json file conformity
	 * 
	 * @param content	file content as String
	 * 
	 * @return True if content is conform to awaited ages.json content, False instead
	 */
	public static boolean ageConformity(String content) {
		boolean ret = true;

		JSONObject jsonObj = FileUtils.readJSONObjectFromContent(content);

		for (String key : jsonObj.keySet()) {
			if (key.endsWith("_Age")) {
				JSONObject ageObj = (JSONObject) jsonObj.get(key);

				ret = checkAge(ageObj, key);
				ageObj.clear();
			} else if (!key.equals("Default")) {
				ret = false;
			}

			if (!ret) {
				break;
			}
		}

		jsonObj.clear();

		return ret;
	}
	
	/**
	 * Check an Age JSON object content
	 * 
	 * @param ageObj	The Age object to check
	 * @param age		The getInstance() Age name
	 * 
	 * @return True if ageObj is valid, False instead
	 */
	private static boolean checkAge(JSONObject ageObj, String age) {
		boolean ret = true;
		
		if (!ageObj.has(NBR_STR)) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang("FC_AGE_NUMBER", Config.getLang()).replace("%s", age));
			ret = false;
		} else if (!ageObj.has("TextColor")) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang("FC_AGE_TEXTCOLOR", Config.getLang()).replace("%s", age));
			ret = false;
		} else if (!ageObj.has("BoxColor")) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang("FC_AGE_BOXCOLOR", Config.getLang()).replace("%s", age));
			ret = false;
		}
		
		if (!ret) {
			return false;
		}

		ageObj.getInt(NBR_STR);
		String color = ageObj.getString("TextColor");
		String box = ageObj.getString("BoxColor") + "_SHULKER_BOX";

		try {
			ChatColor.valueOf(color);
		} catch (IllegalArgumentException e) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang("FC_AGE_TEXTCOLOR_ENUM", Config.getLang()).replace("%ss", color).replace("%s", age));
			ret = false;
		}

		if (ret && Material.matchMaterial(box) == null) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang("FC_AGE_BOXCOLOR_ENUM", Config.getLang()).replace("%ss", box).replace("%s", age));
			ret = false;
		}
		
		return ret;
	}

	
	/**
	 * Checks langs for a specified content and, if needed, checks if material exists.
	 * 
	 * @param content		file content as String
	 * @return True if content is conform to awaited lang file content, False instead
	 */
	public static boolean langConformity(String content) {
		JSONObject jsonObj = FileUtils.readJSONObjectFromContent(content);
		List<String> langs = null;
		boolean ret = true;
		
		for (String key : jsonObj.keySet()) {
			JSONObject langObj = (JSONObject) jsonObj.get(key);
			
			if (langs == null) {
				langs = new ArrayList<>(langObj.keySet());
			}
			
			ret = checkLangKey(langObj, langs);
			
			langObj.clear();
			
			if (!ret) {
				break;
			}
		}
		
		if (langs != null) {
			langs.clear();
		}
		
		jsonObj.clear();
		
		return ret;
	}
	
	/**
	 * Check if the langs for a specific key are good
	 * 
	 * @param langObj	The lang object to check
	 * @param langs		The allowed langs
	 * 
	 * @return True if langObj is conformed, False instead
	 */
	private static boolean checkLangKey(JSONObject langObj, List<String> langs) {
		boolean ret = true;
		
		for (String keyLang : langObj.keySet()) {
			if (!langs.contains(keyLang)) {
				LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang("FC_LANG_EVERYWHERE", Config.getLang()).replace("%s", keyLang));
				ret = false;
				break;
			}
		}
		
		return ret;
	}
	
	/**
	 * Check if the targets file content format is conform and usable for the plugin
	 * 
	 * @param content	The content to analyze
	 * 
	 * @return True if the content format is valid, False instead
	 */
	public static boolean targetsConformity(String content) {
		boolean ret = true;
		
		try {
			JSONObject mainObj = FileUtils.readJSONObjectFromContent(content);
			
			for (String mainKey : mainObj.keySet()) {
				if (!VersionManager.hasVersion(mainKey)) {
					LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang("FC_TARGET_VERSION", Config.getLang()).replace("%s", mainKey));
					ret = false;
				}
				
				if (ret) {
					JSONObject versionObj = mainObj.getJSONObject(mainKey);
					
					ret = checkTargetVersionObj(versionObj, mainKey);
					versionObj.clear();
				}
				
				if (!ret) {
					break;
				}
			}
			
			mainObj.clear();
		} catch(IllegalArgumentException e) {
			Utils.logException(e);
			ret = false;
		}
		
		return ret;
	}
	
	/**
	 * Checks the JSONObject that contains information about a specific version
	 * 
	 * @param versionObj	The JSONObject to check
	 * 
	 * @return True if JSONObject format is valid, False instead
	 */
	private static boolean checkTargetVersionObj(JSONObject versionObj, String version) {
		boolean ret = true;
		
		for (String versionKey : versionObj.keySet()) {
			if (!"BOTH".equalsIgnoreCase(versionKey) &&
					!"BLOCKS".equalsIgnoreCase(versionKey) &&
					!"ITEMS".equalsIgnoreCase(versionKey)) {
				LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang("FC_TARGET_TYPE", Config.getLang()).replace("%ss", version).replace("%s", versionKey));
				ret = false;
			}
			
			if (ret) {
				JSONObject typeObj = versionObj.getJSONObject(versionKey);
				
				ret = checkTargetTypeObj(typeObj, version, versionKey);
				typeObj.clear();
			}
			
			if (!ret) {
				break;
			}
		}
		
		return ret;
	}
	
	/**
	 * Checks the JSONObject that contains information about a specific type
	 * 
	 * @param typeObj	The JSONObject to check
	 * 
	 * @return True if JSONObject format is valid, False instead
	 */
	private static boolean checkTargetTypeObj(JSONObject typeObj, String version, String type) {
		boolean ret = true;
		
		for (String typeKey : typeObj.keySet()) {
			if (!AgeManager.ageExists(typeKey)) {
				LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang("FC_TARGET_AGE", Config.getLang()).replace("%sss", version).replace("%ss", type).replace("%s", typeKey));
				ret = false;
			}
			
			if (ret) {
				JSONObject ageObj = typeObj.getJSONObject(typeKey);
				
				ret = checkTargetAgeObj(ageObj, version);
				ageObj.clear();
			}
			
			if (!ret) {
				break;
			}
		}
		
		return ret;
	}
	
	/**
	 * Checks the JSONObject that contains information about a specific age
	 * 
	 * @param ageObj	The JSONObject to check
	 * 
	 * @return True if JSONObject format is valid, False instead
	 */
	private static boolean checkTargetAgeObj(JSONObject ageObj, String version) {
		boolean ret = true;
		
		for (String ageKey : ageObj.keySet()) {
			if (Material.matchMaterial(ageKey) == null && !ageKey.startsWith("*")) {
				ret = false;
				LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang("FC_TARGET_MATERIAL", Config.getLang()).replace("%ss", version).replace("%s", ageKey));
			}

			if (ret) {
				JSONObject materialObj = ageObj.getJSONObject(ageKey);
				
				ret = checkTargetMaterialObj(materialObj, version, ageKey);
				materialObj.clear();
			}
			
			if (!ret) {
				break;
			}
		}
		
		return ret;
	}
	
	/**
	 * Checks the JSONObject that contains information about a specific target
	 * 
	 * @param materialObj	The JSONObject to check
	 * 
	 * @return True if JSONObject format is valid, False instead
	 */
	private static boolean checkTargetMaterialObj(JSONObject materialObj, String version, String target) {
		boolean ret = true;
		
		if (!materialObj.has("Sbtt") ||
				!materialObj.has("Langs")) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang("FC_TARGET_MATERIAL_KEYS", Config.getLang()).replace("%ss", version).replace("%s", target));
			ret = false;
		} else {
			String sbtt = materialObj.get("Sbtt").toString();
			
			if (!"true".equalsIgnoreCase(sbtt) &&
					!"false".equalsIgnoreCase(sbtt)) {
				LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang("FC_TARGET_MATERIAL_SBTT", Config.getLang()).replace("%sss", sbtt).replace("%ss", version).replace("%s", target));
				ret = false;
			}
			
			JSONObject langObj = materialObj.getJSONObject("Langs");
			
			for (String langKey : langObj.keySet()) {
				if (!LangManager.hasLang(langKey)) {
					LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang("FC_TARGET_MATERIAL_LANG", Config.getLang()).replace("%sss", langKey).replace("%ss", version).replace("%s", target));
					ret = false;
					break;
				}
			}
			
			langObj.clear();
		}
		
		return ret;
	}

	
	/**
	 * Checks rewards_{version}.json file conformity
	 * 
	 * @param content	file content as String
	 * 
	 * @return True if content is conform to awaited rewards_{version}.json content, False instead
	 */
	public static boolean rewardsConformity(String content) {
		boolean ret = true;
		
		try {
			JSONObject mainObj = FileUtils.readJSONObjectFromContent(content);
			
			for (String mainKey : mainObj.keySet()) {
				if (!VersionManager.hasVersion(mainKey)) {
					ret = false;
				}
				
				if (ret) {
					JSONObject versionObj = mainObj.getJSONObject(mainKey);
	
					ret = checkRewardVersionObj(versionObj);
					versionObj.clear();
				}
				
				if (!ret) {
					break;
				}
			}
			
			mainObj.clear();
		} catch (IllegalArgumentException e) {
			Utils.logException(e);
			ret = false;
		}
		
		return ret;
	}
	
	/**
	 * Checks if version object contains valid age objects
	 * 
	 * @param versionObj	The version object to check
	 * 
	 * @return True if ages are all valid, False instead
	 */
	private static boolean checkRewardVersionObj(JSONObject versionObj) {
		boolean ret = true;
		
		for (String versionKey : versionObj.keySet()) {
			if (!AgeManager.ageExists(versionKey)) {
				ret = false;
			}
			
			if (ret) {
				JSONObject ageObj = versionObj.getJSONObject(versionKey);
				
				ret = checkRewardAgeObj(ageObj);
				ageObj.clear();
			}
			
			if (!ret) {
				break;
			}
		}
		
		return ret;
	}
	
	/**
	 * Checks if age object contains valid materials
	 * 
	 * @param ageObj	The age object to check
	 * 
	 * @return True if Materials are all valid, False instead
	 */
	private static boolean checkRewardAgeObj(JSONObject ageObj) {
		boolean ret = true;
		
		for (String ageKey : ageObj.keySet()) {
			if (Material.matchMaterial(ageKey) == null) {
				ret = false;
			}
			
			if (ret) {
				JSONObject materialObj = ageObj.getJSONObject(ageKey);
				
				try {
					ret = checkRewardMaterialObj(ageKey, materialObj);
				} catch (NumberFormatException e) {
					Utils.logException(e);
				}
				
				materialObj.clear();
			}
			
			if (!ret) {
				break;
			}
		}
		
		return ret;
	}
	
	/**
	 * Checks if a reward contains all needed keys and they are valid
	 * 
	 * @param material		The reward name
	 * @param materialObj	The reward object to check
	 * 
	 * @return True if reward contains all mandatory keys and values are conform, False instead
	 */
	private static boolean checkRewardMaterialObj(String material, JSONObject materialObj) throws NumberFormatException {
		boolean ret = true;
		
		if (!materialObj.has("Amount")) {
			return false;
		}
		
		@SuppressWarnings("unused")
		int amount = materialObj.getInt("Amount");
		
		if (materialObj.has("Level")) {
			@SuppressWarnings("unused")
			int level = materialObj.getInt("Level");
		}
		
		if (materialObj.has("Enchant")) {
			String enchants = materialObj.getString("Enchant");
			
			if (enchants.isEmpty() || !checkRewardEnchant(enchants, material)) {
				ret = false;
			}
		}
		
		if (materialObj.has("Effect")) {
			String effects = materialObj.getString("Effect");
			
			if (effects.isEmpty() || !checkRewardEffect(effects, material)) {
				ret = false;
			}
		}
		
		return ret;
	}
	
	/**
	 * Checks if reward enchant(s) exists
	 * 
	 * @param enchants	Enchant(s) to check
	 * @param reward	Reward to print in case of error
	 * 
	 * @return True if enchant(s) exists, False instead
	 */
	private static boolean checkRewardEnchant(String enchants, String reward) {
		boolean containKey = true;
		
		if (enchants.contains(",")) {
			for (String enchant : enchants.split(",")) {
				if (RewardManager.getEnchantment(enchant) == null) {
					LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang("FC_REWARD_ENCHANT", Config.getLang()).replace("%ss", enchant).replace("%s", reward));
					containKey = false;
					break;
				}
			}
		} else if (RewardManager.getEnchantment(enchants) == null) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang("FC_REWARD_ENCHANT", Config.getLang()).replace("%ss", enchants).replace("%s", reward));
			containKey = false;
		}
		
		return containKey;
	}
	
	/**
	 * Checks if reward Effect(s) exists
	 * 
	 * @param effects	Effect(s) to check
	 * @param reward	Reward to print in case of error
	 * 
	 * @return True if Effect(s) exists, False instead
	 */
	private static boolean checkRewardEffect(String effects, String reward) {
		boolean containKey = true;
		
		if (effects.contains(",")) {
			for (String effect : effects.split(",")) {
				if (RewardManager.findEffect(effect) == null) {
					LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang("FC_REWARD_EFFECT", Config.getLang()).replace("%ss", effect).replace("%s", reward));
					containKey = false;
				}
			}
		} else if (!Utils.checkEffect(effects)) {
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang("FC_REWARD_EFFECT", Config.getLang()).replace("%ss", effects).replace("%s", reward));
			containKey = false;
		}
		
		return containKey;
	}
	
	/**
	 * Checks levels.json file conformity
	 * 
	 * @param content	file content as String
	 * 
	 * @return True if content is conform to awaited levels.json content, False instead
	 */
	public static boolean levelsConformity(String content) {
		boolean ret = true;
		
		try {
			JSONObject jsonObj = FileUtils.readJSONObjectFromContent(content);
			
			for (String key : jsonObj.keySet()) {
				JSONObject levelObj = (JSONObject) jsonObj.get(key);
				
				if (!levelObj.has(NBR_STR)) {
					LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang("FC_LEVEL_NUMBER", Config.getLang()).replace("%s", key));
					ret = false;
				} else if (!levelObj.has("Seconds")) {
					LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang("FC_LEVEL_SECONDS", Config.getLang()).replace("%s", key));
					ret = false;
				} else if (!levelObj.has("Lose")) {
					LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang("FC_LEVEL_LOSE", Config.getLang()).replace("%s", key));
					ret = false;
				}
				
				if (ret) {
					@SuppressWarnings("unused")
					int number = levelObj.getInt(NBR_STR);
					@SuppressWarnings("unused")
					int seconds = levelObj.getInt("Seconds");
	
					String lose = levelObj.getString("Lose");
	
					if (!lose.equalsIgnoreCase("true") && !lose.equalsIgnoreCase("false")) {
						ret = false;
					}
				}
				
				levelObj.clear();
				
				if(!ret) {
					break;
				}
			}
			
			jsonObj.clear();
		} catch (NumberFormatException e) {
			Utils.logException(e);
			ret = false;
		}
		
		return ret;
	}
	
	/**
	 * Checks if a file exists at a specific path
	 * 
	 * @param path		The path to check
	 * @param fileName	The file name
	 * 
	 * @return True if the file exists, False instead
	 */
	public static boolean fileExistsInPluginVersionDirectory(String path, String fileName) {
		File tmp = new File(path + File.separator + KuffleMain.getInstance().getDescription().getVersion() + File.separator + fileName);
		
		return tmp.exists();
	}
	
	/**
	 * Create directory at path if not exists
	 * 
	 * @param path	Directory path
	 */
	public static void directoryExistsOrCreate(String path) {
		File file = new File(path);
		 
        if (!file.isDirectory()) {
        	file.mkdir();
        }
	}
}
