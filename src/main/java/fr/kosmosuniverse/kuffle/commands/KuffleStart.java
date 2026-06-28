package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.core.PartyTmp;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleStart extends AKuffleCommand {
	public KuffleStart() {
		super("k-start", true, false, 0, 0, false);
	}

	@Override
	public boolean runCommand() {
		PartyTmp.getInstance().setupParty();
		PartyTmp.getInstance().initGame();

		if (PartyTmp.getInstance().launchChecks(player)) {
			PartyTmp.getInstance().launch(player);
		} else {
			PartyTmp.getInstance().clearGame();
			PartyTmp.getInstance().clearParty();
		}

		return true;
	}
}
