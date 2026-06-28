package fr.kosmosuniverse.kuffle.playerstate;

import fr.kosmosuniverse.kuffle.core.PlayerData;

/**
 * @author KosmosUniverse
 */
public class PlayerDeadState extends PlayerStateDecorator {
    public PlayerDeadState(PlayerGameState playerState) {
        super(playerState);
    }

    @Override
    public State getRawState() {
        return State.DEAD;
    }

    @Override
    public boolean isDead() {
        return true;
    }

    @Override
    public boolean targetFound(PlayerData playerData) {
        return false;
    }

    @Override
    public boolean sbttFound(PlayerData playerData) {
        return false;
    }
}
