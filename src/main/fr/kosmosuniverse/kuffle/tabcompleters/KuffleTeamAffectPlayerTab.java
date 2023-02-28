package main.fr.kosmosuniverse.kuffle.tabcompleters;

import java.util.ArrayList;
import java.util.List;

import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.Team;
import main.fr.kosmosuniverse.kuffle.core.TeamManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleTeamAffectPlayerTab extends AKuffleTabCommand {
	public KuffleTeamAffectPlayerTab() {
		super("k-team-affect-player", 2, 2);
	}

	@Override
	protected void runCommand() throws KuffleCommandFalseException {
		if (currentArgs.length == 1) {
			List<String> ret = new ArrayList<>();
			
			for (Team item : TeamManager.getInstance().getTeams()) {
				ret.add(item.getName());
			}
		} else if (currentArgs.length == 2) {
			List<String> ret = new ArrayList<>();
			
			for (String item : GameManager.getPlayerNames()) {
				if (!TeamManager.getInstance().isInTeam(item)) {
					ret.add(item);
				}
			}
		}
	}
}
