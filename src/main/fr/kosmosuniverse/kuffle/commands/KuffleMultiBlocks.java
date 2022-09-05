package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.multiblock.MultiblockManager;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleMultiBlocks implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang("CMD_PERF", Config.getLang()).replace("<#>", "<kb-multiblocks>"));
		
		if (!player.hasPermission("kb-multiblocks")) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_ALLOWED", Config.getLang()));
			return false;
		}
		
		player.openInventory(MultiblockManager.getMultiblocksInventories());
		
		return true;
	}
}
