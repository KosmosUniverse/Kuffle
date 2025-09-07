package fr.kosmosuniverse.kuffle.tabcompleters;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleTipsTab extends AKuffleTabCommand {
	public KuffleTipsTab() {
		super();
	}

	@Override
	protected void runCommand() {
		if (currentArgs.length == 1) {
			ret.add("TRUE");
			ret.add("FALSE");
		}	
	}
}
