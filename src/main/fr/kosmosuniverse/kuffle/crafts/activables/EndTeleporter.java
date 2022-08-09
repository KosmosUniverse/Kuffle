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

public class EndTeleporter extends ACrafts {
	public EndTeleporter() {
		name = "EndTeleporter";
		
		item = ItemUtils.itemMakerFull(Material.EMERALD, 1, ChatColor.DARK_GREEN + name, "Single Use Teleporter.", "Right click to teleport to The End.");
		
		recipe = new ShapedRecipe(new NamespacedKey(KuffleMain.current, name), item);
		
		((ShapedRecipe) recipe).shape("GWD", "RER", "QLQ");
		((ShapedRecipe) recipe).setIngredient('G', Material.GOLD_BLOCK);
		((ShapedRecipe) recipe).setIngredient('W', Material.WATER_BUCKET);
		((ShapedRecipe) recipe).setIngredient('D', Material.DIAMOND_BLOCK);
		((ShapedRecipe) recipe).setIngredient('R', Material.RED_NETHER_BRICKS);
		((ShapedRecipe) recipe).setIngredient('E', Material.ENDER_PEARL);
		((ShapedRecipe) recipe).setIngredient('Q', Material.QUARTZ_PILLAR);
		((ShapedRecipe) recipe).setIngredient('L', Material.LAVA_BUCKET);
	}
	
	@Override
	public Inventory getInventoryRecipe() {
		Inventory inv = Bukkit.createInventory(null,  27, "§8" + name);
		
		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, redPane);
			} else if (i == 3) {
				inv.setItem(i, new ItemStack(Material.GOLD_BLOCK));
			} else if (i == 4) {
				inv.setItem(i, new ItemStack(Material.WATER_BUCKET));
			} else if (i == 5) {
				inv.setItem(i, new ItemStack(Material.DIAMOND_BLOCK));
			} else if (i == 12 || i == 14) {
				inv.setItem(i, new ItemStack(Material.RED_NETHER_BRICKS));
			} else if (i == 13) {
				inv.setItem(i, new ItemStack(Material.ENDER_PEARL));
			} else if (i == 21 || i == 23) {
				inv.setItem(i, new ItemStack(Material.QUARTZ_PILLAR));
			} else if (i == 22) {
				inv.setItem(i, new ItemStack(Material.LAVA_BUCKET));
			} else if (i == 16) {
				inv.setItem(i, item);
			} else {
				inv.setItem(i, limePane);
			}
		}
		
		return (inv);
	}

}
