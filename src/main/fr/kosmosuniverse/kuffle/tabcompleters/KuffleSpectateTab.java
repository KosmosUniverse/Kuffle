package main.fr.kosmosuniverse.kuffle.tabcompleters;

import java.util.ArrayList;
import java.util.List;

import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleSpectateTab extends AKuffleTabCommand {
	private List<String> list = new ArrayList<>();
	
	/**
	 * Constructor
	 */
	public KuffleSpectateTab() {
		super("k-spectate", 0, 2);
		
		list.add("display");
		list.add("reset");
	}
	
	@Override
	protected void runCommand() throws KuffleCommandFalseException {
		if (currentArgs.length == 1) {
			ret.addAll(list);
		}
	}
}
