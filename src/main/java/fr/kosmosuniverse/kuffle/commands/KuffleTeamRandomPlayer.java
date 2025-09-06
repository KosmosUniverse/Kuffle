package fr.kosmosuniverse.kuffle.commands;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import fr.kosmosuniverse.kuffle.core.*;
import fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
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
		if (Party.getInstance().getStatus() == GameStatus.RUNNING) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_LAUNCHED", Config.getLang()));
			throw new KuffleCommandFalseException();
		}
		
		if (Party.getInstance().getPlayers().getList().size() == 0) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("LIST_EMPTY", Config.getLang()));
			throw new KuffleCommandFalseException();
		}
		
		if (calcMaxPlayers() < Party.getInstance().getPlayers().getList().size()) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_TOO_MANY_PLAYERS", Config.getLang()));
			throw new KuffleCommandFalseException();
		}
		
		if (!checkEmptyTeams()) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_ALREADY_PLAYERS", Config.getLang()));
			throw new KuffleCommandFalseException();
		}
		
		int cnt = 0;
		List<String> players = new ArrayList<>(Party.getInstance().getPlayers().getList());
		
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
		
		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("RANDOM", Config.getLang()).replace("%i", String.valueOf(Party.getInstance().getPlayers().getList().size())).replace("%j", String.valueOf(TeamManager.getInstance().getTeams().size())));

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
		return TeamManager.getInstance().getTeams().stream().allMatch(t -> t.getPlayers().isEmpty());
	}
}
