package main.fr.kosmosuniverse.kuffle.tabcompleters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleConfigTab implements TabCompleter {
	private Map<String, List<String>> all = new HashMap<>();
	
	/**
	 * Constructor that read file and fill display values map
	 */
	public KuffleConfigTab() {
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
			List<String> list = new ArrayList<>();
			
			for (String s : raw) {
				list.add(s);
			}
			
			all.put((String) key, list);
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player)) {
			return new ArrayList<>();
		}
		
		if (args.length == 0) {
			return new ArrayList<>(all.keySet());
		} else if (args.length % 2 == 1) {
			List<String> ret = new ArrayList<>(all.keySet());
			
			for (String arg : args) {
				if (ret.contains(arg)) {
					ret.remove(arg);
				}
			}
			
			return ret;
		} else {
			if (all.containsKey(args[args.length - 2])) {
				return all.get(args[args.length - 2]);
			} else {
				return new ArrayList<>();	
			}
		}
	}
}
