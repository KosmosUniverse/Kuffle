package fr.kosmosuniverse.kuffle.options;

import fr.kosmosuniverse.kuffle.core.PlayerData;
import org.bukkit.entity.Player;

/**
 * @author KosmosUniverse
 */
public interface GameOption {
    boolean launchChecks(Player player);
    void initOption();
    void launch();
    void pause();
    void resume();
    void stop();

    void targetFound();
    String newSbtt(PlayerData playerData);
    String newTarget(PlayerData playerData);
    String getTargetDisplay(PlayerData playerData);
    boolean checkPlayerTarget(String target, String targetToTest);
    boolean checkAge(PlayerData playerData);
    void nextAge(PlayerData playerData);
    void updatePlayerListName(PlayerData playerData);
    String getPlayerBarString(PlayerData playerData);
    long getNbPlayerStillPlaying();
    boolean checkEndCondition();
    void saveOption();
    boolean loadOption();
    long getGlobalTimerInterval();
    void setGlobalTimerInterval(long globalTimerInterval);
    String getTargetMsg(PlayerData playerData);
    String getWaitTargetMsg(PlayerData playerData);
    String getSkipStr(PlayerData playerData);
    void targetSkipped(PlayerData playerData);
}
