package fr.kosmosuniverse.kuffle.tabcompleters;

import fr.kosmosuniverse.kuffle.core.Config;
import fr.kosmosuniverse.kuffle.core.GameManager;
import fr.kosmosuniverse.kuffle.core.Team;
import fr.kosmosuniverse.kuffle.core.TeamManager;
import fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleAddDuringGameTab extends AKuffleTabCommand {
	public KuffleAddDuringGameTab() {
		super("k-add-during-game", 1, 2);
	}

	@Override
	protected void runCommand() throws KuffleCommandFalseException {
		if (currentArgs.length == 1) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!GameManager.hasPlayer(player.getName())) {
					ret.add(player.getName());
				}
			}
		} else if (currentArgs.length == 2 && Config.getTeam()) {
			for (Team team : TeamManager.getInstance().getTeams()) {
				if (Config.getTeamSize() > team.getPlayers().size()) {
					ret.add(team.getName());
				}
			}
		}
	}
}
