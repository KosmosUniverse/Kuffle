package fr.kosmosuniverse.kuffle.core;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author KosmosUniverse
 */
@Getter
public class ResultData implements Serializable {
    @Setter
    private boolean isTeam;
    @Setter
    private boolean isSkip;
    @Setter
    private boolean isSbtt;
    @Setter
    private int lastAge;

    private Map<String, List<String>> teams = new HashMap<>();
    private Map<String, Map<String, Long>> teamTimes = new HashMap<>();
    private Map<String, Integer> teamDeath = new HashMap<>();
    private Map<String, Integer> teamSkip = new HashMap<>();
    private Map<String, Integer> teamSbtt = new HashMap<>();
    private Map<String, Map<String, Long>> playersTimes = new HashMap<>();
    private Map<String, Integer> playersDeath = new HashMap<>();
    private Map<String, Integer> playersSkip = new HashMap<>();
    private Map<String, Integer> playersSbtt = new HashMap<>();

    public void addTeam(String teamName, List<String> players) {
        teams.put(teamName, players);
    }
    public void addTeamTimes(String teamName, Map<String, Long> TeamTimes) {
        teamTimes.put(teamName, TeamTimes);
    }
    public void addTeamDeath(String teamName, int deathCnt) {
        teamDeath.put(teamName, deathCnt);
    }
    public void addTeamSkip(String teamName, int skipCnt) {
        teamSkip.put(teamName, skipCnt);
    }
    public void addTeamSbtt(String teamName, int sbttCnt) {
        teamSbtt.put(teamName, sbttCnt);
    }
    public void addPlayerTimes(String playerName, Map<String, Long> playerTimes) {
        playersTimes.put(playerName, playerTimes);
    }

    public void addPlayerDeath(String playerName, int deathCnt) {
        playersDeath.put(playerName, deathCnt);
    }
    public void addPlayerSkip(String playerName, int skipCnt) {
        playersSkip.put(playerName, skipCnt);
    }

    public void addPlayerSbtt(String playerName, int sbttCnt) {
        playersSbtt.put(playerName, sbttCnt);
    }

    private void writeObject(ObjectOutputStream oStream) throws IOException {
        oStream.writeBoolean(isTeam);
        oStream.writeBoolean(isSkip);
        oStream.writeBoolean(isSbtt);
        oStream.writeInt(lastAge);

        if (isTeam) {
            oStream.writeObject(teams);
            oStream.writeObject(teamTimes);
            oStream.writeObject(teamDeath);

            if (isSkip) {
                oStream.writeObject(teamSkip);
            }

            if (isSbtt) {
                oStream.writeObject(teamSbtt);
            }
        }

        oStream.writeObject(playersTimes);
        oStream.writeObject(playersDeath);

        if (isSkip) {
            oStream.writeObject(playersSkip);
        }

        if (isSbtt) {
            oStream.writeObject(playersSbtt);
        }
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream iStream) throws IOException, ClassNotFoundException {
        isTeam = iStream.readBoolean();
        isSkip = iStream.readBoolean();
        isSbtt = iStream.readBoolean();
        lastAge = iStream.readInt();

        if (isTeam) {
            teams = (Map<String, List<String>>) iStream.readObject();
            teamTimes = (Map<String, Map<String, Long>>) iStream.readObject();
            teamDeath = (Map<String, Integer>) iStream.readObject();

            if (isSkip) {
                teamSkip = (Map<String, Integer>) iStream.readObject();
            }

            if (isSbtt) {
                teamSbtt = (Map<String, Integer>) iStream.readObject();
            }
        }

        playersTimes = (Map<String, Map<String, Long>>) iStream.readObject();
        playersDeath = (Map<String, Integer>) iStream.readObject();

        if (isSkip) {
            playersSkip = (Map<String, Integer>) iStream.readObject();
        }

        if (isSbtt) {
            playersSbtt = (Map<String, Integer>) iStream.readObject();
        }
    }

    public void printResult() {
        StringBuilder sb = new StringBuilder();

        sb.append("Results:\n");
        sb.append("\t- isTeam : [").append(isTeam).append("]\n");
        sb.append("\t- isSkip : [").append(isSkip).append("]\n");
        sb.append("\t- isSbtt : [").append(isSbtt).append("]\n");
        sb.append("\t- LastAge : [").append(lastAge).append("]\n");

        if (isTeam) {
            sb.append("\t- Teams :\n");
            sb.append("\t\t- Teams size : [").append(teams.size()).append("]\n");
            sb.append("\t\t- Teams Death : [").append(teamDeath).append("]\n");

            if (isSkip) {
                sb.append("\t\t- Teams Skip : [").append(teamSkip).append("]\n");
            }

            if (isSbtt) {
                sb.append("\t\t- Teams Sbtt : [").append(teamSbtt).append("]\n");
            }

            sb.append("\t\t- Teams Times : \n");

            teamTimes.forEach((teamName, times) -> {
                sb.append("\t\t\t - [").append(teamName).append("]\n");

                times.forEach((ageName, time) ->
                    sb.append("\t\t\t\t - [").append(ageName).append("] : [").append(time).append("]\n"));
            });
        }

        sb.append("\t- Players :\n");
        sb.append("\t\t- Players size : [").append(playersTimes.size()).append("]\n");
        sb.append("\t\t- Players Death : [").append(playersDeath).append("]\n");

        if (isSkip) {
            sb.append("\t\t- Players Skip : [").append(playersSkip).append("]\n");
        }

        if (isSbtt) {
            sb.append("\t\t- Players Sbtt : [").append(playersSbtt).append("]\n");
        }

        sb.append("\t\t- Players Times : \n");

        playersTimes.forEach((playerName, times) -> {
            sb.append("\t\t\t - [").append(playerName).append("]\n");

            times.forEach((ageName, time) ->
                sb.append("\t\t\t\t - [").append(ageName).append("] : [").append(time).append("]\n"));
        });

        LogManager.getInstanceSystem().logSystemMsg(sb.toString());
    }
}
