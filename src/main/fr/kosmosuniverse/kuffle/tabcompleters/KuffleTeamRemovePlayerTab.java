package main.fr.kosmosuniverse.kuffle.tabcompleters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.core.Team;
import main.fr.kosmosuniverse.kuffle.core.TeamManager;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleTeamRemovePlayerTab implements TabCompleter {
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player)) {
			return new ArrayList<>();
		}
		
		if (args.length == 1) {
			List<String> ret = new ArrayList<>();
			
			for (Team item : TeamManager.getInstance().getTeams()) {
				if (item.getPlayers().size() != 0) {
					ret.add(item.getName());
				}
			}
			
			return ret;
		} else if (args.length == 2 && TeamManager.getInstance().hasTeam(args[0])) {
			return TeamManager.getInstance().getTeam(args[0]).getPlayersName();
		}

		return new ArrayList<>();
	}
}
