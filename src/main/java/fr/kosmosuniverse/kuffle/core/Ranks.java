package fr.kosmosuniverse.kuffle.core;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author KosmosUniverse
 */
@Getter
public class Ranks {
    private Map<String, Integer> playerRanks;
    private Map<String, Integer> teamRanks;
    private int nextGoodPlayerRank;
    private int nextBadPlayerRank;
    private int nextGoodTeamRank;
    private int nextBadTeamRank;

    /**
     * Constructor
     */
    public Ranks() {
        playerRanks = new HashMap<>();
        teamRanks = new HashMap<>();
    }

    /**
     * Initialize ranks
     */
    public void init() {
        nextGoodPlayerRank = 1;
        nextBadPlayerRank = Config.getTeam() ? TeamManager.getInstance().getTeams().size() : Party.getInstance().getPlayers().getList().size();

        Party.getInstance().getPlayers().getList().forEach(name -> playerRanks.put(name, -1));

        if (Config.getTeam()) {
            TeamManager.getInstance().getTeams().forEach(team -> teamRanks.put(team.getName(), -1));
        }
    }

    /**
     * Set the next good rank for specified name
     *
     * @param name The name to give rank
     */
    public void finishRank(String name) {
        if (playerRanks.get(name) == -1) {
            playerRanks.put(name, nextGoodPlayerRank);
            nextGoodPlayerRank++;
        }

        if (Config.getTeam()) {
            String teamName = TeamManager.getInstance().getTeamByPlayer(name).getName();
            int teamRank = teamRanks.get(teamName);

            if (teamRank == -1) {
                teamRanks.put(teamName, nextGoodTeamRank);
                nextGoodTeamRank++;
            }
        }
    }

    /**
     * Set the next bad rank for specified name
     *
     * @param name The name to give rank
     */
    public void abandonRank(String name) {
        if (playerRanks.get(name) == -1) {
            playerRanks.put(name, nextBadPlayerRank);
            nextBadPlayerRank--;
        }

        if (Config.getTeam()) {
            String teamName = TeamManager.getInstance().getTeamByPlayer(name).getName();
            int teamRank = teamRanks.get(teamName);

            if (teamRank == -1) {
                teamRanks.put(teamName, nextBadTeamRank);
                nextBadTeamRank--;
            }
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
        return playerRanks.getOrDefault(name, -1);
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
            return teamRanks.get(TeamManager.getInstance().getTeamByPlayer(name).getName());
        } else {
            return playerRanks.get(name);
        }
    }

    /**
     * Clears the ranks
     */
    public void clear() {
        playerRanks.clear();
    }

    public Map<String, Integer> getNextRanks() {
        Map<String, Integer> nextRanks = new HashMap<>();

        nextRanks.put("NGPR", nextGoodPlayerRank);
        nextRanks.put("NBPR", nextBadPlayerRank);
        nextRanks.put("NGTR", nextGoodTeamRank);
        nextRanks.put("NBTR", nextBadTeamRank);

        return nextRanks;
    }

    public void loadRanks(Map<String, Integer> playerRanks, Map<String, Integer> teamRanks, Map<String, Integer> nextRanks) {
        this.playerRanks = playerRanks;
        this.teamRanks = teamRanks;

        nextGoodPlayerRank = nextRanks.get("NGPR");
        nextBadPlayerRank = nextRanks.get("NBPR");
        nextGoodTeamRank = nextRanks.get("NGTR");
        nextBadTeamRank = nextRanks.get("NBTR");
    }
}
