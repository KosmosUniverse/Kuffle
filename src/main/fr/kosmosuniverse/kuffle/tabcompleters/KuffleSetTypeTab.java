package main.fr.kosmosuniverse.kuffle.tabcompleters;

import java.util.ArrayList;
import java.util.List;

import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import main.fr.kosmosuniverse.kuffle.type.KuffleType;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleSetTypeTab extends AKuffleTabCommand {
	private List<String> types = new ArrayList<>();

	/**
	 * Constructor
	 */
	public KuffleSetTypeTab() {
		super("k-set-type", 1, 1);
		
		for (KuffleType.Type type : KuffleType.Type.values()) {
			types.add(type.name());
		}
	}
	
	/**
	 * Clears @types List
	 */
	public void clear() {
		types.clear();
	}

	@Override
	protected void runCommand() throws KuffleCommandFalseException {
		if (currentArgs.length == 1) {
			ret.addAll(types);
		}
	}
}
