package main.fr.kosmosuniverse.kuffle.core;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
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
	
	/**
	 * Setups scoreboard and objective
	 * 
	 * @param type	The game type to name the objective
	 */
	public static void setupScores(KuffleType.Type type) {
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		targets = scoreboard.registerNewObjective(type.name().toLowerCase(), "dummy", Utils.capitalize(type.name()));
		targets.setDisplaySlot(DisplaySlot.PLAYER_LIST);
	}
	
	/**
	 * setups the players scores
	 */
	public static void setupPlayerScores() {
		if (age != null) {
			age.unregister();
		}
		
		age = scoreboard.registerNewObjective("ages", "dummy", ChatColor.LIGHT_PURPLE + "Ages");
		
		int ageCnt = 0;
		
		for (; ageCnt < Config.getLastAge().number; ageCnt++) {
			sAges.add(age.getScore(AgeManager.getAgeByNumber(ageCnt).color + AgeManager.getAgeByNumber(ageCnt).name.replace("_", " ")));
		}
				
		ageCnt = 1;
		
		for (Score ageScore : sAges) {
			ageScore.setScore(ageCnt);
			ageCnt++;
		}
		
		age.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		for (String playerName : KuffleMain.games.keySet()) {
			KuffleMain.games.get(playerName).setItemScore(targets.getScore(playerName));
			KuffleMain.games.get(playerName).getItemScore().setScore(1);
			KuffleMain.games.get(playerName).getPlayer().setScoreboard(scoreboard);
			KuffleMain.games.get(playerName).updatePlayerListName();
		}
	}
	
	/**
	 * Setup scores for a specific player
	 * 
	 * @param game	The Game object of the player
	 */
	public static void setupPlayerScores(Game game) {
		targets.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		age.setDisplaySlot(DisplaySlot.SIDEBAR);

		game.setItemScore(targets.getScore(game.getPlayer().getName()));
		game.getItemScore().setScore(1);
		game.getPlayer().setScoreboard(scoreboard);
	}
	
	/**
	 * Clears the scores
	 */
	public static void clear() {
		scoreboard.clearSlot(age.getDisplaySlot());
		
		if (targets.getDisplaySlot() != null) {
			scoreboard.clearSlot(targets.getDisplaySlot());
		}

		age.unregister();
		age = null;
		sAges.clear();
		
		KuffleMain.games.forEach((playerName, game) ->
			game.getPlayer().setPlayerListName(ChatColor.WHITE + playerName)
		);
	}
	
	/**
	 * Resets scores for all players
	 */
	public static void reset() {
		KuffleMain.games.forEach((playerName, game) -> {
			game.getItemScore().setScore(1);
			game.getPlayer().setPlayerListName(ChatColor.RED + playerName);
		});
	}
}
