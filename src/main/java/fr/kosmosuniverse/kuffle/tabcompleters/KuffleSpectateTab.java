package fr.kosmosuniverse.kuffle.tabcompleters;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleSpectateTab extends AKuffleTabCommand {
	private final List<String> list = new ArrayList<>();
	
	/**
	 * Constructor
	 */
	public KuffleSpectateTab() {
		super();
		
		list.add("display");
		list.add("reset");
	}
	
	@Override
	protected void runCommand() {
		if (currentArgs.length == 1) {
			ret.addAll(list);
		}
	}
}
