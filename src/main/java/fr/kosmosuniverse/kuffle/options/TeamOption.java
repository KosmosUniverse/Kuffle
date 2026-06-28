package fr.kosmosuniverse.kuffle.options;

import fr.kosmosuniverse.kuffle.core.*;
import fr.kosmosuniverse.kuffle.datamanagers.LangManager;
import fr.kosmosuniverse.kuffle.playerstate.State;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author KosmosUniverse
 */
public class TeamOption extends OptionDecorator {
    protected TeamOption(GameOption option) {
        super(option);
    }

    @Override
    public boolean launchChecks(Player player) {
        boolean ret = super.launchChecks(player);

        if (ret && !TeamManager.getInstance().checkPlayerInTeams()) {
            LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("PLAYER_NOT_IN_TEAM", Config.getLang()));
            ret = false;
        }

        return ret;
    }

    @Override
    public void initOption() {
        super.initOption();

        PartyTmp.getInstance().getGameManager().getRanks().initTeamsRanks();

        if (Config.getTeamInv()) {
            TeamManager.getInstance().setupTeamsInv();
        }
    }

    @Override
    public void nextAge(PlayerData playerData) {
        if (PartyTmp.getInstance().getGameManager().checkTeamMates(playerData)) {
            PartyTmp.getInstance().getGameManager().teamNextAge(TeamManager.getInstance().getTeamByPlayer(playerData.getPlayerName()).getName());
        } else {
            playerData.setState(State.WAITING);
        }
    }

    @Override
    public void updatePlayerListName(PlayerData playerData) {
        Player player = Bukkit.getPlayer(playerData.getPlayerName());

        if (player == null) {
            return;
        }

        Team team = TeamManager.getInstance().getTeamByPlayer(playerData.getPlayerName());

        player.setPlayerListName("[" + team.getColor() + team.getName() + ChatColor.RESET + "] - " + playerData.getAge().getColor() + playerData.getPlayerName());
    }

    @Override
    public long getNbPlayerStillPlaying() {
        return TeamManager.getInstance().getNbTeamsStillPlaying();
    }

    @Override
    public void saveOption() {
        super.saveOption();

        TeamManager.getInstance().saveTeams();
    }

    @Override
    public boolean loadOption() {
        super.saveOption();

        return TeamManager.getInstance().loadTeams();
    }

    @Override
    public String getWaitTargetMsg(PlayerData playerData) {
        return ChatColor.LIGHT_PURPLE + LangManager.getMsgLang("TEAM_WAIT", playerData.getConfigLang());
    }
}
