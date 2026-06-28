package fr.kosmosuniverse.kuffle.states;

import fr.kosmosuniverse.kuffle.core.PartyTmp;
import org.bukkit.entity.Player;

/**
 * @author KosmosUniverse
 */
public class NotRunningState implements GameState {
    @Override
    public States getState() {
        return States.NOT_RUNNING;
    }

    @Override
    public boolean checkConfigUpdatability() {
        return true;
    }

    @Override
    public boolean launch(PartyTmp party, Player player) {
        party.setState(States.RUNNING);
        party.launchGame(player);
        return true;
    }

    @Override
    public boolean pause(PartyTmp party) {
        return false;
    }

    @Override
    public boolean resume(PartyTmp party) {
        return false;
    }

    @Override
    public boolean stop(PartyTmp party) {
        return false;
    }

    @Override
    public void runLoop() {
        throw new UnsupportedOperationException("Not supported.");
    }
}
