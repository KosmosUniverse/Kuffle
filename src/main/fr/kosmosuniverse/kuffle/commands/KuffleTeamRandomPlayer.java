package main.fr.kosmosuniverse.kuffle.commands;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Team;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

public class KuffleTeamRandomPlayer implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		KuffleMain.systemLogs.logMsg(player.getName(), Utils.getLangString(player.getName(), "CMD_PERF").replace("<#>", "<ki-team-random-player>"));
		
		if (!player.hasPermission("ki-team-random-player")) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NOT_ALLOWED"));
			return false;
		}
		
		if (KuffleMain.games.size() > 0 && KuffleMain.gameStarted) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "GAME_LAUNCHED"));
			return true;
		}
		
		if (args.length != 0) {
			return false;
		}
		
		if (KuffleMain.games.size() == 0) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "LIST_EMPTY"));
			return true;
		}
		
		if (calcMAxPlayers() < Utils.getPlayerList().size()) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "TEAM_TOO_MANY_PLAYERS"));
			return true;
		}
		
		if (!checkEmptyTeams()) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "TEAM_ALREADY_PLAYERS"));
			return true;
		}
		
		int cnt = 0;
		List<Player> players = Utils.getPlayerList();
		
		final ThreadLocalRandom random = ThreadLocalRandom.current();
		
		while (players.size() > 0) {
			int idx = random.nextInt(players.size());
			
			KuffleMain.teams.affectPlayer(KuffleMain.teams.getTeams().get(cnt).name, players.get(idx));
			
			players.remove(idx);
			
			cnt++;
			
			if (cnt >= KuffleMain.teams.getTeams().size()) {
				cnt = 0;
			}
		}
		
		KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "RANDOM").replace("%i", "" + Utils.getPlayerNames().size()).replace("%j", "" + KuffleMain.teams.getTeams().size()));

		return true;
	}
	
	public int calcMAxPlayers() {
		return (KuffleMain.config.getTeamSize() * KuffleMain.teams.getTeams().size());
	}

	public boolean checkEmptyTeams() {
		for (Team item : KuffleMain.teams.getTeams()) {
			if (item.players.size() != 0) {
				return false;
			}
		}
		
		return true;
	}
}
