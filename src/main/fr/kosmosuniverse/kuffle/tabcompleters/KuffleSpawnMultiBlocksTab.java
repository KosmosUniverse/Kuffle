package main.fr.kosmosuniverse.kuffle.tabcompleters;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.multiblock.MultiblockManager;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleSpawnMultiBlocksTab implements TabCompleter {
	public List<String> list;
	
	/**
	 * Constructor
	 */
	public KuffleSpawnMultiBlocksTab() {
		list = MultiblockManager.getMultiblocks().stream().map(m -> m.getName()).collect(Collectors.toList());
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender,  Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return null;
		
		if (cmd.getName().equalsIgnoreCase("kb-spawn-multiblock")) {
			if (args.length == 1) {
				return list;	
			}
		}
		
		return new ArrayList<String>();
	}
}
