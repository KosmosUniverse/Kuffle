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
public class KuffleTeamDeleteTab extends AKuffleTabCommand {
	public KuffleTeamDeleteTab() {
		super("k-team-delete", 1, 1);
	}
	
	@Override
	protected void runCommand() throws KuffleCommandFalseException {
		if (currentArgs.length == 1) {
			ret.addAll(TeamManager.getInstance().getTeams().stream().map(Team::getName).collect(Collectors.toList()));
		}
	}
}
