package main.fr.kosmosuniverse.kuffle.crafts.ores;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.crafts.ACrafts;

public class QuartzOre extends ACrafts {
	public QuartzOre() {
		name = "QuartzOre";
		
		recipe = new ShapedRecipe(new NamespacedKey(KuffleMain.current, name), new ItemStack(Material.NETHER_QUARTZ_ORE));
		
		
		((ShapedRecipe) recipe).shape("NQR", "QNR", "RRR");
		((ShapedRecipe) recipe).setIngredient('N', Material.NETHERRACK);
		((ShapedRecipe) recipe).setIngredient('Q', Material.QUARTZ);
		
		item = new ItemStack(Material.NETHER_QUARTZ_ORE);
	}

	@Override
	public Inventory getInventoryRecipe() {
		Inventory inv = Bukkit.createInventory(null,  27, "§8" + name);

		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, redPane);
			} else if (i == 3 || i == 13) {
				inv.setItem(i, new ItemStack(Material.NETHERRACK));
			} else if (i == 4 || i == 12) {
				inv.setItem(i, new ItemStack(Material.QUARTZ));
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
