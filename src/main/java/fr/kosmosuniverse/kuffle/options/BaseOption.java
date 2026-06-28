package fr.kosmosuniverse.kuffle.options;

import fr.kosmosuniverse.kuffle.core.Config;
import fr.kosmosuniverse.kuffle.core.PartyTmp;
import fr.kosmosuniverse.kuffle.datamanagers.LangManager;
import fr.kosmosuniverse.kuffle.core.PlayerData;
import fr.kosmosuniverse.kuffle.datamanagers.targets.TargetManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author KosmosUniverse
 */
public class BaseOption implements GameOption {
    @Override
    public boolean launchChecks(Player player) {
        return true;
    }

    @Override
    public void initOption() {
        PartyTmp.getInstance().getGameManager().getRanks().initPlayersRanks();
    }

    @Override
    public void launch() {}

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void stop() {}

    @Override
    public void targetFound() {}

    @Override
    public String newTarget(PlayerData playerData) {
        return TargetManager.newTarget(playerData.getAlreadyGot(), playerData.getAge().getName());
    }

    @Override
    public String getTargetDisplay(PlayerData playerData) {
        return LangManager.getTargetLang(playerData.getCurrentTarget(), playerData.getConfigLang());
    }

    @Override
    public String newSbtt(PlayerData playerData) {
        return null;
    }

    @Override
    public boolean checkPlayerTarget(String target, String targetToTest) {
        return target != null &&
                (target.equals(targetToTest) ||
                        (target.startsWith("*") && targetToTest.contains(target.replace("*", ""))));
    }

    @Override
    public boolean checkAge(PlayerData playerData) {
        if (playerData.getTargetCount() >= Config.getTargetPerAge()) {
            playerData.getAgeTimes().put(playerData.getAge().getName(), System.currentTimeMillis() - playerData.getTimeStartAge());
        }

        return playerData.getTargetCount() >= Config.getTargetPerAge();
    }

    @Override
    public void nextAge(PlayerData playerData) {
        playerData.nextAge();
    }

    @Override
    public void updatePlayerListName(PlayerData playerData) {
        Player player = Bukkit.getPlayer(playerData.getPlayerName());

        if (player == null) {
            return;
        }

        player.setPlayerListName(playerData.getAge().getColor() + playerData.getPlayerName());
    }

    @Override
    public String getPlayerBarString(PlayerData playerData) {
        return playerData.getState().getBossBarStr(playerData);
    }

    @Override
    public long getNbPlayerStillPlaying() {
        return PartyTmp.getInstance().getGameManager().getPlayersNotFinished();
    }

    @Override
    public boolean checkEndCondition() {
        return getNbPlayerStillPlaying() == 0;
    }

    @Override
    public void saveOption() {
    }

    @Override
    public boolean loadOption() {
        return true;
    }

    @Override
    public long getGlobalTimerInterval() {
        return -1;
    }

    @Override
    public void setGlobalTimerInterval(long globalTimerInterval) {
    }

    @Override
    public String getTargetMsg(PlayerData playerData) {
        return playerData.getCurrentTargetDisplay();
    }

    @Override
    public String getWaitTargetMsg(PlayerData playerData) {
        return ChatColor.LIGHT_PURPLE + LangManager.getMsgLang("SOMETHING_NEW", playerData.getConfigLang());
    }

    @Override
    public String getSkipStr(PlayerData playerData) {
        return LangManager.getMsgLang("ITEM_SKIP", playerData.getConfigLang()).replace("[#]", "[" + playerData.getCurrentTarget() + "]");
    }

    @Override
    public void targetSkipped(PlayerData playerData) {
    }
}
