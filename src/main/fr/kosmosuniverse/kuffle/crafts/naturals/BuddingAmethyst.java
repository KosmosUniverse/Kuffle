package main.fr.kosmosuniverse.kuffle.crafts.naturals;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.crafts.ACrafts;

public class BuddingAmethyst extends ACrafts {
	public BuddingAmethyst() {
		name = "BuddingAmethyst";
		
		recipe = new ShapedRecipe(new NamespacedKey(KuffleMain.current, name), new ItemStack(Material.BUDDING_AMETHYST));
		
		
		((ShapedRecipe) recipe).shape("ARA", "RAR", "ARA");
		((ShapedRecipe) recipe).setIngredient('A', Material.AMETHYST_BLOCK);
		
		item = new ItemStack(Material.BUDDING_AMETHYST);
	}
	
	public Inventory getInventoryRecipe() {
		Inventory inv = Bukkit.createInventory(null,  27, "§8" + name);
		
		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, redPane);
			} else if (i == 3 || i == 5 || i == 13 || i == 21 || i == 23) {
				inv.setItem(i, new ItemStack(Material.AMETHYST_BLOCK));
			} else if (i == 4 || i == 12 || i == 13 || i == 22) {
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
