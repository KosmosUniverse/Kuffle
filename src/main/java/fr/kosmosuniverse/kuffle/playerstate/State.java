package fr.kosmosuniverse.kuffle.playerstate;

import java.util.Arrays;
import java.util.List;

/**
 * @author KosmosUniverse
 */
public enum State {
    FINISHED(),
    ABANDONED(FINISHED),
    DEAD(ABANDONED),
    WAITING(DEAD, FINISHED, ABANDONED),
    PLAYING(DEAD, WAITING, FINISHED, ABANDONED),
    NOT_PLAYING(PLAYING);

    private final List<State> nextAvailableStates;

    State(State... nextState) {
        nextAvailableStates = Arrays.asList(nextState);
    }

    public boolean isStateAllowed(State state) {
        return nextAvailableStates.contains(state);
    }
}
