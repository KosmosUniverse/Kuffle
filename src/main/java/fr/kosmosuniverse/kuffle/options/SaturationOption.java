package fr.kosmosuniverse.kuffle.options;

import fr.kosmosuniverse.kuffle.core.PartyTmp;

/**
 * @author KosmosUniverse
 */
public class SaturationOption extends OptionDecorator
{
	public SaturationOption(GameOption option) {
        super(option);
    }

    @Override
    public void launch() {
        super.launch();

        PartyTmp.getInstance().setSaturation();
    }
}
