package main.fr.kosmosuniverse.kuffle.crafts.armors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.crafts.ACrafts;

public class ChainmailHelmet extends ACrafts {
	public ChainmailHelmet() {
		name = "ChainmailHelmet";
		
		recipe = new ShapedRecipe(new NamespacedKey(KuffleMain.current, name), new ItemStack(Material.CHAINMAIL_HELMET));
		
		((ShapedRecipe) recipe).shape("CCC", "CRC", "RRR");
		((ShapedRecipe) recipe).setIngredient('C', Material.CHAIN);
		
		item = new ItemStack(Material.CHAINMAIL_HELMET);
	}
	
	@Override
	public Inventory getInventoryRecipe() {
		Inventory inv = Bukkit.createInventory(null,  27, "�8" + name);
		
		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, redPane);
			} else if (i == 3 || i == 4 || i == 5 || i == 12 || i == 14) {
				inv.setItem(i, new ItemStack(Material.CHAIN));
			} else if (i == 13 || i == 21 || i == 22 || i == 23) {
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
