package fr.kosmosuniverse.kuffle.states;

import fr.kosmosuniverse.kuffle.core.PartyTmp;
import org.bukkit.entity.Player;

/**
 * @author KosmosUniverse
 */
public class RunningState implements GameState {
    @Override
    public States getState() {
        return States.RUNNING;
    }

    @Override
    public boolean checkConfigUpdatability() {
        return false;
    }

    @Override
    public boolean launch(PartyTmp party, Player player) {
        return false;
    }

    @Override
    public boolean pause(PartyTmp party) {
        party.setState(States.PAUSED);
        party.pauseGame();

        return true;
    }

    @Override
    public boolean resume(PartyTmp party) {
        return false;
    }

    @Override
    public boolean stop(PartyTmp party) {
        party.setState(States.NOT_RUNNING);
        party.stopGame();

        return true;
    }

    @Override
    public void runLoop() {
        PartyTmp.getInstance().getGameManager().getGameLoop().processGame();
    }
}
