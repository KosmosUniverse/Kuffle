package fr.kosmosuniverse.kuffle.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import fr.kosmosuniverse.kuffle.KuffleMain;
import fr.kosmosuniverse.kuffle.crafts.ACraft;
import fr.kosmosuniverse.kuffle.crafts.CraftImpl;
import fr.kosmosuniverse.kuffle.crafts.Template;
import fr.kosmosuniverse.kuffle.type.KuffleType;
import fr.kosmosuniverse.kuffle.utils.FileUtils;
import fr.kosmosuniverse.kuffle.utils.ItemsUtils;
import fr.kosmosuniverse.kuffle.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.Recipe;
import org.json.JSONObject;

import static org.bukkit.Bukkit.getServer;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class CraftManager {
	private static final List<ACraft> recipes = new ArrayList<>();
	private static final List<Inventory> inventories = new ArrayList<>();
	private static int slotCnt;
	private static final String TEMPLATE = "Template";
	
	private CraftManager() {
		throw new IllegalStateException("");
	}
	
	/**
	 * Setups the crafts (Mandatory and Optional if Crafts option is true)
	 * 
	 * @param gameType	The Game type to know which crafts to load
	 */
	public static int setupCrafts(KuffleType.Type gameType, String content) {
		JSONObject crafts = FileUtils.readJSONObjectFromContent(content);

		int i = 1;
		int badCrafts = 0;
		
		while (crafts.has(String.valueOf(i))) {
			JSONObject craft = crafts.getJSONObject(String.valueOf(i));
			
			String version = craft.getString("Version");
			String remVersion = craft.has("RemVersion") ? craft.getString("RemVersion") : null;
			String kuffleType = craft.getString("KuffleType");
			boolean mandatory = craft.getBoolean("Mandatory");

			if (VersionManager.isVersionValid(version, remVersion) &&
					(kuffleType.equals("BOTH") || gameType == KuffleType.Type.valueOf(kuffleType.toUpperCase())) &&
					(mandatory || Config.getCrafts())) {
				try {
					addCraft(new CraftImpl(craft));
				} catch (Exception e) {
					badCrafts++;
					Utils.logException(e);
				}
			}
			
			i++;
		}
		
		setupCraftsInventories();
		return badCrafts;
	}
	
	/**
	 * Clears the recipes list
	 */
	public static void clear() {
		removeCrafts();

		inventories.forEach(Inventory::clear);
		inventories.clear();
	}
	
	/**
	 * Add a craft to the recipes list
	 * 
	 * @param craft	The ACraft object to add
	 */
	public static void addCraft(ACraft craft) {
		recipes.add(craft);
	}
	
	/**
	 * Adds Crafts recipe into Minecraft
	 */
	public static void enableCrafts() {
		recipes.stream()
			.filter(craft -> craft.isMandatory() || Config.getCrafts())
			.forEach(craft -> KuffleMain.getInstance().getServer().addRecipe(craft.getRecipe()));
	}
	
	/**
	 * Makes player discover currently used crafts
	 * 
	 * @param player	The player that have to discover crafts
	 */
	public static void discoverCrafts(Player player) {
		List<NamespacedKey> keys = getGameKeyList();
		
		player.discoverRecipes(keys);
	}
	
	private static List<NamespacedKey> getGameKeyList() {
		return recipes.stream().filter(recipe -> recipe.isMandatory() || Config.getCrafts()).map(ACraft::getKey).collect(Collectors.toList());
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
		}
	}
	
	/**
	 * Removes Crafts from Minecraft
	 */
	public static void disableCrafts() {
		Iterator<Recipe> it = getServer().recipeIterator();
		Recipe recipe;

		while (it.hasNext()) {
			recipe = it.next();

			if (recipe != null &&
					recipes.stream().map(ACraft::getItem).collect(Collectors.toList()).contains(recipe.getResult())) {
				it.remove();
			}
 		}
	}
	
	/**
	 * Makes a player undiscovered recipes
	 * 
	 * @param player	The player that have to undiscovered recipe
	 */
	public static void undiscoverCrafts(Player player) {
		recipes.stream()
			.filter(craft -> craft.isMandatory() || Config.getCrafts())
			.forEach(craft -> player.undiscoverRecipe(craft.getKey()));
	}
	
	/**
	 * Removes all crafts
	 */
	public static void removeCrafts() {
		List<String> names = recipes.stream().map(ACraft::getName).collect(Collectors.toList());
		
		names.forEach(CraftManager::removeCraft);
		names.clear();
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
				inv.setItem(i, ItemsUtils.getBackPane());
			} else if (i == 8 && !last) {
				inv.setItem(i, ItemsUtils.getNextPane());
			} else if (i < 9) {
				inv.setItem(i, ItemsUtils.getLimitPane());
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
			if (ItemsUtils.itemComparison(craft.getItem(), item)) {
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
			if (Objects.requireNonNull(item.getItemMeta()).getDisplayName().equals("<- Back")) {
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
	 * Reloads the templates
	 * 
	 * @param name	The old template name
	 */
	public static void reloadTemplate(String name) {
		String tmpName = name.replace(String.valueOf(ChatColor.DARK_RED), "");
		
		
		removeCraft(tmpName);

		Template t = new Template(tmpName, getMaterials(tmpName.replace(TEMPLATE, "_Age")));

		addCraft(t);

		/*Objects.requireNonNull(GameManager.getGames()).forEach((playerName, game) ->
			game.getPlayer().discoverRecipe(new NamespacedKey(KuffleMain.getInstance(), t.getName()))
		);*/
		
		reloadInventories();
	}
	
	/**
	 * Reloads the inventories
	 */
	private static void reloadInventories() {
		inventories.forEach(Inventory::clear);
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
					ItemsUtils.itemComparison(item, recipe.getItem())) {
				ret = true;
				break;
			}
		}
		
		return ret;
	}
}
