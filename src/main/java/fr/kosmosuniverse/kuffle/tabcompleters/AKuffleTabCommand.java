package fr.kosmosuniverse.kuffle.tabcompleters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class AKuffleTabCommand implements TabCompleter {
	protected String[] currentArgs;
	protected List<String> ret;
	
	protected abstract void runCommand();
	
	protected AKuffleTabCommand() {
	}
	
	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String msg, String[] args) {
		if (!(sender instanceof Player)) {
			return new ArrayList<>();
		}
		
		currentArgs = args;
		
		ret = new ArrayList<>();

		runCommand();

		return ret;
	}
}
