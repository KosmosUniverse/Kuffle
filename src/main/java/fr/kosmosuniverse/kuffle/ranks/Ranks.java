package fr.kosmosuniverse.kuffle.ranks;

import fr.kosmosuniverse.kuffle.core.Config;
import fr.kosmosuniverse.kuffle.core.PartyTmp;
import fr.kosmosuniverse.kuffle.core.Team;
import fr.kosmosuniverse.kuffle.core.TeamManager;
import lombok.Getter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.stream.Collectors;

/**
 * @author KosmosUniverse
 */
@Getter
public class Ranks implements Serializable {
    private RankBoard playerBoard = new RankBoard();
    private RankBoard teamBoard;

    /**
     * Initialize ranks
     */
    public void initPlayersRanks() {
        playerBoard.init(PartyTmp.getInstance().getPlayers().getList());
    }

    public void initTeamsRanks() {
        teamBoard = new RankBoard();
        teamBoard.init(TeamManager.getInstance().getTeams().stream().map(Team::getName).collect(Collectors.toList()));
    }

    /**
     * Set the next good rank for specified name
     *
     * @param name The name to give rank
     */
    public void finishRank(String name) {
        playerBoard.finish(name);

        if (Config.getTeam()) {
            String teamName = TeamManager.getInstance().getTeamByPlayer(name).getName();

            teamBoard.finish(teamName);
        }
    }

    /**
     * Set the next bad rank for specified name
     *
     * @param name The name to give rank
     */
    public void abandonRank(String name) {
        playerBoard.abandon(name);

        if (Config.getTeam()) {
            String teamName = TeamManager.getInstance().getTeamByPlayer(name).getName();

            teamBoard.abandon(teamName);
        }
    }

    /**
     * Gets name rank
     *
     * @param name The name to get
     *
     * @return The rank link to that name or -1 if name does not exist
     */
    public int getRank(String name) {
        return playerBoard.getRank(name);
    }

    /**
     * Get Game rank
     *
     * @param name playerName for whom to get rank
     *
     * @return the rank of player or its team
     */
    public int getGameRank(String name) {
        if (Config.getTeam()) {
            return teamBoard.getRank(TeamManager.getInstance().getTeamByPlayer(name).getName());
        } else {
            return playerBoard.getRank(name);
        }
    }

    /**
     * Clears the ranks
     */
    public void clear() {
        playerBoard.clear();

        if (Config.getTeam()) {
            teamBoard.clear();
        }
    }

    private void writeObject(ObjectOutputStream oStream) throws IOException {
        oStream.writeObject(playerBoard);
        oStream.writeBoolean(Config.getTeam());

        if (Config.getTeam()) {
            oStream.writeObject(teamBoard);
        }
    }

    private void readObject(ObjectInputStream iStream) throws ClassNotFoundException, IOException {
        playerBoard = (RankBoard) iStream.readObject();
        boolean isTeam = iStream.readBoolean();

        if (isTeam) {
            teamBoard = (RankBoard) iStream.readObject();
        }
    }
}
