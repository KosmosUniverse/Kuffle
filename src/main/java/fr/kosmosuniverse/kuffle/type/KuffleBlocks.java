package fr.kosmosuniverse.kuffle.type;

import fr.kosmosuniverse.kuffle.commands.KuffleMultiBlocks;
import fr.kosmosuniverse.kuffle.commands.KuffleSpawnMultiblock;
import fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
import fr.kosmosuniverse.kuffle.listeners.BlocksPlayerInteract;
import fr.kosmosuniverse.kuffle.listeners.PlayerMove;
import fr.kosmosuniverse.kuffle.multiblock.MultiblockManager;
import fr.kosmosuniverse.kuffle.tabcompleters.KuffleSpawnMultiBlocksTab;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

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

		Objects.requireNonNull(plugin.getCommand("k-multiblocks")).setExecutor(new KuffleMultiBlocks());
		Objects.requireNonNull(plugin.getCommand("k-spawn-multiblock")).setExecutor(new KuffleSpawnMultiblock());

		Objects.requireNonNull(plugin.getCommand("k-spawn-multiblock")).setTabCompleter(new KuffleSpawnMultiBlocksTab());
	}

	@Override
	public Type getType() {
		return Type.BLOCKS;
	}
}
