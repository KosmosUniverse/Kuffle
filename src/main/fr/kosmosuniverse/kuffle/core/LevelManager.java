package main.fr.kosmosuniverse.kuffle.core;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class LevelManager {
	private LevelManager() {
		throw new IllegalStateException("Utility class");
	}
	
	public static List<Level> getLevels(String content) {
		List<Level> finalList = new ArrayList<>();

		if (content == null) {
			return null;
		}
		
		JSONObject jsonObj;
		JSONParser parser = new JSONParser();
		
		try {
			jsonObj = (JSONObject) parser.parse(content);
			
			for (Object key : jsonObj.keySet()) {
				JSONObject levelObj = (JSONObject) jsonObj.get(key);
				
				finalList.add(new Level((String) key,
						(Integer) Integer.parseInt(levelObj.get("Number").toString()),
						(Integer) Integer.parseInt(levelObj.get("Seconds").toString()),
						Boolean.parseBoolean(levelObj.get("Lose").toString())));
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		
		return finalList;
	}
	
	public static boolean levelExists(List<Level> levels, String levelName) {
		for (Level level : levels) {
			if (level.name.equals(levelName)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static Level getLevelByNumber(List<Level> levels, int levelNumber) {
		for (Level level : levels) {
			if (level.number == levelNumber) {
				return level;
			}
		}
		
		return null;
	}
	
	public static Level getLevelByName(List<Level> levels, String levelName) {
		for (Level level : levels) {
			if (level.name.equalsIgnoreCase(levelName)) {
				return level;
			}
		}
		
		return null;
	}
	
	public static int getLevelMaxNumber(List<Level> levels) {
		int max = 0;
		
		for (Level level : levels) {
			max = max < level.number ? level.number : max;
		}
		
		return max;
	}
}
