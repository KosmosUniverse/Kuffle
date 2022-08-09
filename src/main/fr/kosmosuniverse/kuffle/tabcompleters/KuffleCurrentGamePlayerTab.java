package main.fr.kosmosuniverse.kuffle.tabcompleters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Game;

public class KuffleCurrentGamePlayerTab implements TabCompleter {
	@Override
	public List<String> onTabComplete(CommandSender sender,  Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return new ArrayList<>();
		
		if (args.length == 1) {
			List<String> list = new ArrayList<>();
			
			for (String playerName : KuffleMain.games.keySet()) {
				Game tmpGame = KuffleMain.games.get(playerName);
				
				if (!tmpGame.getLose() && !tmpGame.getFinished()) {
					list.add(playerName);	
				}
			}
			
			return list;
		}
		
		return new ArrayList<>();
	}
}
