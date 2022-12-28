package main.fr.kosmosuniverse.kuffle.commands;

import main.fr.kosmosuniverse.kuffle.core.GameManager;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KufflePlayers extends AKuffleCommand {
	public KufflePlayers() {
		super("k-players", null, true, 0, 0, false);
	}
	
	@Override
	public boolean runCommand() {
		player.openInventory(GameManager.getPlayersHeadsInventory());
		
		return true;
	}
}
