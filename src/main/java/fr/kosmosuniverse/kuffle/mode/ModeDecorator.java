package fr.kosmosuniverse.kuffle.mode;

import fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
import fr.kosmosuniverse.kuffle.listeners.PlayerInteract;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author KosmosUniverse
 */
public class ModeDecorator implements GameMode {
    protected final GameMode gameMode;

    protected ModeDecorator(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    @Override
    public void clear() {
        gameMode.clear();
    }

    @Override
    public void clearMode() {
        gameMode.clearMode();
    }

    @Override
    public void setupSbtt() {
        gameMode.setupSbtt();
    }

    @Override
    public void clearSbtt() {
        gameMode.clearSbtt();
    }

    @Override
    public Mode getMode() {
        return gameMode.getMode();
    }

    @Override
    public void initMode(JavaPlugin plugin) throws KuffleFileLoadException {
        gameMode.initMode(plugin);
    }

    @Override
    public void setPlayerInteractListener(PlayerInteract listener) {
        gameMode.setPlayerInteractListener(listener);
    }

    @Override
    public PlayerInteract getPlayerInteractListener() {
        return gameMode.getPlayerInteractListener();
    }
}
