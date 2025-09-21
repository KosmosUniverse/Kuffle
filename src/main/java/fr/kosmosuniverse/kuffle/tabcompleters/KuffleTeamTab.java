package fr.kosmosuniverse.kuffle.tabcompleters;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleTeamTab extends AKuffleTabCommand {
	List<String> options = new ArrayList<>();

	public KuffleTeamTab() {
		super();
		options.add("show");
		options.add("load");
		options.add("save");
	}

	@Override
	protected void runCommand() {
		if (currentArgs.length == 1) {
			ret.addAll(options);
		}
	}
}
