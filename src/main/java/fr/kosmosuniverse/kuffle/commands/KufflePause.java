package fr.kosmosuniverse.kuffle.commands;


import fr.kosmosuniverse.kuffle.core.*;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KufflePause extends AKuffleCommand {
	public KufflePause() {
		super("k-pause", null, true, 0, 0, false);
	}

	@Override
	public boolean runCommand() {
		PartyTmp.getInstance().pause();
		
		return true;
	}

}
