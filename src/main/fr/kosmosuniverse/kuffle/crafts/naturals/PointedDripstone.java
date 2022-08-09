package main.fr.kosmosuniverse.kuffle.crafts.naturals;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.crafts.ACrafts;

public class PointedDripstone extends ACrafts {
	public PointedDripstone() {
		name = "PointedDripstone";
		
		recipe = new ShapelessRecipe(new NamespacedKey(KuffleMain.current, name), new ItemStack(Material.POINTED_DRIPSTONE, 4));
		
		((ShapelessRecipe) recipe).addIngredient(Material.DRIPSTONE_BLOCK);
		
		item = new ItemStack(Material.POINTED_DRIPSTONE, 4);
	}
	
	public Inventory getInventoryRecipe() {
		Inventory inv = Bukkit.createInventory(null,  27, "§8" + name);
		
		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, redPane);
			} else if (i == 3) {
				inv.setItem(i, new ItemStack(Material.DRIPSTONE_BLOCK));
			} else if (i == 4 || i == 5 || i == 12 ||
					i == 13 || i == 14 || i == 21 || i == 22 || i == 23) {
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
