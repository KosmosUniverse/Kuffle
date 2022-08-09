package main.fr.kosmosuniverse.kuffle.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class LangManager {
	private LangManager() {
		throw new IllegalStateException("Utility class");
	}
	
	public static Map<String, Map<String, String>> getAllItemsLang(String jsonContent, File dataFolder) {
		Map<String, Map<String, String>> finalMap = new HashMap<>();
		JSONObject langages;
		JSONParser jsonParser = new JSONParser();
		
		try (FileWriter writer = new FileWriter(dataFolder.getPath() + File.separator + "logs.txt", true)) {
			langages = (JSONObject) jsonParser.parse(jsonContent);

			for (Iterator<?> itItem = langages.keySet().iterator(); itItem.hasNext();) {
				String keyItem = (String) itItem.next();
				JSONObject item = (JSONObject) langages.get(keyItem);

				writer.append(keyItem);

				Map<String, String> itemLangs = new HashMap<>();
				
				for (Iterator<?> itLang = item.keySet().iterator(); itLang.hasNext();) {
					String keyLang = (String) itLang.next();
					String value = (String) item.get(keyLang);
					
					writer.append("\t" + keyLang + " :\n" + value + "\n");
					itemLangs.put(keyLang, value);
				}
				
				finalMap.put(keyItem, itemLangs);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		
		return finalMap;
	}
	
	public static String findDisplay(Map<String, Map<String, String>> allLangs, String item, String lang) {
		String res = null;
		
		if (allLangs.containsKey(item)) {
			Map<String, String> langs = allLangs.get(item);
			
			if (langs.containsKey(lang) && !langs.get(lang).isEmpty()) {
				return langs.get(lang);
			} else {
				return item;
			}
		} else {
			res = item;
		}
			
		return res;
	}
	
	public static List<String> findAllLangs(Map<String, Map<String, String>> allitemsLangs) {
		List<String> allLangs = new ArrayList<>();
		
		Map<String, String> first = allitemsLangs.get(allitemsLangs.keySet().toArray()[0]);
		
		for (String key : first.keySet()) {
			allLangs.add(key);
		}
		
		return allLangs;
	}
}
