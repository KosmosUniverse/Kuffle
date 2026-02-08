package fr.kosmosuniverse.kuffle.core;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.kosmosuniverse.kuffle.type.KuffleType;
import fr.kosmosuniverse.kuffle.utils.FileUtils;
import fr.kosmosuniverse.kuffle.utils.Pair;
import org.bukkit.inventory.Inventory;

import org.json.JSONObject;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class TargetManager {
	private static Map<String, List<String>> targets = null;
	private static Map<String, List<String>> sbtts = null;
	private static TargetInventories targetInv = null;
	
	/**
	 * Private TargetManager constructor
	 * 
	 * @throws IllegalStateException Utility Class Constructor Exception
	 */
	private TargetManager() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Setup all targets from file string content
	 * 
	 * @param content	the content to parse
	 *
	 */
	public static void setup(KuffleType.Type type, String content) {
		JSONObject allObj = FileUtils.readJSONObjectFromContent(content);
		targets = new HashMap<>();
		sbtts = new HashMap<>();
		
		setupVersions(type, allObj);
		targetInv = new TargetInventories();
		targetInv.setupInventories(targets);
	}
	
	private static void setupVersions(KuffleType.Type type, JSONObject allObj) {
		for (String version : allObj.keySet()) {
			if (VersionManager.isVersionValid(version, null)) {
				JSONObject versionObj = (JSONObject) allObj.get(version);
				
				setupTypes(type, version, versionObj);
			}
		}
	}
	
	private static void setupTypes(KuffleType.Type type, String version, JSONObject versionObj) {
		for (String kuffleType : versionObj.keySet()) {
			if ("BOTH".equalsIgnoreCase(kuffleType) ||
					type == KuffleType.Type.valueOf(kuffleType.toUpperCase())) {
				JSONObject typeObj = (JSONObject) versionObj.get(kuffleType);
				
				setupAges(version, typeObj);
			}
		}
	}
	
	private static void setupAges(String version, JSONObject typeObj) {
		for (String age : typeObj.keySet()) {
			JSONObject ageObj = (JSONObject) typeObj.get(age);
			
			if (!targets.containsKey(age)) {
				targets.put(age, new ArrayList<>());
			}

			if (!sbtts.containsKey(age)) {
				sbtts.put(age, new ArrayList<>());
			}
			
			setupTargets(version, age, ageObj);
		}
	}
	
	private static void setupTargets(String version, String age, JSONObject ageObj) {
		for (String target : ageObj.keySet()) {
			JSONObject targetObj = (JSONObject) ageObj.get(target);
			boolean sbtt = Boolean.parseBoolean(targetObj.get("Sbtt").toString().toLowerCase());
			
			if (!targetObj.has("remVersion") ||
					VersionManager.isVersionValid(version, targetObj.get("remVersion").toString())) {
				targets.get(age).add(target);
				
				if (sbtt) {
					sbtts.get(age).add(target);
				}
				
				if (!LangManager.hasTarget(target)) {
					JSONObject langObj = (JSONObject) targetObj.get("Langs");
					
					setupLang(target, langObj);
				}
			}
		}
	}
	
	private static void setupLang(String target, JSONObject langObj) {
		Map<String, String> langs = new HashMap<>();
		
		for (String lang : langObj.keySet()) {
			langs.put(lang, langObj.get(lang).toString());
		}
		
		LangManager.addTarget(target, langs);
		langs.clear();
	}
	
	/**
	 * Clears the targets map and the targetsInvs map
	 */
	public static void clear() {
		if (targets != null) {
			targets.forEach((k, v) -> {
				if (v != null) {
					v.clear();
				}
			});
			
			targets.clear();
		}
		
		if (sbtts != null) {
			sbtts.forEach((k, v) -> {
				if (v != null) {
					v.clear();
				}
			});
			
			sbtts.clear();
		}

		if (targetInv != null) {
			targetInv.clear();
		}
	}

	/**
	 * Gets a new target from Age ageName that is not in done list.
	 * 
	 * @param done		The list of excluded targets
	 * @param ageName	The Age name in which list it has to search
	 * 
	 * @return the target as String
	 */
	public static String newTarget(List<String> done, String ageName) {	
		return newObject(targets, done, ageName);
	}
	
	/**
	 * Gets a new sbtt from Age ageName that is not in done list.
	 * 
	 * @param done		The list of excluded sbtts
	 * @param ageName	The Age name in which list it has to search
	 * 
	 * @return the sbtt as String
	 */
	public static String newSbtt(List<String> done, String ageName) {	
		return newObject(sbtts, done, ageName);
	}
	
	/**
	 * Gets a new object (target or sbtt) from Age ageName that is not in done list.
	 * 
	 * @param objects	The map in which the object will be searched
	 * @param done		The list of excluded targets
	 * @param ageName	The Age name in which list it has to search
	 * 
	 * @return the target as String
	 */
	private static String newObject(Map<String, List<String>> objects, List<String> done, String ageName) {	
		List<String> finalList = new ArrayList<>();
		
		objects.get(ageName).stream().filter(s -> !done.contains(s)).forEach(finalList::add);
		
		if (finalList.size() == 1) {
			return finalList.get(0);
		} else if (finalList.isEmpty()) {
			return null;
		}
		
		SecureRandom random = new SecureRandom();
		
		return finalList.get(random.nextInt(finalList.size()));
	}
	
	/**
	 * Gets next target in the ageName Age list
	 * 
	 * @param done		The list of excluded targets
	 * @param ageName	The Age name in which list it has to search
	 * @param sameIdx	The index from which it will begin to search
	 * 
	 * @return a Pair object that has new index as Key and target as Value
	 */
	public static Pair nextTarget(List<String> done, String ageName, int sameIdx) {
		List<String> ageTargets = targets.get(ageName);
		String testTarget = ageTargets.get(sameIdx);
		
		while (done.contains(testTarget)) {
			sameIdx++;
			testTarget = ageTargets.get(sameIdx);
		}
		
		return (new Pair(sameIdx, testTarget));
	}

	public static Inventory getMainInv() {
		return targetInv.getMainInv();
	}

	public static Inventory getInv(String invName) {
		return targetInv.getInv(invName);
	}

	public static boolean hasInv(String invName) {
		return targetInv.hasInv(invName);
	}

	/**
	 * Gets the Target list for a specific Age as an unmodifiable list
	 * 
	 * @param age	The age
	 * 
	 * @return the list of targets of the Age @age
	 */
	public static List<String> getAgeTargets(String age) {
		return Collections.unmodifiableList(targets.get(age));
	}
	
	/**
	 * Shuffles all targets for each ages
	 */
	public static void shuffleTargets() {
		for (Map.Entry<String, List<String>> entry : targets.entrySet()) {
			Collections.shuffle(entry.getValue());
		}
	}
}
