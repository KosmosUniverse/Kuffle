package main.fr.kosmosuniverse.kuffle.tabcompleters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Team;

public class KuffleTeamRemovePlayerTab implements TabCompleter {
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player)) {
			return new ArrayList<>();
		}
		
		if (args.length == 1) {
			List<String> ret = new ArrayList<>();
			
			for (Team item : KuffleMain.teams.getTeams()) {
				if (item.players.size() != 0) {
					ret.add(item.name);
				}
			}
			
			return ret;
		} else if (args.length == 2 && KuffleMain.teams.hasTeam(args[0])) {
			return KuffleMain.teams.getTeam(args[0]).getPlayersName();
		}

		return new ArrayList<>();
	}
}
