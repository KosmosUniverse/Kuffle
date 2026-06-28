package fr.kosmosuniverse.kuffle.datamanagers.results;

import lombok.Getter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author KosmosUniverse
 */
@Getter
public class ResultSingleData implements Serializable {
    private boolean isSkip;
    private boolean isSbtt;

    private Map<String, Map<String, Long>> times = new HashMap<>();
    private Map<String, Integer> death = new HashMap<>();
    private Map<String, Integer> skip = new HashMap<>();
    private Map<String, Integer> sbtt = new HashMap<>();

    public ResultSingleData(boolean isSkip, boolean isSbtt) {
        this.isSkip = isSkip;
        this.isSbtt = isSbtt;
    }

    public void addTimes(String name, Map<String, Long> timesCnt) {
        times.put(name, timesCnt);
    }
    public void addDeath(String name, int deathCnt) {
        death.put(name, deathCnt);
    }
    public void addSkip(String name, int skipCnt) {
        skip.put(name, skipCnt);
    }
    public void addSbtt(String name, int sbttCnt) {
        sbtt.put(name, sbttCnt);
    }

    private void writeObject(ObjectOutputStream oStream) throws IOException {
        oStream.writeBoolean(isSkip);
        oStream.writeBoolean(isSbtt);

        oStream.writeObject(times);
        oStream.writeObject(death);

        if (isSkip) {
            oStream.writeObject(skip);
        }

        if (isSbtt) {
            oStream.writeObject(sbtt);
        }
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream iStream) throws IOException, ClassNotFoundException {
        isSkip = iStream.readBoolean();
        isSbtt = iStream.readBoolean();

        times = (Map<String, Map<String, Long>>) iStream.readObject();
        death = (Map<String, Integer>) iStream.readObject();

        if (isSkip) {
            skip = (Map<String, Integer>) iStream.readObject();
        }

        if (isSbtt) {
            sbtt = (Map<String, Integer>) iStream.readObject();
        }
    }
}
