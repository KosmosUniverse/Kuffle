package main.fr.kosmosuniverse.kuffle.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scoreboard.Score;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class Game {
	public List<String> alreadyGot = null;
	public Map<String, Long> times = null;

	public boolean finished;
	public boolean lose;
	public boolean dead;

	public int time;
	public int targetCount = 1;
	public int age = 0;
	public int gameRank = -1;
	public int sameIdx = 0;

	public int deathCount = 0;
	public int skipCount = 0;
	public int sbttCount = 0;
	
	public long totalTime = 0;
	public long timeShuffle = -1;
	public long interval = -1;
	public long timeBase;

	public String currentTarget = null;
	public String targetDisplay = null;
	public String configLang = null;
	public String teamName = null;

	public Location spawnLoc = null;
	public Location deathLoc = null;

	public Player player = null;
	public Inventory deathInv = null;
	public Score score = null;
	public BossBar ageDisplay = null;

	/**
	 * Constructor
	 * 
	 * @param gamePlayer	The player that own this Game instance as Player object
	 */
	public Game(Player gamePlayer) {
		player = gamePlayer;
		
		alreadyGot = new ArrayList<>();
		times = new HashMap<>();
		
		finished = false;
		lose = false;
		dead = false;
	}
	
	/**
	 * Clears alreadyGot list and times map
	 */
	public void clear() {
		alreadyGot.clear();
		times.clear();
	}
}
