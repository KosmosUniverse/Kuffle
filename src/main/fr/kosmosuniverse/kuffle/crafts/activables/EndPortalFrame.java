package main.fr.kosmosuniverse.kuffle.crafts.activables;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.crafts.ACrafts;

public class EndPortalFrame extends ACrafts {
	public EndPortalFrame() {
		name = "EndPortalFrame";
		
		recipe = new ShapedRecipe(new NamespacedKey(KuffleMain.current, name), new ItemStack(Material.END_PORTAL_FRAME));
		
		((ShapedRecipe) recipe).shape("EEE", "SOS", "SSS");
		((ShapedRecipe) recipe).setIngredient('E', Material.ENDER_PEARL);
		((ShapedRecipe) recipe).setIngredient('S', Material.END_STONE);
		((ShapedRecipe) recipe).setIngredient('O', Material.OBSIDIAN);
		
		item = new ItemStack(Material.END_PORTAL_FRAME);
	}
	
	@Override
	public Inventory getInventoryRecipe() {
		Inventory inv = Bukkit.createInventory(null,  27, "§8" + name);
		
		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, redPane);
			} else if (i == 3 || i == 4 || i == 5) {
				inv.setItem(i, new ItemStack(Material.ENDER_PEARL));
			} else if (i == 12 || i == 14 || i == 21 || i == 22 || i == 23) {
				inv.setItem(i, new ItemStack(Material.END_STONE));
			} else if (i == 13) {
				inv.setItem(i, new ItemStack(Material.OBSIDIAN));
			} else if (i == 16) {
				inv.setItem(i, item);
			} else {
				inv.setItem(i, limePane);
			}
		}
		
		return (inv);
	}
	
}
