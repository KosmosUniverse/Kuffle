package main.fr.kosmosuniverse.kuffle.commands;

import main.fr.kosmosuniverse.kuffle.multiblock.MultiblockManager;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleMultiBlocks extends AKuffleCommand {
	public KuffleMultiBlocks(String cmdName, Boolean typed, Boolean started, Integer aMin, Integer aMax, boolean team) {
		super(cmdName, typed, started, aMin, aMax, team);
	}

	@Override
	public boolean runCommand() {
		player.openInventory(MultiblockManager.getMultiblocksInventories());
		
		return true;
	}
}
