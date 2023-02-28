package main.fr.kosmosuniverse.kuffle.tabcompleters;

import java.util.List;

import org.bukkit.ChatColor;

import main.fr.kosmosuniverse.kuffle.core.TeamManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleTeamColorTab extends AKuffleTabCommand {
	public KuffleTeamColorTab() {
		super("k-team-color", 2, 2);
	}

	@Override
	protected void runCommand() throws KuffleCommandFalseException {
		if (currentArgs.length == 1) {
			TeamManager.getInstance().getTeams().stream().forEach(t -> ret.add(t.getName()));
		} else if (currentArgs.length == 2) {
			List<String> colorUsed = TeamManager.getInstance().getTeamColors();
			
			for (ChatColor item : ChatColor.values()) {
				if (!colorUsed.contains(item.name())) {
					ret.add(item.name());	
				}
			}
			
			colorUsed.clear();
		}
	}
}
