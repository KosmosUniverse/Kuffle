package main.fr.kosmosuniverse.kuffle.crafts.resources;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapelessRecipe;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.crafts.ACrafts;
import main.fr.kosmosuniverse.kuffle.utils.ItemUtils;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

public class RawGold extends ACrafts {
	MaterialChoice mc;
	
	public RawGold() {
		name = "RawGold";
		
		recipe = new ShapelessRecipe(new NamespacedKey(KuffleMain.current, name), new ItemStack(Material.RAW_GOLD, 2));
		
		List<Material> ores = new ArrayList<>();
		
		ores.add(Material.GOLD_ORE);
		
		if (Utils.findVersionNumber(Utils.getVersion()) >= Utils.findVersionNumber("1.17")) {
			ores.add(Material.DEEPSLATE_GOLD_ORE);
		}
		
		mc = new MaterialChoice(ores);
		
		((ShapelessRecipe) recipe).addIngredient(mc);
		
		item = new ItemStack(Material.RAW_GOLD, 2);
	}
	
	public Inventory getInventoryRecipe() {
		Inventory inv = Bukkit.createInventory(null,  27, "§8" + name);
		ItemStack customOre = mc.getChoices().size() > 1 ? ItemUtils.itemMakerName(Material.GOLD_ORE, 1, ChatColor.BLUE + "Any" + ChatColor.GREEN + " Gold " + ChatColor.RED + "Ore") : new ItemStack(Material.GOLD_ORE);
		
		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, redPane);
			} else if (i == 3) {
				inv.setItem(i, customOre);
			} else if (i == 4 || i == 5 || i == 12 ||
					i == 13 || i == 14 || i == 21 || i == 22 || i == 23) {
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
