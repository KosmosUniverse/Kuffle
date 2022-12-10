package main.fr.kosmosuniverse.kuffle.tabcompleters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.core.AgeManager;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleAgeTargetsTab implements TabCompleter  {
	private List<String> ages = new ArrayList<>();

	/**
	 * Constructor
	 */
	public KuffleAgeTargetsTab() {
		int max = AgeManager.getLastAgeIndex();
		
		for (int cnt = 0; cnt <= max; cnt++) {
			String age = AgeManager.getAgeByNumber(cnt).getName();

			ages.add(age);
		}
	}
	
	/**
	 * Clears the @ages list
	 */
	public void clear() {
		ages.clear();
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player)) {
			return new ArrayList<>();
		}
		
		if (args.length == 1) {
			return ages;
		}
		
		return new ArrayList<>();
	}
}
