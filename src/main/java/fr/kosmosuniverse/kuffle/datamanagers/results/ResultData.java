package fr.kosmosuniverse.kuffle.datamanagers.results;

import fr.kosmosuniverse.kuffle.core.LogManager;
import lombok.Getter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author KosmosUniverse
 */
@Getter
public class ResultData implements Serializable {
    private boolean isTeam;
    private boolean isSkip;
    private boolean isSbtt;
    private int lastAge;
    private ResultSingleData playersData;
    private ResultSingleData teamsData;

    public ResultData(boolean isTeam, boolean isSkip, boolean isSbtt, int lastAge) {
        this.isTeam = isTeam;
        this.isSkip = isSkip;
        this.isSbtt = isSbtt;
        this.lastAge = lastAge;

        playersData = new ResultSingleData(isSkip, isSbtt);
        teamsData = new ResultSingleData(isSkip, isSbtt);
    }

    private void writeObject(ObjectOutputStream oStream) throws IOException {
        oStream.writeBoolean(isTeam);
        oStream.writeBoolean(isSkip);
        oStream.writeBoolean(isSbtt);
        oStream.writeInt(lastAge);

        oStream.writeObject(playersData);

        if (isTeam) {
            oStream.writeObject(teamsData);
        }
    }

    private void readObject(ObjectInputStream iStream) throws IOException, ClassNotFoundException {
        isTeam = iStream.readBoolean();
        isSkip = iStream.readBoolean();
        isSbtt = iStream.readBoolean();
        lastAge = iStream.readInt();

        playersData = (ResultSingleData) iStream.readObject();

        if (isTeam) {
            teamsData = (ResultSingleData) iStream.readObject();
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
            sb.append("\t\t- Teams size : [").append(teamsData.getTimes().size()).append("]\n");
            sb.append("\t\t- Teams Death : [").append(teamsData.getDeath()).append("]\n");

            if (isSkip) {
                sb.append("\t\t- Teams Skip : [").append(teamsData.getSkip()).append("]\n");
            }

            if (isSbtt) {
                sb.append("\t\t- Teams Sbtt : [").append(teamsData.getSbtt()).append("]\n");
            }

            sb.append("\t\t- Teams Times : \n");

            teamsData.getTimes().forEach((teamName, times) -> {
                sb.append("\t\t\t - [").append(teamName).append("]\n");

                times.forEach((ageName, time) ->
                        sb.append("\t\t\t\t - [").append(ageName).append("] : [").append(time).append("]\n"));
            });
        }

        sb.append("\t- Players :\n");
        sb.append("\t\t- Players size : [").append(playersData.getTimes().size()).append("]\n");
        sb.append("\t\t- Players Death : [").append(playersData.getDeath()).append("]\n");

        if (isSkip) {
            sb.append("\t\t- Players Skip : [").append(playersData.getSkip()).append("]\n");
        }

        if (isSbtt) {
            sb.append("\t\t- Players Sbtt : [").append(playersData.getSbtt()).append("]\n");
        }

        sb.append("\t\t- Players Times : \n");

        playersData.getTimes().forEach((playerName, times) -> {
            sb.append("\t\t\t - [").append(playerName).append("]\n");

            times.forEach((ageName, time) ->
                    sb.append("\t\t\t\t - [").append(ageName).append("] : [").append(time).append("]\n"));
        });

        LogManager.getInstanceSystem().logSystemMsg(sb.toString());
    }
}
