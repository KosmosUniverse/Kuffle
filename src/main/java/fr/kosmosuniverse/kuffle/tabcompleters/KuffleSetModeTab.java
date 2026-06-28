package fr.kosmosuniverse.kuffle.tabcompleters;

import fr.kosmosuniverse.kuffle.mode.Mode;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleSetModeTab extends AKuffleTabCommand {
	private final List<String> modes = new ArrayList<>();

	/**
	 * Constructor
	 */
	public KuffleSetModeTab() {
		super();

		for (Mode type : Mode.values()) {
			modes.add(type.name());
		}
	}
	
	/**
	 * Clears @types List
	 */
	public void clear() {
		modes.clear();
	}

	@Override
	protected void runCommand() {
		if (currentArgs.length == 1) {
			ret.addAll(modes);
		}
	}
}
