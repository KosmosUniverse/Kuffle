package main.fr.kosmosuniverse.kuffle.tabcompleters;

import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleTipsTab extends AKuffleTabCommand {
	public KuffleTipsTab() {
		super("k-tips", 0, 0);
	}

	@Override
	protected void runCommand() throws KuffleCommandFalseException {
		if (currentArgs.length == 1) {
			ret.add("TRUE");
			ret.add("FALSE");
		}	
	}
}
