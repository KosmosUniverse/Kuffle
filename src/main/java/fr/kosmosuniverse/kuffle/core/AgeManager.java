package fr.kosmosuniverse.kuffle.core;

import fr.kosmosuniverse.kuffle.utils.FileUtils;
import lombok.Getter;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class AgeManager {
	@Getter
	private static List<Age> ages = null;
	
	/**
	 * Private AgeManager constructor
	 * 
	 * @throws IllegalStateException Utility Class Constructor Exception
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
	 */
	public static void setupAges(String ageContent) throws IllegalArgumentException {
		ages = new ArrayList<>();

		if (ageContent == null) {
			throw new IllegalArgumentException("Input content is null !");
		}
		
		JSONObject jsonObj = FileUtils.readJSONObjectFromContent(ageContent);
		
		for (String key : jsonObj.keySet()) {
			JSONObject ageObj = (JSONObject) jsonObj.get(key);
			
			ages.add(new Age(key,
					ageObj.getInt("Number"),
					ageObj.getString("TextColor"),
					ageObj.getString("BoxColor") + "_SHULKER_BOX"));
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
		return ages.stream().anyMatch(age -> age.getName().equals(ageName));
	}
	
	/**
	 * Get an age exists from its index
	 * 
	 * @param ageNumber	Age index
	 * 
	 * @return the Age object if exists, null instead
	 */
	public static Age getAgeByNumber(int ageNumber) {
		return ages.stream().filter(age -> age.getNumber() == ageNumber).findAny().orElse(getDefaultAge());
	}
	
	/**
	 * Get an age exists from its name
	 * 
	 * @param ageName	Age name
	 * 
	 * @return the Age object if exists, null instead
	 */
	public static Age getAgeByName(String ageName) {
		return ages.stream().filter(age -> Objects.equals(age.getName(), ageName)).findAny().orElse(getDefaultAge());
	}
	
	/**
	 * Gets the default Age if exists
	 * 
	 * @return the Age object of Default age is exists, null instead
	 */
	public static Age getDefaultAge() {
		return ages.stream().filter(age -> age.getNumber() == -1).findAny().orElse(null);
	}

	/**
	 * Gets the last Age of ages list
	 * 
	 * @return the last age as Age object
	 */
	public static Age getLastAge() {
		return ages.get(ages.size() - 1);
	}
	
	/**
	 * Gets the first Age of ages list
	 * 
	 * @return the first age as Age object
	 */
	public static Age getFirstAge() {
		return ages.get(0);
	}
	
	/**
	 * Gets last age index
	 * 
	 * @return the index
	 */
	public static int getLastAgeIndex() {
		int max = 0;
		
		for (Age age : ages) {
			max = Math.max(max, age.getNumber());
		}
		
		return max;
	}
	
	/**
	 * Gets the list of Ages name
	 * 
	 * @return Ages name as List of Strings
	 */
	public static List<String> getAgesNameList() {
		return Collections.unmodifiableList(ages.stream().map(Age::getName).collect(Collectors.toList()));
	}

}
