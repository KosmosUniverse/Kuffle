package main.fr.kosmosuniverse.kuffle.tabcompleters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Team;

public class KuffleAddDuringGameTab implements TabCompleter {
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player)) {
			return new ArrayList<>();
		}
		
		List<String> ret = new ArrayList<>();
		
		if (args.length == 1) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!KuffleMain.games.containsKey(player.getName())) {
					ret.add(player.getName());
				}
			}
		} else if (args.length == 2 && KuffleMain.config.getTeam()) {
			for (Team team : KuffleMain.teams.getTeams()) {
				if (KuffleMain.config.getTeamSize() > team.players.size()) {
					ret.add(team.name);
				}
			}
		}
		
		return ret;
	}
}
