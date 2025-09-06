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
public class KuffleTeamResetPlayersTab extends AKuffleTabCommand {
	public KuffleTeamResetPlayersTab() {
		super("k-team-reset-players", 1, 1);
	}

	@Override
	protected void runCommand() throws KuffleCommandFalseException {
		if (currentArgs.length == 1) {
			ret.addAll(TeamManager.getInstance().getTeams().stream()
					.filter(t -> !t.getPlayers().isEmpty())
					.map(Team::getName)
					.collect(Collectors.toList()));
		}
	}
}
