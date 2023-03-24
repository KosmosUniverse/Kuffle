package main.fr.kosmosuniverse.kuffle.core;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import main.fr.kosmosuniverse.kuffle.type.KuffleType;
import main.fr.kosmosuniverse.kuffle.utils.ItemUtils;
import main.fr.kosmosuniverse.kuffle.utils.Pair;
import net.md_5.bungee.api.ChatColor;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class TargetManager {
	private static Map<String, List<String>> targets = null;
	private static Map<String, List<String>> sbtts = null;
	private static Map<String, List<Inventory>> targetsInvs = null;
	
	/**
	 * Private TargetManager constructor
	 * 
	 * @throws IllegalStateException
	 */
	private TargetManager() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Setup all targets from file string content
	 * 
	 * @param content	the content to parse
	 * 
	 * @throws ParseException if JSONParser.parse fails
	 */
	public static void setup(KuffleType.Type type, String content) throws ParseException {
		JSONParser jsonParser = new JSONParser();
		JSONObject allObj = (JSONObject) jsonParser.parse(content);
		targets = new HashMap<>();
		sbtts = new HashMap<>();
		
		setupVersions(type, allObj);
		setupTargetsInvs();
	}
	
	private static void setupVersions(KuffleType.Type type, JSONObject allObj) {
		for (Object version : allObj.keySet()) {
			if (VersionManager.isVersionValid(version.toString(), null)) {
				JSONObject versionObj = (JSONObject) allObj.get(version);
				
				setupTypes(type, version.toString(), versionObj);
			}
		}
	}
	
	private static void setupTypes(KuffleType.Type type, String version, JSONObject versionObj) {
		for (Object kuffleType : versionObj.keySet()) {
			if ("BOTH".equalsIgnoreCase(kuffleType.toString()) ||
					type == KuffleType.Type.valueOf(kuffleType.toString().toUpperCase())) {
				JSONObject typeObj = (JSONObject) versionObj.get(kuffleType);
				
				setupAges(version, typeObj);
			}
		}
	}
	
	private static void setupAges(String version, JSONObject typeObj) {
		for (Object age : typeObj.keySet()) {
			JSONObject ageObj = (JSONObject) typeObj.get(age);
			
			if (!targets.containsKey(age.toString())) {
				targets.put(age.toString(), new ArrayList<>());
			}

			if (!sbtts.containsKey(age.toString())) {
				sbtts.put(age.toString(), new ArrayList<>());
			}
			
			setupTargets(version, age.toString(), ageObj);
		}
	}
	
	private static void setupTargets(String version, String age, JSONObject ageObj) {
		for (Object target : ageObj.keySet()) {
			JSONObject targetObj = (JSONObject) ageObj.get(target);
			boolean sbtt = Boolean.parseBoolean(targetObj.get("Sbtt").toString().toLowerCase());
			
			if (!targetObj.containsKey("remVersion") ||
					VersionManager.isVersionValid(version, targetObj.get("remVersion").toString())) {
				targets.get(age).add(target.toString());
				
				if (sbtt) {
					sbtts.get(age).add(target.toString());
				}
				
				if (!LangManager.hasTarget(target.toString())) {
					JSONObject langObj = (JSONObject) targetObj.get("Langs");
					
					setupLang(target.toString(), langObj);
				}
			}
		}
	}
	
	private static void setupLang(String target, JSONObject langObj) {
		Map<String, String> langs = new HashMap<>();
		
		for (Object lang : langObj.keySet()) {
			langs.put(lang.toString(), langObj.get(lang).toString());
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
		
		if (targetsInvs != null) {
			targetsInvs.forEach((k, v) -> {
				if (v != null) {
					v.clear();
				}
			});
			
			targetsInvs.clear();
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
	
	/**
	 * Setup targets inventories for all Ages
	 */
	private static void setupTargetsInvs() {
		targetsInvs = new HashMap<>();

		targets.forEach((k, v) -> targetsInvs.put(k, setupAgeInvs(k, v)));
	}

	/**
	 * Setup Inventories for a specific age
	 * 
	 * @param age			The Age name
	 * @param ageTargets	The Age targets
	 * 
	 * @return the Inventory list
	 */
	private static List<Inventory> setupAgeInvs(String age, List<String> ageTargets) {
		List<Inventory> invs = new ArrayList<>();
		Inventory inv;
		int invCnt = 9;
		int nbInv = 1;
		boolean hasNext = ageTargets.size() > 45;

		if (ageTargets.size() > 45) {
			inv = Bukkit.createInventory(null, 54, ChatColor.BLACK + age + " Targets Tab 1");
		} else {
			inv = Bukkit.createInventory(null, 54, ChatColor.BLACK + age + " Targets");
		}
		
		setupFirstRow(inv, true, hasNext);
		
		for (String target : ageTargets) {
			inv.addItem(getMaterial(target));
			
			if (invCnt == 53) {
				invCnt = 9;
				invs.add(inv);
				nbInv++;
				inv = Bukkit.createInventory(null, 54, ChatColor.BLACK + age + " Targets Tab " + nbInv);
				
				setupFirstRow(inv, false, hasNext);
			} else {
				invCnt++;
			}
		}
		
		inv.setItem(8, ItemUtils.itemMaker(Material.LIME_STAINED_GLASS_PANE, 1, " "));
		
		invs.add(inv);
		
		return invs;
	}
	
	/**
	 * Setup the first row of an inventory
	 * 
	 * @param inv		the inv inwhich the first row will be set
	 * @param isFirst	True if it is the first inventory of the Age, False instead
	 * @param hasNext	True if there is an inventory after this one, False instead
	 */
	private static void setupFirstRow(Inventory inv, boolean isFirst, boolean hasNext) {
		int invCnt = 0;
		ItemStack bluePane = ItemUtils.itemMaker(Material.BLUE_STAINED_GLASS_PANE, 1, "Next ->");
		ItemStack limePane = ItemUtils.itemMaker(Material.LIME_STAINED_GLASS_PANE, 1, " ");
		ItemStack redPane = ItemUtils.itemMaker(Material.RED_STAINED_GLASS_PANE, 1, "<- Previous");
		
		for (; invCnt < 9; invCnt++) {
			if (invCnt == 0 && !isFirst) {
				inv.setItem(invCnt, redPane);
			} else if (invCnt == 8 && hasNext) {
				inv.setItem(invCnt, bluePane);
			} else {
				inv.setItem(invCnt, limePane);
			}
		}
	}
	
	/**
	 * Gets the corresponding ItemStack for a specific target
	 * 
	 * @param target	the target
	 * 
	 * @return the ItemStack if target exists in Minecraft, null instead
	 */
	private static ItemStack getMaterial(String target) {
		for (Material mat : Material.values()) {
			if (mat.toString().equals(target.toUpperCase())) {
				return new ItemStack(mat);
			} else if (mat.toString().contains(target.toUpperCase()) &&
					target.toUpperCase().contains(mat.toString())) {
				return ItemUtils.itemMaker(mat, 1, target);
			}
		}
		
		return ItemUtils.itemMaker(Material.GRAY_STAINED_GLASS_PANE, 1, target);
	}
	
	/**
	 * Gets Inventory in inventories list of a specific Age and depending on a clicked itemName
	 * 
	 * @param age		The Age inventory list
	 * @param current	The current age that is open
	 * @param modifier	The modifier is 1 if player want to move to next page and -1 if he wants to go backwards
	 * 
	 * @return the inventory to display to the player
	 */
	public static Inventory getAgeInv(String age, Inventory current, int modifier) {
		List<Inventory> ageInvs = targetsInvs.get(age);
		
		int idx = ageInvs.indexOf(current);
		
		return ageInvs.get(idx + modifier);
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
	 * Gets the Target invs list for a specific Age as an unmodifiable list
	 * 
	 * @param age	The age
	 * 
	 * @return the list of targets of the Age @age
	 */
	public static List<Inventory> getAgeTargetsInvs(String age) {
		return Collections.unmodifiableList(targetsInvs.get(age));
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
