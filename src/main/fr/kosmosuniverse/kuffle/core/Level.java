package main.fr.kosmosuniverse.kuffle.core;

public class Level {
	public String name;
	public int number;
	public int seconds;
	public boolean losable;
	
	public Level(String levelName, int levelNumber, int levelSeconds, boolean levelLosable) {
		name = levelName;
		number = levelNumber;
		seconds = levelSeconds;
		losable = levelLosable;
	}
}
