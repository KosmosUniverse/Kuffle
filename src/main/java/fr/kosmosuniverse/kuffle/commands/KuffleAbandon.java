package fr.kosmosuniverse.kuffle.commands;

import java.util.*;

import fr.kosmosuniverse.kuffle.KuffleMain;
import fr.kosmosuniverse.kuffle.core.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleAbandon extends AKuffleCommand {
	private final List<UUID> abandonToConfirm;
	private final Map<String, List<String>> abandoned;
	
	/**
	 * KuffleAbandon constructor
	 */
	public KuffleAbandon() {
		super("k-abandon", null, true, 0, 0, false);
		
		abandoned = new HashMap<>();
		abandonToConfirm = new ArrayList<>();
	}

	@Override
	public boolean runCommand() {
		if (!Party.getInstance().getPlayers().has(player.getName())) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_PLAYING", Party.getInstance().getGames().getGames().get(player.getName()).getConfigLang()));
		} else {
			if (abandonToConfirm.contains(player.getUniqueId())) {
				abandonConfirmed();
			} else {
				abandonToConfirm(player);
			}
		}

		return true;
	}

	/**
	 * Waits for the abandon confirmation
	 */
	private void abandonToConfirm(Player player) {
		abandonToConfirm.add(player.getUniqueId());
		LogManager.getInstanceSystem().writeMsg(player, "Please, re-send the exact same command within 10sec to confirm abandon.");
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
			if (abandonToConfirm.contains(player.getUniqueId())) {
				abandonToConfirm.remove(player.getUniqueId());
				LogManager.getInstanceSystem().writeMsg(player, "[Warning] : Command /k-abandon cancelled.");
			}
		}, 200);
	}
	
	/**
	 * Confirm the abandon of the player
	 */
	private void abandonConfirmed() {
		abandonToConfirm.remove(player.getUniqueId());
		
		if (Config.getTeam()) {
			Team team = TeamManager.getInstance().getTeamByPlayer(player.getName());
			List<String> players;
			
			if (abandoned.containsKey(team.getName())) {
				players = abandoned.get(team.getName());
			} else {
				players = new ArrayList<>();
			}
			
			if (!players.contains(player.getName())) {
				players.add(player.getName());
			}
			
			abandoned.put(team.getName(), players);
			
			if (checkTeam(team)) {
				Party.getInstance().getGames().teamLose(team.getName());
			} else {
				LogManager.getInstanceSystem().writeMsg(player, "All players of the team have to abandon for this to be effective.");
			}
		} else {
			Party.getInstance().getGames().playerLose(player.getName());
		}
	}
	
	/**
	 * Checks if all teammates have abandoned
	 * 
	 * @param team	name of the Team to check
	 * 
	 * @return True if all teammates have abandoned, False instead
	 */
	private boolean checkTeam(Team team) {
		return new HashSet<>(abandoned.get(team.getName())).containsAll(team.getPlayers());
	}

	/**
	 * Clears the abandoned map
	 */
	public void clear() {
		if (abandoned != null) {
			abandoned.forEach((k, v) -> {
				if (v != null) {
					v.clear();
				}
			});

			abandoned.clear();
		}

		if (abandonToConfirm != null) {
			abandonToConfirm.clear();
		}
	}
}
