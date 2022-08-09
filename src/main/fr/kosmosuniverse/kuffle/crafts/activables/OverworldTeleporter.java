package main.fr.kosmosuniverse.kuffle.crafts.activables;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.crafts.ACrafts;

public class OverworldTeleporter extends ACrafts {
	public OverworldTeleporter() {
		name = "OverworldTeleporter";
		
		item = new ItemStack(Material.EMERALD);
		
		ItemMeta itM = item.getItemMeta();
		itM.setDisplayName(ChatColor.DARK_BLUE + name);
		
		List<String> lore = new ArrayList<>();
		
		lore.add("Single Use Teleporter.");
		lore.add("Right click to teleport to The Overworld.");
		
		itM.setLore(lore);
		
		item.setItemMeta(itM);
		
		recipe = new ShapedRecipe(new NamespacedKey(KuffleMain.current, name), item);
		
		((ShapedRecipe) recipe).shape("RER", "PSP", "BEB");
		((ShapedRecipe) recipe).setIngredient('R', Material.END_ROD);
		((ShapedRecipe) recipe).setIngredient('P', Material.PURPUR_PILLAR);
		((ShapedRecipe) recipe).setIngredient('S', Material.SHULKER_SHELL);
		((ShapedRecipe) recipe).setIngredient('E', Material.ENDER_PEARL);
		((ShapedRecipe) recipe).setIngredient('B', Material.END_STONE_BRICKS);
	}
	
	@Override
	public Inventory getInventoryRecipe() {
		Inventory inv = Bukkit.createInventory(null,  27, "§8" + name);
		
		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, redPane);
			} else if (i == 3 || i == 5) {
				inv.setItem(i, new ItemStack(Material.END_ROD));
			} else if (i == 4 || i == 22) {
				inv.setItem(i, new ItemStack(Material.ENDER_PEARL));
			} else if (i == 12 || i == 14) {
				inv.setItem(i, new ItemStack(Material.PURPUR_PILLAR));
			} else if (i == 13) {
				inv.setItem(i, new ItemStack(Material.SHULKER_SHELL));
			} else if (i == 21 || i == 23) {
				inv.setItem(i, new ItemStack(Material.END_STONE_BRICKS));
			} else if (i == 16) {
				inv.setItem(i, item);
			} else {
				inv.setItem(i, limePane);
			}
		}
		
		return (inv);
	}

}
