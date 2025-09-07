package fr.kosmosuniverse.kuffle.tabcompleters;

import fr.kosmosuniverse.kuffle.core.Team;
import fr.kosmosuniverse.kuffle.core.TeamManager;

import java.util.stream.Collectors;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleTeamResetPlayersTab extends AKuffleTabCommand {
	public KuffleTeamResetPlayersTab() {
		super();
	}

	@Override
	protected void runCommand() {
		if (currentArgs.length == 1) {
			ret.addAll(TeamManager.getInstance().getTeams().stream()
					.filter(t -> !t.getPlayers().isEmpty())
					.map(Team::getName)
					.collect(Collectors.toList()));
		}
	}
}
