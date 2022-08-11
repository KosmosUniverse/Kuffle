package main.fr.kosmosuniverse.kuffle.core;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class Level {
	public String name;
	public int number;
	public int seconds;
	public boolean losable;
	
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
