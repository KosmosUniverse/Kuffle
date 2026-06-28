package fr.kosmosuniverse.kuffle.datamanagers;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fr.kosmosuniverse.kuffle.utils.FileUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.json.JSONObject;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class VersionManager {
	private static Map<String, Integer> allVersions;
	private static List<String> allowedVersion;
	@Getter
	private static String mcVersion;
	@Getter
	private static String lastMcVersionDataVersion;
	
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
		if (allowedVersion != null) {
			allowedVersion.clear();
		}

		if (allVersions != null) {
			allVersions.clear();
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
		allVersions = new HashMap<>();
		mcVersion = Bukkit.getBukkitVersion().substring(0, Bukkit.getBukkitVersion().indexOf("-"));

		for (String key : jsonObj.keySet()) {
			allVersions.put(key, jsonObj.getInt(key));
		}

		allowedVersion = allVersions.keySet()
				.stream()
				.filter(s -> checkIfV1SupportV2(mcVersion, s))
				.collect(Collectors.toList());

		lastMcVersionDataVersion = allVersions.entrySet()
				.stream()
				.filter(e -> checkIfV1SupportV2(mcVersion, e.getKey()))
				.sorted(Comparator.comparingInt(Map.Entry::getValue))
				.map(Map.Entry::getKey)
				.findFirst().orElse(null);

		jsonObj.clear();
	}

	private static boolean checkIfV1SupportV2(String v1, String v2) {
		String[] rawV1Number = v1.split("\\.");
		String[] rawV2Number = v2.split("\\.");

		int length = Integer.min(rawV1Number.length, rawV2Number.length);

		for (int i = 0; i < length; i++) {
			int v1Number = Integer.parseInt(rawV1Number[i]);
			int v2Number = Integer.parseInt(rawV2Number[i]);

			if (v1Number < v2Number) {
				return false;
			} else if (v1Number > v2Number) {
				return true;
			}
		}

		return true;
	}

	public static boolean isAllowedVersion(String version) {
		return allowedVersion.contains(version);
	}

	public static boolean isRemVersionNotReached(String remVersion) {
		return !allowedVersion.contains(remVersion);
	}

	public static boolean versionNotExists(String version) {
		return !allVersions.containsKey(version);
	}
}
