package main.fr.kosmosuniverse.kuffle.tabcompleters;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleConfigTab extends AKuffleTabCommand {
	private Map<String, List<String>> all = new HashMap<>();
	
	/**
	 * Constructor that read file and fill display values map
	 */
	public KuffleConfigTab() {
		super("k-config", -1, -1);
		
		try {
			String rawValues = Utils.readFileContent(KuffleMain.getInstance().getResource("configValuesDisplay.json"));
			
			processStringsToList(rawValues);
		} catch (IOException | ParseException e) {
			Utils.logException(e);
		}
	}
	
	/**
	 * Process file content into a map, key is config element and value are possible values
	 * 
	 * @param content	File content to parse
	 * 
	 * @throws ParseException raised in case of JSON parse fail
	 */
	private void processStringsToList(String content) throws ParseException {
		JSONParser parser = new JSONParser();
		JSONObject mainObj = (JSONObject) parser.parse(content);
		
		for (Object key : mainObj.keySet()) {
			String[] raw = ((String) mainObj.get(key)).split(":");

			List<String> list = Arrays.asList(raw);
			
			all.put((String) key, list);
		}
	}

	@Override
	protected void runCommand() throws KuffleCommandFalseException {
		if (currentArgs.length == 0) {
			ret.addAll(all.keySet());
		} else if (currentArgs.length % 2 == 1) {
			ret.addAll(all.keySet());
			
			for (String arg : currentArgs) {
				if (ret.contains(arg)) {
					ret.remove(arg);
				}
			}
		} else {
			if (all.containsKey(currentArgs[currentArgs.length - 2])) {
				ret.addAll(all.get(currentArgs[currentArgs.length - 2]));
			}
		}
	}
}
