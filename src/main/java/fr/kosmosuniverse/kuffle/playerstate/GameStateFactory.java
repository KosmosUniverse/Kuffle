package fr.kosmosuniverse.kuffle.playerstate;

import fr.kosmosuniverse.kuffle.core.PlayerData;

/**
 * @author KosmosUniverse
 */
public class GameStateFactory {
    public static PlayerGameState create(PlayerData playerData, State state, PlayerGameState playerState) {
        if (!playerState.getRawState().isStateAllowed(state)) {
            throw new IllegalStateException("Cannot use the state [" + state.name() + "] onto the state [" + playerState.getRawState().name() + "]");
        }

        switch (state) {
            case DEAD:
                return new PlayerDeadState(playerState);
            case PLAYING:
                return new PlayerPlayingState(playerState);
            case WAITING:
                return new PlayerWaitingState(playerState);
            case ABANDONED:
                return new PlayerAbandonedState(playerData, playerState);
            case FINISHED:
                return new PlayerFinishedState(playerData, playerState);
            default:
                return playerState;
        }
    }
}
