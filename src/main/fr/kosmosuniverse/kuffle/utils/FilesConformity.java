package main.fr.kosmosuniverse.kuffle.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.AgeManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.RewardManager;
import main.fr.kosmosuniverse.kuffle.core.VersionManager;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class FilesConformity {
	/**
	 * Private FilesConformity constructor
	 * This class is utility class and must not be instantiate
	 * 
	 * @throws IllegalStateException if somebody try to instantiate
	 */
	private FilesConformity() {
		throw new IllegalStateException("Utility class");
	}
	
	/**
	 * Get a file content
	 * 
	 * @param file	The file of whom we will get the content
	 * 
	 * @return the file content as String, null if file is null
	 */
	public static String getContent(String file) {
		return getContent(file, false);
	}
	
	/**
	 * Get a file content
	 * 
	 * @param file			The file of whom we will get the content
	 * @param onlyResource	The file is searched only in plugin resource files
	 * 
	 * @return the file content as String, null if file is null
	 */
	public static String getContent(String file, boolean onlyResource) {
		String content = null;
		
		if (file.contains("%v")) {
			file = Utils.findFileExistVersion(file);
			
			if (file == null) {
				return null;
			}
		}
		
		content = getFromFile(file);
		
		if (content == null || !checkContent(file, content)) {
			content = getFromResource(file);
			
			if (content != null)  {
				LogManager.getInstanceSystem().logMsg(KuffleMain.current.getName(), "Load " + file + " from Resource.");
			} else {
				LogManager.getInstanceSystem().logMsg(KuffleMain.current.getName(), "File : " + file + " does not exist.");
			}
		} else {
			LogManager.getInstanceSystem().logMsg(KuffleMain.current.getName(), "Load " + file + " from File.");
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
		if (fileExistsInPluginVersionDirectory(KuffleMain.current.getDataFolder().getPath(), file)) {
			try (FileReader reader = new FileReader(KuffleMain.current.getDataFolder().getPath() + File.separator + KuffleMain.current.getDescription().getVersion() + File.separator  + file)) {
				JSONParser parser = new JSONParser();
				
				return ((JSONObject) parser.parse(reader)).toString();
			} catch (IOException | ParseException e) {
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
		String path = KuffleMain.current.getDataFolder().getPath() + File.separator + KuffleMain.current.getDescription().getVersion();
		directoryExistsOrCreate(path);		
		
		try (FileWriter writer = new FileWriter(path + File.separator + fileName)) {
			InputStream in = KuffleMain.current.getResource(fileName);

			writer.write(Utils.readFileContent(in));
			in.close();
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
		try {
			InputStream in = KuffleMain.current.getResource(file);
			String result = Utils.readFileContent(in);
			JSONParser parser = new JSONParser();
			JSONObject mainObject = (JSONObject) parser.parse(result);
			
			result = mainObject.toString();
			
			in.close();
			mainObject.clear();
			
			return result;
		} catch (IOException | ParseException e) {
			Utils.logException(e);
		}
		
		return null;
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
		boolean ret = true;
		
		if (content.equals(getFromResource(file))) {
			ret = false;
		}
		
		if (ret && file.equals("ages.json")) {
			ret = ageConformity(content);
		} else if (ret && file.equals("items_lang.json")) {
			ret = itemLangConformity(content);
		} else if (ret && file.equals("langs.json")) {
			ret = msgLangConformity(content);
		} else if (ret && file.equals("items_" + VersionManager.getVersion() + ".json") ||
				file.equals("sbtt_" + VersionManager.getVersion() + ".json")) {
			ret = itemsConformity(content);
		} else if (ret && file.equals("rewards_" + VersionManager.getVersion() + ".json")) {
			ret = rewardsConformity(content);
		} else if (ret && file.equals("levels.json")) {
			ret = levelsConformity(content);
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
		
		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObj;
			
			jsonObj = (JSONObject) parser.parse(content);
			
			for (Object key : jsonObj.keySet()) {
				if (((String) key).endsWith("_Age")) {
					JSONObject ageObj = (JSONObject) jsonObj.get(key);
					
					ret = checkAge(ageObj, (String) key);
					ageObj.clear();
				} else if (!((String) key).equals("Default")) {
					ret = false;
					break;
				}
				
				if (!ret) {
					break;
				}
			}
			
			jsonObj.clear();
		} catch (ParseException | IllegalArgumentException e) {
			ret = false;
			Utils.logException(e);
		}
		
		return ret;
	}
	
	/**
	 * Check an Age JSON object content
	 * 
	 * @param ageObj	The Age objetc to check
	 * @param age		The current Age name
	 * 
	 * @return True if ageObj is valid, False instead
	 */
	private static boolean checkAge(JSONObject ageObj, String age) {
		boolean ret = true;
		
		if (!ageObj.containsKey("Number")) {
			LogManager.getInstanceSystem().logSystemMsg("Age [" + age + "] does not contain 'Number' Object.");
			ret = false;
		} else if (ret && !ageObj.containsKey("TextColor")) {
			LogManager.getInstanceSystem().logSystemMsg("Age [" + age + "] does not contain 'TextColor' Object.");
			ret = false;
		} else if (ret && !ageObj.containsKey("BoxColor")) {
			LogManager.getInstanceSystem().logSystemMsg("Age [" + age + "] does not contain 'BoxColor' Object.");
			ret = false;
		}
		
		if (!ret) {
			return ret;
		}
		
		@SuppressWarnings("unused")
		int number = Integer.parseInt(ageObj.get("Number").toString());
		String color = (String) ageObj.get("TextColor");
		String box = (String) ageObj.get("BoxColor") + "_SHULKER_BOX";
		
		if (ChatColor.valueOf(color) == null) {
			LogManager.getInstanceSystem().logSystemMsg("Age [" + age + "] color [" + color + "] is not in ChatColor Enum.");
			ret = false;
		}
		
		if (ret && Material.matchMaterial(box) == null) {
			LogManager.getInstanceSystem().logSystemMsg("Age [" + age + "] box [" + box + "] is not in Material Enum.");
			ret = false;
		}
		
		return ret;
	}
	
	/**
	 * Checks items_lang.json file conformity
	 * 
	 * @param content	file content as String
	 * 
	 * @return True if content is conform to awaited items_lang.json content, False instead
	 */
	public static boolean itemLangConformity(String content) {
		return langConformity(content, true);
	}
	
	/**
	 * Checks langs.json file conformity
	 * 
	 * @param content	file content as String
	 * 
	 * @return True if content is conform to awaited langs.json content, False instead
	 */
	public static boolean msgLangConformity(String content) {
		return langConformity(content, false);
	}
	
	/**
	 * Checks langs for a specified content and, if needed, checks if material exists.
	 * 
	 * @param content		file content as String
	 * @param areMaterial	If True, it will check if the material exists
	 * 
	 * @return True if content is conform to awaited lang file content, False instead
	 */
	private static boolean langConformity(String content, boolean areMaterial) {
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = null;
		List<String> langs = null;
		
		try {
			jsonObj = (JSONObject) parser.parse(content);
		} catch (ParseException e) {
			LogManager.getInstanceSystem().logSystemMsg(e.getMessage());
			return false;
		}
		
		boolean ret = true;
		
		for (Object key : jsonObj.keySet()) {
			
			if (areMaterial && Material.matchMaterial((String) key) == null) {
				LogManager.getInstanceSystem().logSystemMsg("Material [" + (String) key + "] does not exist.");
				ret = false;
				break;
			}
			
			JSONObject langObj = (JSONObject) jsonObj.get(key);
			
			if (langs == null) {
				langs = new ArrayList<>();
				
				for (Object keyLang : langObj.keySet()) {
					langs.add((String) keyLang);
				}
			}
			
			if (!elementLangCheck(langObj, langs)) {
				ret = false;
			}
			
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
	 * Checks if the langObj keys are contained in langs list
	 * 
	 * @param langObj	The JSONObject that contains keys that will be checked
	 * @param langs		List that contains good languages
	 * 
	 * @return True if materialObj keys are all in langs, False instead
	 */
	private static boolean elementLangCheck(JSONObject langObj, List<String> langs) {
		for (Object keyLang : langObj.keySet()) {
			if (!langs.contains(keyLang)) {
				LogManager.getInstanceSystem().logSystemMsg("Lang [" + (String) keyLang + "] is not everywhere in lang file.");
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Checks items_{version}.json file conformity
	 * 
	 * @param content	file content as String
	 * 
	 * @return True if content is conform to awaited items_{version}.json content, False instead
	 */
	public static boolean itemsConformity(String content) {
		boolean ret = true;
		
		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObj;
			
			jsonObj = (JSONObject) parser.parse(content);
			
			for (Object key : jsonObj.keySet()) {
				if (!AgeManager.ageExists((String) key)) {
					LogManager.getInstanceSystem().logSystemMsg("Age [" + (String) key + "] does not exist in ages.json.");
					ret = false;
					break;
				}
				
				JSONObject categories = (JSONObject) jsonObj.get(key);
				
				for (Object category : categories.keySet()) {
					JSONArray array = (JSONArray) categories.get(category);
					
					for (int i = 0; i < array.size(); i++) {
						if (Material.matchMaterial((String) array.get(i)) == null) {
							LogManager.getInstanceSystem().logSystemMsg("Material [" + (String) array.get(i) + "] for category [" + (String) category + "] of Age [" + (String) key + "] does not exist.");
							ret = false;
							break;
						}
					}
					
					array.clear();
					
					if (!ret) {
						break;
					}
				}
				
				categories.clear();
				
				if (!ret) {
					break;
				}
			}
			
			jsonObj.clear();
		} catch (ParseException | IllegalArgumentException e) {
			Utils.logException(e);
			ret = false;
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
			JSONParser parser = new JSONParser();
			JSONObject jsonObj;
			
			jsonObj = (JSONObject) parser.parse(content);
			
			for (Object key : jsonObj.keySet()) {
				if (!AgeManager.ageExists((String) key)) {
					LogManager.getInstanceSystem().logSystemMsg("Age [" + (String) key + "] does not exist in ages.json.");
					ret = false;
					break;
				}
				
				JSONObject rewards = (JSONObject) jsonObj.get(key);
				
				ret = checkRewards(rewards);
				
				rewards.clear();
				
				if (!ret) {
					break;
				}
			}
			
			jsonObj.clear();
		} catch (ParseException | IllegalArgumentException e) {
			Utils.logException(e);
			ret = false;
		}
		
		return ret;
	}
	
	/**
	 * Checks if rewards from an age are conform
	 * 
	 * @param rewards	rewards from an Age
	 * 
	 * @return True if rewards are conform, False instead
	 */
	private static boolean checkRewards(JSONObject rewards) {
		boolean ret = true;
		
		for (Object reward : rewards.keySet()) {
			if (Material.matchMaterial((String) reward) == null) {
				LogManager.getInstanceSystem().logSystemMsg("Material [" + (String) reward + "] is not in Material Enum.");
				ret = false;
				break;
			}
			
			JSONObject itemObj = (JSONObject) rewards.get(reward);
			
			if (!checkRewardElem(itemObj, (String) reward)) {
				ret = false;
				itemObj.clear();
				break;
			}
			
			try {
				@SuppressWarnings("unused")
				int number = Integer.parseInt(itemObj.get("Amount").toString());
				number = Integer.parseInt(itemObj.get("Level").toString());
				
				String enchants = (String) itemObj.get("Enchant");
				String effects = (String) itemObj.get("Effect");
				
				if ((!enchants.equals("") && !checkRewardEnchant(enchants, (String) reward)) ||
						(!effects.equals("") && !checkRewardEffect(effects, (String) reward))) {
					ret = false;
				}
			} catch (NumberFormatException e) {
				Utils.logException(e);
				ret = false;
			}
			
			itemObj.clear();
			
			if (!ret) {
				break;
			}
		}
		
		return ret;
	}
	
	/**
	 * Checks if a reward contains all needed keys
	 * 
	 * @param itemObj	The reward to check
	 * @param reward	The String to print in case of error
	 * 
	 * @return True is reward contains all keys, False instead
	 */
	private static boolean checkRewardElem(JSONObject itemObj, String reward) {
		boolean containKey = true;
		
		if (!itemObj.containsKey("Amount")) {
			LogManager.getInstanceSystem().logSystemMsg("Reward [" + reward + "] does not contain 'Amount' Object.");
			containKey = false;
		} else if (!itemObj.containsKey("Enchant")) {
			LogManager.getInstanceSystem().logSystemMsg("Reward [" + reward + "] does not contain 'Enchant' Object.");
			containKey = false;
		} else if (!itemObj.containsKey("Level")) {
			LogManager.getInstanceSystem().logSystemMsg("Reward [" + reward + "] does not contain 'Level' Object.");
			containKey = false;
		} else if (!itemObj.containsKey("Effect")) {
			LogManager.getInstanceSystem().logSystemMsg("Reward [" + reward + "] does not contain 'Effect' Object.");
			containKey = false;
		}
		
		return containKey;
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
					LogManager.getInstanceSystem().logSystemMsg("Reward [" + reward + "] contains unknown enchant : [" + enchant + "].");
					containKey = false;
					break;
				}
			}
		} else if (RewardManager.getEnchantment(enchants) == null) {
			LogManager.getInstanceSystem().logSystemMsg("Reward [" + reward + "] contains unknown enchant : [" + enchants + "].");
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
					LogManager.getInstanceSystem().logSystemMsg("Reward [" + reward + "] contains unknown effect : [" + effect + "].");
					containKey = false;
				}
			}
		} else if (!Utils.checkEffect(effects)) {
			LogManager.getInstanceSystem().logSystemMsg("Reward [" + reward + "] contains unknown enchant : [" + effects + "].");
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
			JSONParser parser = new JSONParser();
			JSONObject jsonObj;
			
			jsonObj = (JSONObject) parser.parse(content);
			
			for (Object key : jsonObj.keySet()) {
				JSONObject levelObj = (JSONObject) jsonObj.get(key);
				
				if (!levelObj.containsKey("Number")) {
					LogManager.getInstanceSystem().logSystemMsg("Level [" + (String) key + "] does not contain 'Number' Object.");
					ret = false;
				} else if (!levelObj.containsKey("Seconds")) {
					LogManager.getInstanceSystem().logSystemMsg("Level [" + (String) key + "] does not contain 'Seconds' Object.");
					ret = false;
				} else if (!levelObj.containsKey("Lose")) {
					LogManager.getInstanceSystem().logSystemMsg("Level [" + (String) key + "] does not contain 'Lose' Object.");
					ret = false;
				}
				
				if (!ret) {
					levelObj.clear();
					break;
				}
				
				@SuppressWarnings("unused")
				int number = Integer.parseInt(levelObj.get("Number").toString());
				number = Integer.parseInt(levelObj.get("Seconds").toString());

				String lose = levelObj.get("Lose").toString();

				if (!lose.equalsIgnoreCase("true") && !lose.equalsIgnoreCase("false")) {
					ret = false;
				}
				
				levelObj.clear();
				
				if(!ret) {
					break;
				}
			}
			
			jsonObj.clear();
		} catch (ParseException | NumberFormatException e) {
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
		File tmp = new File(path + File.separator + KuffleMain.current.getDescription().getVersion() + File.separator + fileName);
		
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
