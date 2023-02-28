package main.fr.kosmosuniverse.kuffle.tabcompleters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

public abstract class AKuffleTabCommand implements TabCompleter {
	protected String name = null;
	protected int argsMin = -1;
	protected int argsMax = -1;
	protected String[] currentArgs;
	protected List<String> ret;
	
	protected abstract void runCommand() throws KuffleCommandFalseException;
	
	protected AKuffleTabCommand(String cmdName, Integer aMin, Integer aMax) {
		name = cmdName;
		argsMin = aMin;
		argsMax = aMax;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player)) {
			return new ArrayList<>();
		}
		
		currentArgs = args;
		
		ret = new ArrayList<>();
		
		try {
			runCommand();
		} catch (KuffleCommandFalseException e) {
			Utils.logException(e);
		}
		
		return ret;
	}
}
