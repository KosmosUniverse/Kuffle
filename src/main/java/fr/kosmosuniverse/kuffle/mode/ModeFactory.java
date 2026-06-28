package fr.kosmosuniverse.kuffle.mode;

import fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author KosmosUniverse
 */
public class ModeFactory {
    public static GameMode create(Mode mode, GameMode baseMode, JavaPlugin javaPlugin) throws KuffleFileLoadException {
        if (!baseMode.getMode().isModeAllowedNext(mode)) {
            throw new IllegalStateException("Cannot use the mode [" + mode.name() + "] onto the state [" + baseMode.getMode().name() + "]");
        }

        switch (mode) {
            case ITEMS:
                return new ItemMode(baseMode, javaPlugin);
            case BLOCKS:
                return new BlockMode(baseMode, javaPlugin);
            default:
                return baseMode;
        }
    }
}
