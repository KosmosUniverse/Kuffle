package main.fr.kosmosuniverse.kuffle.tabcompleters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.core.GameManager;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleListTab implements TabCompleter {
	private List<String> list = new ArrayList<>();
	
	/**
	 * Constructor
	 */
	public KuffleListTab() {
		list.add("add");
		list.add("remove");
		list.add("reset");
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender,  Command cmd, String msg, String[] args) {
		List<String> ret = new ArrayList<>();
		
		if (!(sender instanceof Player)) {
			return ret;
		}
		
		if (args.length == 1) {
			return list;
		} else if (args.length == 2) {
			if (args[0].equals("add")) {
				ret.add("@a");
				
				for (Player player : Bukkit.getOnlinePlayers()) {
					ret.add(player.getName());
				}
			} else if (args[0].equals("remove")) {
				for (String playerName : GameManager.getPlayerNames()) {
					ret.add(playerName);
				}
			}
		}
		
		return ret;
	}
}
