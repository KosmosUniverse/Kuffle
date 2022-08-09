package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

public class KuffleTeamShow implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		KuffleMain.systemLogs.logMsg(player.getName(), Utils.getLangString(player.getName(), "CMD_PERF").replace("<#>", "<ki-team-show>"));
		
		if (!player.hasPermission("ki-team-show")) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NOT_ALLOWED"));
			return false;
		}
		
		if (args.length > 1) {
			return false;
		}
		
		if (args.length == 0) {
			KuffleMain.systemLogs.writeMsg(player, KuffleMain.teams.toString());
		} else if (args.length == 1) {
			if (KuffleMain.teams.hasTeam(args[0])) {
				KuffleMain.systemLogs.writeMsg(player, KuffleMain.teams.printTeam(args[0]));
			} else {
				KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "TEAM_NOT_EXISTS").replace("<#>", "<" + args[0] + ">"));
			}
		}
		
		return true;
	}

}
