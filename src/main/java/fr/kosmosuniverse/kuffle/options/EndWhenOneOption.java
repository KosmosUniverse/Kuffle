package fr.kosmosuniverse.kuffle.options;

import fr.kosmosuniverse.kuffle.core.PartyTmp;

/**
 * @author KosmosUniverse
 */
public class EndWhenOneOption extends OptionDecorator {
    public EndWhenOneOption(GameOption option) {
        super(option);
    }

    @Override
    public void stop() {
        if (specificEndCondition()) {
            PartyTmp.getInstance().sendAll("All but 1 have finished ! The remaining player/team is forced abandon. Game is finished.");
            PartyTmp.getInstance().getGameManager().forceLastFinish();
        } else {
            super.stop();
        }
    }

    @Override
    public boolean checkEndCondition() {
        return super.checkEndCondition() || specificEndCondition();
    }

    private boolean specificEndCondition() {
        return getNbPlayerStillPlaying() == 1;
    }
}
