package fr.kosmosuniverse.kuffle.tabcompleters;

import fr.kosmosuniverse.kuffle.core.Team;
import fr.kosmosuniverse.kuffle.core.TeamManager;

import java.util.stream.Collectors;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleTeamRemovePlayerTab extends AKuffleTabCommand {
	public KuffleTeamRemovePlayerTab() {
		super();
	}

	@Override
	protected void runCommand() {
		if (currentArgs.length == 1) {
			ret.addAll(TeamManager.getInstance().getTeams().stream()
					.filter(t -> !t.getPlayers().isEmpty())
					.map(Team::getName)
					.collect(Collectors.toList()));
		} else if (currentArgs.length == 2 && TeamManager.getInstance().hasTeam(currentArgs[0])) {
			ret.addAll(TeamManager.getInstance().getTeam(currentArgs[0]).getPlayers());
		}
	}
}
