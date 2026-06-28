package fr.kosmosuniverse.kuffle.mode;

import fr.kosmosuniverse.kuffle.commands.KuffleMultiBlocks;
import fr.kosmosuniverse.kuffle.commands.KuffleSpawnMultiblock;
import fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
import fr.kosmosuniverse.kuffle.listeners.BlocksPlayerInteract;
import fr.kosmosuniverse.kuffle.listeners.PlayerMove;
import fr.kosmosuniverse.kuffle.multiblock.MultiblockManager;
import fr.kosmosuniverse.kuffle.tabcompleters.KuffleSpawnMultiBlocksTab;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

/**
 * @author KosmosUniverse
 */
public class BlockMode extends ModeDecorator {
    /**
     * Constructor
     *
     * @param plugin	the plugin itself
     *
     * @throws KuffleFileLoadException if one of the resource file load fails
     */
    public BlockMode(GameMode mode, JavaPlugin plugin) throws KuffleFileLoadException {
        super(mode);
        initMode(plugin);
    }

    @Override
    public void setupSbtt() {
        MultiblockManager.createTemplates();
    }

    @Override
    public void clearSbtt() {
        MultiblockManager.removeTemplates();
    }

    @Override
    public Mode getMode() {
        return Mode.BLOCKS;
    }

    /**
     * Setups the kuffle type
     *
     * @param plugin	the plugin itself
     *
     * @throws KuffleFileLoadException if file loading fails
     */
    @Override
    public void initMode(JavaPlugin plugin) throws KuffleFileLoadException {
        super.initMode(plugin);

        if (getPlayerInteractListener() == null) {
            setPlayerInteractListener(new BlocksPlayerInteract());
            plugin.getServer().getPluginManager().registerEvents(getPlayerInteractListener(), plugin);
        }

        plugin.getServer().getPluginManager().registerEvents(new PlayerMove(), plugin);

        MultiblockManager.setup();

        Objects.requireNonNull(plugin.getCommand("k-multiblocks")).setExecutor(new KuffleMultiBlocks());
        Objects.requireNonNull(plugin.getCommand("k-spawn-multiblock")).setExecutor(new KuffleSpawnMultiblock());

        Objects.requireNonNull(plugin.getCommand("k-spawn-multiblock")).setTabCompleter(new KuffleSpawnMultiBlocksTab());
    }
}
