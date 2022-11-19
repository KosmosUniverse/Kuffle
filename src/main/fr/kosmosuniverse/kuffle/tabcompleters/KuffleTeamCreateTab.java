package main.fr.kosmosuniverse.kuffle.tabcompleters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.core.TeamManager;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleTeamCreateTab implements TabCompleter {
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player)) {
			return new ArrayList<>();
		}
		
		if (args.length == 2) {
			List<String> colorList = new ArrayList<>();
			List<String> colorUsed = TeamManager.getInstance().getTeamColors();
			
			for (ChatColor item : ChatColor.values()) {
				if (!colorUsed.contains(item.name())) {
					colorList.add(item.name());	
				}
			}
			
			colorUsed.clear();
			
			return colorList;
		}

		return new ArrayList<>();
	}
}
