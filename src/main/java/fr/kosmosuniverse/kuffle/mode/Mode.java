package fr.kosmosuniverse.kuffle.mode;

import java.util.Arrays;
import java.util.List;

/**
 * @author KosmosUniverse
 */
public enum Mode {
    ITEMS(),
    BLOCKS(),
    NO_MODE(ITEMS, BLOCKS);

    final List<Mode> nextAllowedModes;

    Mode(Mode... modes) {
        nextAllowedModes = Arrays.asList(modes);
    }

    public boolean isModeAllowedNext(Mode mode) {
        return nextAllowedModes.contains(mode);
    }

    public static boolean compareRealModeToString(Mode mode, String rawMode) {
        if (mode == NO_MODE) {
            return false;
        }

        if (mode == ITEMS ||  mode == BLOCKS) {
            if (rawMode.equalsIgnoreCase("BOTH")) {
                return true;
            }

            return ITEMS.name().equalsIgnoreCase(rawMode) ||  BLOCKS.name().equalsIgnoreCase(rawMode);
        }

        return false;
    }

    public static boolean hasMode(String mode) {
        try {
            Mode.valueOf(mode);
        } catch (IllegalArgumentException ignored) {
            return false;
        }

        return true;
    }
}
