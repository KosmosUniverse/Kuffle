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
public class AgeManager {
	public static List<Age> ages = null;
	
	/**
	 * Private AgeManager constructor
	 * 
	 * @throws IllegalStateException
	 */
	private AgeManager() {
		throw new IllegalStateException("Utility class");
    }
	
	/**
	 * Clears the ages list
	 */
	public static void clear() {
		if (ages != null) {
			ages.clear();
		}
	}
	
	/**
	 * Setup ages from string file content
	 * 
	 * @param ageContent	The file content
	 * 
	 * @throws IllegalArgumentException if ageContent is null
	 * @throws ParseException if JSONParser.parse fails
	 */
	public static void setupAges(String ageContent) throws IllegalArgumentException, ParseException {
		ages = new ArrayList<>();

		if (ageContent == null) {
			throw new IllegalArgumentException("Input content is null !");
		}
		
		JSONObject jsonObj;
		JSONParser parser = new JSONParser();
		
		jsonObj = (JSONObject) parser.parse(ageContent);
		
		for (Object key : jsonObj.keySet()) {
			JSONObject ageObj = (JSONObject) jsonObj.get(key);
			
			ages.add(new Age((String) key,
					(Integer) Integer.parseInt(ageObj.get("Number").toString()),
					(String) ageObj.get("TextColor"),
					((String) ageObj.get("BoxColor") + "_SHULKER_BOX")));
		}
	}
	
	/**
	 * Checks if an age exists from its name
	 * 
	 * @param ageName	the Age name to search for
	 * 
	 * @return True if Age exists, False instead
	 */
	public static boolean ageExists(String ageName) {
		for (Age age : ages) {
			if (age.name.equals(ageName)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Get an age exists from its index
	 * 
	 * @param ageNumber	Age index
	 * 
	 * @return the Age object if exists, null instead
	 */
	public static Age getAgeByNumber(int ageNumber) {
		for (Age age : ages) {
			if (age.number == ageNumber) {
				return age;
			}
		}
		
		return getDefaultAge();
	}
	
	/**
	 * Get an age exists from its name
	 * 
	 * @param ageName	Age name
	 * 
	 * @return the Age object if exists, null instead
	 */
	public static Age getAgeByName(String ageName) {
		for (Age age : ages) {
			if (age.name.equalsIgnoreCase(ageName)) {
				return age;
			}
		}
		
		return getDefaultAge();
	}
	
	/**
	 * Gets the default Age if exists
	 * 
	 * @return the Age object of Default age is exists, null instead
	 */
	public static Age getDefaultAge() {
		for (Age age : ages) {
			if (age.number == -1) {
				return age;
			}
		}
		
		return null;
	}
	
	/**
	 * Gets last age index
	 * 
	 * @return the index
	 */
	public static int getLastAgeIndex() {
		int max = 0;
		
		for (Age age : ages) {
			max = max < age.number ? age.number : max;
		}
		
		return max;
	}
}
