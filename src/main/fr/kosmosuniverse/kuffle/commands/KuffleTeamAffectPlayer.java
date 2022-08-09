package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

public class KuffleTeamAffectPlayer implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		KuffleMain.systemLogs.logMsg(player.getName(), Utils.getLangString(player.getName(), "CMD_PERF").replace("<#>", "<ki-team-affect-player>"));
		
		if (!player.hasPermission("ki-team-affect-player")) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NOT_ALLOWED"));
			return false;
		}
		
		if (KuffleMain.games.size() > 0 && KuffleMain.gameStarted) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "GAME_LAUNCHED"));
			return true;
		}
		
		if (args.length != 2) {
			return false;
		}
		
		if (!KuffleMain.teams.hasTeam(args[0])) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "TEAM_NOT_EXISTS").replace("<#>", "<" + args[0] + ">"));
			return true;
		}
		
		if (KuffleMain.teams.getTeam(args[0]).players.size() == KuffleMain.config.getTeamSize()) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "TEAM_FULL"));
			return true;
		}
		
		if (!KuffleMain.games.containsKey(args[1])) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "PLAYER_NOT_IN_GAME"));
			return true;
		}
		
		KuffleMain.teams.affectPlayer(args[0], KuffleMain.games.get(args[1]).getPlayer());
		KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "TEAM_ADD_PLAYER").replace("<#>", "<" + args[1] + ">").replace("<##>", "<" + args[0] + ">"));
		
		return true;
	}

}
