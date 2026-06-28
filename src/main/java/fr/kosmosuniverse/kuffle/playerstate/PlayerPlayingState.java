package fr.kosmosuniverse.kuffle.playerstate;

import fr.kosmosuniverse.kuffle.core.LogManager;
import fr.kosmosuniverse.kuffle.core.PartyTmp;
import fr.kosmosuniverse.kuffle.datamanagers.LangManager;
import fr.kosmosuniverse.kuffle.datamanagers.age.AgeManager;
import fr.kosmosuniverse.kuffle.core.Config;
import fr.kosmosuniverse.kuffle.core.PlayerData;
import fr.kosmosuniverse.kuffle.datamanagers.reward.RewardManager;
import fr.kosmosuniverse.kuffle.event.PlayerFinishEvent;
import fr.kosmosuniverse.kuffle.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

/**
 * @author KosmosUniverse
 */
public class PlayerPlayingState extends PlayerStateDecorator {
    public PlayerPlayingState(PlayerGameState playerState) {
        super(playerState);
    }

    @Override
    public State getRawState() {
        return State.PLAYING;
    }

    @Override
    public boolean isPlaying() {
        return true;
    }

    @Override
    public boolean targetFound(PlayerData playerData) {
        playerData.incrementTarget();
        Bukkit.getPlayer(playerData.getPlayerName()).playSound(Bukkit.getPlayer(playerData.getPlayerName()).getLocation(), Sound.BLOCK_BELL_USE, 1f, 1f);
        playerData.getScore().setScore(playerData.getTargetCount());

        return true;
    }

    @Override
    public boolean sbttFound(PlayerData playerData) {
        boolean ret;

        ret = targetFound(playerData);

        if (ret) {
            playerData.incrementSbtt();
        }

        return ret;
    }

    @Override
    public void updateBossBar(PlayerData playerData) {
        playerData.getAgeDisplay().setTitle(getBossBarStr(playerData));
        playerData.getAgeDisplay().setProgress(getBossBarProgress(playerData));
        playerData.getAgeDisplay().setColor(getBossBarColor(playerData));
    }

    @Override
    public String getBossBarStr(PlayerData playerData) {
        return playerData.getAge().getName().replace("_", " ") + ": " + playerData.getTargetCount();
    }

    @Override
    public double getBossBarProgress(PlayerData playerData) {
        double calc = ((double) playerData.getTargetCount()) / Config.getTargetPerAge();

        return Math.min(calc, 1.0);
    }

    @Override
    public String getActionBarStr(PlayerData playerData) {
        String withoutTimer = PartyTmp.getInstance().getOptions().getTargetMsg(playerData);

        long timer = getTime(playerData) * 60000L;
        timer -= (System.currentTimeMillis() - playerData.getTimeTarget());
        timer /= 1000;

        ChatColor color = getTimerColor(timer);

        return color + LangManager.getMsgLang("COUNTDOWN", playerData.getConfigLang()).replace("%i", String.valueOf(timer)).replace("%s", withoutTimer);
    }

    private int getTime(PlayerData playerData) {
        return Config.getStartTime() + (Config.getAddedTime() * playerData.getAge().getNumber());
    }

    private ChatColor getTimerColor(long timer) {
        ChatColor color;

        if (timer < 30) {
            color = ChatColor.RED;
        } else if (timer < 60) {
            color = ChatColor.YELLOW;
        } else {
            color = ChatColor.GREEN;
        }

        return color;
    }

    @Override
    public void nextAge(PlayerData playerData) {
        Bukkit.getPlayer(playerData.getPlayerName()).sendMessage(LangManager.getMsgLang("TIME_AGE", playerData.getConfigLang()).replace("%t", Utils.getTimeFromSec((System.currentTimeMillis() - playerData.getTimeStartAge()) / 1000)));
        LogManager.getInstanceGame().logSystemMsg(LangManager.getMsgLang("AGE_VALIDATED", "en").replace("[#]", "[" + playerData.getAge().getName() + "]").replace("<#>", "<" + playerData.getPlayerName() + ">"));

        if (Config.getRewards()) {
            RewardManager.removePreviousRewardEffects(playerData.getAge().getName(), Bukkit.getPlayer(playerData.getPlayerName()));
        }

        playerData.clearAlreadyGot();

        if (playerData.getAge() == Config.getLastAge()) {
            Bukkit.getPluginManager().callEvent(new PlayerFinishEvent(Bukkit.getPlayer(playerData.getPlayerName())));
            return;
        }

        if (Config.getRewards()) {
            RewardManager.givePlayerReward(playerData.getAge().getName(), Bukkit.getPlayer(playerData.getPlayerName()));
        }

        playerData.setTimeStartAge(System.currentTimeMillis());
        playerData.setTargetCount(1);
        playerData.getScore().setScore(playerData.getTargetCount());
        playerData.setSameIdx(0);
        playerData.setAge(AgeManager.getAgeByNumber(playerData.getAge().getNumber() + 1));

        PartyTmp.getInstance().updatePlayerListName(playerData.getPlayerName());

        if (playerData.isTips()) {
            PartyTmp.getInstance().getGameManager().sendTips(playerData);
        }
    }
}
