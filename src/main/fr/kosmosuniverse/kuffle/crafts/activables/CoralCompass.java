package main.fr.kosmosuniverse.kuffle.crafts.activables;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.crafts.ACrafts;
import main.fr.kosmosuniverse.kuffle.utils.ItemUtils;

public class CoralCompass extends ACrafts {
	public CoralCompass() {
		name = "CoralCompass";
		item = ItemUtils.itemMakerFull(Material.COMPASS, 1, ChatColor.GOLD + name, "This compass shows you", "the nearest warm ocean biome.");
		recipe = new ShapedRecipe(new NamespacedKey(KuffleMain.current, name), item);
		
		((ShapedRecipe) recipe).shape("DLD", "PSP", "DLD");
		((ShapedRecipe) recipe).setIngredient('D', Material.DARK_PRISMARINE);
		((ShapedRecipe) recipe).setIngredient('L', Material.SEA_LANTERN);
		((ShapedRecipe) recipe).setIngredient('P', Material.PRISMARINE_BRICKS);
		((ShapedRecipe) recipe).setIngredient('S', Material.SPONGE);
	}
	
	@Override
	public Inventory getInventoryRecipe() {
		Inventory inv = Bukkit.createInventory(null,  27, "§8" + name);
		
		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, redPane);
			} else if (i == 3 || i == 5 || i == 21 || i == 23) {
				inv.setItem(i, new ItemStack(Material.DARK_PRISMARINE));
			} else if (i == 12 || i == 14) {
				inv.setItem(i, new ItemStack(Material.PRISMARINE_BRICKS));
			} else if (i == 13) {
				inv.setItem(i, new ItemStack(Material.SPONGE));
			} else if (i == 4 || i == 22) {
				inv.setItem(i, new ItemStack(Material.SEA_LANTERN));
			} else if (i == 16) {
				inv.setItem(i, item);
			} else {
				inv.setItem(i, limePane);
			}
		}
		
		return (inv);
	}
	
}
