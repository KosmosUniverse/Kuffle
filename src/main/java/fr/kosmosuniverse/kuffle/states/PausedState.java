package fr.kosmosuniverse.kuffle.states;

import fr.kosmosuniverse.kuffle.core.PartyTmp;
import org.bukkit.entity.Player;

/**
 * @author KosmosUniverse
 */
public class PausedState implements GameState {
    @Override
    public States getState() {
        return States.PAUSED;
    }

    @Override
    public boolean checkConfigUpdatability() {
        return false;
    }

    @Override
    public boolean launch(PartyTmp party,  Player player) {
        return false;
    }

    @Override
    public boolean pause(PartyTmp party) {
        return false;
    }

    @Override
    public boolean resume(PartyTmp party) {
        party.setState(States.RUNNING);
        party.resumeGame();

        return true;
    }

    @Override
    public boolean stop(PartyTmp party) {
        party.setState(States.NOT_RUNNING);
        party.stopGame();

        return true;
    }

    @Override
    public void runLoop() {
        PartyTmp.getInstance().getGameManager().getGameLoop().processPause();
    }
}
