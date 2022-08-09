package main.fr.kosmosuniverse.kuffle.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

public class KuffleTeamColor implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		KuffleMain.systemLogs.logMsg(player.getName(), Utils.getLangString(player.getName(), "CMD_PERF").replace("<#>", "<ki-team-color>"));
		
		if (!player.hasPermission("ki-team-color")) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NOT_ALLOWED"));
			return false;
		}
		
		if (KuffleMain.games.size() > 0 && KuffleMain.gameStarted) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "GAME_LAUNCHED"));
			return true;
		}
		
		if (args.length < 1 || args.length > 2) {
			return false;
		}
		
		if (!KuffleMain.teams.hasTeam(args[0])) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "TEAM_NOT_EXISTS").replace("<#>", "<" + args[0] + ">"));
			return true;
		}
		if (KuffleMain.teams.getTeam(args[0]).hasPlayer(args[1])) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "TEAM_PLAYER"));
			return true;
		}
		
		ChatColor tmp;
		
		if ((tmp = Utils.findChatColor(args[1])) == null) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "COLOR_NOT_EXISTS").replace("[#]", "[" + args[1] + "]"));
			return true;
		}
		
		List<String> colorUsed = KuffleMain.teams.getTeamColors();
		
		if (colorUsed.contains(tmp.name())) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "COLOR_ALREADY_USED").replace("[#]", "[" + tmp.name() + "]"));
			colorUsed.clear();
			return true;
		}
		
		colorUsed.clear();

		String tmpColor = KuffleMain.teams.getTeam(args[0]).color.name();
		
		KuffleMain.teams.changeTeamColor(args[0], tmp);	
		KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "COLOR_CHANGED").replace("[#]", "[" + tmpColor + "]").replace("[##]", "[" + tmp.name() + "]").replace("<#>",	"<" + args[0] + ">"));
		
		return true;
	}

}
