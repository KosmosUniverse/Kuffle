package fr.kosmosuniverse.kuffle.tabcompleters;

import fr.kosmosuniverse.kuffle.core.Team;
import fr.kosmosuniverse.kuffle.core.TeamManager;

import java.util.stream.Collectors;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleTeamShowTab extends AKuffleTabCommand {
	public KuffleTeamShowTab() {
		super();
	}

	@Override
	protected void runCommand() {
		if (currentArgs.length == 1) {
			ret.addAll(TeamManager.getInstance().getTeams().stream()
					.map(Team::getName)
					.collect(Collectors.toList()));
		}
	}
}
