package main.fr.kosmosuniverse.kuffle.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.utils.ItemUtils;
import main.fr.kosmosuniverse.kuffle.utils.Pair;

public class ItemManager {
	private ItemManager() {
		throw new IllegalStateException("Utility class");
	}
	
	public static Map<String, List<String>> getAllItems(List<Age> ages, String itemsContent, File dataFolder) {
		Map<String, List<String>> finalMap = new HashMap<>();
		
		int max = AgeManager.getAgeMaxNumber(ages);

		try {
			for (int ageCnt = 0; ageCnt <= max; ageCnt++) {
				finalMap.put(AgeManager.getAgeByNumber(ages, ageCnt).name, getAgeItems(AgeManager.getAgeByNumber(ages, ageCnt).name, itemsContent, dataFolder));
			}
		} catch (IOException | ParseException e) {
			KuffleMain.systemLogs.logSystemMsg(e.getMessage());
			finalMap = null;
		}
		
		return finalMap;
	}
	
	public static synchronized List<String> getAgeItems(String age, String itemsContent, File dataFolder) throws IOException, ParseException {
		List<String> finalList = new ArrayList<>();
		JSONObject items;
		JSONParser jsonParser = new JSONParser();

		try (FileWriter writer = new FileWriter(dataFolder.getPath() + File.separator + "logs.txt")) {
			items = (JSONObject) jsonParser.parse(itemsContent);
			
			JSONObject ageObject = new JSONObject();
			JSONArray agePart = new JSONArray();
			
			ageObject = (JSONObject) items.get(age);
						
			for (Object k : ageObject.keySet()) {
				agePart = (JSONArray) ageObject.get(k);
				for (int j = 0; j < agePart.size(); j++) {
					finalList.add((String) agePart.get(j));
					if (Material.matchMaterial((String) agePart.get(j)) == null) {
						writer.append((String) agePart.get(j));
					}
				}
			}
		
		}
		
		return finalList;
	}

	public static synchronized String newItem(List<String> done, List<String> allAgeItems) {	
		List<String> finalList = new ArrayList<>();
		
		if (allAgeItems == null) {
			return null;
		}
		
		for (String s : allAgeItems) {
			if (!done.contains(s)) {
				finalList.add(s);
			}
		}
		
		if (finalList.size() == 1) {
			return finalList.get(0);
		}
		
		return finalList.get(ThreadLocalRandom.current().nextInt(finalList.size()));
	}
	
	public static synchronized Pair nextItem(List<String> done, List<String> allAgeItems, int sameIdx) {	
		String testItem = allAgeItems.get(sameIdx);
		
		while (done.contains(testItem)) {
			sameIdx++;
			testItem = allAgeItems.get(sameIdx);
		}
		
		return (new Pair(sameIdx, testItem));
	}
	
	public static Map<String, List<Inventory>> getItemsInvs(Map<String, List<String>> allItems) {
		Map<String, List<Inventory>> invs = new HashMap<>();

		allItems.forEach((k, v) -> invs.put(k, getAgeInvs(k, v)));

		return invs;
	}

	public static List<Inventory> getAgeInvs(String age, List<String> ageItems) {
		List<Inventory> invs = new ArrayList<>();
		Inventory inv;
		int invCnt = 0;
		int nbInv = 1;
		boolean hasNext = ageItems.size() > 45;

		
		if (ageItems.size() > 45) {
			inv = Bukkit.createInventory(null, 54, "§8" + age + " Items Tab 1");
		} else {
			inv = Bukkit.createInventory(null, 54, "§8" + age + " Items");
		}
		
		setFirstRow(inv, true, hasNext);
		
		for (String item : ageItems) {
			inv.addItem(getMaterial(item));
			
			if (invCnt == 53) {
				invCnt = 0;
				invs.add(inv);
				nbInv++;
				inv = Bukkit.createInventory(null, 54, "§8" + age + " Items Tab " + nbInv);
				
				setFirstRow(inv, false, hasNext);
			} else {
				invCnt++;
			}
		}
		
		inv.setItem(8, ItemUtils.itemMakerName(Material.LIME_STAINED_GLASS_PANE, 1, " "));
		
		invs.add(inv);
		
		return invs;
	}
	
	private static void setFirstRow(Inventory inv, boolean isFirst, boolean hasNext) {
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
	
	private static ItemStack getMaterial(String item) {
		for (Material mat : Material.values()) {
			if (mat.getKey().toString().split(":")[1].equals(item) && mat.isItem()) {
				return new ItemStack(mat);
			}
		}
		
		ItemStack retItem;
		
		for (Material mat : Material.values()) {
			if (mat.getKey().toString().split(":")[1].contains(item) && mat.isItem()) {
				retItem = ItemUtils.itemMakerName(mat, 1, item);

				return retItem;
			}
		}
		
		for (Material mat : Material.values()) {
			if (item.contains(mat.getKey().toString().split(":")[1]) && mat.isItem()) {
				retItem = ItemUtils.itemMakerName(mat, 1, item);

				return retItem;
			}
		}
		
		return null;
	}
}
