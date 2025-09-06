package fr.kosmosuniverse.kuffle.core;

import fr.kosmosuniverse.kuffle.utils.FileUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class LevelManager {
	private static LevelManager instance = null;
	private List<Level> levels = null;
	
	/**
	 * Clears levels list
	 */
	public void clear() {
		if (levels != null) {
			levels.clear();
		}
	}
	
	/**
	 * Get the LevelManager instance
	 * 
	 * @return the instance
	 */
	public static synchronized LevelManager getInstance() {
		if (instance == null ) {
			instance = new LevelManager();
		}
		
		return instance;
	}
	
	/**
	 * Setup levels from string file content
	 * 
	 * @param content	The file content
	 * 
	 * @throws IllegalArgumentException if content is null
	 */
	public void setupLevels(String content) throws IllegalArgumentException {
		levels = new ArrayList<>();

		if (content == null) {
			throw new IllegalArgumentException("Input content is null !");
		}
		
		JSONObject jsonObj = FileUtils.readJSONObjectFromContent(content);
		
		for (String key : jsonObj.keySet()) {
			JSONObject levelObj = (JSONObject) jsonObj.get(key);
			
			levels.add(new Level(key,
					levelObj.getInt("Number"),
					levelObj.getInt("Seconds"),
					levelObj.getBoolean("Lose")));
		}
	}
	
	/**
	 * Checks if level exists from its name
	 * 
	 * @param levelName	the level name
	 * 
	 * @return True if level exists, False instead
	 */
	public boolean levelNotExists(String levelName) {
		return levels.stream().noneMatch(level -> level.getName().equals(levelName));
	}
	
	/**
	 * Gets level by its number
	 * 
	 * @param levelNumber	the level number
	 * 
	 * @return the Level object if exists, null instead
	 */
	public Level getLevelByNumber(int levelNumber) {
		return levels.stream().filter(level -> level.getNumber() == levelNumber).findAny().orElse(null);
	}
	
	/**
	 * Gets level by its name
	 * 
	 * @param levelName	the level name
	 * 
	 * @return the Level object if exists, null instead
	 */
	public Level getLevelByName(String levelName) {
		return levels.stream().filter(level -> Objects.equals(level.getName(), levelName)).findAny().orElse(null);
	}
	
	/**
	 * Gets the lowest level
	 * 
	 * @return the levels' first Level
	 */
	public Level getFirstLevel() {
		return levels.get(0);
	}
}
