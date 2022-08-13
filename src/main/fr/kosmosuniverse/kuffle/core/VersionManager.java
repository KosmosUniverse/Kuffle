package main.fr.kosmosuniverse.kuffle.core;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class VersionManager {
	private static Map<Integer, String> versions = null;
	
	/**
	 * Private VersionManager constructor
	 * 
	 * @throws IllegalStateException
	 */
	private VersionManager() {
		throw new IllegalStateException("Utility class");
    }
	
	/**
	 * Clears the versions list
	 */
	public static void clear() {
		if (versions != null) {
			versions.clear();
		}
	}
	
	/**
	 * Setup versions from string file content
	 * 
	 * @param content	The file content
	 * 
	 * @throws IllegalArgumentException if content is null
	 * @throws ParseException if JSONParser.parse fails
	 */
	public static void setupVersions(String content) throws IllegalArgumentException, ParseException {
		if (content == null) {
			throw new IllegalArgumentException("Input content is null !");
		}
		
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = ((JSONObject) parser.parse(content));

		versions = new HashMap<>();

		for (Object key : jsonObj.keySet()) {
			versions.put(Integer.parseInt(jsonObj.get(key).toString()), (String) key);
		}
		
		jsonObj.clear();
	}
	
	/**
	 * Get the current Minecraft version
	 * 
	 * @return the version as a String
	 */
	public static String getVersion() {
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

		version = version.split("v")[1];
		version = version.split("_")[0] + "." + version.split("_")[1];

		return version;
	}
	
	/**
	 * Gets version key from version value
	 * 
	 * @param version	The version value
	 * 
	 * @return the key if found, -1 instead
	 */
	public static int getVersionByValue(String version) {
		for (int key : versions.keySet()) {
			if (versions.get(key).equals(version)) {
				return key;
			}
		}

		return -1;
	}
	
	/**
	 * Gets version key from version index
	 * 
	 * @param version	The version index
	 * 
	 * @return the version if found, null instead
	 */
	public static String getVersionByIndex(int version) {
		return versions.get(version);
	}
}
