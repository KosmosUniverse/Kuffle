package fr.kosmosuniverse.kuffle.mode;

import fr.kosmosuniverse.kuffle.datamanagers.crafts.CraftManager;
import fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
import fr.kosmosuniverse.kuffle.listeners.ItemsPlayerInteract;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author KosmosUniverse
 */
public class ItemMode extends ModeDecorator {
    /**
     * Constructor
     *
     * @param plugin	the plugin itself
     *
     * @throws KuffleFileLoadException if one of the resource file load fails
     */
    public ItemMode(GameMode mode, JavaPlugin plugin) throws KuffleFileLoadException {
        super(mode);
        initMode(plugin);
    }

    public void setupSbtt() {
        CraftManager.setupCraftTemplates();
    }

    public void clearSbtt() {
        CraftManager.removeCraftTemplates();
    }

    /**
     * Gets the current type
     *
     * @return Type.ITEMS
     */
    @Override
    public Mode getMode() {
        return Mode.ITEMS;
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
            setPlayerInteractListener(new ItemsPlayerInteract());
            plugin.getServer().getPluginManager().registerEvents(getPlayerInteractListener(), plugin);
        }
    }


}
