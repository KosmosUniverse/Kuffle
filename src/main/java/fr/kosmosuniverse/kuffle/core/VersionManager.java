package fr.kosmosuniverse.kuffle.core;

import java.util.HashMap;
import java.util.Map;

import fr.kosmosuniverse.kuffle.utils.FileUtils;
import org.bukkit.Bukkit;
import org.json.JSONObject;

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
	 * @throws IllegalStateException Utility Class Constructor Exception
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
	 */
	public static void setupVersions(String content) throws IllegalArgumentException {
		if (content == null) {
			throw new IllegalArgumentException("Input content is null !");
		}

		JSONObject jsonObj = FileUtils.readJSONObjectFromContent(content);
		versions = new HashMap<>();

		for (String key : jsonObj.keySet()) {
			versions.put(jsonObj.getInt(key), key);
		}
		
		jsonObj.clear();
	}
	
	/**
	 * Get the current Minecraft version
	 * 
	 * @return the version as a String
	 */
	public static String getVersion() {
		return Bukkit.getBukkitVersion().substring(0, 6);
	}
	
	/**
	 * Checks if a specific version exists
	 * 
	 * @param version	The version to check
	 * 
	 * @return True if the version exists, False instead
	 */
	public static boolean hasVersion(String version) {
		return versions.entrySet().stream().anyMatch(entry -> entry.getValue().equals(version));
	}
	
	/**
	 * Gets version key from version value
	 * 
	 * @param version	The version value
	 * 
	 * @return the key if found, -1 instead
	 */
	public static int getVersionByValue(String version) {
		return versions.entrySet().stream().filter(e -> e.getValue().equals(version)).map(Map.Entry::getKey).findAny().orElse(-1);
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
	
	/**
	 * Checks if the actual version is greater or equals than the given @version and, if there is one, lower or equals than the given @remVersion
	 * 
	 * @param version		The version to check
	 * @param remVersion	The remove version to check
	 * 
	 * @return True if @version is greater or equals to server version and, if there is one, lower than @remVersion, False instead
	 */
	public static boolean isVersionValid(String version, String remVersion) {
		int versionIdx = getVersionByValue(version);
		int remVersionIdx = remVersion == null ? -1 : getVersionByValue(remVersion);
		int currentIdx = getVersionByValue(getVersion());
		if (currentIdx == -1) {
			currentIdx = getVersionByValue(getVersion().substring(0, 4));
		}
		if (currentIdx < versionIdx) {
			return false;
		}
		
		return !(remVersionIdx != -1 && currentIdx >= remVersionIdx);
	}
}
