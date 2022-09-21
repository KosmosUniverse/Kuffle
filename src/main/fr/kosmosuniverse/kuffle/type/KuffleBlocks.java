package main.fr.kosmosuniverse.kuffle.type;

import org.bukkit.plugin.java.JavaPlugin;

import main.fr.kosmosuniverse.kuffle.commands.KuffleMultiBlocks;
import main.fr.kosmosuniverse.kuffle.commands.KuffleSpawnMuliblock;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
import main.fr.kosmosuniverse.kuffle.multiblock.MultiblockManager;
import main.fr.kosmosuniverse.kuffle.tabcompleters.KuffleSpawnMultiBlocksTab;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleBlocks extends KuffleTypeDecorator {
	/**
	 * Constructor
	 * 
	 * @param plugin	the plugin itself
	 * 
	 * @throws KuffleFileLoadException if one of the resource file load fails
	 */
	public KuffleBlocks(KuffleType type, JavaPlugin plugin) throws KuffleFileLoadException {
		super(type);
		setupKuffleType(plugin);
	}
	
	/**
	 * Setups the kuffle type
	 * 
	 * @param plugin	the plugin itself
	 * 
	 * @throws KuffleFileLoadException if file loading fails
	 */
	public void setupKuffleType(JavaPlugin plugin) throws KuffleFileLoadException {
		setupType(plugin);
		
		MultiblockManager.setup();
		
		if (Config.getSBTT()) {
			MultiblockManager.createTemplates();
		}
		
		plugin.getCommand("k-multiblocks").setExecutor(new KuffleMultiBlocks());
		plugin.getCommand("k-spawn-multiblock").setExecutor(new KuffleSpawnMuliblock());
		plugin.getCommand("k-spawn-multiblock").setTabCompleter(new KuffleSpawnMultiBlocksTab());
	}
	
	@Override
	public Type getType() {
		return Type.BLOCKS;
	}
}
