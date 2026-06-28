package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.core.PartyTmp;
import fr.kosmosuniverse.kuffle.mode.Mode;
import fr.kosmosuniverse.kuffle.multiblock.AMultiblock;
import fr.kosmosuniverse.kuffle.multiblock.MultiblockManager;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleSpawnMultiblock extends AKuffleCommand {
	public KuffleSpawnMultiblock() {
		super("k-spawn-multiblock", true, null, 1, 1, false);
	}

	@Override
	public boolean runCommand() {
		if (PartyTmp.getInstance().getGameMode().getMode() != Mode.BLOCKS ||
				args.length != 1) {
			return false;
		}
		
		AMultiblock tmp = MultiblockManager.searchMultiBlockByName(args[0]);
		
		if (tmp != null) {
			tmp.getMultiblock().spawnMultiBlock(player);
		}
		
		return true;
	}
}
