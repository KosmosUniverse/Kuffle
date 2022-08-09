package main.fr.kosmosuniverse.kuffle.crafts.naturals;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.crafts.ACrafts;

public class MossyCobblestone extends ACrafts {
	public MossyCobblestone() {
		name = "MossyCobblestone";
		
		recipe = new ShapelessRecipe(new NamespacedKey(KuffleMain.current, name), new ItemStack(Material.MOSSY_COBBLESTONE));
		
		((ShapelessRecipe) recipe).addIngredient(Material.COBBLESTONE);
		((ShapelessRecipe) recipe).addIngredient(Material.GRASS);
		
		item = new ItemStack(Material.MOSSY_COBBLESTONE);
	}
	
	public Inventory getInventoryRecipe() {
		Inventory inv = Bukkit.createInventory(null,  27, "§8" + name);
		
		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, redPane);
			} else if (i == 3) {
				inv.setItem(i, new ItemStack(Material.COBBLESTONE));
			} else if (i == 4) {
				inv.setItem(i, new ItemStack(Material.GRASS));
			} else if (i == 5 || i == 12 || i == 13 ||
					i == 14 || i == 21 || i == 22 || i == 23) {
				inv.setItem(i, grayPane);
			} else if (i == 16) {
				inv.setItem(i, item);
			} else {
				inv.setItem(i, limePane);
			}
		}
		
		return (inv);
	}
}
