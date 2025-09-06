package fr.kosmosuniverse.kuffle.tabcompleters;

import fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import fr.kosmosuniverse.kuffle.type.KuffleType;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleSetTypeTab extends AKuffleTabCommand {
	private final List<String> types = new ArrayList<>();

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
