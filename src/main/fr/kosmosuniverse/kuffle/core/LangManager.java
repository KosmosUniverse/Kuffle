package main.fr.kosmosuniverse.kuffle.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class LangManager {
	private static List<String> langs = null;
	private static Map<String, Map<String, String>> targetsLangs = null;
	private static Map<String, Map<String, String>> msgsLangs = null;
	
	/**
	 * Private LangManager constructor
	 * 
	 * @throws IllegalStateException
	 */
	private LangManager() {
		throw new IllegalStateException("Utility class");
	}
	
	/**
	 * Setup msgs langs
	 * 
	 * @param jsonContent	file string content to parse
	 * 
	 * @throws ParseException
	 */
	public static void setupMsgsLangs(String jsonContent) throws ParseException {
		if (langs == null) {
			langs = new ArrayList<>();
		}
		
		msgsLangs = new HashMap<>();
		JSONParser jsonParser = new JSONParser();
		JSONObject langages = (JSONObject) jsonParser.parse(jsonContent);

		for (Object key : langages.keySet()) {
			String keyItem = key.toString();
			JSONObject target = (JSONObject) langages.get(keyItem);

			Map<String, String> targetLangs = new HashMap<>();
			
			for (Object keylang : target.keySet()) {
				String lang = keylang.toString();
				String value = (String) target.get(lang);
				
				targetLangs.put(lang, value);
				
				if (!langs.contains(lang)) {
					langs.add(lang);
				}
			}
			
			msgsLangs.put(keyItem, targetLangs);
		}
	}
	
	/**
	 * Add target's langs to @targets map
	 * 
	 * @param target	The target to add
	 * @param langs		The langs of targets
	 */
	public static void addTarget(String target, Map<String, String> langs) {
		if (targetsLangs == null)  {
			targetsLangs = new HashMap<>();
		}
		
		targetsLangs.computeIfAbsent(target, t -> new HashMap<>());
		
		langs.forEach((key, value) -> {
			if (!targetsLangs.get(target).containsKey(key)) {
				targetsLangs.get(target).put(key, value);
			}
		});
	}
	
	/**
	 * Clears the targetsLangs and msgsLangs lists
	 */
	public static void clear() {
		if (targetsLangs != null) {
			targetsLangs.forEach((k, v) -> {
				if (v != null) {
					v.clear();
				}
			});
			
			targetsLangs.clear();
		}
		
		if (msgsLangs != null) {
			msgsLangs.forEach((k, v) -> {
				if (v != null) {
					v.clear();
				}
			});
			
			msgsLangs.clear();
		}
		
		langs.clear();
	}
	
	/**
	 * Checks if a specific lang exists in the plugin
	 * 
	 * @param lang	the lang to check
	 * 
	 * @return True if lang found, False instead
	 */
	public static boolean hasLang(String lang) {
		return langs.contains(lang);
	}
	
	/**
	 * Gets target for a specific key and lang
	 * 
	 * @param key	target key
	 * @param lang	target lang
	 * 
	 * @return the target corresponding to the key in the selected lang
	 */
	public static String getTargetLang(String key, String lang) {
		return targetsLangs.get(key).get(lang);
	}
	
	/**
	 * Checks if a specific target exists in the plugin
	 * 
	 * @param target	the target to check
	 * 
	 * @return True if target found, False instead
	 */
	public static boolean hasTarget(String target) {
		if (targetsLangs != null) {
			return targetsLangs.containsKey(target);
		}
		
		return false;
	}
	
	/**
	 * Checks if a specific msg exists in the plugin
	 * 
	 * @param msg	the msg to check
	 * 
	 * @return True if msg found, False instead
	 */
	public static boolean msgExists(String msg) {
		return msgsLangs.containsKey(msg);
	}
	
	/**
	 * Gets the whole langs list
	 * 
	 * @return the lang String list
	 */
	public static List<String> getLangs() {
		return langs;
	}
	
	/**
	 * Gets message for a specific key and lang
	 * 
	 * @param key	message key
	 * @param lang	message lang
	 * 
	 * @return the message corresponding to the key in the selected lang
	 */
	public static String getMsgLang(String key, String lang) {
		return msgsLangs.get(key).get(lang);
	}
}
