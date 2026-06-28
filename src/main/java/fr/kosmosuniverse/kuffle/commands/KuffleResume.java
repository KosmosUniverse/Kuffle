package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.core.*;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleResume extends AKuffleCommand {
	public KuffleResume() {
		super("k-resume", null, true, 0, 0, false);
	}

	@Override
	public boolean runCommand() {
		PartyTmp.getInstance().resume();

		return true;
	}

}
