package main.fr.kosmosuniverse.kuffle.core;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import main.fr.kosmosuniverse.kuffle.type.KuffleType;
import main.fr.kosmosuniverse.kuffle.utils.Utils;
import net.md_5.bungee.api.ChatColor;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class ScoreManager {
	private static Scoreboard scoreboard;
	private static Objective age = null;
	private static Objective targets = null;
	private static List<Score> sAges = new ArrayList<>();
	
	private ScoreManager() {
		throw new IllegalStateException("");
	}
	
	/**
	 * Setups scoreboard and objective
	 * 
	 * @param type	The game type to name the objective
	 */
	public static void setupScores(KuffleType.Type type) {
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		targets = scoreboard.registerNewObjective(type.name().toLowerCase(), "dummy", Utils.capitalize(type.name()));
		age = scoreboard.registerNewObjective("ages", "dummy", ChatColor.LIGHT_PURPLE + "Ages");
		
		targets.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		age.setDisplaySlot(DisplaySlot.SIDEBAR);
	}
	
	/**
	 * setups the players scores
	 */
	public static void setupPlayersScores() {
		if (age != null) {
			age.unregister();
		}
		
		age = scoreboard.registerNewObjective("ages", "dummy", ChatColor.LIGHT_PURPLE + "Ages");
		
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
		
		GameManager.applyToPlayers(game -> {
			GameManager.setupPlayerScores(game.player.getName(), scoreboard, targets.getScore(game.player.getName()));
			GameManager.updatePlayerListName(game.player.getName());

			game.score.setScore(game.targetCount);
		});
		
		targets.setDisplaySlot(DisplaySlot.PLAYER_LIST);
	}
	
	/**
	 * Setups score for a specific player
	 * 
	 * @param player	The player that will be setup
	 */
	public static void setupPlayerScore(String player) {
		GameManager.applyToPlayer(player, game -> {
			GameManager.setupPlayerScores(game.player.getName(), scoreboard, targets.getScore(game.player.getName()));
			GameManager.updatePlayerListName(game.player.getName());
		});
	}
	
	/**
	 * Clears the scores
	 */
	public static void clear() {
		if (age != null) {
			scoreboard.clearSlot(age.getDisplaySlot());
			age.unregister();
			age = null;
		}
		
		if (targets.getDisplaySlot() != null) {
			scoreboard.clearSlot(targets.getDisplaySlot());
		}

		if (sAges != null) {
			sAges.clear();
		}
		
		GameManager.clearPlayersListNames();
	}
	
	/**
	 * Resets scores for all players
	 */
	public static void reset() {
		GameManager.resetPlayersListNames();
	}
	
	public static Scoreboard getScoreboard() {
		return scoreboard;
	}
	
	public static Score getPlayerScore(String playerName) {
		return targets.getScore(playerName);
	}
}
