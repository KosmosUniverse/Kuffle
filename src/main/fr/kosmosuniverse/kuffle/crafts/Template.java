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
	private List<Material> compose;
	
	public Template(String craftName, List<Material> craftCompose) {
		compose = craftCompose;
		name = craftName;
		item = ItemUtils.itemMaker(Material.EMERALD, 1, ChatColor.DARK_RED + name, "Single Use " + name.replace("Template", "") + " Template.", "Right click to validate your item.");
		
		ShapelessRecipe r = new ShapelessRecipe(new NamespacedKey(KuffleMain.getInstance(), name), item);
		
		setupInventoryBase(Type.WORKBENCH);
		
		List<ItemStack> ings = new ArrayList<>();
		
		for (int cnt = 0; cnt < Config.getSBTTAmount(); cnt++) {
			r.addIngredient(compose.get(cnt));
			ings.add(new ItemStack(compose.get(cnt)));
		}
		
		key = r.getKey();
		recipe = r;
		
		addInvItems(ings);
	}
}
