package fr.kosmosuniverse.kuffle.tabcompleters;

import fr.kosmosuniverse.kuffle.core.AgeManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleAgeTargetsTab extends AKuffleTabCommand {
	private final List<String> ages = new ArrayList<>();

	/**
	 * Constructor
	 */
	public KuffleAgeTargetsTab() {
		super();
		
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
	protected void runCommand() {
		if (currentArgs.length == 1) {
			ret.addAll(ages);
		}
	}
}
