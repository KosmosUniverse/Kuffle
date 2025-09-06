package fr.kosmosuniverse.kuffle.tabcompleters;

import java.util.ArrayList;
import java.util.List;

import fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import fr.kosmosuniverse.kuffle.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class AKuffleTabCommand implements TabCompleter {
	protected String name;
	protected int argsMin;
	protected int argsMax;
	protected String[] currentArgs;
	protected List<String> ret;
	
	protected abstract void runCommand() throws KuffleCommandFalseException;
	
	protected AKuffleTabCommand(String cmdName, Integer aMin, Integer aMax) {
		name = cmdName;
		argsMin = aMin;
		argsMax = aMax;
	}
	
	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String msg, String[] args) {
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
