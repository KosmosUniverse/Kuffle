package fr.kosmosuniverse.kuffle.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.kosmosuniverse.kuffle.type.KuffleType;
import fr.kosmosuniverse.kuffle.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import net.md_5.bungee.api.ChatColor;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class ScoreManager {
	private static final String DUMMY = "dummy";
	private static Scoreboard scoreboard;
	private static Objective age = null;
	private static Objective targets = null;
	private static final List<Score> sAges = new ArrayList<>();
	
	private ScoreManager() {
		throw new IllegalStateException("");
	}
	
	/**
	 * Setups scoreboard and objective
	 * 
	 * @param type	The game type to name the objective
	 */
	public static void setupScores(KuffleType.Type type) {
		scoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard();
		targets = scoreboard.registerNewObjective(type.name().toLowerCase(), DUMMY, Utils.capitalize(type.name()));
		age = scoreboard.registerNewObjective("ages", DUMMY, ChatColor.LIGHT_PURPLE + "Ages");
		
		targets.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		age.setDisplaySlot(DisplaySlot.SIDEBAR);

		int ageCnt = 0;

		for (; ageCnt < (Config.getLastAge().getNumber() + 1); ageCnt++) {
			sAges.add(age.getScore(AgeManager.getAgeByNumber(ageCnt).getColor() + AgeManager.getAgeByNumber(ageCnt).getName().replace("_", " ")));
		}

		ageCnt = 1;

		for (Score ageScore : sAges) {
			ageScore.setScore(ageCnt);
			ageCnt++;
		}
	}

	public static void setupPlayerScores(String playerName) {
		Party.getInstance().getGames().getGames().get(playerName).setupScores(Objects.requireNonNull(Bukkit.getPlayer(playerName)), scoreboard, targets.getScore(playerName));
		Party.getInstance().getGames().updatePlayerBar(playerName);
		Party.getInstance().getGames().updatePlayerListName(playerName);
	}

	/**
	 * setups the players scores
	 */
	public static void setupPlayersScores() {
		if (age != null) {
			age.unregister();
		}
		
		age = scoreboard.registerNewObjective("ages", DUMMY, ChatColor.LIGHT_PURPLE + "Ages");
		
		int ageCnt = 0;
		
		for (; ageCnt < (Config.getLastAge().getNumber() + 1); ageCnt++) {
			sAges.add(age.getScore(AgeManager.getAgeByNumber(ageCnt).getColor() + AgeManager.getAgeByNumber(ageCnt).getName().replace("_", " ")));
		}
				
		ageCnt = 1;
		
		for (Score ageScore : sAges) {
			ageScore.setScore(ageCnt);
			ageCnt++;
		}
		
		age.setDisplaySlot(DisplaySlot.SIDEBAR);

		Party.getInstance().getGames().getGames().forEach((playerName, playerData) -> {
			setupPlayerScores(playerName);
			playerData.getScore().setScore(playerData.getTargetCount());
		});

		targets.setDisplaySlot(DisplaySlot.PLAYER_LIST);
	}
	
	/**
	 * Clears the scores
	 */
	public static void clear() {
		if (age != null) {
			scoreboard.clearSlot(Objects.requireNonNull(age.getDisplaySlot()));
			age.unregister();
			age = null;
		}

		if (targets.getDisplaySlot() != null) {
			scoreboard.clearSlot(targets.getDisplaySlot());
		}

		sAges.clear();

		Party.getInstance().getGames().getGames().forEach((name, data) -> Objects.requireNonNull(Bukkit.getPlayer(name)).setPlayerListName(ChatColor.WHITE + name));
	}
	
	/**
	 * Resets scores for all players
	 */
	public static void reset() {
		Party.getInstance().getGames().getGames().forEach((name, data) -> {
			Objects.requireNonNull(Bukkit.getPlayer(name)).setPlayerListName(ChatColor.WHITE + name);
			data.getScore().setScore(1);
		});
	}
	
	public static Scoreboard getScoreboard() {
		return scoreboard;
	}
	
	public static Score getPlayerScore(String playerName) {
		return targets.getScore(playerName);
	}
}
