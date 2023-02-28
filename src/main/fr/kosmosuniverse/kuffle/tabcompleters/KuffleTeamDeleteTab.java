package main.fr.kosmosuniverse.kuffle.tabcompleters;

import main.fr.kosmosuniverse.kuffle.core.Team;
import main.fr.kosmosuniverse.kuffle.core.TeamManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;

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
			for (Team item : TeamManager.getInstance().getTeams()) {
				ret.add(item.getName());
			}
		}
	}
}
