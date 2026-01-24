package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.core.*;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleResults extends AKuffleCommand {
	public KuffleResults() {
		super("k-results", true, null, 0, 1, false);
	}

	@Override
	public boolean runCommand() {
		if (args.length == 0) {
			tryOpenResults();
		} else if (args.length == 1) {
			if (ResultManager.getInstance().loadGameResult(args[0])) {
				tryOpenResults();
			}
		}
		
		return true;
	}

	private void tryOpenResults() {
		if (ResultManager.getInstance().isResultsLoaded()) {
			player.openInventory(ResultManager.getInstance().getMainResults());
		} else {
			LogManager.getInstanceSystem().writeMsg(player, "No result loaded, please add party name to load.");
		}
	}
}
