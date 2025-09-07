package fr.kosmosuniverse.kuffle.commands;

import java.util.List;
import fr.kosmosuniverse.kuffle.core.LogManager;
import fr.kosmosuniverse.kuffle.core.Party;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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
	public boolean runCommand() {
		if (args.length != 1) {
			return false;
		}
		
		if (!Party.getInstance().getPlayers().has(args[0])) {
			return true;
		}
		
		List<ItemStack> items = Party.getInstance().getGames().getGames().get(args[0]).getDeathInv();
		
		if (items == null || items.isEmpty()) {
			LogManager.getInstanceGame().writeMsg(player, args[0] + " does not have any items in its saved inventory.");
			return true;
		}

		Inventory inv = Bukkit.createInventory(null, 54, ChatColor.BLACK + args[0] + " Inventory");
		
		items.forEach(inv::addItem);
		player.openInventory(inv);
		
		return true;
	}
}
