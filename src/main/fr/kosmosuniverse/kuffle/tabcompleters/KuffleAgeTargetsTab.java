package main.fr.kosmosuniverse.kuffle.tabcompleters;

import java.util.ArrayList;
import java.util.List;

import main.fr.kosmosuniverse.kuffle.core.AgeManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleAgeTargetsTab extends AKuffleTabCommand {
	private List<String> ages = new ArrayList<>();

	/**
	 * Constructor
	 */
	public KuffleAgeTargetsTab() {
		super("k-agetargets", 0, 1);
		
		int max = AgeManager.getLastAgeIndex();
		
		for (int cnt = 0; cnt <= max; cnt++) {
			String age = AgeManager.getAgeByNumber(cnt).getName();

			ages.add(age);
		}
	}
	
	/**
	 * Clears the @ages list
	 */
	public void clear() {
		ages.clear();
	}
	
	@Override
	protected void runCommand() throws KuffleCommandFalseException {
		if (currentArgs.length == 1) {
			ret.addAll(ages);
		}
	}
}
