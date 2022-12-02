package main.fr.kosmosuniverse.kuffle.crafts;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.utils.ItemUtils;

public class Template extends ACraft {
	List<Material> compose;
	
	public Template(String _name, List<Material> craftCompose) {
		compose = craftCompose;
		name = _name;
		item = ItemUtils.itemMaker(Material.EMERALD, 1, ChatColor.DARK_RED + name, "Single Use " + name.replace("Template", "") + " Template.", "Right click to validate your item.");
		
		recipe = new ShapelessRecipe(new NamespacedKey(KuffleMain.getInstance(), name), item);
		
		setupInventoryBase(Type.WORKBENCH);
		
		List<ItemStack> ings = new ArrayList<>();
		
		for (int cnt = 0; cnt < Config.getSBTTAmount(); cnt++) {
			((ShapelessRecipe) recipe).addIngredient(compose.get(cnt));
			ings.add(new ItemStack(compose.get(cnt)));
		}
		
		addInvItems(ings);
	}
}
