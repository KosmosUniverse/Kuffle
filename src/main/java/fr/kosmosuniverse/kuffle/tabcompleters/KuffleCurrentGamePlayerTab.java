package fr.kosmosuniverse.kuffle.tabcompleters;

import fr.kosmosuniverse.kuffle.core.PartyTmp;

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
		if (PartyTmp.getInstance().getPlayers().getList() != null && currentArgs.length == 1) {
			ret.addAll(PartyTmp.getInstance()
					.getGameManager()
					.getPlayerNotFinishedList());
		}
	}
}
