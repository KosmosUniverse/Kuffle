package fr.kosmosuniverse.kuffle.tabcompleters;

import fr.kosmosuniverse.kuffle.KuffleMain;
import fr.kosmosuniverse.kuffle.utils.FileUtils;
import fr.kosmosuniverse.kuffle.utils.Utils;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleConfigTab extends AKuffleTabCommand {
	private final Map<String, List<String>> all = new HashMap<>();
	
	/**
	 * Constructor that read file and fill display values map
	 */
	public KuffleConfigTab() {
		super();
		
		try {
			String rawValues = Utils.readFileContent(KuffleMain.getInstance().getResource("configValuesDisplay.json"));
			
			processStringsToList(rawValues);
		} catch (IOException e) {
			Utils.logException(e);
		}
	}
	
	/**
	 * Process file content into a map, key is config element and value are possible values
	 * 
	 * @param content	File content to parse
	 *
	 */
	private void processStringsToList(String content) {
		JSONObject mainObj = FileUtils.readJSONObjectFromContent(content);
		
		for (String key : mainObj.keySet()) {
			String[] raw = ((String) mainObj.get(key)).split(":");

			List<String> list = Arrays.asList(raw);
			
			all.put(key, list);
		}
	}

	@Override
	protected void runCommand() {
		if (currentArgs.length == 0) {
			ret.addAll(all.keySet());
		} else if (currentArgs.length % 2 == 1) {
			ret.addAll(all.keySet());
			
			for (String arg : currentArgs) {
				ret.remove(arg);
			}
		} else {
			if (all.containsKey(currentArgs[currentArgs.length - 2])) {
				ret.addAll(all.get(currentArgs[currentArgs.length - 2]));
			}
		}
	}
}
