package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.core.CraftManager;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleCrafts extends AKuffleCommand {
	public KuffleCrafts() {
		super("k-crafts", true, null, 0, 0, false);
	}

	@Override
	public boolean runCommand() {
		player.openInventory(CraftManager.getCraftsInventory());
		
		return true;
	}
}
