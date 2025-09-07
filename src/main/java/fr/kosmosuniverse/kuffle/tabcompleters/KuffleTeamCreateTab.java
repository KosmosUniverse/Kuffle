package fr.kosmosuniverse.kuffle.tabcompleters;

import java.util.List;

import fr.kosmosuniverse.kuffle.core.TeamManager;
import org.bukkit.ChatColor;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleTeamCreateTab extends AKuffleTabCommand {
	public KuffleTeamCreateTab() {
		super();
	}

	@Override
	protected void runCommand() {
		if (currentArgs.length == 2) {
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
