package fr.kosmosuniverse.kuffle.playerstate;

import fr.kosmosuniverse.kuffle.core.PlayerData;
import org.bukkit.boss.BarColor;

/**
 * @author KosmosUniverse
 */
public interface PlayerGameState {
    State getRawState();
    PlayerGameState getState();

    boolean isFinal();

    boolean isPlaying();
    boolean isWaiting();
    boolean isFinished();
    boolean isAbandoned();
    boolean isDead();

    boolean targetFound(PlayerData playerData);
    boolean sbttFound(PlayerData playerData);
    boolean finish();
    void updateBossBar(PlayerData playerData);
    String getBossBarStr(PlayerData playerData);
    double getBossBarProgress(PlayerData playerData);
    BarColor getBossBarColor(PlayerData playerData);
    String getActionBarStr(PlayerData playerData);
    void nextAge(PlayerData playerData);
}
