package main.fr.kosmosuniverse.kuffle.core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.crafts.ACraft;
import main.fr.kosmosuniverse.kuffle.crafts.CraftImpl;
import main.fr.kosmosuniverse.kuffle.crafts.Template;
import main.fr.kosmosuniverse.kuffle.type.KuffleType;
import main.fr.kosmosuniverse.kuffle.utils.ItemUtils;
import net.md_5.bungee.api.ChatColor;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class CraftManager {
	private static List<ACraft> recipes = new ArrayList<>();
	private static List<Inventory> inventories = new ArrayList<>();
	private static ItemStack limePane = ItemUtils.itemMaker(Material.LIME_STAINED_GLASS_PANE, 1, " ");
	private static ItemStack redPane = ItemUtils.itemMaker(Material.RED_STAINED_GLASS_PANE, 1, "<- Back");
	private static ItemStack bluePane = ItemUtils.itemMaker(Material.BLUE_STAINED_GLASS_PANE, 1, "Next ->");
	private static int slotCnt;
	private static final String TEMPLATE = "Template";
	
	private CraftManager() {
		throw new IllegalStateException("");
	}
	
	/**
	 * Setups the crafts (Mandatory and Optional if Crafts option is true)
	 * 
	 * @param gameType	The Game type to know which crafts to load
	 * 
	 * @throws ParseException raised by JSON parser at crafts.json file reading
	 */
	public static void setupCrafts(KuffleType.Type gameType, String content) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject crafts = (JSONObject) parser.parse(content);
		int i = 1;
		
		while (crafts.containsKey("" + i)) {
			JSONObject craft = (JSONObject) crafts.get("" + i);
			
			String version = craft.get("Version").toString();
			String remVersion = craft.containsKey("RemVersion") ? craft.get("RemVersion").toString() : null;
			String kuffleType = craft.get("KuffleType").toString();
			boolean mandatory = Boolean.parseBoolean(craft.get("Mandatory").toString().toUpperCase());
			
			if (VersionManager.isVersionValid(version, remVersion) &&
					(kuffleType.equals("BOTH") || gameType == KuffleType.Type.valueOf(kuffleType.toUpperCase())) &&
					(mandatory || Config.getCrafts())) {
				addCraft(new CraftImpl(craft));
			}
			
			i++;
		}
		
		setupCraftsInventories();
	}
	
	/**
	 * Clears the recipes list
	 */
	public static void clear() {
		if (recipes != null) {
			removeCrafts();
		}
		
		if (inventories != null) {
			inventories.forEach(i -> i.clear());
			inventories.clear();
		}
	}
	
	/**
	 * Add a craft to the recipes list
	 * 
	 * @param craft	The ACraft object to add
	 */
	public static void addCraft(ACraft craft) {
		recipes.add(craft);
		KuffleMain.getInstance().getServer().addRecipe(craft.getRecipe());
	}
	
	/**
	 * Removes a craft from the recipes list by name
	 * 
	 * @param name	The ACraft object name
	 */
	public static void removeCraft(String name) {
		ACraft craft = null;
		
		for (ACraft tmp : recipes) {
			if (tmp.getName().equals(name)) {
				craft = tmp;
			}
		}
		
		if (craft != null) {
			recipes.remove(craft);
			
			NamespacedKey n = new NamespacedKey(KuffleMain.getInstance(), name);
			KuffleMain.getInstance().getServer().removeRecipe(n);
		}
	}
	
	/**
	 * Removes all crafts
	 */
	public static void removeCrafts() {
		List<String> names = recipes.stream().map(recipe -> recipe.getName()).collect(Collectors.toList());
		
		names.forEach(CraftManager::removeCraft);
		names.clear();
	}
	
	/**
	 * Gets the recipes list
	 * 
	 * @return the recipes list as ACraft list
	 */
	public static List<ACraft> getRecipeList() {
		return (recipes);
	}
	
	/**
	 * Setup inventories that contains all crafts
	 */
	private static void setupCraftsInventories() {
		int recipesCnt = recipes.size();		
		int nbRows = recipesCnt / 9;
		
		nbRows = nbRows + (recipesCnt % 9 > 0 ? 1 : 0);
		
		int nbInvTotal = nbRows / 5;
		int nbRowsRest = nbRows % 5;
		int cnt = 1;
		slotCnt = 0;
				
		 for (int i = 0; i < nbInvTotal; i++) {
			 inventories.add(setupInventory(54, cnt, (nbRowsRest <= 0 && (i + 1) == nbInvTotal)));
			 cnt++;
		 }
		 
		 if (nbRowsRest > 0) {
			 inventories.add(setupInventory((nbRowsRest + 1) * 9, cnt, true));
		 }
	}
	
	/**
	 * Creates this craft recipe inventory base
	 */
	private static Inventory setupInventory(int nbSlot, int cnt, boolean last) {
		Inventory inv = Bukkit.createInventory(null,  nbSlot, ChatColor.BLACK + "AllCustomCrafts " + cnt);
		
		for (int i = 0; i < nbSlot; i++) {
			if (i == 0 && cnt != 1) {
				inv.setItem(i, redPane);
			} else if (i == 8 && !last) {
				inv.setItem(i, bluePane);
			} else if (i >= 0 && i < 9) {
				inv.setItem(i, limePane);
			} else if (slotCnt < recipes.size()) {
				inv.setItem(i, recipes.get(slotCnt).getItem());
				slotCnt++;
			}
		}
		
		return inv;
	}
	
	/**
	 * Get an Inventory containing all custom crafts
	 * 
	 * @return the inventory of all crafts
	 */
	public static Inventory getCraftsInventory() {
		return (inventories.get(0));
	}
	
	/**
	 * Gets the inventory of a specific @craft
	 * 
	 * @param craft	The craft searched
	 * 
	 * @return the @craft inventory
	 */
	public static Inventory getCraftsInventory(ACraft craft) {
		int idx = recipes.indexOf(craft);
		int invIdx = idx / 45;
		
		return (inventories.get(invIdx));
	}
	
	/**
	 * Gets ACraft object by ItemStack
	 * 
	 * @param item	the item as a key to find ACraft
	 * 
	 * @return the found ACraft object, null instead
	 */
	public static ACraft getCraftByItem(ItemStack item) {
		for (ACraft craft : recipes) {
			if (ItemUtils.itemComparison(craft.getItem(), item)) {
				return (craft);
			}
		}
		
		return null;
	}
	
	/**
	 * Gets ACraft object by Inventory
	 * 
	 * @param invName	the inventory name as a key to find craft
	 * 
	 * @return the found ACraft object, null instead
	 */
	public static ACraft getCraftByInventoryName(String invName) {
		for (ACraft craft : recipes) {
			String name = ChatColor.BLACK + craft.getName();
			
			if (invName.contains(name)) {
				return (craft);
			}
		}
		
		return null;
	}
	
	/**
	 * Gets the inventory depending on @current inv anc clicked @item
	 * 
	 * @param current	The current inventory
	 * @param item		The clicked item
	 * 
	 * @return the inventory
	 */
	public static Inventory getInventory(Inventory current, ItemStack item) {
		int idx = -1;
		
		for (Inventory inv : inventories) {
			if (inv.equals(current)) {
				idx = inventories.indexOf(inv);
				break;
			}
		}
		
		if (idx == -1) {
			return null;
		}
		
		if (item.getType() == Material.BLUE_STAINED_GLASS_PANE) {
			if (idx == inventories.size() - 1) {
				return null;
			}
			idx += 1;
			return (inventories.get(idx));
		} else if (item.getType() == Material.RED_STAINED_GLASS_PANE) {
			if (item.getItemMeta().getDisplayName().equals("<- Back")) {
				return (inventories.get(0));
			} else if (item.getItemMeta().getDisplayName().equals("<- Previous") && idx > 0) {
				idx -= 1;
				return (inventories.get(idx));
			}
		}
		
		return null;
	}
	
	/**
	 * Gets craft result by item name
	 * 
	 * @param itemName	the name to search in recipe results
	 * 
	 * @return the ItemStack object found by name, null instead
	 */
	public static ItemStack findItemByName(String itemName) {
		for (ACraft craft : recipes) {
			if (itemName.equals(craft.getName())) {
				return (craft.getItem());
			}
		}
		
		return null;
	}
	
	/**
	 * Setups the template items
	 */
	public static void setupCraftTemplates() {
		List<Template> templates = new ArrayList<>();

		for (int i = 0; i <= Config.getLastAge().getNumber(); i++)  {
			String name = AgeManager.getAgeByNumber(i).getName();

			name = name.replace("_Age", TEMPLATE);
			templates.add(new Template(name, getMaterials(AgeManager.getAgeByNumber(i).getName())));
		}

		for (Template t : templates) {
			addCraft(t);
		}
		
		reloadInventories();
	}

	/**
	 * Removes the template items
	 */
	public static void removeCraftTemplates() {
		for (int i = 0; i <= Config.getLastAge().getNumber(); i++)  {
			String name = AgeManager.getAgeByNumber(i).getName();
			
			name = name.replace("_Age", TEMPLATE);

			removeCraft(name);
		}
		
		reloadInventories();
	}
	
	/**
	 * Reloads templates
	 */
	public static void reloadTemplates() {
		for (Age age : AgeManager.getAges()) {
			if (age.getNumber() != -1) {
				reloadTemplate(age.getName().replace("_Age", TEMPLATE));
			}
		}
	}
	
	/**
	 * Reloads the templates
	 * 
	 * @param name	The old template name
	 */
	public static void reloadTemplate(String name) {
		String tmpName = name.replace("" + ChatColor.DARK_RED, "");
		
		
		removeCraft(tmpName);

		Template t = new Template(tmpName, getMaterials(tmpName.replace(TEMPLATE, "_Age")));

		addCraft(t);

		GameManager.getGames().forEach((playerName, game) ->
			game.getPlayer().discoverRecipe(new NamespacedKey(KuffleMain.getInstance(), t.getName()))
		);
		
		reloadInventories();
	}
	
	/**
	 * Reloads the inventories
	 */
	private static void reloadInventories() {
		inventories.forEach(i -> i.clear());
		inventories.clear();
		setupCraftsInventories();
	}
	
	/**
	 * Get Material list for templates in a specific Age
	 * 
	 * @param age	The specific Age
	 * 
	 * @return the material list
	 */
	private static List<Material> getMaterials(String age) {
		List<Material> compose = new ArrayList<>();
		List<String> done = new ArrayList<>();
		
		for (int cnt = 0; cnt < Config.getSBTTAmount(); cnt++) {
			done.add(TargetManager.newSbtt(done, age));
		}
		
		for (String item : done) {
			Material m = Material.matchMaterial(item.toUpperCase());
			
			if (m == null) {
				m = Material.valueOf(item.toUpperCase());
			}
			
			compose.add(m);
		}

		done.clear();
		return compose;
	}
	
	/**
	 * Checks if a specific item is a template
	 * 
	 * @param item	The item to check
	 * 
	 * @return True if item is a template, False instead
	 */
	public static boolean isTemplate(ItemStack item) {
		boolean ret = false;
		
		for (ACraft recipe : recipes) {
			if (recipe.getName().toLowerCase().contains("template") &&
					ItemUtils.itemComparison(item, recipe.getItem())) {
				ret = true;
				break;
			}
		}
		
		return ret;
	}
}
