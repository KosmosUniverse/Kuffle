package fr.kosmosuniverse.kuffleblocks.TabCmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import fr.kosmosuniverse.kuffleblocks.KuffleMain;

public class KuffleSpawnMultiBlocksTab implements TabCompleter {
	private KuffleMain km;
	public ArrayList<String> list = new ArrayList<String>();
	
	public KuffleSpawnMultiBlocksTab(KuffleMain _km) {
		km = _km;
		
		for (String key : km.multiBlock.getMultiBlocks().keySet()) {
			list.add(km.multiBlock.getMultiBlocks().get(key).getName());
		}
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
