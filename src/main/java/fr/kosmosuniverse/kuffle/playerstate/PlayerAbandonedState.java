package fr.kosmosuniverse.kuffle.playerstate;

import fr.kosmosuniverse.kuffle.core.Config;
import fr.kosmosuniverse.kuffle.core.PartyTmp;
import fr.kosmosuniverse.kuffle.core.PlayerData;
import fr.kosmosuniverse.kuffle.datamanagers.LangManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;

import java.security.SecureRandom;
import java.util.Objects;

/**
 * @author KosmosUniverse
 */
public class PlayerAbandonedState extends PlayerStateDecorator {
    public PlayerAbandonedState(PlayerData playerData, PlayerGameState playerState) {
        super(playerState);

        //Set abandon rank
        PartyTmp.getInstance().getGameManager().getRanks().abandonRank(playerData.getPlayerName());

        //Add Finished state
        playerData.setState(playerData, State.FINISHED);

        playerData.getAgeDisplay().setProgress(0.0f);

        //Update player heads
        /*PartyTmp.getInstance().getPlayers()
                .updatePlayersHeads(Games.getInstance().getGames().entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> (e.getValue().getCurrentTarget() != null ? e.getValue().getCurrentTarget() : "null"))));*/

        //Inform Player and Specs
        PartyTmp.getInstance().getGameManager().sendMsgToAll((receiverLang) -> LangManager.getMsgLang("GAME_ABANDONED", receiverLang).replace("<#>", ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + playerData.getPlayerName() + ChatColor.BLUE));
        PartyTmp.getInstance().getSpectators().getList().forEach(specName -> Objects.requireNonNull(Bukkit.getPlayer(specName)).sendMessage(LangManager.getMsgLang("GAME_ABANDONED", Config.getLang()).replace("<#>", ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + playerData.getPlayerName() + ChatColor.BLUE)));
    }

    @Override
    public State getRawState() {
        return State.ABANDONED;
    }

    @Override
    public boolean isAbandoned() {
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
        return 0.0d;
    }
}
