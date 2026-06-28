package fr.kosmosuniverse.kuffle.ranks;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author KosmosUniverse
 */
public class RankBoard implements Serializable {
    private final Map<String, Integer> ranks = new HashMap<>();
    private int nextTop;
    private int nextBottom;

    public void init(List<String> names) {
        ranks.clear();

        names.forEach(name -> ranks.put(name, -1));

        nextTop = 1;
        nextBottom = names.size();
    }

    public void clear() {
        ranks.clear();
    }

    public void finish(String name) {
        if (ranks.containsKey(name) && ranks.get(name) == -1) {
            ranks.put(name, nextTop++);
        }
    }

    public void abandon(String name) {
        if (ranks.containsKey(name) && ranks.get(name) == -1) {
            ranks.put(name, nextBottom--);
        }
    }

    public int getRank(String name) {
        return ranks.getOrDefault(name, -1);
    }
}
