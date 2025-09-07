package fr.kosmosuniverse.kuffle.tabcompleters;

import fr.kosmosuniverse.kuffle.multiblock.AMultiblock;
import fr.kosmosuniverse.kuffle.multiblock.MultiblockManager;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleSpawnMultiBlocksTab extends AKuffleTabCommand {
	private final List<String> list;
	
	/**
	 * Constructor
	 */
	public KuffleSpawnMultiBlocksTab() {
		super();
		
		list = MultiblockManager.getMultiblocks().stream().map(AMultiblock::getName).collect(Collectors.toList());
	}

	@Override
	protected void runCommand() {
		if (currentArgs.length == 1) {
			ret.addAll(list);
		}
	}
}
