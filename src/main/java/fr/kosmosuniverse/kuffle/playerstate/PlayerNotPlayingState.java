package fr.kosmosuniverse.kuffle.playerstate;

import fr.kosmosuniverse.kuffle.core.PlayerData;
import org.bukkit.boss.BarColor;

/**
 * @author KosmosUniverse
 */
public class PlayerNotPlayingState implements PlayerGameState {
    @Override
    public boolean isFinal() {
        return false;
    }

    @Override
    public State getRawState() {
        return State.NOT_PLAYING;
    }

    @Override
    public PlayerGameState getState() {
        return null;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public boolean isWaiting() {
        return false;
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isAbandoned() {
        return false;
    }

    @Override
    public boolean isDead() {
        return false;
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
    public boolean finish() {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public void updateBossBar(PlayerData playerData) {
        throw new UnsupportedOperationException("Not supported.");
    }

    @Override
    public String getBossBarStr(PlayerData playerData) {
        return "";
    }

    @Override
    public double getBossBarProgress(PlayerData playerData) {
        return 0.0d;
    }

    @Override
    public BarColor getBossBarColor(PlayerData playerData) {
        return BarColor.PURPLE;
    }

    @Override
    public String getActionBarStr(PlayerData playerData) {
        return "";
    }

    @Override
    public void nextAge(PlayerData playerData) {
        throw new UnsupportedOperationException("Not supported.");
    }
}
