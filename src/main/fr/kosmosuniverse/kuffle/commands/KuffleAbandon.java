package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

public class KuffleAbandon implements CommandExecutor  {
	@Override
	public boolean onCommand(CommandSender sender, Command cnd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		KuffleMain.systemLogs.logMsg(player.getName(), Utils.getLangString(player.getName(), "CMD_PERF").replace("<#>", "<ki-abandon>"));
		
		if (!player.hasPermission("ki-abandon")) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NOT_ALLOWED"));
			return false;
		}
		
		if (KuffleMain.gameStarted) {
			if (!KuffleMain.games.containsKey(player.getName())) {
				KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NOT_PLAYING"));
				return true;
			}
			
			KuffleMain.games.get(player.getName()).setLose(true);
		} else {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "GAME_NOT_LAUNCHED"));
		}

		return true;
	}
}
