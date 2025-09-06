package fr.kosmosuniverse.kuffle.core;

import lombok.Getter;

/**
 * 
 * @author KosmosUniverse
 *
 */
@Getter
public class Level {
	private final String name;
	private final int number;
	private final int seconds;
	private final boolean losable;
	
	/**
	 * Constructor
	 * 
	 * @param levelName		The level name
	 * @param levelNumber	The level number
	 * @param levelSeconds	The amount of seconds before teleportation
	 * @param levelLosable	True if death is allowed in this level, False instead
	 */
	public Level(String levelName, int levelNumber, int levelSeconds, boolean levelLosable) {
		name = levelName;
		number = levelNumber;
		seconds = levelSeconds;
		losable = levelLosable;
	}
}
