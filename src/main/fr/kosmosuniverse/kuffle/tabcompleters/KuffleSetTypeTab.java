package main.fr.kosmosuniverse.kuffle.tabcompleters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.type.KuffleType;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleSetTypeTab implements TabCompleter  {
	private List<String> types = new ArrayList<>();

	/**
	 * Constructor
	 */
	public KuffleSetTypeTab() {
		for (KuffleType.Type type : KuffleType.Type.values()) {
			types.add(type.name());
		}
	}
	
	/**
	 * Clears <types> List
	 */
	public void clear() {
		types.clear();
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player)) {
			return new ArrayList<>();
		}
		
		if (args.length == 1) {
			return types;
		}
		
		return new ArrayList<>();
	}
}