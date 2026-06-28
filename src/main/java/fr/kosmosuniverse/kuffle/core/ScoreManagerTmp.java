package fr.kosmosuniverse.kuffle.core;

import fr.kosmosuniverse.kuffle.datamanagers.age.AgeManager;
import fr.kosmosuniverse.kuffle.mode.Mode;
import fr.kosmosuniverse.kuffle.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * @author KosmosUniverse
 */
public class ScoreManagerTmp {
    private static ScoreManagerTmp instance;

    public static void createInstance(Mode mode) {
        instance = new ScoreManagerTmp(mode);
    }

    public static synchronized ScoreManagerTmp getInstance() {
        return instance;
    }

    @Getter
    private Scoreboard scoreboard;
    private Objective ages;
    private Objective targets;

    public ScoreManagerTmp(Mode mode) {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        ages = scoreboard.registerNewObjective("ages", "dummy", ChatColor.LIGHT_PURPLE + "Ages");
        targets = scoreboard.registerNewObjective(mode.name(), "dummy", Utils.capitalize(mode.name()));

        ages.setDisplaySlot(DisplaySlot.SIDEBAR);
        targets.setDisplaySlot(DisplaySlot.PLAYER_LIST);
    }

    public static void clear() {
        if (instance == null) {
            return;
        }

        instance.scoreboard.getObjectives().clear();
        instance.scoreboard = null;
        instance.ages = null;
        instance.targets = null;

        instance = null;
    }

    public void setupScores() {
        for (int i = 0; i < (Config.getLastAge().getNumber() + 1); i++) {
            Score ageScore = ages.getScore(AgeManager.getAgeByNumber(i).getColor() + AgeManager.getAgeByNumber(i).getName().replace("_", " "));
            ageScore.setScore(i + 1);
        }
    }

    public void setupPlayerScore(PlayerData playerData) {
        playerData.setupScores(scoreboard, targets.getScore(playerData.getPlayerName()));
        playerData.updateBossBar();
    }
}
