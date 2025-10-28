package fr.kosmosuniverse.kuffle.core;

import fr.kosmosuniverse.kuffle.utils.SerializeUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author KosmosUniverse
 */
@Getter
public class PlayerData implements Serializable {
    @Setter
    private boolean finished;
    @Setter
    private boolean lose;
    @Setter
    private boolean dead;
    @Setter
    private boolean discovered;
    @Setter
    private boolean tips;
    @Setter
    private int targetCount = 1;
    @Setter
    private int age = 0;
    @Setter
    private int deathCount = 0;
    @Setter
    private int skipCount = 0;
    @Setter
    private int sbttCount = 0;
    @Setter
    private int sameIdx = 0;
    @Setter
    private String currentTarget = null;
    @Setter
    private String currentTargetDisplay = null;
    @Setter
    private String configLang;
    @Setter
    private Location spawnLoc = null;
    @Setter
    private Location deathLoc = null;
    @Setter
    private long timeStartAge;
    @Setter
    private long totalTime = 0;
    @Setter
    private long timeTarget = 0;
    @Setter
    private long interval = -1;
    private List<String> alreadyGot;
    private Map<String, Long> ageTimes;
    private List<ItemStack> deathInv = null;
    private Score score = null;
    private BossBar ageDisplay = null;

    public PlayerData() {
        tips = Config.hasTips();
        configLang = Config.getLang();
        alreadyGot = new ArrayList<>();
        ageTimes = new HashMap<>();
    }

    public void setup(Player player) {
        ageDisplay = Bukkit.createBossBar(LangManager.getMsgLang("START", configLang), BarColor.PURPLE, BarStyle.SOLID);
        ageDisplay.addPlayer(player);
        timeStartAge = System.currentTimeMillis();
    }

    /**
     * Add target to all target already validated by player
     *
     * @param target to add to the list
     */
    public void addAlreadyGot(String target) {
        alreadyGot.add(target);
    }

    /**
     * @param target	The target to remove
     */
    public void removeAlreadyGot(String target) {
        alreadyGot.remove(target);
    }

    /**
     * Setup scores for a specific player
     *
     * @param scoreboard	The scoreboard to apply to the player
     * @param score			The score to apply
     */
    public void setupScores(Player player, Scoreboard scoreboard, Score score) {
        this.score = score;
        this.score.setScore(1);
        player.setScoreboard(scoreboard);
    }

    public void incrementTarget() {
        targetCount++;
    }

    public void incrementSbtt() {
        sbttCount++;
    }

    public void clear() {
        clearAgeTime();
        clearAlreadyGot();
        clearDeathInv();
    }

    /**
     * Clear all already validated targets
     */
    public void clearAlreadyGot() {
        alreadyGot.clear();
    }

    /**
     * Clear all age times
     */
    public void clearAgeTime() {
        ageTimes.clear();
    }

    public void saveDeathInv(List<ItemStack> deathContent) {
        if (deathInv == null) {
            deathInv = new ArrayList<>();
        }

        deathInv.addAll(deathContent);
    }

    /**
     * Clear death inventory
     */
    public void clearDeathInv() {
        if (deathInv != null) {
            deathInv.clear();
            deathInv = null;
        }
    }

    /**
     * Defines what will be stored in the player save file
     *
     * @param oStream	The ObjectOutputStream
     *
     * @throws IOException Exception if write does not work
     */
    private void writeObject(ObjectOutputStream oStream) throws IOException {
        oStream.writeBoolean(finished);
        oStream.writeBoolean(lose);
        oStream.writeBoolean(dead);
        oStream.writeBoolean(discovered);
        oStream.writeBoolean(tips);

        oStream.writeInt(targetCount);
        oStream.writeInt(age);
        oStream.writeInt(deathCount);
        oStream.writeInt(skipCount);
        oStream.writeInt(sbttCount);
        oStream.writeInt(sameIdx);

        oStream.writeLong(totalTime);
        oStream.writeLong(timeStartAge);
        oStream.writeLong(timeTarget);
        oStream.writeLong(interval);

        oStream.writeUTF(currentTarget);
        oStream.writeUTF(currentTargetDisplay);
        oStream.writeUTF(configLang);

        oStream.writeObject(spawnLoc.serialize());

        if (dead) {
            oStream.writeObject(deathLoc.serialize());
            oStream.writeInt(deathInv.size());

            for (ItemStack item : deathInv) {
                oStream.writeObject(item.serialize());
            }
        }

        if (!finished) {
            ageTimes.put("INTERVAL_TIME", System.currentTimeMillis() - timeStartAge);
        }

        oStream.writeObject(ageTimes);
        oStream.writeObject(alreadyGot);
    }

    /**
     * Read Game info from input stream
     *
     * @param iStream	The stream that contains all Game info
     *
     * @throws ClassNotFoundException In case Cast is not working
     * @throws IOException  Classic read input stream exception
     */
    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream iStream) throws ClassNotFoundException, IOException {
        finished = iStream.readBoolean();
        lose = iStream.readBoolean();
        dead = iStream.readBoolean();
        discovered = iStream.readBoolean();
        tips = iStream.readBoolean();

        targetCount = iStream.readInt();
        age = iStream.readInt();
        deathCount = iStream.readInt();
        skipCount = iStream.readInt();
        sbttCount = iStream.readInt();
        sameIdx = iStream.readInt();

        totalTime = iStream.readLong();
        timeStartAge = iStream.readLong();
        timeTarget = iStream.readLong();
        interval = iStream.readLong();

        currentTarget = SerializeUtils.readString(iStream);
        currentTargetDisplay = SerializeUtils.readString(iStream);
        configLang = SerializeUtils.readString(iStream);

        spawnLoc = Location.deserialize((Map<String, Object>) iStream.readObject());

        if (dead) {
            deathLoc = Location.deserialize((Map<String, Object>) iStream.readObject());
            int size = iStream.readInt();
            deathInv = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                deathInv.add(ItemStack.deserialize((Map<String, Object>) iStream.readObject()));
            }
        }

        ageTimes = (Map<String, Long>) iStream.readObject();

        if (!finished) {
            timeStartAge = System.currentTimeMillis() - ageTimes.get("INTERVAL_TIME");
            ageTimes.remove("INTERVAL_TIME");
        }

        alreadyGot = (List<String>) iStream.readObject();
    }
}
