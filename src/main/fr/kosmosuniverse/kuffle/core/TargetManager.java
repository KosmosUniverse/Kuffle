package main.fr.kosmosuniverse.kuffle.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import main.fr.kosmosuniverse.kuffle.utils.ItemUtils;
import main.fr.kosmosuniverse.kuffle.utils.Pair;

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
	 * Setup Targets map
	 * 
	 * @param content	the content to parse
	 * 
	 * @throws ParseException if JSONParser.parse fails
	 */
	public static void setupTargets(String content) throws ParseException {
		targets = setup(content);
		setupTargetsInvs();
	}
	
	/**
	 * Setup Sbtts templates map
	 * 
	 * @param content	the content to parse
	 * 
	 * @throws ParseException if JSONParser.parse fails
	 */
	public static void setupSbtts(String content) throws ParseException {
		sbtts = setup(content);
	}
	
	/**
	 * Setup all targets from file string content
	 * 
	 * @param content	the content to parse
	 * 
	 * @return the map that contains targets
	 * 
	 * @throws ParseException if JSONParser.parse fails
	 */
	private static Map<String, List<String>> setup(String content) throws ParseException {
		Map<String, List<String>> targets = new HashMap<>();
		
		int max = AgeManager.getLastAgeIndex();

		for (int ageCnt = 0; ageCnt <= max; ageCnt++) {
			Age age = AgeManager.getAgeByNumber(ageCnt);
			
			targets.put(age.name, setupAgeTargets(age.name, content));
		}
		
		return targets;
	}
	
	/**
	 * Setup targets for a specific Age
	 * 
	 * @param age		the age to set targets
	 * @param content	the file content to parse
	 * 
	 * @return the String target list
	 * 
	 * @throws ParseException if JSONParser.parse fails
	 */
	private static List<String> setupAgeTargets(String age, String content) throws ParseException {
		List<String> finalList = new ArrayList<>();
		JSONParser jsonParser = new JSONParser();
		JSONObject targets = (JSONObject) jsonParser.parse(content);
		JSONObject ageObject = (JSONObject) targets.get(age);

		for (Object key : ageObject.keySet()) {
			JSONArray ageElems = (JSONArray) ageObject.get(key);
			
			for (int target = 0; target < ageElems.size(); target++) {
				finalList.add((String) ageElems.get(target));
				
				if (Material.matchMaterial((String) ageElems.get(target)) == null) {
					LogManager.getInstanceSystem().logSystemMsg("Material [" + (String) ageElems.get(target) + "] does not exists !");
				}
			}
			
			ageElems.clear();
		}
		
		ageObject.clear();
		
		return finalList;
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
		List<String> finalList = new ArrayList<>();
		
		for (String s : targets.get(ageName)) {
			if (!done.contains(s)) {
				finalList.add(s);
			}
		}
		
		if (finalList.size() == 1) {
			return finalList.get(0);
		}
		
		return finalList.get(ThreadLocalRandom.current().nextInt(finalList.size()));
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
		int invCnt = 0;
		int nbInv = 1;
		boolean hasNext = ageTargets.size() > 45;

		
		if (ageTargets.size() > 45) {
			inv = Bukkit.createInventory(null, 54, "§8" + age + " Targets Tab 1");
		} else {
			inv = Bukkit.createInventory(null, 54, "§8" + age + " Targets");
		}
		
		setupFirstRow(inv, true, hasNext);
		
		for (String target : ageTargets) {
			inv.addItem(getMaterial(target));
			
			if (invCnt == 53) {
				invCnt = 0;
				invs.add(inv);
				nbInv++;
				inv = Bukkit.createInventory(null, 54, "§8" + age + " Targets Tab " + nbInv);
				
				setupFirstRow(inv, false, hasNext);
			} else {
				invCnt++;
			}
		}
		
		inv.setItem(8, ItemUtils.itemMakerName(Material.LIME_STAINED_GLASS_PANE, 1, " "));
		
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
		ItemStack bluePane = ItemUtils.itemMakerName(Material.BLUE_STAINED_GLASS_PANE, 1, "Next ->");
		ItemStack limePane = ItemUtils.itemMakerName(Material.LIME_STAINED_GLASS_PANE, 1, " ");
		ItemStack redPane = ItemUtils.itemMakerName(Material.RED_STAINED_GLASS_PANE, 1, "<- Previous");
		
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
			if (mat.getKey().toString().split(":")[1].equals(target) && mat.isItem()) {
				return new ItemStack(mat);
			}
		}
		
		ItemStack retItem;
		
		for (Material mat : Material.values()) {
			if (mat.getKey().toString().split(":")[1].contains(target) && mat.isItem()) {
				retItem = ItemUtils.itemMakerName(mat, 1, target);

				return retItem;
			}
		}
		
		for (Material mat : Material.values()) {
			if (target.contains(mat.getKey().toString().split(":")[1]) && mat.isItem()) {
				retItem = ItemUtils.itemMakerName(mat, 1, target);

				return retItem;
			}
		}
		
		return null;
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
	 * @return the list of targets of the Age <age>
	 */
	public static List<String> getAgeTargets(String age) {
		return Collections.unmodifiableList(targets.get(age));
	}
	
	/**
	 * Gets the Target invs list for a specific Age as an unmodifiable list
	 * 
	 * @param age	The age
	 * 
	 * @return the list of targets of the Age <age>
	 */
	public static List<Inventory> getAgeTargetsInvs(String age) {
		return Collections.unmodifiableList(targetsInvs.get(age));
	}
}
