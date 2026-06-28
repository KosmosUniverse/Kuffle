package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.core.PartyTmp;

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
		player.openInventory(PartyTmp.getInstance().getPlayers().getPlayerHeads());
		
		return true;
	}
}
