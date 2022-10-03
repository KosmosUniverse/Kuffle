package main.fr.kosmosuniverse.kuffle.multiblock;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.utils.ItemUtils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class Template extends AMultiblock {
	List<Material> compose;
	
	public Template(String _name, List<Material> tmp) {
		name = _name;
		compose = tmp;
		
		squareSize = 1;
		
		item = ItemUtils.itemMaker(compose.get(compose.size() - 1), 1, name);
		
		multiblock = new MultiBlock(compose.get(compose.size() - 1));
		
		for (int i = 0; i < compose.size(); i++) {
			multiblock.addLevel(new Level(i - (compose.size() - 1), squareSize, new Pattern(compose.get(i), 0, i - (compose.size() - 1), 0)));
		}
		
		createInventories();
	}
	
	@Override
	public void onActivate(Player player, ActivationType type) {
		if (type != ActivationType.ACTIVATE) {
			return;
		}

		String age = GameManager.getPlayerAge(player.getName()).name;
	
		if (!name.contains(age)) {
			return ;
		}
		
		GameManager.sendMsgToPlayers(ChatColor.GOLD + "" + ChatColor.BOLD + player.getName() + ChatColor.RESET + "" + ChatColor.BLUE + " just used Template !");
		GameManager.playerFoundSBTT(player.getName());
		MultiblockManager.reloadTemplate(age);
	}

	@Override
	public void createInventories() {
		Inventory inv;
		
		ItemStack grayPane = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
		ItemStack limePane = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
		ItemStack redPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
		ItemStack redPanePrev = new ItemStack(Material.RED_STAINED_GLASS_PANE);
		ItemStack bluePane = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
		ItemMeta itM = grayPane.getItemMeta();
		
		itM.setDisplayName(" ");
		grayPane.setItemMeta(itM);
		itM = limePane.getItemMeta();
		itM.setDisplayName(" ");
		limePane.setItemMeta(itM);
		itM = redPane.getItemMeta();
		itM.setDisplayName("<- Back");
		redPane.setItemMeta(itM);
		itM = bluePane.getItemMeta();
		itM.setDisplayName("Next ->");
		bluePane.setItemMeta(itM);
		itM = redPanePrev.getItemMeta();
		itM.setDisplayName("<- Previous");
		redPanePrev.setItemMeta(itM);
		
		for (int cnt = 0; cnt < compose.size(); cnt++) {
			inv = Bukkit.createInventory(null, 27, ChatColor.BLACK + name + " Layer " + (cnt + 1));
			
			for (int i = 0; i < 27; i++) {
				if (i == 0) {
					inv.setItem(i, new ItemStack(cnt == 0 ? redPane : redPanePrev));
				} else if (i == 8) {
					inv.setItem(i, new ItemStack(cnt == (compose.size() - 1) ? limePane : bluePane));
				} else if (i == 13) {
					inv.setItem(i, new ItemStack(compose.get(cnt)));
				} else {
					inv.setItem(i, new ItemStack(limePane));
				}
			}
			
			invs.add(inv);
		}
	}
}
