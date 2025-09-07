package fr.kosmosuniverse.kuffle.crafts;

import java.util.ArrayList;
import java.util.List;

import fr.kosmosuniverse.kuffle.KuffleMain;
import fr.kosmosuniverse.kuffle.core.Config;
import fr.kosmosuniverse.kuffle.utils.ItemMaker;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

public class Template extends ACraft {
	@Getter
	private final List<Material> compose;
	
	public Template(String craftName, List<Material> craftCompose) {
		compose = craftCompose;
		name = craftName;
		item = ItemMaker.newItem(Material.EMERALD).addName(ChatColor.DARK_RED + name).addLores("Single Use " + name.replace("Template", "") + " Template.", "Right click to validate your item.").getItem();
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
