package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.type.KuffleType;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleAbandon implements CommandExecutor  {
	@Override
	public boolean onCommand(CommandSender sender, Command cnd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		LogManager.getInstanceSystem().logMsg(player.getName(), LangManager.getMsgLang("CMD_PERF", Config.getLang()).replace("<#>", "<k-abandon>"));
		
		if (!player.hasPermission("k-abandon")) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_ALLOWED", Config.getLang()));
			return false;
		}
		
		if (KuffleMain.type.getType() == KuffleType.Type.NO_TYPE) {
			LogManager.getInstanceSystem().writeMsg(player, "Kuffle type not configured, please set it with /k-set-type");
			return true;
		}
		
		if (KuffleMain.gameStarted) {
			if (!GameManager.hasPlayer(player.getName())) {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_PLAYING", GameManager.getPlayerLang(player.getName())));
				return true;
			}
			
			GameManager.setLose(player.getName(), true);
		} else {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_NOT_LAUNCHED", GameManager.getPlayerLang(player.getName())));
		}

		return true;
	}
}
