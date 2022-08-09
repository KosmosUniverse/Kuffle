package main.fr.kosmosuniverse.kuffle.core;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import net.md_5.bungee.api.ChatColor;

public class Scores {
	private Scoreboard scoreboard;
	private Objective age = null;
	private Objective items;
	private List<Score> sAges = new ArrayList<>();
	
	public Scores() {
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		items = scoreboard.registerNewObjective("items", "dummy", "Items");
	}
	
	public void setupPlayerScores() {
		items.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		
		if (age != null) {
			age.unregister();
		}
		
		age = scoreboard.registerNewObjective("ages", "dummy", ChatColor.LIGHT_PURPLE + "Ages");
		
		int ageCnt = 0;
		
		for (; ageCnt < KuffleMain.config.getMaxAges(); ageCnt++) {
			sAges.add(age.getScore(AgeManager.getAgeByNumber(KuffleMain.ages, ageCnt).color + AgeManager.getAgeByNumber(KuffleMain.ages, ageCnt).name.replace("_", " ")));
		}
				
		ageCnt = 1;
		
		for (Score ageScore : sAges) {
			ageScore.setScore(ageCnt);
			ageCnt++;
		}
		
		age.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		for (String playerName : KuffleMain.games.keySet()) {
			KuffleMain.games.get(playerName).setItemScore(items.getScore(playerName));
			KuffleMain.games.get(playerName).getItemScore().setScore(1);
			KuffleMain.games.get(playerName).getPlayer().setScoreboard(scoreboard);
			KuffleMain.games.get(playerName).updatePlayerListName();
		}
	}
	
	public void setupPlayerScores(Game game) {
		items.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		age.setDisplaySlot(DisplaySlot.SIDEBAR);

		game.setItemScore(items.getScore(game.getPlayer().getName()));
		game.getItemScore().setScore(1);
		game.getPlayer().setScoreboard(scoreboard);
	}
	
	public void clear() {
		scoreboard.clearSlot(age.getDisplaySlot());
		
		if (items.getDisplaySlot() != null) {
			scoreboard.clearSlot(items.getDisplaySlot());
		}

		age.unregister();
		age = null;
		sAges.clear();
		
		KuffleMain.games.forEach((playerName, game) ->
			game.getPlayer().setPlayerListName(ChatColor.WHITE + playerName)
		);
	}
	
	public void reset() {
		KuffleMain.games.forEach((playerName, game) -> {
			game.getItemScore().setScore(1);
			game.getPlayer().setPlayerListName(ChatColor.RED + playerName);
		});
	}
}
