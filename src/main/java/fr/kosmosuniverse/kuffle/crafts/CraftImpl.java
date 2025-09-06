package fr.kosmosuniverse.kuffle.crafts;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class CraftImpl extends ACraft {
	/**
	 * Test crafts.json reading
	 * 
	 * @param jsonRecipe	Json object that represents crafts
	 */
	public CraftImpl(JSONObject jsonRecipe) {
		name = jsonRecipe.getString("Name");
		mandatory = jsonRecipe.getBoolean("Mandatory");
		setupResult(jsonRecipe.getJSONObject("Result"));
		
		Type type = Type.valueOf(jsonRecipe.getString("Type").toUpperCase());
		String shape = jsonRecipe.has("Shape") ? jsonRecipe.getString("Shape") : null;
		Map<String, Map<String, String>> ingredients = new HashMap<>();
		JSONObject ings = jsonRecipe.getJSONObject("Ingredients");
		
		for (String key : ings.keySet()) {
			Map<String, String> i = new HashMap<>();

			for (String iKey : ings.getJSONObject(key).keySet()) {
				i.put(iKey, ings.getJSONObject(key).getString(iKey));
			}
			
			ingredients.put(key, i);
		}
		
		ings.clear();
		
		setupRecipe(type, shape, ingredients);
		
		ingredients.forEach((k, v) -> v.clear());
		ingredients.clear();
	}
}
