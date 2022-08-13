package main.fr.kosmosuniverse.kuffle.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.utils.Utils;
import net.md_5.bungee.api.ChatColor;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class GameManager {
	private static Map<String, Game> games = null;
	private static Map<String, Integer> playersRanks = null;
	private static Inventory playersHeads = null;
	private static List<Material> exceptions;
	
	/**
	 * Private GameManager constructor
	 * 
	 * @throws IllegalStateException
	 */
	private GameManager() {
		throw new IllegalStateException("Utility class");
    }
	
	/**
	 * Setups the games map
	 */
	public static void setupGame() {
		games = new HashMap<>();
		playersRanks = new HashMap<>();
		
		exceptions = new ArrayList<>();
		
		for (Material m : Material.values()) {
			if (m.name().contains("SHULKER_BOX")) {
				exceptions.add(m);
			}
		}
		
		exceptions.add(Material.CRAFTING_TABLE);
		exceptions.add(Material.FURNACE);
		exceptions.add(Material.STONECUTTER);
	}
	
	/**
	 * Clears the games map
	 */
	public static void clear() {
		if (games != null) {
			games.forEach((k, v) -> v.clear());
			games.clear();
		}
		
		if (playersRanks != null) {
			playersRanks.clear();
		}
		
		if (playersHeads != null) {
			playersHeads.clear();
		}
	}
	
	/**
	 * Adds a player to the game list
	 * 
	 * @param player	The player to add
	 * 
	 * @return True if success, False if player is already in the list
	 */
	public static boolean addPlayer(Player player) {
		boolean ret;
		
		if (!games.containsKey(player.getName())) {
			//insert log
			ret = false;
		} else {
			games.put(player.getName(), new Game(player));
			ret = true;
		}
		
		return ret;
	}
	
	/**
	 * Removes a player from the game list
	 * 
	 * @param player	The player to add
	 * 
	 * @return True if success, False if player is not in the list
	 */
	public static boolean removePlayer(String player) {
		boolean ret;
		
		if (!games.containsKey(player)) {
			//insert log
			ret = false;
		} else {
			games.get(player).clear();
			games.remove(player);
			ret = true;
		}
		
		return ret;
	}
	
	/**
	 * Checks if games map contains a specific player
	 * 
	 * @param playe	The player to check
	 * 
	 * @return True if games has player key, False instead
	 */
	public static boolean hasPlayer(String player) {
		return games.containsKey(player);
	}
	
	/**
	 * Gets the games map
	 * 
	 * @return the games map as an unmodifiable map
	 */
	public static Map<String, Game> getGames() {
		return Collections.unmodifiableMap(games);
	}
	
	/**
	 * Get the currently playing players list
	 * 
	 * @return the player list
	 */
	public static List<Player> getPlayerList() {
		List<Player> players = new ArrayList<>();

		for (String playerName : games.keySet()) {
			players.add(games.get(playerName).player);
		}

		return players;
	}

	/**
	 * Get the currently playing player names list
	 * 
	 * @return the player names list
	 */
	public static List<String> getPlayerNames() {
		List<String> players = new ArrayList<>();

		for (String playerName : games.keySet()) {
			players.add(playerName);
		}

		return players;
	}
	
	public static Location getPlayerSpawnLoc(String player) {
		return games.get(player).spawnLoc;
	}
	
	/**
	 * Clears Player's game related effects and data
	 * 
	 * @param player	The player to clear
	 */
	public static void stopPlayer(String player) {
		Game game = games.get(player);
		
		for (PotionEffect pe : game.player.getActivePotionEffects()) {
			game.player.removePotionEffect(pe.getType());
		}

		resetPlayerBar(player);
		game.clear();
	}
	
	/**
	 * Convert player datas into a stringify JSON
	 * 
	 * @param player	The player to save
	 * 
	 * @return the JSON formatted string of player's data
	 */
	@SuppressWarnings("unchecked")
	public static String savePlayer(String player) {
		Game game = games.get(player);
		JSONObject jsonSpawn = new JSONObject();

		jsonSpawn.put("World", game.spawnLoc.getWorld().getName());
		jsonSpawn.put("X", game.spawnLoc.getX());
		jsonSpawn.put("Y", game.spawnLoc.getY());
		jsonSpawn.put("Z", game.spawnLoc.getZ());

		JSONObject jsonDeath = new JSONObject();
		if (game.deathLoc == null) {
			jsonDeath = null;
		} else {
			jsonDeath.put("World", game.deathLoc.getWorld().getName());
			jsonDeath.put("X", game.deathLoc.getX());
			jsonDeath.put("Y", game.deathLoc.getY());
			jsonDeath.put("Z", game.deathLoc.getZ());
		}

		JSONObject global = new JSONObject();

		if (game.deathInv != null) {
			try {
				savePlayerInventory(player);
			} catch (IOException e) {
				LogManager.getInstanceSystem().logSystemMsg(e.getMessage());
			}
		}

		global.put("age", game.age);
		global.put("current", game.currentTarget);
		global.put("interval", System.currentTimeMillis() - game.timeShuffle);
		global.put("time", game.time);
		global.put("isDead", game.dead);
		global.put("itemCount", game.targetCount);
		global.put("spawn", jsonSpawn);
		global.put("death", jsonDeath);
		global.put("teamName", game.teamName);
		global.put("sameIdx", game.sameIdx);
		global.put("deathCount", game.deathCount);
		global.put("skipCount", game.skipCount);
		global.put("finished", game.finished);
		global.put("lose", game.lose);

		JSONArray got = new JSONArray();

		for (String item : game.alreadyGot) {
			got.add(item);
		}

		global.put("alreadyGot", got);

		JSONObject saveTimes = new JSONObject();

		game.times.forEach(saveTimes::put);

		saveTimes.put("interval", System.currentTimeMillis() - game.timeBase);

		global.put("times", saveTimes);

		return (global.toString());
	}
	
	/**
	 * Saves a player's inventory in a YAML file
	 * 
	 * @param player	The player to whom the inventory will be saved
	 * 
	 * @throws IOException if FileConfiguration.save() fails
	 */
	public static void savePlayerInventory(String player) throws IOException {
		Game game = games.get(player);
        File f = new File(KuffleMain.current.getDataFolder().getPath(), game.player.getName() + ".yml");
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        
        c.set("inventory.content", game.deathInv.getContents());
        c.save(f);
    }
	
	/**
	 * loads a player's inventory from a YAML file
	 * 
	 * @param player	The player to whom the inventory will be loaded
	 */
	@SuppressWarnings("unchecked")
	public static void loadPlayerInventory(String player) {
		Game game = games.get(player);
		File f = new File(KuffleMain.current.getDataFolder().getPath(), game.player.getName() + ".yml");
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);

        game.deathInv = Bukkit.createInventory(null, 54);

        ItemStack[] content = ((List<ItemStack>) c.get("inventory.content")).toArray(new ItemStack[0]);
        game.deathInv.setContents(content);
    }
	
	/**
	 * Loads a new Game for a specific Player
	 * 
	 * @param player	The player that will be loaded
	 * 
	 * @throws IOException if FileReader fails
	 * @throws FileNotFoundException if FileReader does not find the player file
	 * @throws ParseException if JSONParser.parse fails
	 */
	public static void loadPlayerGame(Player player) throws FileNotFoundException, IOException, ParseException {
		JSONParser parser = new JSONParser();
		Game tmpGame = new Game(player);

		try (FileReader reader = new FileReader(KuffleMain.current.getDataFolder().getPath() + File.separator + player.getName() + ".ki")) {
			JSONObject mainObject = (JSONObject) parser.parse(reader);

			tmpGame.dead = (boolean) mainObject.get("isDead");
			tmpGame.finished = (boolean) mainObject.get("finished");
			tmpGame.lose = (boolean) mainObject.get("lose");

			tmpGame.age = Integer.parseInt(((Long) mainObject.get("age")).toString());
			tmpGame.currentTarget = (String) mainObject.get("current");
			tmpGame.timeShuffle = System.currentTimeMillis() - (Long) mainObject.get("interval");
			tmpGame.time = Integer.parseInt(((Long) mainObject.get("time")).toString());
			tmpGame.targetCount = Integer.parseInt(((Long) mainObject.get("itemCount")).toString());
			tmpGame.teamName = (String) mainObject.get("teamName");
			tmpGame.sameIdx = Integer.parseInt(((Long) mainObject.get("sameIdx")).toString());
			tmpGame.deathCount = Integer.parseInt(mainObject.get("deathCount").toString());
			tmpGame.skipCount = Integer.parseInt(mainObject.get("skipCount").toString());

			tmpGame.alreadyGot = getAlreadyGotListFromJSON((JSONArray) mainObject.get("alreadyGot"));
			tmpGame.times = getTimesMapFromJSON((JSONObject) mainObject.get("times"));
			tmpGame.spawnLoc = getLocationFromJSON((JSONObject) mainObject.get("spawn"));
			tmpGame.deathLoc = getLocationFromJSON((JSONObject) mainObject.get("death"));
						
			mainObject.clear();
		}
		
		games.put(player.getName(), tmpGame);
		
		String playerName = player.getName();
		Game game = games.get(playerName);
		
		updatePlayersHeads();
		setupPlayerScores(playerName, ScoreManager.getScoreboard(), ScoreManager.getPlayerScore(playerName));
		
		if (Utils.fileExists(KuffleMain.current.getDataFolder().getPath(), playerName + ".yml")) {
			loadPlayerInventory(player.getName());
			Utils.fileDelete(KuffleMain.current.getDataFolder().getPath(), playerName + ".yml");
		}

		
		if (game.dead) {
			teleportAutoBack(playerName);
		}
		
		if (game.finished) {
			game.gameRank = playersRanks.get(playerName);
		}
		
		updatePlayerBar(playerName);
		reloadPlayerEffects(playerName);
		updatePlayerListName(playerName);
		
		game.score.setScore(game.targetCount);
	}
	
	/**
	 * Pause the player's game by saving the difference between actual time and player's timer
	 * 
	 * @param player	The player to pause
	 */
	public static void pauseplayer(String player) {
		Game game = games.get(player);
		
		game.interval = System.currentTimeMillis() - game.timeShuffle;
	}

	/**
	 * Resume the player's game by loading the difference between actual time and pause time
	 * 
	 * @param player	The player to resume
	 */
	public static void resume(String player) {
		Game game = games.get(player);
		
		game.timeShuffle = System.currentTimeMillis() - game.interval;
		game.interval = -1;
	}
	
	/**
	 * Updates the player boss bar
	 * 
	 * @param player	The player to update
	 */
	public static void updatePlayerBar(String player) {
		Game game = games.get(player);
		
		if (game.lose) {
			game.ageDisplay.setProgress(0.0);
			game.ageDisplay.setTitle(LangManager.getMsgLang("GAME_DONE", game.configLang).replace("%i", "" + game.gameRank));

			return ;
		}

		if (game.finished) {
			game.ageDisplay.setProgress(1.0);
			game.ageDisplay.setTitle(LangManager.getMsgLang("GAME_DONE", game.configLang).replace("%i", "" + game.gameRank));

			return ;
		}

		double calc = ((double) game.targetCount) / Config.getTargetPerAge();
		calc = calc > 1.0 ? 1.0 : calc;
		game.ageDisplay.setProgress(calc);
		game.ageDisplay.setTitle(AgeManager.getAgeByNumber(game.age).name.replace("_", " ") + ": " + game.targetCount);
	}
	
	/**
	 * Setup basics variables for a player
	 * 
	 * @param player	The player that will be setup
	 */
	public static void setupPlayer(String player) {
		Game game = games.get(player);
		
		game.time = Config.getStartTime();
		game.timeBase = System.currentTimeMillis();
		game.configLang = Config.getLang();
		game.ageDisplay = Bukkit.createBossBar(LangManager.getMsgLang("START", game.configLang), BarColor.PURPLE, BarStyle.SOLID);
		game.ageDisplay.addPlayer(game.player);
		
		updatePlayerBar(player);
	}
	
	/**
	 * Reset player's BossBar
	 * 
	 * @param player	The player
	 */
	public static void resetPlayerBar(String player) {
		Game game = games.get(player);
		
		if (game.ageDisplay != null && game.ageDisplay.getPlayers().size() != 0) {
			game.ageDisplay.removeAll();
			game.ageDisplay = null;
		}
	}
	
	/**
	 * Player used Sbtt
	 * 
	 * @param player	The player that used sbtt
	 */
	public static void playerFoundSBTT(String player) {
		Game game = games.get(player);
		
		game.sbttCount++;
		playerFoundTarget(player);
	}
	
	/**
	 * Player found its target
	 * 
	 * @param player	The player that found
	 */
	public static void playerFoundTarget(String player) {
		Game game = games.get(player);
		
		game.currentTarget = null;
		game.targetCount++;
		game.player.playSound(game.player.getLocation(), Sound.BLOCK_BELL_USE, 1f, 1f);
		game.score.setScore(game.targetCount);
		updatePlayerBar(player);
	}
	
	/**
	 * Player goes to the next Age
	 * 
	 * @param player	The player that is moving to the next Age
	 */
	public static void nextPlayerAge(String player) {
		Game game = games.get(player);
		
		if (Config.getRewards()) {
			if (game.age > 0) {
				RewardManager.removePreviousRewardEffects(AgeManager.getAgeByNumber(game.age - 1).name, game.player);
			}

			RewardManager.givePlayerReward(AgeManager.getAgeByNumber(game.age).name, game.player);
		}

		game.times.put(AgeManager.getAgeByNumber(game.age).name, System.currentTimeMillis() - game.timeBase);
		game.totalTime += game.times.get(AgeManager.getAgeByNumber(game.age).name) / 1000;

		game.timeBase = System.currentTimeMillis();
		game.alreadyGot.clear();
		game.currentTarget = null;
		game.targetCount = 1;
		game.sameIdx = 0;
		game.age++;
		game.time = game.time + Config.getAddedTime();
		game.player.playSound(game.player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1f, 1f);
		game.score.setScore(game.targetCount);
		updatePlayerListName(player);
		updatePlayerBar(player);

		Age tmpAge = AgeManager.getAgeByNumber(game.age);

		games.forEach((playerName, playerGame) -> playerGame.player.sendMessage(LangManager.getMsgLang("AGE_MOVED", game.configLang).replace("<#>", ChatColor.BLUE + "<§6§l" + player + ChatColor.BLUE + ">").replace("<##>", "<" + tmpAge.color + tmpAge.name.replace("_Age", "") + ChatColor.BLUE + ">")));
		game.player.sendMessage(LangManager.getMsgLang("TIME_AGE", game.configLang).replace("%t", Utils.getTimeFromSec(game.totalTime)));
	}
	
	/**
	 * Makes a Player finish with specific rank
	 * 
	 * @param player	The player that finished
	 * @param rank		The player rank
	 */
	public static void finish(String player, int rank) {
		Game game = games.get(player);
		
		game.finished = true;

		if (Config.getTeam()) {
			int tmpRank;

			if ((tmpRank = checkTeamMateRank(game.teamName)) != -1) {
				rank = tmpRank;
			}
		}

		game.gameRank = rank;
		game.ageDisplay.setTitle(LangManager.getMsgLang("GAME_DONE", game.configLang).replace("%i", "" + game.gameRank));

		if (game.lose) {
			game.ageDisplay.setProgress(0.0f);
		} else {
			game.ageDisplay.setProgress(1.0f);
		}

		playersRanks.put(player, game.gameRank);
		updatePlayersHeadData(player, null);

		for (PotionEffect pe : game.player.getActivePotionEffects()) {
			game.player.removePotionEffect(pe.getType());
		}

		if (game.lose) {
			for (int cnt = game.age; cnt < Config.getLastAge().number; cnt++) {
				game.times.put(AgeManager.getAgeByNumber(cnt).name, (long) -1);
			}
		} else {
			game.times.put(AgeManager.getAgeByNumber(game.age).name, System.currentTimeMillis() - game.timeBase);
		}

		game.age = -1;

		updatePlayerListName(player);

		printPlayer(player, player);
		logPlayer(player);
	}
	
	/**
	 * Gets the Rank of other team mates
	 * 
	 * @param teamName	The team name
	 * 
	 * @return the rank of the first found team mates in games map
	 */
	private static int checkTeamMateRank(String teamName) {
		int tmp = -1;
		
		for (String playerName : games.keySet()) {
			if (games.get(playerName).teamName.equals(teamName) &&
					games.get(playerName).gameRank != -1) {
				tmp = games.get(playerName).gameRank;
			}
		}

		return tmp;
	}
	
	/**
	 * Set player BossBar color randomly
	 * 
	 * @param player
	 */
	public static void playerRandomBarColor(String player) {
		Game game = games.get(player);
		BarColor[] colors = BarColor.values();
		
		game.ageDisplay.setColor(colors[ThreadLocalRandom.current().nextInt(colors.length)]);
	}
	
	/**
	 * Skip target for a specific player
	 * 
	 * @param player	Player for whose it will skip the target
	 * @param malus		If True a malus of 1 target is applied
	 * 
	 * @return True if target skipped, False instead
	 */
	public static boolean skipPlayerTarget(String player, boolean malus) {
		Game game = games.get(player);
		
		game.skipCount++;

		if (malus) {
			if ((game.age + 1) < Config.getSkipAge().number) {
				LogManager.getInstanceGame().writeMsg(game.player, LangManager.getMsgLang("CANT_SKIP_AGE", game.configLang));

				return false;
			}

			if (game.targetCount == 1) {
				LogManager.getInstanceGame().writeMsg(game.player, LangManager.getMsgLang("CANT_SKIP_FIRST", game.configLang));

				return false;
			}

			game.targetCount--;

			if (game.currentTarget.contains("/")) {
				LogManager.getInstanceGame().writeMsg(game.player, LangManager.getMsgLang("ITEMS_SKIP", game.configLang).replace("[#]", "[" + game.currentTarget.split("/")[0] + "]").replace("[##]", "[" + game.currentTarget.split("/")[1] + "]"));
			} else {
				LogManager.getInstanceGame().writeMsg(game.player, LangManager.getMsgLang("ITEM_SKIP", game.configLang).replace("[#]", "[" + game.currentTarget + "]"));
			}

			game.score.setScore(game.targetCount);
			game.currentTarget = null;
			
			updatePlayerBar(player);
		} else {
			game.score.setScore(game.targetCount);
			game.currentTarget = null;

			updatePlayerBar(player);
		}

		return true;
	}
	
	/**
	 * Gives effects to a player depending on his current Age
	 * 
	 * @param player	The player that will benefit of the effects
	 */
	public static void reloadPlayerEffects(String player) {
		Game game = games.get(player);
		
		if (Config.getRewards()) {
			if (Config.getSaturation()) {
				game.player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 10, false, false, false));
			}

			int tmp = game.age - 1;

			if (tmp < 0)
				return;

			RewardManager.givePlayerRewardEffect(game.player, AgeManager.getAgeByNumber(tmp).name);
		}
	}
	
	/**
	 * Store actual player inventory into another one
	 * 
	 * @param player	The player for whom the inventory will be saved
	 */
	public static void savePlayerInv(String player) {
		Game game = games.get(player);
		
		game.deathInv = Bukkit.createInventory(null, 54);

		for (ItemStack item : game.player.getInventory().getContents()) {
			if (item != null) {
				game.deathInv.addItem(item);
			}
		}
	}

	/**
	 * Give to a player the content of its saved inventory
	 * 
	 * @param player	The player that will receive the inventory items
	 */
	public static void restorePlayerInv(String player) {
		Game game = games.get(player);
		
		game.player.getInventory().clear();
		
		for (ItemStack item : game.deathInv.getContents()) {
			if (item != null) {
				HashMap<Integer, ItemStack> ret = game.player.getInventory().addItem(item);
				
				if (!ret.isEmpty()) {
					for (Integer cnt : ret.keySet()) {
						game.player.getWorld().dropItem(game.player.getLocation(), ret.get(cnt));
					}
				}
				
				ret.clear();
			}
		}

		game.deathInv.clear();
		game.deathInv = null;
		game.deathLoc = null;
		game.dead = false;
	}
	
	/**
	 * Updates the player name display in tab
	 * 
	 * @param player	The player that it will update the name
	 */
	public static void updatePlayerListName(String player) {
		Game game = games.get(player);
		
		if (Config.getTeam()) {
			game.player.setPlayerListName("[" + TeamManager.getTeam(game.teamName).color + game.teamName + ChatColor.RESET + "] - " + AgeManager.getAgeByNumber(game.age).color + player);
		} else {
			game.player.setPlayerListName(AgeManager.getAgeByNumber(game.age).color + player);
		}
	}
	
	/**
	 * Gets the alreadyGot list from JSON
	 * 
	 * @param got	The alreadyGot list as List from JSONArray
	 */
	public static List<String> getAlreadyGotListFromJSON(JSONArray got) {
		List<String> list = new ArrayList<>();
		
		for (Object element : got) {
			list.add((String) element);
		}
		
		return list;
	}

	/**
	 * Sets the game finish for a specific player
	 * 
	 * @param player		The player
	 * @param gameFinished	The game state, True if finished
	 */
	public static void setFinished(String player, boolean gameFinished) {
		games.get(player).finished = gameFinished;
	}

	/**
	 * Sets the game lose for a specific player
	 * 
	 * @param player	The player
	 * @param gameLose	The lose state, True if player lose
	 */
	public static void setLose(String player, boolean gameLose) {
		games.get(player).lose = gameLose;
	}
	
	/**
	 * 
	 * 
	 * @param player
	 * @param deathLoc
	 */
	public static void playerDied(String player, Location deathLoc) {
		Game game = games.get(player);
		
		if (Config.getLevel().losable) {
			game.lose = true;
		} else {
			savePlayerInv(player);
		}
		
		game.deathLoc = deathLoc;
		game.dead = true;
	}

	/**
	 * Sets the game death for a specific player
	 * 
	 * @param player	The player
	 * @param death		The death state, True if player is dead
	 */
	public static void setDead(String player, boolean death) {
		Game game = games.get(player);
		
		game.dead = death;

		if (game.dead) {
			game.deathCount++;
		}
	}

	/**
	 * Set time for a specific player
	 * 
	 * @param player	The player
	 * @param gameTime	The time to set
	 */
	@Deprecated
	public static void setTime(String player, int gameTime) {
		games.get(player).time = gameTime;
	}

	/**
	 * Sets the target count for a specific player
	 * 
	 * @param player	The player
	 * @param targetNb	The target count to set
	 */
	@Deprecated
	public static void setTargetCount(String player, int targetNb) {
		Game game = games.get(player);
		
		game.targetCount = targetNb;

		if (game.score != null) {
			game.score.setScore(game.targetCount);
		}

		updatePlayerBar(player);
	}

	/**
	 * Set the Age for a specific player
	 * 
	 * @param player	The player
	 * @param gameAge	The Age to set
	 */
	@Deprecated
	public static void setAge(String player, int gameAge) {
		Game game = games.get(player);
		
		if (game.age == gameAge) {
			return;
		}

		game.age = gameAge;
		game.alreadyGot.clear();
	}

	/**
	 * Set the same mode index for a specific player
	 * 
	 * @param player	The player
	 * @param idx		The index to set
	 */
	public static void setSameIdx(String player, int idx) {
		games.get(player).sameIdx = idx;
	}

	/**
	 * Sets the death count for a specific player
	 * 
	 * @param player	The player
	 * @param death		The death count to set
	 */
	@Deprecated
	public static void setDeathCount(String player, int death) {
		games.get(player).deathCount = death;
	}

	/**
	 * Sets the skip count for a specific player
	 * 
	 * @param player	The player
	 * @param skip		The skip count to set
	 */
	@Deprecated
	public static void setSkipCount(String player, int skip) {
		games.get(player).skipCount = skip;
	}

	/**
	 * Sets the time shuffle for a specific player
	 * 
	 * @param player	The player
	 * @param shuffle	The time shuffle to set
	 */
	@Deprecated
	public static void setTimeShuffle(String player, long shuffle) {
		games.get(player).timeShuffle = shuffle;
	}

	/**
	 * Sets the player's current target
	 * 
	 * @param player	The player
	 * @param target	The target to set
	 */
	public static void setPlayerCurrentTarget(String player, String target) {
		Game game = games.get(player);
		
		game.currentTarget = target;

		if (game.currentTarget == null) {
			return ;
		}

		if (Config.getDouble()) {
			game.timeShuffle = System.currentTimeMillis();

			game.targetDisplay = LangManager.getTargetLang(game.currentTarget.split("/")[0], game.configLang) + "/" + LangManager.getTargetLang(game.currentTarget.split("/")[1], game.configLang);
			updatePlayersHeadData(player, game.targetDisplay);
		} else {
			if (!game.alreadyGot.contains(game.currentTarget)) {
				game.alreadyGot.add(game.currentTarget);
			}

			game.timeShuffle = System.currentTimeMillis();
			game.targetDisplay = LangManager.getTargetLang(game.currentTarget, game.configLang);
			updatePlayersHeadData(player, game.targetDisplay);
		}
	}

	/**
	 * Sets the Team name
	 * 
	 * @param player	The player
	 * @param team		The team name to set
	 */
	public static void setPlayerTeamName(String player, String team) {
		games.get(player).teamName = team;
	}

	/**
	 * Sets the player lang
	 * 
	 * @param player	The player
	 * @param lang		The lang to set
	 */
	public static void setPlayerLang(String player, String lang) {
		Game game = games.get(player);
		
		if (lang.equals(game.configLang)) {
			return ;
		}

		game.configLang = lang;

		if (game.currentTarget != null) {
			if (Config.getDouble()) {
				game.targetDisplay = LangManager.getTargetLang(game.currentTarget.split("/")[0], game.configLang) + "/" + LangManager.getTargetLang(game.currentTarget.split("/")[1], game.configLang);
			} else {
				game.targetDisplay = LangManager.getTargetLang(game.currentTarget, game.configLang);
			}
		}
	}

	/**
	 * Sets the player target score
	 * 
	 * @param player	The player
	 * @param gameScore	The score to set
	 */
	@Deprecated
	public static void setPlayerTargetScore(String player, Score gameScore) {
		games.get(player).score = gameScore;
	}

	/**
	 * Sets the player death inventory
	 * 	
	 * @param player	The player
	 * @param death		The inventory to set
	 */
	@Deprecated
	public static void setPlayerDeathInv(String player, Inventory death) {
		games.get(player).deathInv = death;
	}

	/**
	 * Sets the player spawn location from Location object
	 * 
	 * @param player	The player
	 * @param spawn		The location to set
	 */
	@Deprecated
	public static void setPlayerSpawnLoc(String player, Location spawn) {
		games.get(player).spawnLoc = spawn;
	}

	/**
	 * Sets the player spawn location from JSONObject
	 * 
	 * @param player	The player
	 * @param spawn		The location to set
	 */
	@Deprecated
	public static void setPlayerSpawnLoc(String player, JSONObject spawn) {
		games.get(player).spawnLoc = new Location(Bukkit.getWorld((String) spawn.get("World")),
				(double) spawn.get("X"),
				(double) spawn.get("Y"),
				(double) spawn.get("Z"));
	}

	/**
	 * Sets player death location from Location object
	 * 
	 * @param player	The player
	 * @param spawn		The death location to set
	 */
	@Deprecated
	public static void setPlayerDeathLoc(String player, Location spawn) {
		games.get(player).deathLoc = spawn;
	}

	/**
	 * Gets location object from JSONObject
	 * 
	 * @param spawn	The JSON death location to transform to Location object
	 */
	private static Location getLocationFromJSON(JSONObject spawn) {
		if (spawn == null) {
			return null;
		} else {
			return new Location(Bukkit.getWorld((String) spawn.get("World")),
					(double) spawn.get("X"),
					(double) spawn.get("Y"),
					(double) spawn.get("Z"));
		}
	}

	/**
	 * Sets player times map
	 * 
	 * @param player	The player
	 * @param gameTimes	The times map to set as JSONObject
	 */
	public static Map<String, Long> getTimesMapFromJSON(JSONObject gameTimes) {
		Map<String, Long> times = new HashMap<>();

		for (int i = 0; i < Config.getLastAge().number; i++) {
			Age ageTime = AgeManager.getAgeByNumber(i);

			if (gameTimes.containsKey(ageTime.name)) {
				times.put(ageTime.name, (Long) gameTimes.get(ageTime.name));
			}
		}
		
		return times;
	}

	/**
	 * Adds a target to alreadyGot list for a specific player
	 * 
	 * @param player	The player
	 * @param target	The target to add
	 */
	@Deprecated
	public static void addToAlreadyGot(String player, String target) {
		games.get(player).alreadyGot.add(target);
	}

	/**
	 * Removes targets from alreadyGot list for a specific player
	 * 
	 * @param player	The player
	 * @param array		An array of all targets to remove
	 */
	@Deprecated
	public static void removeFromList(String player, String[] array) {
		Game game = games.get(player);
		
		game.alreadyGot.remove(game.currentTarget.equals(array[0]) ? array[1] : array[0]);
	}

	/**
	 * Resets alreadyGot list
	 * 
	 * @param player	The player
	 */
	@Deprecated
	public static void resetList(String player) {
		games.get(player).alreadyGot.clear();
	}
	
	/**
	 * Updates players heads in playersHeads inventory
	 */
	public static void updatePlayersHeads() {
		Inventory newInv = Bukkit.createInventory(null, Utils.getNbInventoryRows(games.size()), "§8Players");
		
		for (String playerName : games.keySet()) {
			newInv.addItem(Utils.getHead(games.get(playerName).player, games.get(playerName).targetDisplay));
		}
		
		playersHeads.clear();
		playersHeads = newInv;
	}

	/**
	 * Updates the head of a specific player in playersHeads inventory
	 * 
	 * @param player		The player to be updated
	 * @param currentTarget	The new lore to set to the player head
	 */
	public static void updatePlayersHeadData(String player, String currentTarget) {
		ItemMeta itM;

		if (playersHeads == null) {
			return;
		}

		for (ItemStack item : playersHeads) {
			if (item != null) {
				itM = item.getItemMeta();

				if (itM.getDisplayName().equals(player)) {
					List<String> lore = new ArrayList<>();

					if (currentTarget != null) {
						lore.add(currentTarget);
					}

					itM.setLore(lore);
					item.setItemMeta(itM);
				}
			}
		}
	}
	
	/**
	 * Sends a Msg to games players
	 * 
	 * @param msg	The message to send
	 */
	public static void sendMsgToPlayers(String msg) {
		games.forEach((playerName, playerGame) -> playerGame.player.sendMessage(msg));
	}
	
	/**
	 * Teleport a specific player to his death location
	 * 
	 * @param player	The player to teleport
	 */
	public static void teleportAutoBack(String player) {
		Game game = games.get(player);
		
		game.player.sendMessage("You will be tp back to your death spot in " + Config.getLevel().seconds + " seconds.");
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () -> {
			Location loc = game.deathLoc;
				
			if (loc.getWorld().getName().contains("the_end") && loc.getY() < 0) {
				changeLocForEnd(loc);
			}
			
			createSafeBox(loc, player);
			
			game.player.teleport(loc.add(0, 1, 0));
			
			for (Entity e : game.player.getNearbyEntities(3.0, 3.0, 3.0)) {
				if (e.getType() != EntityType.DROPPED_ITEM) {
					e.remove();
				}
			}
			
			restorePlayerInv(player);

			for (PotionEffect p : game.player.getActivePotionEffects()) {
				game.player.removePotionEffect(p.getType());
			}
			
			reloadPlayerEffects(player);
		}, (Config.getLevel().seconds * 20));
	}
	
	/**
	 * Changes the location Y value to highest block, Or 61 if no block
	 * 
	 * @param loc
	 */
	private static void changeLocForEnd(Location loc) {
		int tmp = loc.getWorld().getHighestBlockYAt(loc);
		
		if (tmp != -1) {
			loc.setY(loc.getWorld().getHighestBlockYAt(loc) + 1);
		} else {
			loc.setY(61);
		}
	}
	
	/**
	 * Create safe box 5x5x5 made of Dirt around player location
	 * 
	 * @param loc			The box center location
	 * @param playerName	The player name that will be teleported inside
	 */
	private static void createSafeBox(Location loc, String playerName) {
		Location wall;
		
		for (double x = -2; x <= 2; x++) {
			for (double y = -2; y <= 2; y++) {
				for (double z = -2; z <= 2; z++) {
					wall = loc.clone();
					wall.add(x, y, z);
					
					if (x == 0 && y == -1 && z == 0) {
						setSign(wall, playerName);
					} else if (x <= 1 && x >= -1 && y <= 1 && y >= -1 && z <= 1 && z >= -1) {
						replaceExeption(wall, Material.AIR);
					} else {
						replaceExeption(wall, Material.DIRT);
					}
				}
			}
		}
	}
	
	/**
	 * Set safe box Dirt block only if no exception block is at this location
	 * 
	 * @param loc	The location to transform
	 * @param m		The block type to set
	 */
	private static void replaceExeption(Location loc, Material m) {
		if (!exceptions.contains(loc.getBlock().getType())) {
			loc.getBlock().setType(m);
		}
	}
	
	/**
	 * Put a sign with the dead player name on it, only if no exception block at this location
	 * 
	 * @param loc			The Location to put sign
	 * @param playerName	The player name to put on the sign
	 */
	private static void setSign(Location loc, String playerName) {
		if (!exceptions.contains(loc.getBlock().getType())) {
			loc.getBlock().setType(Material.OAK_SIGN);
			
			Sign sign = (Sign) loc.getBlock().getState();
			
			sign.setLine(0, "[" + KuffleMain.current.getName() + "]");
			sign.setLine(1, LangManager.getMsgLang("HERE_DIES", games.get(playerName).configLang));
			sign.setLine(2, playerName);
			sign.update(true);
		}
	}
	
	/**
	 * Give to a players one or more Potion Effects
	 * 
	 * @param player	The player that will receive effects
	 * @param effects	The effect(s) he will receive
	 */
	public static void giveEffectsToPlayer(String player, PotionEffect... effects) {
		Game game = games.get(player);
		
		for (PotionEffect effect : effects) {
			game.player.addPotionEffect(effect);
		}
	}
	
	/**
	 * Setup scores for a specific player
	 * 
	 * @param game	The Game object of the player
	 */
	public static void setupPlayerScores(String player, Scoreboard scoreboard, Score score) {
		Game game = games.get(player);
		
		game.score = score;
		game.score.setScore(1);
		game.player.setScoreboard(scoreboard);
	}
	
	/**
	 * Get the number of players that have not finished the game.
	 * 
	 * @return the number of player that are still playing
	 */
	public static int getNbPlayerStillPlaying() {
		int end = 0;

		for (String playerName : games.keySet()) {
			if (!games.get(playerName).finished) {
				end++;
				break;
			}
		}

		return end;
	}
	
	/**
	 * Logs and Prints game end result tab
	 */
	public static void printGameEnd() {
		games.forEach((playerName, game) -> {
			logPlayer(playerName);

			games.forEach((playerToSend, gameToSend) -> printPlayer(playerName, playerToSend));
		});
	}
	
	/**
	 * Prints the game end result tab from a specific player
	 * 
	 * @param playerName	The player name whose game will be printed
	 * @param toSend		The player name that will receive this print
	 */
	private static void printPlayer(String playerName, String toSend) {
		long total = 0;
		Game game = games.get(playerName);
		String receiverLang = games.get(toSend).configLang;
		StringBuilder sb = new StringBuilder();
		
		sb.append(ChatColor.GOLD + "" + ChatColor.BOLD + playerName + ChatColor.RESET + ":").append("\n");
		sb.append(ChatColor.BLUE + LangManager.getMsgLang("DEATH_COUNT", receiverLang).replace("%i", "" + ChatColor.RESET + game.deathCount)).append("\n");
		sb.append(ChatColor.BLUE + LangManager.getMsgLang("SKIP_COUNT", receiverLang).replace("%i", "" + ChatColor.RESET + game.skipCount)).append("\n");
		sb.append(ChatColor.BLUE + LangManager.getMsgLang("TEMPLATE_COUNT", receiverLang).replace("%i", "" + ChatColor.RESET + game.sbttCount)).append("\n");
		sb.append(ChatColor.BLUE + LangManager.getMsgLang("TIME_TAB", receiverLang)).append("\n");

		for (int i = 0; i < Config.getLastAge().number; i++) {
			Age age = AgeManager.getAgeByNumber(i);

			if (game.times.get(age.name) == -1) {
				sb.append(LangManager.getMsgLang("FINISH_ABANDON", receiverLang).replace("%s", age.color + age.name.replace("_Age", "") + ChatColor.RESET)).append("\n");
			} else {
				sb.append(LangManager.getMsgLang("FINISH_TIME", receiverLang).replace("%s", age.color + age.name.replace("_Age", "") + ChatColor.BLUE).replace("%t", ChatColor.RESET + Utils.getTimeFromSec(game.times.get(age.name) / 1000))).append("\n");
				total += game.times.get(age.name) / 1000;
			}
		}

		sb.append(ChatColor.BLUE + LangManager.getMsgLang("FINISH_TOTAL", receiverLang).replace("%t", ChatColor.RESET + Utils.getTimeFromSec(total)));
		games.get(toSend).player.sendMessage(sb.toString());
	}
	
	/**
	 * Logs the game end result tab from a specific player
	 * 
	 * @param playerName	The player name whose game will be printed
	 */
	private static void logPlayer(String playerName) {
		long total = 0;
		Game game = games.get(playerName);
		String configLang = Config.getLang();
		StringBuilder sb = new StringBuilder();
		
		sb.append(playerName + ":").append("\n");
		sb.append(LangManager.getMsgLang("DEATH_COUNT", configLang).replace("%i", "" + game.deathCount)).append("\n");
		sb.append(LangManager.getMsgLang("SKIP_COUNT", configLang).replace("%i", "" + game.skipCount)).append("\n");
		sb.append(LangManager.getMsgLang("TEMPLATE_COUNT", configLang).replace("%i", "" + game.sbttCount)).append("\n");
		sb.append(LangManager.getMsgLang("TIME_TAB", configLang)).append("\n");

		for (int i = 0; i < Config.getLastAge().number; i++) {
			Age age = AgeManager.getAgeByNumber(i);

			if (game.times.get(age.name) == -1) {
				sb.append(LangManager.getMsgLang("FINISH_ABANDON", configLang).replace("%s", age.name.replace("_Age", ""))).append("\n");
			} else {
				sb.append(LangManager.getMsgLang("FINISH_TIME", configLang).replace("%s", age.name.replace("_Age", "")).replace("%t", Utils.getTimeFromSec(game.times.get(age.name) / 1000))).append("\n");
				total += game.times.get(age.name) / 1000;
			}
		}

		sb.append(LangManager.getMsgLang("FINISH_TOTAL", configLang).replace("%t", Utils.getTimeFromSec(total)));
		LogManager.getInstanceGame().logSystemMsg(sb.toString());
	}
	
	/**
	 * Resets players tab list names
	 */
	public static void clearPlayersListNames() {
		games.forEach((playerName, playerGame) -> playerGame.player.setPlayerListName(ChatColor.WHITE + playerName));
	}
	
	/**
	 * Resets players tab list names
	 */
	public static void resetPlayersListNames() {
		games.forEach((playerName, playerGame) -> {
			playerGame.player.setPlayerListName(ChatColor.WHITE + playerName);
			playerGame.score.setScore(1);
		});
	}
}
