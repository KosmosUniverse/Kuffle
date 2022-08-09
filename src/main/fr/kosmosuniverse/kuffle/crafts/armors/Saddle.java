package main.fr.kosmosuniverse.kuffle.crafts.armors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.crafts.ACrafts;

public class Saddle extends ACrafts {
	public Saddle() {
		name = "Saddle";
		
		recipe = new ShapedRecipe(new NamespacedKey(KuffleMain.current, name), new ItemStack(Material.SADDLE));
		
		((ShapedRecipe) recipe).shape("LLL", "RLR", "TRT");
		((ShapedRecipe) recipe).setIngredient('L', Material.LEATHER);
		((ShapedRecipe) recipe).setIngredient('T', Material.TRIPWIRE_HOOK);
		
		item = new ItemStack(Material.SADDLE);
	}
	
	@Override
	public Inventory getInventoryRecipe() {
		Inventory inv = Bukkit.createInventory(null,  27, "§8" + name);
		
		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, redPane);
			} else if (i == 3 || i == 4 || i == 5 || i == 13) {
				inv.setItem(i, new ItemStack(Material.LEATHER));
			} else if (i == 21 || i == 23) {
				inv.setItem(i, new ItemStack(Material.TRIPWIRE_HOOK));
			} else if (i == 12 || i == 14 || i == 22) {
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
