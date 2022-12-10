package main.fr.kosmosuniverse.kuffle.core;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class Level {
	private String name;
	private int number;
	private int seconds;
	private boolean losable;
	
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

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * @return the seconds
	 */
	public int getSeconds() {
		return seconds;
	}

	/**
	 * @return the losable
	 */
	public boolean isLosable() {
		return losable;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * @param seconds the seconds to set
	 */
	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}

	/**
	 * @param losable the losable to set
	 */
	public void setLosable(boolean losable) {
		this.losable = losable;
	}
}
