package main.fr.kosmosuniverse.kuffle.tabcompleters;

import main.fr.kosmosuniverse.kuffle.core.TeamManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;

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
			TeamManager.getInstance().getTeams().stream().forEach(t -> ret.add(t.getName()));
		}
	}
}
