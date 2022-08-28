package main.fr.kosmosuniverse.kuffle.core;

import java.util.HashMap;
import java.util.Iterator;
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
	 * Setup targets langs
	 * 
	 * @param jsonContent	file string content to parse
	 * 
	 * @throws ParseException
	 */
	public static void setupTargetsLangs(String jsonContent) throws ParseException {
		targetsLangs = setupLangs(jsonContent);
	}
	
	/**
	 * Setup msgs langs
	 * 
	 * @param jsonContent	file string content to parse
	 * 
	 * @throws ParseException
	 */
	public static void setupMsgsLangs(String jsonContent) throws ParseException {
		msgsLangs = setupLangs(jsonContent);
	}
	
	/**
	 * Setup langs
	 * 
	 * @param jsonContent	file string content to parse
	 * @return 
	 * 
	 * @throws ParseException if JSONParser.parse fails
	 */
	private static Map<String, Map<String, String>> setupLangs(String jsonContent) throws ParseException {
		Map<String, Map<String, String>> langs = new HashMap<>();
		JSONParser jsonParser = new JSONParser();
		JSONObject langages = (JSONObject) jsonParser.parse(jsonContent);

		for (Iterator<?> itTarget = langages.keySet().iterator(); itTarget.hasNext();) {
			String keyItem = (String) itTarget.next();
			JSONObject target = (JSONObject) langages.get(keyItem);

			Map<String, String> targetLangs = new HashMap<>();
			
			for (Iterator<?> itLang = target.keySet().iterator(); itLang.hasNext();) {
				String keyLang = (String) itLang.next();
				String value = (String) target.get(keyLang);
				
				targetLangs.put(keyLang, value);
			}
			
			langs.put(keyItem, targetLangs);
		}
		
		return langs;
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
	public static boolean targetExists(String target) {
		return targetsLangs.containsKey(target);
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
