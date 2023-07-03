package main.fr.kosmosuniverse.kuffle.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class KuffleAbandon extends AKuffleCommand {
	private Map<String, List<String>> abandoned;
	
	/**
	 * KuffleAbandon constructor
	 */
	public KuffleAbandon() {
		super("k-abandon", null, true, 0, 0, false);
		
		abandoned = new HashMap<>();
	}
	
	/**
	 * Clears the abandoned map
	 */
	public void clear() {
		if (abandoned != null) {
			abandoned.forEach((k, v) -> {
				if (v != null) {
					v.clear();
				}
			});
			
			abandoned.clear();
		}
	}

	@Override
	public boolean runCommand() {
		if (!GameManager.hasPlayer(player.getName())) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_PLAYING", GameManager.getPlayerLang(player.getName())));
		}
		
		if (Config.getTeam()) {
			Team team = TeamManager.getInstance().getTeam(GameManager.getPlayerTeamName(player.getName()));
			List<String> players;
			
			if (abandoned.containsKey(team.getName())) {
				players = abandoned.get(team.getName());
			} else {
				players = new ArrayList<>();
			}
			
			if (!players.contains(player.getName())) {
				players.add(player.getName());
			}
			
			abandoned.put(team.getName(), players);
			
			if (checkTeam(team)) {
				GameManager.setTeamLose(team.getName(), true);
			} else {
				LogManager.getInstanceSystem().writeMsg(player, "All players of the team have to abandon for this to be effective.");
			}
		} else {
			GameManager.setLose(player.getName(), true);
		}
		
		return true;
	}
	
	/**
	 * Checks if all team mates have abandoned
	 * 
	 * @param team	name of the Team to check
	 * 
	 * @return True if all team mates have abandoned, False instead
	 */
	private boolean checkTeam(Team team) {
		return team.getPlayers().stream()
		.allMatch(p -> abandoned.get(team.getName()).contains(p.getName()));
	}
}
