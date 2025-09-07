package fr.kosmosuniverse.kuffle.tabcompleters;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.kosmosuniverse.kuffle.core.Party;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleListTab extends AKuffleTabCommand {
	private final List<String> list = new ArrayList<>();
	
	/**
	 * Constructor
	 */
	public KuffleListTab() {
		super();
		
		list.add("add");
		list.add("remove");
		list.add("reset");
	}
	
	@Override
	protected void runCommand() {
		if (currentArgs.length == 1) {
			ret.addAll(list);
		} else if (currentArgs.length == 2) {
			listAddRemove();
		}
	}
	
	/**
	 * List all players and target op can add or remove
	 */
	private void listAddRemove() {
		if (currentArgs[0].equals("add")) {
			ret.add("@a");

			ret.addAll(Bukkit.getOnlinePlayers().stream().filter(p -> !Party.getInstance().getPlayers().has(p.getName()) && !Party.getInstance().getSpectators().has(p.getName())).map(Player::getName).collect(Collectors.toList()));
		} else if (currentArgs[0].equals("remove")) {
			ret.addAll(Party.getInstance().getPlayers().getList());
		}
	}
}
