package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.multiblock.MultiblockManager;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleMultiBlocks extends AKuffleCommand {
	public KuffleMultiBlocks() {
		super("k-multiblocks", null, null, 0, 0, false);
	}

	@Override
	public boolean runCommand() {
		player.openInventory(MultiblockManager.getMultiblocksInventories());
		
		return true;
	}
}
