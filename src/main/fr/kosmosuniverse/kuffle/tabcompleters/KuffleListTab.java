package main.fr.kosmosuniverse.kuffle.tabcompleters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleListTab extends AKuffleTabCommand {
	private List<String> list = new ArrayList<>();
	
	/**
	 * Constructor
	 */
	public KuffleListTab() {
		super("k-list", 0, 2);
		
		list.add("add");
		list.add("remove");
		list.add("reset");
	}
	
	@Override
	protected void runCommand() throws KuffleCommandFalseException {
		if (currentArgs.length == 1) {
			ret.addAll(list);
		} else if (currentArgs.length == 2) {
			if (currentArgs[0].equals("add")) {
				ret.add("@a");
				
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (!GameManager.hasPlayer(player.getName()) && !GameManager.hasSpectator(player)) {
						ret.add(player.getName());
					}
				}
			} else if (currentArgs[0].equals("remove")) {
				for (String playerName : GameManager.getPlayerNames()) {
					ret.add(playerName);
				}
			}
		}
	}
}
