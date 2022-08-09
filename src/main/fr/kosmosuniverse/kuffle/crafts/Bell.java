package main.fr.kosmosuniverse.kuffle.crafts;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.StonecuttingRecipe;

import main.fr.kosmosuniverse.kuffle.KuffleMain;

public class Bell extends ACrafts {
	public Bell() {
		name = "Bell-StoneCutter";
		
		recipe = new StonecuttingRecipe(new NamespacedKey(KuffleMain.current, name), new ItemStack(Material.BELL), Material.GOLD_BLOCK);
		
		item = new ItemStack(Material.BELL);
	}
	
	public Inventory getInventoryRecipe() {
		Inventory inv = Bukkit.createInventory(null,  27, "§8" + name);

		
		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, redPane);
			} else if (i == 13) {
				inv.setItem(i, new ItemStack(Material.GOLD_BLOCK));
			} else if (i == 16) {
				inv.setItem(i, item);
			} else {
				inv.setItem(i, limePane);
			}
		}
		
		return (inv);
	}
}
