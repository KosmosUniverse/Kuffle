package fr.kosmosuniverse.kuffle.core;

import java.util.Objects;

import fr.kosmosuniverse.kuffle.KuffleMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class GameLoop {
	private BukkitTask runnable;

	/**
	 * Starts the runnable
	 */
	public void startRunnable() {
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				PartyTmp.getInstance().getGameState().runLoop();
			}
		}.runTaskTimer(KuffleMain.getInstance(), 0, 20);
	}

	public void processGame() {
		if (PartyTmp.getInstance().getOptions().checkEndCondition()) {
			PartyTmp.getInstance().stopGame();
		} else {
			PartyTmp.getInstance().getGameState().runLoop();
		}
	}

	public void processPause() {

	}

	/**
	 * Check Block for kuffle Block type
	 *
	 * @param playerName	The player name
	 * @param playerData	The player data to check
	 * 
	 * @return True if the block is valid, False instead
	 */
	private boolean checkBlock(String playerName, PlayerData playerData) {
		if (Bukkit.getOnlinePlayers().stream().noneMatch(p -> p.getName().equals(playerName))) {
			return false;
		}

		Location pPosition = Objects.requireNonNull(Bukkit.getPlayer(playerName)).getLocation().clone().add(0, -1, 0);
		double pY = pPosition.getY();
		
		for (double y = pY; y < (pY + 3); y++) {
			pPosition.setY(y);
			
			if (Config.getDouble()) {
				String[] targets = playerData.getCurrentTarget().split("/");
				
				if (targets[0].equals(pPosition.getBlock().getType().name().toLowerCase()) ||
						targets[1].equals(pPosition.getBlock().getType().name().toLowerCase())) {
					
					return true;
				}
			} else if (playerData.getCurrentTarget().equals(pPosition.getBlock().getType().name().toLowerCase())) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Stops the runnable
	 */
	public void kill() {
		if (runnable != null) {
			runnable.cancel();
		}
	}
}
