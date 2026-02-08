package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.core.TargetManager;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleAgeTargets extends AKuffleCommand {
	public KuffleAgeTargets() {
		super("k-agetargets", true, null, 0, 1, false);
	}

	@Override
	public boolean runCommand() {
		player.openInventory(TargetManager.getMainInv());
		
		return true;
	}
}
