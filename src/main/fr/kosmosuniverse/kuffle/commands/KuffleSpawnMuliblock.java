package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.multiblock.AMultiblock;
import main.fr.kosmosuniverse.kuffle.multiblock.MultiblockManager;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleSpawnMuliblock implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang("CMD_PERF", Config.getLang()).replace("<#>", "<kb-spawn-multiblock>"));
		
		if (!player.hasPermission("kb-spawn-multiblock")) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_ALLOWED", Config.getLang()));
			return false;
		}
		
		if (args.length != 1) {
			return false;
		}
		
		AMultiblock tmp = MultiblockManager.searchMultiBlockByName(args[0]);
		
		if (tmp != null) {
			tmp.getMultiblock().spawnMultiBlock(player);
		}
		
		return true;
	}
}
