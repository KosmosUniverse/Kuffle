package main.fr.kosmosuniverse.kuffle.crafts.ores;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.crafts.ACrafts;

public class GoldOre extends ACrafts{
	public GoldOre() {
		name = "GoldOre";
		
		recipe = new ShapedRecipe(new NamespacedKey(KuffleMain.current, name), new ItemStack(Material.GOLD_ORE));
		
		
		((ShapedRecipe) recipe).shape("SGR", "GSR", "RRR");
		((ShapedRecipe) recipe).setIngredient('S', Material.STONE);
		((ShapedRecipe) recipe).setIngredient('G', Material.RAW_GOLD);
		
		item = new ItemStack(Material.GOLD_ORE);
	}
	
	public Inventory getInventoryRecipe() {
		Inventory inv = Bukkit.createInventory(null,  27, "§8" + name);
		
		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, redPane);
			} else if (i == 3 || i == 13) {
				inv.setItem(i, new ItemStack(Material.STONE));
			} else if (i == 4 || i == 12) {
				inv.setItem(i, new ItemStack(Material.RAW_GOLD));
			} else if (i == 5 || i == 14 || i == 21 || i == 22 || i == 23) {
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
