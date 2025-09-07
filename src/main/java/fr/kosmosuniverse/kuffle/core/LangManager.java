package fr.kosmosuniverse.kuffle.core;

import fr.kosmosuniverse.kuffle.utils.FileUtils;
import lombok.Getter;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class LangManager {
	@Getter
	private static List<String> langs = null;
	private static Map<String, Map<String, String>> targetsLangs = null;
	private static Map<String, Map<String, String>> msgsLangs = null;
	
	/**
	 * Private LangManager constructor
	 * 
	 * @throws IllegalStateException Utility Class Constructor Exception
	 */
	private LangManager() {
		throw new IllegalStateException("Utility class");
	}
	
	/**
	 * Setup msgs langs
	 * 
	 * @param jsonContent	file string content to parse
	 */
	public static void setupMsgsLangs(String jsonContent) {
		if (langs == null) {
			langs = new ArrayList<>();
		}
		
		msgsLangs = new HashMap<>();
		JSONObject languages = FileUtils.readJSONObjectFromContent(jsonContent);

		for (String key : languages.keySet()) {
			JSONObject target = languages.getJSONObject(key);
			Map<String, String> targetLangs = new HashMap<>();
			
			for (String keyLang : target.keySet()) {
				String value = target.getString(keyLang);
				
				targetLangs.put(keyLang, value);
				
				if (!langs.contains(keyLang)) {
					langs.add(keyLang);
				}
			}
			
			msgsLangs.put(key, targetLangs);
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
