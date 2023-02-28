package main.fr.kosmosuniverse.kuffle.tabcompleters;

import main.fr.kosmosuniverse.kuffle.core.Team;
import main.fr.kosmosuniverse.kuffle.core.TeamManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;

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
			for (Team item : TeamManager.getInstance().getTeams()) {
				if (item.getPlayers().size() != 0) {
					ret.add(item.getName());	
				}
			}
		}
	}
}
