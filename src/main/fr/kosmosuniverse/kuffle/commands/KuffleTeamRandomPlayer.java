package main.fr.kosmosuniverse.kuffle.commands;

import java.security.SecureRandom;
import java.util.List;

import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.Team;
import main.fr.kosmosuniverse.kuffle.core.TeamManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleTeamRandomPlayer extends AKuffleCommand {
	public KuffleTeamRandomPlayer() {
		super("k-team-random-player", null, false, 0, 0, true);
	}

	@Override
	public boolean runCommand() throws KuffleCommandFalseException {
		if (GameManager.getGames().size() > 0) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_LAUNCHED", Config.getLang()));
			throw new KuffleCommandFalseException();
		}
		
		if (GameManager.getGames().size() == 0) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("LIST_EMPTY", Config.getLang()));
			throw new KuffleCommandFalseException();
		}
		
		if (calcMaxPlayers() < GameManager.getPlayerNames().size()) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_TOO_MANY_PLAYERS", Config.getLang()));
			throw new KuffleCommandFalseException();
		}
		
		if (!checkEmptyTeams()) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_ALREADY_PLAYERS", Config.getLang()));
			throw new KuffleCommandFalseException();
		}
		
		int cnt = 0;
		List<Player> players = GameManager.getPlayerList();
		
		final SecureRandom random = new SecureRandom();
		
		while (players.size() > 0) {
			int idx = random.nextInt(players.size());
			
			TeamManager.getInstance().affectPlayer(TeamManager.getInstance().getTeams().get(cnt).getName(), players.get(idx));
			
			players.remove(idx);
			
			cnt++;
			
			if (cnt >= TeamManager.getInstance().getTeams().size()) {
				cnt = 0;
			}
		}
		
		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("RANDOM", Config.getLang()).replace("%i", "" + GameManager.getPlayerNames().size()).replace("%j", "" + TeamManager.getInstance().getTeams().size()));

		return true;
	}
	
	/**
	 * Calculates the max amount of players allowed with config and team amount
	 * 
	 * @return the multiplication of Config team size and team number
	 */
	public int calcMaxPlayers() {
		return (Config.getTeamSize() * TeamManager.getInstance().getTeams().size());
	}

	/**
	 * Checks if any team is empty
	 * 
	 * @return True if all team are empty, False instead
	 */
	public boolean checkEmptyTeams() {
		for (Team item : TeamManager.getInstance().getTeams()) {
			if (!item.getPlayers().isEmpty()) {
				return false;
			}
		}
		
		return true;
	}
}
