package main.fr.kosmosuniverse.kuffle.crafts.ores;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.crafts.ACrafts;

public class OxidizedCopper extends ACrafts {
	public OxidizedCopper() {
		name = "OxidizedCopper";
		
		recipe = new ShapelessRecipe(new NamespacedKey(KuffleMain.current, name), new ItemStack(Material.OXIDIZED_COPPER));
		
		((ShapelessRecipe) recipe).addIngredient(Material.WEATHERED_COPPER);
		((ShapelessRecipe) recipe).addIngredient(Material.WATER_BUCKET);
		((ShapelessRecipe) recipe).addIngredient(Material.WATER_BUCKET);
		((ShapelessRecipe) recipe).addIngredient(Material.WATER_BUCKET);
		
		item = new ItemStack(Material.OXIDIZED_COPPER);
	}
	
	public Inventory getInventoryRecipe() {
		Inventory inv = Bukkit.createInventory(null,  27, "§8" + name);

		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, redPane);
			} else if (i == 4) {
				inv.setItem(i, new ItemStack(Material.WEATHERED_COPPER));
			} else if (i == 3 || i == 5 || i == 13) {
				inv.setItem(i, new ItemStack(Material.WATER_BUCKET));
			} else if (i == 12 || i == 14 || i == 21 || i == 22 || i == 23) {
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
