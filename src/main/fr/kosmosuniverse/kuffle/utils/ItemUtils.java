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
	public static ItemStack itemMakerName(Material material, int amount, String name) {
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
	 * @param rawLore	The lore as an Array of this item
	 * 
	 * @return the item with the previous characteristics
	 */
	public static ItemStack itemMakerLore(Material material, int amount, String... rawLore) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta itM = item.getItemMeta();
		List<String> lore = new ArrayList<>();		
		
		Collections.addAll(lore, rawLore);
		
		itM.setLore(lore);
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
	public static ItemStack itemMakerLore(Material material, int amount, List<String> lore) {
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
	public static ItemStack itemMakerFull(Material material, int amount, String name, String... rawLore) {
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
	public static ItemStack itemMakerFull(Material material, int amount, String name, List<String> lore) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta itM = item.getItemMeta();
		
		itM.setDisplayName(name);
		itM.setLore(lore);
		item.setItemMeta(itM);
		
		return item;
	}
	
	/**
	 * Compares two items by Material and if hasItemMeta is true, by name and/or by lore
	 * 
	 * @param first				The first item
	 * @param second			The second item
	 * @param hasItemMeta		If True, it compares names and/or lores
	 * @param hasDisplayName	If True, it compares names
	 * @param hasLore			If True, it compares lores
	 * 
	 * @return True if both items are the same, False instead
	 */
	public static boolean itemComparison(ItemStack first, ItemStack second, boolean hasItemMeta, boolean hasDisplayName, boolean hasLore) {
		boolean retValue = true;
		
		retValue = first.getType() == second.getType();
		
		if (retValue && hasItemMeta) {
			retValue = first.hasItemMeta() == second.hasItemMeta();

			if (retValue) {
				retValue = first.hasItemMeta() == second.hasItemMeta();
			}
		}
		
		if (retValue) {
			retValue = compareName(first, second, hasItemMeta, hasDisplayName);
		}
		
		if (retValue) {
			retValue = compareLore(first, second, hasItemMeta, hasLore);
		}

		return retValue;
	}
	
	/**
	 * Compare first and second item's names
	 * 
	 * @param first				The first item
	 * @param second			The second item
	 * @param hasItemMeta		If True compares if both items has meta
	 * @param hasDisplayName	If True compares if both items has same name
	 * 
	 * @return True if first and second item's Names are same, False instead
	 */
	private static boolean compareName(ItemStack first, ItemStack second, boolean hasItemMeta, boolean hasDisplayName) {
		boolean retValue = true;
		
		if (hasItemMeta && hasDisplayName) {
			retValue = first.hasItemMeta() == second.hasItemMeta();

			if (retValue && (first.getItemMeta().hasDisplayName() == second.getItemMeta().hasDisplayName())) {
				retValue = first.getItemMeta().getDisplayName().equals(second.getItemMeta().getDisplayName());
			} else {
				retValue = false;
			}
		}
		
		return retValue;
	}
	
	/**
	 * Compare first and second item's lores
	 * 
	 * @param first				The first item
	 * @param second			The second item
	 * @param hasItemMeta		If True compares if both items has meta
	 * @param hasLore			If True compares if boht items has same lore
	 * 
	 * @return True if first and second item's Lore are same, False instead
	 */
	private static boolean compareLore(ItemStack first, ItemStack second, boolean hasItemMeta, boolean hasLore) {
		boolean retValue = true;
		
		if (hasItemMeta && hasLore) {
			retValue = first.hasItemMeta() == second.hasItemMeta();

			if (retValue && (first.getItemMeta().hasLore() == second.getItemMeta().hasLore())) {
				retValue = compareLoreElements(first, second);
			} else {
				retValue = false;
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
