package main.fr.kosmosuniverse.kuffle.core;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class LevelManager {
	public static List<Level> levels = null;
	
	/**
	 * Private LevelManager constructor
	 * 
	 * @throws IllegalStateException
	 */
	private LevelManager() {
		throw new IllegalStateException("Utility class");
	}
	
	/**
	 * Clears levels list
	 */
	public static void clear() {
		if (levels != null) {
			levels.clear();
		}
	}
	
	/**
	 * Setup levels from string file content
	 * 
	 * @param content	The file content
	 * 
	 * @throws IllegalArgumentException if content is null
	 * @throws ParseException if JSONParser.parse fails
	 */
	public static void setupLevels(String content) throws IllegalArgumentException, ParseException {
		levels = new ArrayList<>();

		if (content == null) {
			throw new IllegalArgumentException("Input content is null !");
		}
		
		JSONObject jsonObj;
		JSONParser parser = new JSONParser();
		
		jsonObj = (JSONObject) parser.parse(content);
		
		for (Object key : jsonObj.keySet()) {
			JSONObject levelObj = (JSONObject) jsonObj.get(key);
			
			levels.add(new Level((String) key,
					(Integer) Integer.parseInt(levelObj.get("Number").toString()),
					(Integer) Integer.parseInt(levelObj.get("Seconds").toString()),
					Boolean.parseBoolean(levelObj.get("Lose").toString())));
		}
	}
	
	/**
	 * Checks if level exists from its name
	 * 
	 * @param levelName	the level name
	 * 
	 * @return True if level exists, False instead
	 */
	public static boolean levelExists(String levelName) {
		for (Level level : levels) {
			if (level.name.equals(levelName)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Gets level by its number
	 * 
	 * @param levelNumber	the level number
	 * 
	 * @return the Level object if exists, null instead
	 */
	public static Level getLevelByNumber(int levelNumber) {
		for (Level level : levels) {
			if (level.number == levelNumber) {
				return level;
			}
		}
		
		return null;
	}
	
	/**
	 * Gets level by its name
	 * 
	 * @param levelName	the level name
	 * 
	 * @return the Level object if exists, null instead
	 */
	public static Level getLevelByName(String levelName) {
		for (Level level : levels) {
			if (level.name.equalsIgnoreCase(levelName)) {
				return level;
			}
		}
		
		return null;
	}
	
	/**
	 * Gets highest level number
	 * 
	 * @return the highest number
	 */
	public static int getLevelMaxNumber() {
		int max = 0;
		
		for (Level level : levels) {
			max = max < level.number ? level.number : max;
		}
		
		return max;
	}
}
