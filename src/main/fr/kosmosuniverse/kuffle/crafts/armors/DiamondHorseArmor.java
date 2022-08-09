package main.fr.kosmosuniverse.kuffle.crafts.armors;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapedRecipe;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.crafts.ACrafts;
import main.fr.kosmosuniverse.kuffle.utils.ItemUtils;

public class DiamondHorseArmor extends ACrafts {
	MaterialChoice mc;
	
	public DiamondHorseArmor() {
		name = "DiamondHorseArmor";
		
		recipe = new ShapedRecipe(new NamespacedKey(KuffleMain.current, name), new ItemStack(Material.DIAMOND_HORSE_ARMOR));
		
		List<Material> wools = new ArrayList<>();
		
		for (Material m : Material.values()) {
			if (m.name().toLowerCase().contains("wool") && !m.name().toLowerCase().contains("legacy")) {
				wools.add(m);
			}
		}
		
		mc = new MaterialChoice(wools);
		
		((ShapedRecipe) recipe).shape("RRD", "DWD", "DDD");
		((ShapedRecipe) recipe).setIngredient('D', Material.DIAMOND);
		((ShapedRecipe) recipe).setIngredient('W', mc);
		
		item = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
	}
	
	public Inventory getInventoryRecipe() {
		Inventory inv = Bukkit.createInventory(null,  27, "§8" + name);
		ItemStack anyWool = ItemUtils.itemMakerName(Material.GRAY_WOOL, 1, ChatColor.BLUE + "Any" + ChatColor.GREEN + " Wool " + ChatColor.RED + "Color");
		
		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, redPane);
			} else if (i == 5 || i == 12 || i == 14 || i == 21 || i == 22 || i == 23) {
				inv.setItem(i, new ItemStack(Material.DIAMOND));
			} else if (i == 13) {
				inv.setItem(i, anyWool);
			} else if (i == 3 || i == 4) {
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
