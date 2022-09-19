package main.fr.kosmosuniverse.kuffle.crafts;

import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CraftImpl extends ACraft {
	public CraftImpl(JSONObject jsonRecipe) {
		name = jsonRecipe.get("Name").toString();
		setupResult((JSONObject) jsonRecipe.get("Result"));
		
		Type type = Type.valueOf(jsonRecipe.get("Type").toString().toUpperCase());
		String shape = jsonRecipe.containsKey("Shape") ? jsonRecipe.get("Shape").toString() : null;
		Map<String, Map<String, String>> ingredients = new HashMap<>();
		JSONObject ings = (JSONObject) jsonRecipe.get("Ingredients");
		
		for (Object key : ings.keySet()) {
			Map<String, String> i = new HashMap<>();

			for (Object iKey : ((JSONObject) ings.get(key)).keySet()) {
				i.put(iKey.toString(), ((JSONObject) ings.get(key)).get(iKey).toString());
			}
			
			ingredients.put(key.toString(), i);
		}
		
		ings.clear();
		
		setupRecipe(type, shape, ingredients);
		
		ingredients.forEach((k, v) -> v.clear());
		ingredients.clear();
	}
}
