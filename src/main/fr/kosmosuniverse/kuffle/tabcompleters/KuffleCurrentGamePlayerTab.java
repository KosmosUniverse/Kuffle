package main.fr.kosmosuniverse.kuffle.tabcompleters;

import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleCurrentGamePlayerTab extends AKuffleTabCommand {
	public KuffleCurrentGamePlayerTab() {
		super(null, -1, -1);
	}

	@Override
	protected void runCommand() throws KuffleCommandFalseException {
		if (GameManager.getGames() != null && currentArgs.length == 1) {
			GameManager.applyToPlayers(game -> {
				if (!game.isLose() && !game.isFinished()) {
					ret.add(game.getPlayer().getName());	
				}
			});
		}
	}
}
