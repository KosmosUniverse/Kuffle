package main.fr.kosmosuniverse.kuffle.crafts.naturals;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.crafts.ACrafts;

public class RedSand extends ACrafts {
	
	public RedSand() {
		name = "RedSand";
		recipe = new ShapedRecipe(new NamespacedKey(KuffleMain.current, name), new ItemStack(Material.RED_SAND, 8));
		
		((ShapedRecipe) recipe).shape("SSS", "SRS", "SSS");
		((ShapedRecipe) recipe).setIngredient('S', Material.SAND);
		((ShapedRecipe) recipe).setIngredient('R', Material.RED_DYE);

		item = new ItemStack(Material.RED_SAND, 8);
	}
	
	public Inventory getInventoryRecipe() {
		Inventory inv = Bukkit.createInventory(null,  27, "§8" + name);
		
		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, redPane);
			} else if (i == 3 || i == 4 || i == 5 ||
					i == 12 || i == 14 || i == 21 || i == 22 || i == 23) {
				inv.setItem(i, new ItemStack(Material.SAND));
			} else if (i == 13) {
				inv.setItem(i, new ItemStack(Material.RED_DYE));
			} else if (i == 16) {
				inv.setItem(i, item);
			} else {
				inv.setItem(i, limePane);
			}
		}
		
		return (inv);
	}
}
