package main.fr.kosmosuniverse.kuffle.crafts;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import main.fr.kosmosuniverse.kuffle.utils.ItemUtils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public abstract class ACrafts {
	protected String name;
	protected ItemStack item;
	protected Recipe recipe;
	
	protected ItemStack grayPane = ItemUtils.itemMakerName(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1, " ");
	protected ItemStack limePane = ItemUtils.itemMakerName(Material.LIME_STAINED_GLASS_PANE, 1, " ");
	protected ItemStack redPane = ItemUtils.itemMakerName(Material.RED_STAINED_GLASS_PANE, 1, "<- Back");
	
	/**
	 * Get an inventory that represent the craft shape
	 * 
	 * @return the inventory
	 */
	public abstract Inventory getInventoryRecipe();
	
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
}
