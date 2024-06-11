package main.fr.kosmosuniverse.kuffle.type;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import main.fr.kosmosuniverse.kuffle.commands.KuffleMultiBlocks;
import main.fr.kosmosuniverse.kuffle.commands.KuffleSpawnMuliblock;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
import main.fr.kosmosuniverse.kuffle.listeners.BlocksPlayerInteract;
import main.fr.kosmosuniverse.kuffle.listeners.PlayerMove;
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
	public KuffleBlocks(Player player, KuffleType type, JavaPlugin plugin) throws KuffleFileLoadException {
		super(type);
		setupKuffleType(player, plugin);
	}
	
	public void setupSbtt() {
		MultiblockManager.createTemplates();
	}
	
	public void clearSbtt() {
		MultiblockManager.removeTemplates();
	}
	
	/**
	 * Setups the kuffle type
	 * 
	 * @param plugin	the plugin itself
	 * 
	 * @throws KuffleFileLoadException if file loading fails
	 */
	public void setupKuffleType(Player player, JavaPlugin plugin) throws KuffleFileLoadException {
		setupType(player, plugin);
		
		if (playerInteractBlocks == null) {
			playerInteractBlocks = new BlocksPlayerInteract();
			plugin.getServer().getPluginManager().registerEvents(playerInteractBlocks, plugin);
		}
		
		plugin.getServer().getPluginManager().registerEvents(new PlayerMove(), plugin);
		
		MultiblockManager.setup();
		
		plugin.getCommand("k-multiblocks").setExecutor(new KuffleMultiBlocks());
		plugin.getCommand("k-spawn-multiblock").setExecutor(new KuffleSpawnMuliblock());
		plugin.getCommand("k-spawn-multiblock").setTabCompleter(new KuffleSpawnMultiBlocksTab());
	}
	
	@Override
	public Type getType() {
		return Type.BLOCKS;
	}
}
