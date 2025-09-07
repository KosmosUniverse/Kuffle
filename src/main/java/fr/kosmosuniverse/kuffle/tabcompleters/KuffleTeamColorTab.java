package fr.kosmosuniverse.kuffle.tabcompleters;

import java.util.List;

import fr.kosmosuniverse.kuffle.core.TeamManager;
import org.bukkit.ChatColor;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleTeamColorTab extends AKuffleTabCommand {
	public KuffleTeamColorTab() {
		super();
	}

	@Override
	protected void runCommand() {
		if (currentArgs.length == 1) {
			TeamManager.getInstance().getTeams().forEach(t -> ret.add(t.getName()));
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
