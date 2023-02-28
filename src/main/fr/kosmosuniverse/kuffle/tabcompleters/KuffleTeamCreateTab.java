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
public class KuffleTeamCreateTab extends AKuffleTabCommand {
	public KuffleTeamCreateTab() {
		super("k-team-create", 1, 2);
	}

	@Override
	protected void runCommand() throws KuffleCommandFalseException {
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
