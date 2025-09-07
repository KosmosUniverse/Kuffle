package fr.kosmosuniverse.kuffle.tabcompleters;

import fr.kosmosuniverse.kuffle.core.LangManager;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleLangTab extends AKuffleTabCommand {
	public KuffleLangTab() {
		super();
	}

	@Override
	protected void runCommand() {
		if (currentArgs.length == 1) {
			ret.addAll(LangManager.getLangs());
		}
	}
}
