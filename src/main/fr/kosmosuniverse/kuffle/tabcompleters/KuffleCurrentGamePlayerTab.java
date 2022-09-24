package main.fr.kosmosuniverse.kuffle.tabcompleters;

import java.util.ArrayList;
import java.util.List;

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
public class KuffleCurrentGamePlayerTab implements TabCompleter {
	@Override
	public List<String> onTabComplete(CommandSender sender,  Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return new ArrayList<>();
		
		if (GameManager.getGames() != null && args.length == 1) {
			List<String> list = new ArrayList<>();
			
			GameManager.applyToPlayers((game) -> {
				if (!game.lose && !game.finished) {
					list.add(game.player.getName());	
				}
			});
			
			return list;
		}
		
		return new ArrayList<>();
	}
}
