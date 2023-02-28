package main.fr.kosmosuniverse.kuffle.tabcompleters;

import java.util.List;
import java.util.stream.Collectors;

import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import main.fr.kosmosuniverse.kuffle.multiblock.MultiblockManager;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleSpawnMultiBlocksTab extends AKuffleTabCommand {
	private List<String> list;
	
	/**
	 * Constructor
	 */
	public KuffleSpawnMultiBlocksTab() {
		super("k-spawn-multiblock", 1, 1);
		
		list = MultiblockManager.getMultiblocks().stream().map(m -> m.getName()).collect(Collectors.toList());
	}

	@Override
	protected void runCommand() throws KuffleCommandFalseException {
		if (currentArgs.length == 1) {
			ret.addAll(list);
		}
	}
}
