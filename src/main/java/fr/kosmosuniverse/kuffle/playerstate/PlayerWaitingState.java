package fr.kosmosuniverse.kuffle.playerstate;

import fr.kosmosuniverse.kuffle.core.PartyTmp;
import fr.kosmosuniverse.kuffle.core.PlayerData;

/**
 * @author KosmosUniverse
 */
public class PlayerWaitingState extends PlayerStateDecorator {
    public PlayerWaitingState(PlayerGameState playerState) {
        super(playerState);
    }

    @Override
    public State getRawState() {
        return State.WAITING;
    }

    @Override
    public boolean isWaiting() {
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

    @Override
    public String getActionBarStr(PlayerData playerData) {
        return PartyTmp.getInstance().getOptions().getWaitTargetMsg(playerData);
    }
}
