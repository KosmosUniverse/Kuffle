package fr.kosmosuniverse.kuffle.playerstate;

import fr.kosmosuniverse.kuffle.core.Config;
import fr.kosmosuniverse.kuffle.core.LogManager;
import fr.kosmosuniverse.kuffle.core.PartyTmp;
import fr.kosmosuniverse.kuffle.core.PlayerData;
import fr.kosmosuniverse.kuffle.datamanagers.LangManager;
import fr.kosmosuniverse.kuffle.datamanagers.age.AgeManager;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.potion.PotionEffect;

import java.security.SecureRandom;
import java.util.Objects;

/**
 * @author KosmosUniverse
 */
public class PlayerFinishedState extends PlayerStateDecorator {
    public PlayerFinishedState(PlayerData playerData, PlayerGameState playerState) {
        super(playerState);

        //Set finished rank
        PartyTmp.getInstance().getGameManager().getRanks().finishRank(playerData.getPlayerName());

        //Define Age Display with rank
        /*playerData
                .getAgeDisplay()
                .setTitle(LangManager
                        .getMsgLang("GAME_DONE", playerData.getConfigLang())
                        .replace("%i", String.valueOf(Party.getInstance().getRanks().getGameRank(playerData.getPlayerName()))));*/

        //Remove remaining potion effects
        for (PotionEffect pe : Objects.requireNonNull(Bukkit.getPlayer(playerData.getPlayerName())).getActivePotionEffects()) {
            Objects.requireNonNull(Bukkit.getPlayer(playerData.getPlayerName())).removePotionEffect(pe.getType());
        }

        //Reset Age index
        playerData.setAge(AgeManager.getDefaultAge());

        //Update player tab list format
        PartyTmp.getInstance().updatePlayerListName(playerData.getPlayerName());

        //Print end tab if configured
        if (Config.getPrintTab()) {
            //Objects.requireNonNull(Bukkit.getPlayer(playerData.getPlayerName())).sendMessage(PartyTmp.getInstance().playerString(playerData.getPlayerName(), playerData.getConfigLang()));
            LogManager.getInstanceGame().logSystemMsg(PartyTmp.getInstance().getGameManager().logString(playerData));
        }

        if (isAbandoned()) {
            return;
        }

        playerData.getAgeDisplay().setProgress(1.0f);
    }

    @Override
    public State getRawState() {
        return State.FINISHED;
    }

    @Override
    public boolean isFinished() {
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
    public void updateBossBar(PlayerData playerData) {
        playerData.getAgeDisplay().setColor(getBossBarColor(playerData));
    }

    @Override
    public String getBossBarStr(PlayerData playerData) {
        return LangManager.getMsgLang("GAME_DONE", playerData.getConfigLang()).replace("%i", String.valueOf(PartyTmp.getInstance().getGameManager().getRanks().getRank(playerData.getPlayerName())));
    }

    @Override
    public BarColor getBossBarColor(PlayerData playerData) {
        BarColor[] colors = BarColor.values();
        SecureRandom random = new SecureRandom();

        return colors[random.nextInt(colors.length)];
    }

    @Override
    public double getBossBarProgress(PlayerData playerData) {
        return 1.0d;
    }
}
