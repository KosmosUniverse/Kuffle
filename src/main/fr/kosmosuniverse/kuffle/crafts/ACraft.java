package main.fr.kosmosuniverse.kuffle.crafts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.StonecuttingRecipe;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.utils.ItemUtils;
import net.md_5.bungee.api.ChatColor;

/**
 * 
 * @author KosmosUniverse
 *
 */
public abstract class ACraft {
	protected String name;
	protected ItemStack item;
	protected Recipe recipe;
	protected String type;
	protected Inventory inv;
	protected boolean mandatory;
	protected NamespacedKey key;
	
	protected ItemStack grayPane = ItemUtils.itemMaker(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1, " ");
	protected ItemStack limePane = ItemUtils.itemMaker(Material.LIME_STAINED_GLASS_PANE, 1, " ");
	protected ItemStack redPane = ItemUtils.itemMaker(Material.RED_STAINED_GLASS_PANE, 1, "<- Back");
	
	/**
	 * 
	 * @author KosmosUniverse
	 *
	 */
	protected enum Type {
		WORKBENCH,
		STONECUTTER
	}
	
	/**
	 * Setups the result item of this craft
	 * 
	 * @param resultObj	The result item as JSONObject
	 */
	protected final void setupResult(JSONObject resultObj) {
		String resultName = resultObj.containsKey("Name") ? resultObj.get("Name").toString() : null;
		ChatColor resultColor = resultObj.containsKey("Color") ? ChatColor.valueOf(resultObj.get("Color").toString()) : null;
		List<String> lore = resultObj.containsKey("Lore") ? jsonArrayToList((JSONArray) resultObj.get("Lore")) : null;
		Material resultType = Material.valueOf(resultObj.get("Type").toString().toUpperCase());
		int amount = Integer.parseInt(resultObj.get("Amount").toString());
		
		if (resultName != null && resultColor != null) {
			resultName = resultColor + resultName;
		}
		
		if (resultName == null && lore == null) {
			item = new ItemStack(resultType, amount);
		} else if (lore == null) {
			item = ItemUtils.itemMaker(resultType, amount, resultName);
		} else if (resultName == null) {
			item = ItemUtils.itemMaker(resultType, amount, lore);
		} else {
			item = ItemUtils.itemMaker(resultType, amount, resultName, lore);
		}
	}
	
	private List<String> jsonArrayToList(JSONArray array) {
		List<String> list = new ArrayList<>();
		
		for (Object elem : array) {
			list.add((String) elem);
		}
		
		return list;
	}
	
	/**
	 * Setups the recipe of this craft
	 * 
	 * @param recipeType	Recipe type
	 * @param shape			Recipe shape or null if shapeless recipe
	 * @param ingredients	Recipe components
	 */
	protected void setupRecipe(Type recipeType, String shape, Map<String, Map<String, String>> ingredients) {
		setupInventoryBase(recipeType);
		List<ItemStack> ings = new ArrayList<>();
		
		if (recipeType == Type.STONECUTTER) {
			ings = setupStonecutter(ingredients);
		} else if (recipeType == Type.WORKBENCH && shape != null) {
			ings = setupShaped(shape, ingredients);
		} else if (recipeType == Type.WORKBENCH) {
			ings = setupShapeless(ingredients);
		}
		
		addInvItems(ings);
	}
	
	private final List<ItemStack> setupStonecutter(Map<String, Map<String, String>> ingredients) {
		List<ItemStack> ings = new ArrayList<>();
		
		type = "STONECUTTER";
		Material ing = Material.valueOf(ingredients.get(ingredients.keySet().toArray()[0]).get("Type"));
		
		StonecuttingRecipe r = new StonecuttingRecipe(new NamespacedKey(KuffleMain.getInstance(), name), item, ing);
		
		key = r.getKey();
		recipe = r;
		
		ings.add(new ItemStack(ing));
		
		return ings;
	}
	
	private final List<ItemStack> setupShaped(String shape, Map<String, Map<String, String>> ingredients) {
		List<ItemStack> ings = new ArrayList<>();
		String[] shapeRows = shape.split("-");
		
		type = "SHAPED";
		
		ShapedRecipe r = new ShapedRecipe(new NamespacedKey(KuffleMain.getInstance(), name), item);
		r.shape(shapeRows[0], shapeRows[1], shapeRows[2]);
		
		for (String line : shapeRows) {
			for (char c : line.toCharArray()) {
				setupIngredients(r, ingredients, c, ings);
			}
		}
		
		key = r.getKey();
		recipe = r;
		
		return ings;
	}
	
	private static void setupIngredients(ShapedRecipe recipe, Map<String, Map<String, String>> ingredients, char c, List<ItemStack> ings) {
		if (ingredients.containsKey("" + c)) {
			String ingType = ingredients.get("" + c).get("Type").toUpperCase();
			
			if (ingType.contains(",")) {
				List<Material> list = new ArrayList<>();
				
				for (String s : ingType.split(",")) {
					list.add(Material.valueOf(s.toUpperCase()));
				}
				
				MaterialChoice mc = new MaterialChoice(list);

				recipe.setIngredient(c, mc);
				ings.add(ItemUtils.itemMaker(list.get(0), 1, ingredients.get("" + c).get("Name")));
			} else {
				recipe.setIngredient(c, Material.valueOf(ingType));
				ings.add(new ItemStack(Material.valueOf(ingType)));
			}
		} else {
			ings.add(null);
		}
	}
	
	private final List<ItemStack> setupShapeless(Map<String, Map<String, String>> ingredients) {
		List<ItemStack> ings = new ArrayList<>();
		
		type = "SHAPELESS";
		ShapelessRecipe r = new ShapelessRecipe(new NamespacedKey(KuffleMain.getInstance(), name), item);
		
		ingredients.forEach((c, m) -> {
			String ingType = m.get("Type").toUpperCase();
			
			if (ingType.contains(",")) {
				List<Material> list = new ArrayList<>();
				
				for (String s : ingType.split(",")) {
					list.add(Material.valueOf(s.toUpperCase()));
				}
				
				MaterialChoice mc = new MaterialChoice(list);

				r.addIngredient(mc);
				ings.add(ItemUtils.itemMaker(list.get(0), 1, m.get("Name")));
			} else {
				r.addIngredient(Material.valueOf(ingType));
				ings.add(new ItemStack(Material.valueOf(ingType)));
			}
		});
		
		key = r.getKey();
		recipe = r;
		
		return ings;
	}
	
	/**
	 * Creates this craft recipe inventory base
	 * 
	 * @param recipeType	The recipe type to put in inventory title
	 */
	protected final void setupInventoryBase(Type recipeType) {
		inv = Bukkit.createInventory(null, 27, ChatColor.BLACK + name + " - " + recipeType.toString());
		
		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, redPane);
			} else if (i == 16) {
				inv.setItem(i, item);
			} else if ((i < 3 || i > 5) && (i < 12 || i > 14) && (i < 21 || i > 23)) {
				inv.setItem(i, limePane);
			}
		}
	}
	
	/**
	 * Add the recipe to the Inventory base previously created
	 * 
	 * @param ings	The recipe components
	 */
	protected final void addInvItems(List<ItemStack> ings) {
		int cnt = 0;
		int i = 3;
		
		if (ings == null) {
			return ;
		}
		
		while (i < 24) {
			if (cnt >= ings.size() || ings.get(cnt) == null) {
				inv.setItem(i, grayPane);
			} else {
				inv.setItem(i, ings.get(cnt));
			}
			
			if (i == 5 || i == 14) {
				i += 7;
			} else {
				i++;
			}
			
			cnt++;
		}
	}
	
	/**
	 * Get the craft name
	 * 
	 * @return the name
	 */
	public String getName() {
		return (name);
	}
	
	/**
	 * Get the craft result item
	 * 
	 * @return the item
	 */
	public ItemStack getItem() {
		return (item);
	}
	
	/**
	 * Get the craft recipe
	 * 
	 * @return the recipe
	 */
	public Recipe getRecipe() {
		return (recipe);
	}
	
	/**
	 * Get an inventory that represent the craft shape
	 * 
	 * @return the inventory
	 */
	public Inventory getInventory() {
		return inv;
	}
	
	/**
	 * Get the mandatory property of the craft
	 * 
	 * @return the mandatory state
	 */
	public boolean isMandatory() {
		return mandatory;
	}
	
	/**
	 * Get the recipe NamespacedKey
	 * 
	 * @return the key
	 */
	public NamespacedKey getKey() {
		return key;
	}
}
