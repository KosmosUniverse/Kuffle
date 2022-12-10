package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import main.fr.kosmosuniverse.kuffle.multiblock.AMultiblock;
import main.fr.kosmosuniverse.kuffle.multiblock.MultiblockManager;
import main.fr.kosmosuniverse.kuffle.type.KuffleType;
import main.fr.kosmosuniverse.kuffle.utils.CommandUtils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleSpawnMuliblock implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		Player player = null;
		
		try {
			player = CommandUtils.initCommand(sender, "k-spawn-multiblock", true, false, false);
		} catch (KuffleCommandFalseException e) {
			return false;
		}
		
		if (KuffleMain.getInstance().getType().getType() != KuffleType.Type.BLOCKS || args.length != 1) {
			return false;
		}
		
		AMultiblock tmp = MultiblockManager.searchMultiBlockByName(args[0]);
		
		if (tmp != null) {
			tmp.getMultiblock().spawnMultiBlock(player);
		}
		
		return true;
	}
}
