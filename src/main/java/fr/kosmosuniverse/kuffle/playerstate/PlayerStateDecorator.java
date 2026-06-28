package fr.kosmosuniverse.kuffle.playerstate;

import fr.kosmosuniverse.kuffle.core.PlayerData;
import lombok.Getter;
import org.bukkit.boss.BarColor;

/**
 * @author KosmosUniverse
 */
public class PlayerStateDecorator implements PlayerGameState {
    @Getter
    protected final PlayerGameState state;

    public PlayerStateDecorator(PlayerGameState playerState) {
        this.state = playerState;
    }

    @Override
    public State getRawState() {
        return state.getRawState();
    }

    @Override
    public boolean isFinal() {
        return state.isFinal();
    }

    @Override
    public boolean isPlaying() {
        return state.isPlaying();
    }

    @Override
    public boolean isWaiting() {
        return state.isWaiting();
    }

    @Override
    public boolean isFinished() {
        return state.isFinished();
    }

    @Override
    public boolean isAbandoned() {
        return state.isAbandoned();
    }

    @Override
    public boolean isDead() {
        return state.isDead();
    }

    @Override
    public boolean targetFound(PlayerData playerData) {
        return state.targetFound(playerData);
    }

    @Override
    public boolean sbttFound(PlayerData playerData) {
        return state.sbttFound(playerData);
    }

    @Override
    public boolean finish() {
        return state.finish();
    }

    @Override
    public void updateBossBar(PlayerData playerData) {
        state.updateBossBar(playerData);
    }

    @Override
    public String getBossBarStr(PlayerData playerData) {
        return state.getBossBarStr(playerData);
    }

    @Override
    public double getBossBarProgress(PlayerData playerData) {
        return state.getBossBarProgress(playerData);
    }

    @Override
    public BarColor getBossBarColor(PlayerData playerData) {
        return state.getBossBarColor(playerData);
    }

    @Override
    public String getActionBarStr(PlayerData playerData) {
        return state.getActionBarStr(playerData);
    }

    @Override
    public void nextAge(PlayerData playerData) {
        state.nextAge(playerData);
    }
}
