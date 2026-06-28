package fr.kosmosuniverse.kuffle.core;

import fr.kosmosuniverse.kuffle.datamanagers.LangManager;
import fr.kosmosuniverse.kuffle.datamanagers.age.Age;
import fr.kosmosuniverse.kuffle.datamanagers.age.AgeManager;
import fr.kosmosuniverse.kuffle.event.NewTargetEvent;
import fr.kosmosuniverse.kuffle.playerstate.*;
import fr.kosmosuniverse.kuffle.utils.SerializeUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author KosmosUniverse
 */
@Getter
public class PlayerData implements Serializable {
    @Setter
    private boolean discovered;
    @Setter
    private boolean tips;
    @Setter
    private int targetCount = 1;
    @Setter
    private int deathCount = 0;
    @Setter
    private int skipCount = 0;
    @Setter
    private int sbttCount = 0;
    @Setter
    private int sameIdx = 0;
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
    private long timeTarget = 0;
    @Setter
    private long interval = -1;
    private List<String> alreadyGot;
    private Map<String, Long> ageTimes;
    private List<ItemStack> deathInv = null;
    private Score score = null;
    private BossBar ageDisplay = null;
    private final String playerName;
    private PlayerGameState state;
    @Setter
    private Age age;

    public PlayerData(String playerName) {
        tips = Config.hasTips();
        configLang = Config.getLang();
        alreadyGot = new ArrayList<>();
        ageTimes = new HashMap<>();
        state = new PlayerNotPlayingState();
        this.playerName = playerName;
    }

    public void setup() {
        ageDisplay = Bukkit.createBossBar(LangManager.getMsgLang("START", configLang), BarColor.PURPLE, BarStyle.SOLID);
        ageDisplay.addPlayer(Bukkit.getPlayer(playerName));
        timeStartAge = System.currentTimeMillis();
        age = AgeManager.getFirstAge();
    }

    public void setState(State newState) {
        state = GameStateFactory.create(this, newState, state);
    }

    public void setState(PlayerData playerData, State newState) {
        state = GameStateFactory.create(playerData, newState, state);
    }

    public void revertState() {
        state = state.getState();
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

    public void setCurrentTarget(String target, String targetDisplay) {
        currentTarget = target;
        currentTargetDisplay = targetDisplay;
    }

    /**
     * Setup scores for a specific player
     *
     * @param scoreboard	The scoreboard to apply to the player
     * @param score			The score to apply
     */
    public void setupScores(Scoreboard scoreboard, Score score) {
        this.score = score;
        this.score.setScore(1);
        Bukkit.getPlayer(playerName).setScoreboard(scoreboard);
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

    public boolean foundTarget() {
        return state.targetFound(this);
    }

    public boolean foundSbtt() {
        return state.sbttFound(this);
    }

    public boolean isFinished() {
        return state.isFinished();
    }

    public boolean isLose() {
        return state.isAbandoned();
    }

    public boolean isDead() {
        return state.isDead();
    }

    public void finish() {
        setState(State.FINISHED);
    }

    public void abandon() {
        setState(State.ABANDONED);
    }

    public void nextAge() {
        state.nextAge(this);
    }

    public void updateBossBar() {
        if (!state.isFinished()) {
            ageDisplay.setTitle(PartyTmp.getInstance().getOptions().getPlayerBarString(this));
            ageDisplay.setProgress(state.getBossBarProgress(this));
        }

        ageDisplay.setColor(state.getBossBarColor(this));
    }

    /**
     * Reset player's BossBar
     */
    public void resetPlayerBar() {
        if (ageDisplay != null && !ageDisplay.getPlayers().isEmpty()) {
            ageDisplay.removeAll();
        }
    }

    public String getActionBarStr() {
        return state.getActionBarStr(this);
    }

    public boolean skip(boolean penalty) {
        if (penalty) {
            if (age.getNumber() < Config.getSkipAge().getNumber()) {
                LogManager.getInstanceGame().writeMsg(Bukkit.getPlayer(playerName), LangManager.getMsgLang("CANT_SKIP_AGE", configLang));

                return false;
            }

            if (targetCount == 1) {
                LogManager.getInstanceGame().writeMsg(Bukkit.getPlayer(playerName), LangManager.getMsgLang("CANT_SKIP_FIRST", configLang));

                return false;
            }

            targetCount--;
            skipCount++;

            LogManager.getInstanceGame().writeMsg(Bukkit.getPlayer(playerName), PartyTmp.getInstance().getOptions().getSkipStr(this));
        }

        LogManager.getInstanceSystem().writeMsg(Bukkit.getPlayer(playerName), LangManager.getMsgLang("ITEM_SKIPPED", Config.getLang()).replace("[#]", " [" + currentTarget + "] ").replace("<#>", " <" + playerName + ">"));

        score.setScore(targetCount);
        Bukkit.getPluginManager().callEvent(new NewTargetEvent(Bukkit.getPlayer(playerName)));

        return true;
    }

    public void pause() {
        ActionBar.sendRawTitle(ChatColor.BOLD + String.valueOf(ChatColor.DARK_PURPLE) + LangManager.getMsgLang("GAME_PAUSED", configLang) + ChatColor.RESET, Bukkit.getPlayer(playerName));
        Bukkit.getPlayer(playerName).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 10, false, false, false));
        interval = System.currentTimeMillis() - timeTarget;
    }

    public void resume() {
        Bukkit.getPlayer(playerName).removePotionEffect(PotionEffectType.INVISIBILITY);
        timeTarget = System.currentTimeMillis() - interval;
        interval = -1;
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

    public void died(Location deathLoc) {
        deathCount++;
        this.deathLoc = deathLoc;
        clearDeathInv();
        saveDeathInv(Arrays.stream(Bukkit.getPlayer(playerName).getInventory().getContents())
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        setState(State.DEAD);
    }

    public void saveDeathInv(List<ItemStack> deathContent) {
        if (deathInv == null) {
            deathInv = new ArrayList<>();
        }

        deathInv.addAll(deathContent);
    }

    public void revive() {
        restoreDeathInv();
        deathLoc = null;
        revertState();
    }

    public void restoreDeathInv() {
        Player player = Bukkit.getPlayer(playerName);

        for (ItemStack item : deathInv) {
            HashMap<Integer, ItemStack> ret = player.getInventory().addItem(item);

            if (!ret.isEmpty()) {
                for (Integer cnt : ret.keySet()) {
                    player.getWorld().dropItem(player.getLocation(), ret.get(cnt));
                }
            }

            ret.clear();
        }
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
        oStream.writeBoolean(isFinished());
        oStream.writeBoolean(isLose());
        oStream.writeBoolean(isDead());
        oStream.writeBoolean(discovered);
        oStream.writeBoolean(tips);

        oStream.writeInt(targetCount);
        oStream.writeInt(age.getNumber());
        oStream.writeInt(deathCount);
        oStream.writeInt(skipCount);
        oStream.writeInt(sbttCount);
        oStream.writeInt(sameIdx);

        oStream.writeLong(timeStartAge);
        oStream.writeLong(timeTarget);
        oStream.writeLong(interval);

        oStream.writeUTF(currentTarget == null ? "null" : currentTarget);
        oStream.writeUTF(currentTargetDisplay == null ? "null" : currentTargetDisplay);
        oStream.writeUTF(configLang);

        oStream.writeObject(spawnLoc.serialize());

        if (isDead()) {
            oStream.writeObject(deathLoc.serialize());
            oStream.writeInt(deathInv.size());

            for (ItemStack item : deathInv) {
                oStream.writeObject(item.serialize());
            }
        }

        if (!isFinished()) {
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
        boolean isFinished = iStream.readBoolean();
        boolean isLose = iStream.readBoolean();
        boolean isDead = iStream.readBoolean();
        discovered = iStream.readBoolean();
        tips = iStream.readBoolean();

        targetCount = iStream.readInt();
        age = AgeManager.getAgeByNumber(iStream.readInt());
        deathCount = iStream.readInt();
        skipCount = iStream.readInt();
        sbttCount = iStream.readInt();
        sameIdx = iStream.readInt();

        timeStartAge = iStream.readLong();
        timeTarget = iStream.readLong();
        interval = iStream.readLong();

        currentTarget = SerializeUtils.readString(iStream);

        if (currentTarget.equals("null")) {
            currentTarget = null;
        }

        currentTargetDisplay = SerializeUtils.readString(iStream);

        if (currentTargetDisplay.equals("null")) {
            currentTargetDisplay = null;
        }

        configLang = SerializeUtils.readString(iStream);

        spawnLoc = Location.deserialize((Map<String, Object>) iStream.readObject());

        if (isDead) {
            deathLoc = Location.deserialize((Map<String, Object>) iStream.readObject());
            int size = iStream.readInt();
            deathInv = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                deathInv.add(ItemStack.deserialize((Map<String, Object>) iStream.readObject()));
            }
        }

        ageTimes = (Map<String, Long>) iStream.readObject();

        if (!isFinished) {
            timeStartAge = System.currentTimeMillis() - ageTimes.get("INTERVAL_TIME");
            ageTimes.remove("INTERVAL_TIME");
        }

        alreadyGot = (List<String>) iStream.readObject();
    }
}
