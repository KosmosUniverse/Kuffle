package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

public class KuffleTeamDelete implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		KuffleMain.systemLogs.logMsg(player.getName(), Utils.getLangString(player.getName(), "CMD_PERF").replace("<#>", "<ki-team-delete>"));
		
		if (!player.hasPermission("ki-team-delete")) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NOT_ALLOWED"));
			return false;
		}
		
		if (KuffleMain.games.size() > 0 && KuffleMain.gameStarted) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "GAME_LAUNCHED"));
			return true;
		}
				
		if (args.length != 1) {
			return false;
		}
		
		if (!KuffleMain.teams.hasTeam(args[0])) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "TEAM_NOT_EXISTS").replace("<#>", "<" + args[0] + ">"));
			return true;
		}
		
		KuffleMain.teams.deleteTeam(args[0]);
		
		KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "TEAM_DELETED").replace("<#>", "<" + args[0] + ">"));
		
		return true;
	}

}
