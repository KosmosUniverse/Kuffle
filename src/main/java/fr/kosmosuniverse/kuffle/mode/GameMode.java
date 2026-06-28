package fr.kosmosuniverse.kuffle.mode;

import fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
import fr.kosmosuniverse.kuffle.listeners.PlayerInteract;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author KosmosUniverse
 */
public interface GameMode {
    void clear();
    void clearMode();
    void setupSbtt();
    void clearSbtt();
    Mode getMode();
    void initMode(JavaPlugin plugin) throws KuffleFileLoadException;
    void setPlayerInteractListener(PlayerInteract listener);
    PlayerInteract getPlayerInteractListener();
}
