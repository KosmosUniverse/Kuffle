package fr.kosmosuniverse.kuffle.tabcompleters;

import fr.kosmosuniverse.kuffle.core.Party;
import fr.kosmosuniverse.kuffle.core.Team;
import fr.kosmosuniverse.kuffle.core.TeamManager;

import java.util.stream.Collectors;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleTeamAffectPlayerTab extends AKuffleTabCommand {
	public KuffleTeamAffectPlayerTab() {
		super();
	}

	@Override
	protected void runCommand() {
		if (currentArgs.length == 1) {
			for (Team item : TeamManager.getInstance().getTeams()) {
				ret.add(item.getName());
			}
		} else if (currentArgs.length == 2) {
			ret.addAll(Party.getInstance().getPlayers().getList().stream().filter(p -> !TeamManager.getInstance().isInTeam(p)).collect(Collectors.toList()));
		}
	}
}
