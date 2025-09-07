package fr.kosmosuniverse.kuffle.tabcompleters;

import fr.kosmosuniverse.kuffle.core.Party;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleCurrentGamePlayerTab extends AKuffleTabCommand {
	public KuffleCurrentGamePlayerTab() {
		super();
	}

	@Override
	protected void runCommand() {
		if (Party.getInstance().getPlayers().getList() != null && currentArgs.length == 1) {
			ret.addAll(Party.getInstance().getGames().getGames().entrySet().stream().filter(e -> !e.getValue().isLose() && !e.getValue().isFinished()).map(Map.Entry::getKey).collect(Collectors.toList()));
		}
	}
}
