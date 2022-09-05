package main.fr.kosmosuniverse.kuffle.commands;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.Team;
import main.fr.kosmosuniverse.kuffle.core.TeamManager;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleTeamRandomPlayer implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		LogManager.getInstanceSystem().logMsg(player.getName(), LangManager.getMsgLang("CMD_PERF", Config.getLang()).replace("<#>", "<ki-team-random-player>"));
		
		if (!player.hasPermission("ki-team-random-player")) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_ALLOWED", Config.getLang()));
			return false;
		}
		
		if (KuffleMain.gameStarted && GameManager.getGames().size() > 0) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_LAUNCHED", Config.getLang()));
			return true;
		}
		
		if (args.length != 0) {
			return false;
		}
		
		if (GameManager.getGames().size() == 0) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("LIST_EMPTY", Config.getLang()));
			return true;
		}
		
		if (calcMaxPlayers() < GameManager.getPlayerNames().size()) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_TOO_MANY_PLAYERS", Config.getLang()));
			return true;
		}
		
		if (!checkEmptyTeams()) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_ALREADY_PLAYERS", Config.getLang()));
			return true;
		}
		
		int cnt = 0;
		List<Player> players = GameManager.getPlayerList();
		
		final ThreadLocalRandom random = ThreadLocalRandom.current();
		
		while (players.size() > 0) {
			int idx = random.nextInt(players.size());
			
			TeamManager.affectPlayer(TeamManager.getTeams().get(cnt).name, players.get(idx));
			
			players.remove(idx);
			
			cnt++;
			
			if (cnt >= TeamManager.getTeams().size()) {
				cnt = 0;
			}
		}
		
		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("RANDOM", Config.getLang()).replace("%i", "" + GameManager.getPlayerNames().size()).replace("%j", "" + TeamManager.getTeams().size()));

		return true;
	}
	
	/**
	 * Calculates the max amount of players allowed with config and team amount
	 * 
	 * @return the multiplication of Config team size and team number
	 */
	public int calcMaxPlayers() {
		return (Config.getTeamSize() * TeamManager.getTeams().size());
	}

	/**
	 * Checks if any team is empty
	 * 
	 * @return True if all team are empty, False instead
	 */
	public boolean checkEmptyTeams() {
		for (Team item : TeamManager.getTeams()) {
			if (item.players.size() != 0) {
				return false;
			}
		}
		
		return true;
	}
}
