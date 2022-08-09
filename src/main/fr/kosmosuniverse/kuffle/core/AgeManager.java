package main.fr.kosmosuniverse.kuffle.core;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class AgeManager {
	private AgeManager() {
		throw new IllegalStateException("Utility class");
    }
	
	public static List<Age> getAges(String ageContent) {
		List<Age> finalList = new ArrayList<>();

		if (ageContent == null) {
			return null;
		}
		
		JSONObject jsonObj;
		JSONParser parser = new JSONParser();
		
		try {
			jsonObj = (JSONObject) parser.parse(ageContent);
			
			for (Object key : jsonObj.keySet()) {
				JSONObject ageObj = (JSONObject) jsonObj.get(key);
				
				finalList.add(new Age((String) key,
						(Integer) Integer.parseInt(ageObj.get("Number").toString()),
						(String) ageObj.get("TextColor"),
						((String) ageObj.get("BoxColor") + "_SHULKER_BOX")));
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		
		return finalList;
	}
	
	public static boolean ageExists(List<Age> ages, String ageName) {
		for (Age age : ages) {
			if (age.name.equals(ageName)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static Age getAgeByNumber(List<Age> ages, int ageNumber) {
		for (Age age : ages) {
			if (age.number == ageNumber) {
				return age;
			}
		}
		
		return getDefaultAge(ages);
	}
	
	public static Age getAgeByName(List<Age> ages, String ageName) {
		for (Age age : ages) {
			if (age.name.equalsIgnoreCase(ageName)) {
				return age;
			}
		}
		
		return getDefaultAge(ages);
	}
	
	public static Age getDefaultAge(List<Age> ages) {
		for (Age age : ages) {
			if (age.number == -1) {
				return age;
			}
		}
		
		return null;
	}
	
	public static int getAgeMaxNumber(List<Age> ages) {
		int max = 0;
		
		for (Age age : ages) {
			max = max < age.number ? age.number : max;
		}
		
		return max;
	}
}
