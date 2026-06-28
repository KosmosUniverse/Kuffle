package fr.kosmosuniverse.kuffle.options;

import fr.kosmosuniverse.kuffle.core.PlayerData;
import org.bukkit.entity.Player;

/**
 * @author KosmosUniverse
 */
public class OptionDecorator implements GameOption {
    private final GameOption option;

    protected OptionDecorator(GameOption option) {
        this.option = option;
    }

    public boolean launchChecks(Player player) {
        return option.launchChecks(player);
    }

    public void initOption() {
        option.initOption();
    }

    public void launch() {
        option.launch();
    }

    public void pause() {
        option.pause();
    }

    public void resume() {
        option.resume();
    }

    public void stop() {
        option.stop();
    }

    public void targetFound() {
        option.targetFound();
    }

    public String newTarget(PlayerData playerData) {
        return option.newTarget(playerData);
    }

    public String getTargetDisplay(PlayerData playerData) {
        return option.getTargetDisplay(playerData);
    }

    public String newSbtt(PlayerData playerData) {
        return option.newSbtt(playerData);
    }

    public boolean checkPlayerTarget(String target, String targetToTest) {
        return option.checkPlayerTarget(target, targetToTest);
    }

    public boolean checkAge(PlayerData playerData) {
        return option.checkAge(playerData);
    }

    public void nextAge(PlayerData playerData) {
        option.nextAge(playerData);
    }

    public void updatePlayerListName(PlayerData playerData) {
        option.updatePlayerListName(playerData);
    }

    public String getPlayerBarString(PlayerData playerData) {
        return option.getPlayerBarString(playerData);
    }

    public long getNbPlayerStillPlaying() {
        return option.getNbPlayerStillPlaying();
    }

    public boolean checkEndCondition() {
        return option.checkEndCondition();
    }

    public void saveOption() {
        option.saveOption();
    }

    public boolean loadOption() {
        return option.loadOption();
    }

    public long getGlobalTimerInterval() {
        return option.getGlobalTimerInterval();
    }

    public void setGlobalTimerInterval(long globalTimerInterval) {
        option.setGlobalTimerInterval(globalTimerInterval);
    }

    public String getTargetMsg(PlayerData playerData) {
        return option.getTargetMsg(playerData);
    }

    public String getWaitTargetMsg(PlayerData playerData) {
        return option.getWaitTargetMsg(playerData);
    }

    public String getSkipStr(PlayerData playerData) {
        return option.getSkipStr(playerData);
    }

    public void targetSkipped(PlayerData playerData) {
        option.targetSkipped(playerData);
    }
}
