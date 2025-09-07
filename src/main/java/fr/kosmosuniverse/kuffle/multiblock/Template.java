package fr.kosmosuniverse.kuffle.multiblock;

import java.util.List;
import java.util.Objects;

import fr.kosmosuniverse.kuffle.core.AgeManager;
import fr.kosmosuniverse.kuffle.core.Party;
import fr.kosmosuniverse.kuffle.utils.ItemMaker;
import fr.kosmosuniverse.kuffle.utils.ItemsUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class Template extends AMultiblock {
	final List<Material> compose;
	
	public Template(String templateName, List<Material> tmp) {
		name = templateName;
		compose = tmp;
		squareSize = 1;
		item = ItemMaker.newItem(compose.get(compose.size() - 1)).addName(name).getItem();
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

		String age = AgeManager.getAgeByNumber(Party.getInstance().getGames().getGames().get(player.getName()).getAge()).getName();
	
		if (!name.contains(age)) {
			return ;
		}

		Party.getInstance().getGames().playerFoundSbtt(player.getName());
		Party.getInstance().getPlayers().getList().forEach(playerName -> Objects.requireNonNull(Bukkit.getPlayer(playerName)).sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + player.getName() + ChatColor.RESET + ChatColor.BLUE + " just used Template !"));
		Party.getInstance().getSpectators().getList().forEach(playerName -> Objects.requireNonNull(Bukkit.getPlayer(playerName)).sendMessage(ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + player.getName() + ChatColor.RESET + ChatColor.BLUE + " just used Template !"));

		MultiblockManager.reloadTemplate(age);
	}

	@Override
	public void createInventories() {
		for (int cnt = 0; cnt < compose.size(); cnt++) {
			invs.add(setupLayer(cnt));
		}
	}
	
	/**
	 * Create template layer
	 * 
	 * @param cnt	compose counter
	 * 
	 * @return the layer inventory
	 */
	private Inventory setupLayer(int cnt) {
		Inventory inv = Bukkit.createInventory(null, 27, ChatColor.BLACK + name + " Layer " + (cnt + 1));
		
		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, new ItemStack(cnt == 0 ? ItemsUtils.getBackPane() : ItemsUtils.getPreviousPane()));
			} else if (i == 8) {
				inv.setItem(i, new ItemStack(cnt == (compose.size() - 1) ? ItemsUtils.getLimitPane() : ItemsUtils.getNextPane()));
			} else if (i == 13) {
				inv.setItem(i, new ItemStack(compose.get(cnt)));
			} else {
				inv.setItem(i, new ItemStack(ItemsUtils.getLimitPane()));
			}
		}
		
		return inv;
	}

	@Override
	public Location createLocation(Player player) {
		return null;
	}
}
