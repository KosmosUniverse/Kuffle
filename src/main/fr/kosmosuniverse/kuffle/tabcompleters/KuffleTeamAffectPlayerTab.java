package main.fr.kosmosuniverse.kuffle.tabcompleters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.Team;
import main.fr.kosmosuniverse.kuffle.core.TeamManager;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleTeamAffectPlayerTab implements TabCompleter {
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player)) {
			return new ArrayList<>();
		}
		
		if (args.length == 1) {
			List<String> ret = new ArrayList<>();
			
			for (Team item : TeamManager.getTeams()) {
				ret.add(item.name);
			}
			
			return ret;
		} else if (args.length == 2) {
			List<String> ret = new ArrayList<>();
			
			for (String item : GameManager.getPlayerNames()) {
				if (!TeamManager.isInTeam(item)) {
					ret.add(item);
				}
			}
			
			return ret;
		}

		return new ArrayList<>();
	}
}
