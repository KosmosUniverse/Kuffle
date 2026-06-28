package fr.kosmosuniverse.kuffle.states;

import fr.kosmosuniverse.kuffle.core.PartyTmp;
import org.bukkit.entity.Player;

/**
 * @author KosmosUniverse
 */
public interface GameState {
    States getState();

    boolean checkConfigUpdatability();
    boolean launch(PartyTmp party, Player player);
    boolean pause(PartyTmp party);
    boolean resume(PartyTmp party);
    boolean stop(PartyTmp party);

    void runLoop();
}
