package main.fr.kosmosuniverse.kuffle.commands;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.multiblock.AMultiblock;
import main.fr.kosmosuniverse.kuffle.multiblock.MultiblockManager;
import main.fr.kosmosuniverse.kuffle.type.KuffleType;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleSpawnMuliblock extends AKuffleCommand {
	public KuffleSpawnMuliblock() {
		super("k-spawn-multiblock", true, true, 1, 1, false);
	}

	@Override
	public boolean runCommand() {
		if (KuffleMain.getInstance().getType().getType() != KuffleType.Type.BLOCKS || args.length != 1) {
			return false;
		}
		
		AMultiblock tmp = MultiblockManager.searchMultiBlockByName(args[0]);
		
		if (tmp != null) {
			tmp.getMultiblock().spawnMultiBlock(player);
		}
		
		return true;
	}
}
