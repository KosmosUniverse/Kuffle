package fr.kosmosuniverse.kuffle.options;

import fr.kosmosuniverse.kuffle.core.ConfigHolder;

/**
 * @author KosmosUniverse
 */
public class OptionFactory {
    public static GameOption createOptionsFromConfig(GameOption options, ConfigHolder configValues) {
        if (configValues.isCoop()) {
            options = new CoopOption(options);
        }

        if (configValues.isSame()) {
            options = new SameOption(options);
        }

        if (configValues.isSame()) {
            options = new SaturationOption(options);
        }

        if (configValues.isDuoMode()) {
            options = new DoubleOption(options);
        }

        if (configValues.isSbttMode()) {
            options = new SbttOption(options);
        }

        if (configValues.isTeam()) {
            options = new TeamOption(options);
        }

        if (configValues.isEndOne()) {
            options = new EndWhenOneOption(options);
        }

        return options;
    }
}
