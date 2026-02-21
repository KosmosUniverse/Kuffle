package fr.kosmosuniverse.kuffle.multiblock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	public Map<String, Inventory> createInventories(String mainInv) {
		Map<String, Inventory> invs = new HashMap<>();

		for (int counter = 0; counter < compose.size(); counter++) {
			invs.put(name + " Layer " + (counter + 1), createInventory(mainInv, counter));
		}

		return invs;
	}

	private Inventory createInventory(String mainInv, int counter) {
		Inventory inv = Bukkit.createInventory(null, 36, name + " Layer " + (counter + 1));

		setupFirstRow(inv,
				mainInv,
				counter == 0 ? null : name + " Layer " + counter,
				counter == compose.size() - 1 ? null : name + " Layer " + (counter + 2));

		for (int i = 9; i < 36; i++) {
			if ((i >= 12 && i <= 14) ||
					(i >= 21 && i <= 23) ||
					(i >= 30 && i <= 32)) {
				if (i == 22) {
					inv.setItem(i, ItemMaker.newItem(compose.get(counter)).getItem());
				}
			} else {
				inv.setItem(i, ItemMaker.newItem(ItemsUtils.getLimitPane()).getItem());
			}
		}

		return inv;
	}

	@Override
	public Location createLocation(Player player) {
		return null;
	}
}
