package main.fr.kosmosuniverse.kuffle.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * 
 * @author KosmosUniverse
 *
 */

public class ItemUtils {
	/**
	 * Private ItemUtils constructor
	 * 
	 * @throws IllegalStateException
	 */
	private ItemUtils() {
		throw new IllegalStateException("Utility class");
	}
	
	/**
	 * Makes an Item with custom name
	 * 
	 * @param material	The item type
	 * @param amount	The amount of this item
	 * @param name		The name of this item
	 * 
	 * @return the item with the previous characteristics
	 */
	public static ItemStack itemMaker(Material material, int amount, String name) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta itM = item.getItemMeta();
		
		itM.setDisplayName(name);
		item.setItemMeta(itM);
		
		return item;
	}
	
	/**
	 * Makes an item with custom lore
	 * 
	 * @param material	The item type
	 * @param amount	The amount of this item
	 * @param lore		The lore as a List of this item
	 * 
	 * @return the item with the previous characteristics
	 */
	public static ItemStack itemMaker(Material material, int amount, List<String> lore) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta itM = item.getItemMeta();
		
		itM.setLore(lore);
		item.setItemMeta(itM);
		
		return item;
	}
	
	/**
	 * Makes an item with custom name and lore
	 * 
	 * @param material	The item type
	 * @param amount	The amount of this item
	 * @param name		The name of this item
	 * @param rawLore	The lore as an Array of this item
	 * 
	 * @return the item with the previous characteristics
	 */
	public static ItemStack itemMaker(Material material, int amount, String name, String... rawLore) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta itM = item.getItemMeta();
		List<String> lore = new ArrayList<>();		
		
		Collections.addAll(lore, rawLore);
		
		itM.setDisplayName(name);
		itM.setLore(lore);
		item.setItemMeta(itM);
		
		return item;
	}
	
	/**
	 * Makes an item with custom name and lore
	 * 
	 * @param material	The item type
	 * @param amount	The amount of this item
	 * @param name		The name of this item
	 * @param lore		The lore as a List of this item
	 * 
	 * @return the item with the previous characteristics
	 */
	public static ItemStack itemMaker(Material material, int amount, String name, List<String> lore) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta itM = item.getItemMeta();
		
		itM.setDisplayName(name);
		itM.setLore(lore);
		item.setItemMeta(itM);
		
		return item;
	}
	
	/**
	 * Compares two items by Material, itemMeta, DisplayName and Lore
	 * 
	 * @param first				The first item
	 * @param second			The second item
	 * 
	 * @return True if both items are the same, False instead
	 */
	public static boolean itemComparison(ItemStack first, ItemStack second) {
		boolean retValue = true;
		
		retValue = !(first == null || second == null);
		
		if (retValue) {
			retValue = first.getType() == second.getType();
		}
		
		if (retValue) {
			retValue = first.hasItemMeta() == second.hasItemMeta();
		}
		
		if (retValue && first.hasItemMeta()) {
			retValue = first.getItemMeta().hasDisplayName() == second.getItemMeta().hasDisplayName();
			
			if (retValue && first.getItemMeta().hasDisplayName()) {
				retValue = first.getItemMeta().getDisplayName().equalsIgnoreCase(second.getItemMeta().getDisplayName());
			}
		}
		
		if (retValue && first.hasItemMeta()) {
			retValue = first.getItemMeta().hasLore() == second.getItemMeta().hasLore();
			
			if (retValue && first.getItemMeta().hasLore()) {
				retValue = compareLoreElements(first, second);
			}
		}

		return retValue;
	}
	
	/**
	 * Compares two item's lore
	 * 
	 * @param first		The first item
	 * @param second	The second item
	 * 
	 * @return True if lore are same, False instead
	 */
	private static boolean compareLoreElements(ItemStack first, ItemStack second) {
		List<String> firstLore = first.getItemMeta().getLore();
		List<String> secondLore = second.getItemMeta().getLore();
		
		if (firstLore.size() != secondLore.size()) {
			return false;
		}
		
		for (int i = 0; i < firstLore.size(); i++) {
			if (!firstLore.get(i).equals(secondLore.get(i))) {
				return false;
			}
		}
		
		return true;
	}
}
