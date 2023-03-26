package main.fr.kosmosuniverse.kuffle.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import net.md_5.bungee.api.ChatColor;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleRestoreInv extends AKuffleCommand {
	public KuffleRestoreInv() {
		super("k-restoreinv", true, true, 1, 1, false);
	}

	@Override
	public boolean runCommand() throws KuffleCommandFalseException {
		if (args.length != 1) {
			return false;
		}
		
		if (!GameManager.hasPlayer(args[0])) {
			return true;
		}
		
		List<ItemStack> items = GameManager.getGames().get(args[0]).getDeathInv();
		
		if (items == null || items.isEmpty()) {
			LogManager.getInstanceGame().writeMsg(player, args[0] + " does not have any items in its saved inventory.");
			return true;
		}

		Inventory inv = Bukkit.createInventory(null, 54, ChatColor.BLACK + args[0] + " Inventory");
		
		items.forEach(item -> inv.addItem(item));
		player.openInventory(inv);
		
		return true;
	}
}
