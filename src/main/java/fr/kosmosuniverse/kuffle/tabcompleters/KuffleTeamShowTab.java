package fr.kosmosuniverse.kuffle.tabcompleters;

import fr.kosmosuniverse.kuffle.core.Team;
import fr.kosmosuniverse.kuffle.core.TeamManager;
import fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;

import java.util.stream.Collectors;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleTeamShowTab extends AKuffleTabCommand {
	public KuffleTeamShowTab() {
		super("k-team-show", 0, 1);
	}

	@Override
	protected void runCommand() throws KuffleCommandFalseException {
		if (currentArgs.length == 1) {
			ret.addAll(TeamManager.getInstance().getTeams().stream()
					.map(Team::getName)
					.collect(Collectors.toList()));
		}
	}
}
