package main.fr.kosmosuniverse.kuffle.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.json.simple.JSONObject;

import main.fr.kosmosuniverse.kuffle.utils.Utils;
import net.md_5.bungee.api.ChatColor;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class GameManager {
	private static Map<String, Game> games = new HashMap<>();
	private static Map<String, Integer> playersRanks = null;
	private static Inventory playersHeads = null;
	
	
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
		playersRanks = new HashMap<>();
		Utils.setupLists();
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
		
		if (games.containsKey(player.getName())) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("PLAYER_ALREADY_LIST", Config.getLang()));
			ret = false;
		} else {
			games.put(player.getName(), new Game(player));
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("ADDED_ONE_LIST", Config.getLang()));
			ret = true;
		}
		
		return ret;
	}
	
	/**
	 * Adds players to the game list
	 * 
	 * @param players	The players list to add
	 * 
	 * @return the number of player successfuly added to the list
	 */
	public static int addPlayers(List<Player> players) {
		int cnt = 0;
		
		for (Player player : players) {
			if (!games.containsKey(player.getName())) {
				games.put(player.getName(), new Game(player));
				cnt++;
			}
		}
		
		return cnt;
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
			ret = false;
		} else {
			if (Config.getTeam() && TeamManager.getInstance().isInTeam(player)) {
				TeamManager.getInstance().removePlayer(TeamManager.getInstance().findTeamByPlayer(player).getName(), games.get(player).getPlayer());
			}
			
			games.get(player).clear();
			games.remove(player);
			ret = true;
		}
		
		return ret;
	}
	
	/**
	 * Resets the games map
	 */
	public static void resetList() {
		if (games != null) {
			games.forEach((k, v) -> v.clear());
			games.clear();
		}
	}
	
	/**
	 * Checks if games map contains a specific player
	 * 
	 * @param player	The player to check
	 * 
	 * @return True if games has player key, False instead
	 */
	public static boolean hasPlayer(String player) {
		return games.containsKey(player);
	}
	
	/**
	 * Checks if player has already finished
	 * 
	 * @param player	The player
	 * 
	 * @return True if player has finished, False instead
	 */
	public static boolean hasPlayerFinished(String player) {
		return games.get(player).isFinished();
	}
	
	/**
	 * Gets the games map
	 * 
	 * @return the games map as an unmodifiable map
	 */
	public static Map<String, Game> getGames() {
		return games == null ? null : Collections.unmodifiableMap(games);
	}
	
	/**
	 * Get the currently playing players list
	 * 
	 * @return the player list
	 */
	public static List<Player> getPlayerList() {
		List<Player> players = new ArrayList<>();

		for (String playerName : games.keySet()) {
			players.add(games.get(playerName).getPlayer());
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
		return games.get(player).getSpawnLoc();
	}
	
	/**
	 * Clears Player's game related effects and data
	 * 
	 * @param player	The player to clear
	 */
	public static void stopPlayer(String player) {
		stopPlayer(games.get(player));
	}
	
	/**
	 * Clears Player's game related effects and data
	 * 
	 * @param game	The game of player to clear
	 */
	public static void stopPlayer(Game game) {
		for (PotionEffect pe : game.getPlayer().getActivePotionEffects()) {
			game.getPlayer().removePotionEffect(pe.getType());
		}

		resetPlayerBar(game);
		game.clear();
	}
	
	/**
	 * Saves players data into files
	 * 
	 * @param path	The location in which files will be generated
	 */
	public static void savePlayers(String path) {
		games.forEach((playerName, playerGame) -> {
			savePlayer(path, playerGame);
			
			stopPlayer(playerGame);
		});
	}

	/**
	 * Save Player game in a file
	 * 
	 * @param path		File Path
	 * @param player	The player linked to the game to save
	 */
	public static void savePlayer(String path, String player) {
		savePlayer(path, games.get(player));
	}
	
	/**
	 * Save Player game in a file
	 * 
	 * @param path	File Path
	 * @param game	The game to save
	 */
	public static void savePlayer(String path, Game game) {
		try (FileOutputStream fos = new FileOutputStream(path + File.separator + game.getPlayer().getName() + ".k")) {
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(game);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			Utils.logException(e);
		}
	}
	
	public static void loadPlayers(String path) throws IOException, ClassNotFoundException {
		List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
		
		for (Player player : players) {
			if (Utils.fileExists(path, player.getName() + ".k")) {
				loadPlayerGame(path, player);
			}
		}
		
		players.clear();
	}
	
	public static void loadPlayer(String path, Player player) throws IOException, ClassNotFoundException {
		try (FileInputStream fos = new FileInputStream(path + File.separator + player.getName() + ".k")) {
			ObjectInputStream ois = new ObjectInputStream(fos);
			
			Game game = (Game) ois.readObject();
			ois.close();
			
			game.setupPostLoad(player);
			games.put(player.getName(), game);
		}
	}
	
	/**
	 * Loads a new Game for a specific Player
	 * 
	 * @param player	The player that will be loaded
	 * 
	 * @throws IOException if FileReader fails
	 * @throws ClassNotFoundException 
	 */
	public static void loadPlayerGame(String path, Player player) throws IOException, ClassNotFoundException {
		
		loadPlayer(path, player);
		
		String playerName = player.getName();
		Game game = games.get(playerName);
		
		updatePlayerDisplayTarget(game);		
		updatePlayersHeads();

		if (game.isDead()) {
			teleportAutoBack(playerName);
		}
		
		if (game.isFinished()) {
			game.setGameRank(playersRanks.get(playerName));
		}
		
		game.updatePlayerBar();
		reloadPlayerEffects(playerName);
		game.updatePlayerListName();
	}
	
	public static void updatePlayerDisplayTarget(Game tmpGame) {
		if (tmpGame.getCurrentTarget() == null) {
			return ;
		}

		tmpGame.setTimeShuffle(System.currentTimeMillis());
		
		if (Config.getDouble()) {
			tmpGame.setTargetDisplay(LangManager.getTargetLang(tmpGame.getCurrentTarget().split("/")[0], tmpGame.getConfigLang()) + "/" + LangManager.getTargetLang(tmpGame.getCurrentTarget().split("/")[1], tmpGame.getConfigLang()));
		} else {
			tmpGame.addAlreadyGot(tmpGame.getCurrentTarget());
			
			tmpGame.setTargetDisplay(LangManager.getTargetLang(tmpGame.getCurrentTarget(), tmpGame.getConfigLang()));
		}
		
		updatePlayersHeadData(tmpGame.getPlayer().getName(), tmpGame.getTargetDisplay());
	}
	
	/**
	 * Setup basics variables for a player
	 * 
	 * @param player	The Player that will be setup
	 */
	public static void setupPlayer(String player) {
		games.get(player).setupPlayer();
	}
	
	/**
	 * Reset player's BossBar
	 * 
	 * @param game	The player's game
	 */
	public static void resetPlayerBar(Game game) {
		if (game.getAgeDisplay() != null && game.getAgeDisplay().getPlayers().size() != 0) {
			game.getAgeDisplay().removeAll();
			game.setAgeDisplay(null);
		}
	}
	
	/**
	 * Player used Sbtt
	 * 
	 * @param playerName	The name of player that used sbtt
	 */
	public static void playerFoundSBTT(String playerName) {
		games.get(playerName).playerFoundTarget(true);
	}
	
	/**
	 * Player found its target
	 * 
	 * @param playerName	The name of player that found
	 */
	public static void playerFoundTarget(String playerName) {
		games.get(playerName).playerFoundTarget(false);
	}
	
	/**
	 * Player goes to the next Age
	 * 
	 * @param player	The name of the player that is moving to the next Age
	 */
	public static void nextPlayerAge(String player) {
		games.get(player).nextPlayerAge();
		games.forEach((playerName, playerGame) -> playerGame.getPlayer().sendMessage(LangManager.getMsgLang("AGE_MOVED", playerGame.getConfigLang()).replace("<#>", ChatColor.BLUE + "<" + ChatColor.GOLD + playerName + ChatColor.BLUE + ">").replace("<##>", "<" + AgeManager.getAgeByNumber(playerGame.getAge()).getColor() + AgeManager.getAgeByNumber(playerGame.getAge()).getName().replace("_Age", "") + ChatColor.BLUE + ">")));
	}
	
	/**
	 * Makes a Player finish with specific rank
	 * 
	 * @param playerName	The name of player that finished
	 * @param rank			The player rank
	 */
	public void finish(String playerName, int rank) {
		if (Config.getTeam()) {
			int tmpRank;

			if ((tmpRank = checkTeamMateRank(games.get(playerName).getTeamName())) != -1) {
				rank = tmpRank;
			}
		}
		playersRanks.put(playerName, rank);
		games.get(playerName).finish(rank);
		
		updatePlayersHeadData(playerName, null);
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
		
		Optional<Entry<String, Game>> teamMate = games.entrySet().stream()
			.filter(entry -> entry.getValue().getTeamName().equals(teamName))
			.filter(entry -> entry.getValue().getGameRank() != -1)
			.findFirst();
		
		if (teamMate.isPresent()) {
			tmp = teamMate.get().getValue().getGameRank();
		}

		return tmp;
	}
	
	/**
	 * Set player BossBar color randomly
	 * 
	 * @param playerName	The playerName for which it will change game bossBar color
	 */
	public static void playerRandomBarColor(String playerName) {
		games.get(playerName).playerRandomBarColor();
	}
	
	/**
	 * Skip target for a specific player
	 * 
	 * @param playerName	The player form whom the target will be skipped
	 * @param malus			If True a malus of 1 target is applied
	 * 
	 * @return True if target skipped, False instead
	 */
	public static boolean skipPlayerTarget(String playerName, boolean malus) {
		return games.get(playerName).skipPlayerTarget(malus);
	}
	
	/**
	 * Gives effects to a player depending on his current Age
	 * 
	 * @param playerName	The player that will benefit of the effects
	 */
	public static void reloadPlayerEffects(String playerName) {
		games.get(playerName).reloadPlayerEffects();
	}
	
	/**
	 * Store actual player inventory into another one
	 * 
	 * @param playerName	The player for whom the inventory will be saved
	 */
	public static void storePlayerInv(String playerName) {
		games.get(playerName).storePlayerInv();
	}

	/**
	 * Give to a player the content of its saved inventory
	 * 
	 * @param playerName	The player that will receive the inventory items
	 */
	public static void restorePlayerInv(String playerName) {
		games.get(playerName).restorePlayerInv();
	}
	
	/**
	 * Gets the player age as Age object
	 * 
	 * @param player	The player from who it will take the age
	 * 
	 * @return The player's Age object
	 */
	public static Age getPlayerAge(String player) {
		return AgeManager.getAgeByNumber(games.get(player).getAge());
	}
	
	/**
	 * Gets the alreadyGot list of a specific player 
	 * 
	 * @param playerName	The player in which it takes the list
	 * 
	 * @return the list as an unmodifiable list
	 */
	public static List<String> getPlayerAlreadyGot(String playerName) {
		return games.get(playerName).getAlreadyGot();
	}
	
	/**
	 * Sets the game lose for a specific player
	 * 
	 * @param player	The player
	 * @param gameLose	The lose state, True if player lose
	 */
	public static void setLose(String player, boolean gameLose) {
		games.get(player).setLose(gameLose);
	}
	
	/**
	 * Player Died
	 * 
	 * @param playerName	The player that died
	 * @param deathLoc		The death Location
	 */
	public static void playerDied(String playerName, Location deathLoc) {
		games.get(playerName).playerDied(deathLoc);
	}

	/**
	 * Sets the Team name
	 * 
	 * @param player	The player
	 * @param team		The team name to set
	 */
	public static void setPlayerTeamName(String player, String team) {
		games.get(player).setTeamName(team);
	}

	/**
	 * Sets the player lang
	 * 
	 * @param playerName	The player
	 * @param lang		The lang to set
	 */
	public static void setPlayerLang(String playerName, String lang) {
		games.get(playerName).setPlayerLang(lang);
	}

	/**
	 * Adds a target to the alreadyGot list of a specific player's game
	 * 
	 * @param playerName	The name of player for whom the target will be added
	 * @param target		The target to add
	 */
	public static void addToAlreadyGot(String playerName, String target) {
		games.get(playerName).addAlreadyGot(target);
	}

	/**
	 * Removes target from the alreadyGot list of a specific player's game
	 * 
	 * @param playerName	The name of player for whom the target will be removed
	 * @param target		The target to remove
	 */
	public static void removeFromList(String playerName, String target) {
		games.get(playerName).removeAlreadyGot(target);
	}

	/**
	 * Resets alreadyGot list for a specific player's gama
	 * 
	 * @param playerName	The name of player for whome the taregt list will be reset
	 */
	public static void resetPlayerList(String playerName) {
		games.get(playerName).resetAlreadyGot();
	}
	
	/**
	 * Updates players heads in playersHeads inventory
	 */
	public static void updatePlayersHeads() {
		int slots = Utils.getNbInventoryRows(games.size());
		Inventory newInv = Bukkit.createInventory(null, slots == 0 ? 9 : slots, ChatColor.BLACK + "Players");
		
		for (String playerName : games.keySet()) {
			newInv.addItem(Utils.getHead(games.get(playerName).getPlayer(), games.get(playerName).getTargetDisplay()));
		}
		
		if (playersHeads != null) {
			playersHeads.clear();
		}
		
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
		games.forEach((playerName, playerGame) -> playerGame.getPlayer().sendMessage(msg));
	}
	
	/**
	 * Teleport a specific player to his death location
	 * 
	 * @param playerName	The player to teleport
	 */
	public static void teleportAutoBack(String playerName) {
		games.get(playerName).teleportAutoBack();
	}
	
	/**
	 * Give to a players one or more Potion Effects
	 * 
	 * @param playerName	The player that will receive effects
	 * @param effects	The effect(s) he will receive
	 */
	public static void giveEffectsToPlayer(String playerName, PotionEffect... effects) {
		games.get(playerName).giveEffectsToPlayer(effects);
	}
	
	/**
	 * Setup scores for a specific player
	 * 
	 * @param playerName	The player for whom the score will be set
	 * @param scoreboard	The scoreboard to apply to the player
	 * @param score			The score to apply
	 */
	public static void setupPlayerScores(String playerName, Scoreboard scoreboard, Score score) {
		games.get(playerName).setupPlayerScores(scoreboard, score);
	}
	
	/**
	 * Get the number of players that have not finished the game.
	 * 
	 * @return the number of player that are still playing
	 */
	public static int getNbPlayerStillPlaying() {
		return GameManager.games.size() - (int) games.entrySet().stream()
				.filter(entry -> entry.getValue().isFinished())
				.count();
	}
	
	/**
	 * Fore finish for last player
	 * 
	 * @param rank	The rank to set to the last player
	 * 
	 * @return
	 */
	public static boolean finishLast(int rank) {
		boolean ret = false;
		
		if (getNbPlayerStillPlaying() == 1) {
			games.entrySet().stream()
			.filter(entry -> entry.getValue().isFinished())
			.findFirst()
			.ifPresent(entry -> entry.getValue().finish(rank));
			
			ret = true;
		}
		
		return ret;
	}
	
	/**
	 * Finish an Age for a player
	 * 
	 * @param player	The player
	 */
	public static void finishAge(String player) {
		games.get(player).finishAge(player);
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
		games.get(toSend).getPlayer().sendMessage(games.get(playerName).playerString(games.get(toSend).getConfigLang()));
	}
	
	/**
	 * Logs the game end result tab from a specific player
	 * 
	 * @param playerName	The player name whose game will be printed
	 */
	private static void logPlayer(String playerName) {
		LogManager.getInstanceGame().logSystemMsg(games.get(playerName).logString());
	}
	
	/**
	 * Resets players tab list names
	 */
	public static void clearPlayersListNames() {
		games.forEach((playerName, playerGame) -> playerGame.getPlayer().setPlayerListName(ChatColor.WHITE + playerName));
	}
	
	/**
	 * Resets players tab list names
	 */
	public static void resetPlayersListNames() {
		games.forEach((playerName, playerGame) -> {
			playerGame.getPlayer().setPlayerListName(ChatColor.WHITE + playerName);
			playerGame.getScore().setScore(1);
		});
	}
	
	/**
	 * Checks if a specific target is the target of this player
	 * 
	 * @param playerName	The player
	 * @param target		The target to check
	 * 
	 * @return True if @target is the @player's target
	 */
	public static boolean checkPlayerTarget(String playerName, ItemStack target) {
		return games.get(playerName).checkPlayerTarget(target);
	}
	
	/**
	 * Teleports a specific player to a target player
	 * 
	 * @param player		The player to teleport
	 * @param targetPlayer	The target for teleportation
	 */
	public static void teleportPlayerToPlayer(Player player, String targetPlayer) {
		player.teleport(games.get(targetPlayer).getPlayer());
	}
	
	/**
	 * Apply behavior to a player
	 * 
	 * @param player	The player that will follow the behavior
	 * @param loop		The behavior to follow
	 */
	public static void applyToPlayer(String player, Consumer<Game> loop) {
		loop.accept(games.get(player));
	}
	
	/**
	 * Apply behavior to all players.
	 * 
	 * @param loop	The actions to apply on all players as Lambda
	 */
	public static void applyToPlayers(Consumer<Game> loop) {
		games.forEach((playerName, playerGame) -> loop.accept(playerGame));
	}
	
	/**
	 * Apply behavior to all players.
	 * 
	 * @param loop	The actions to apply on all players as Lambda
	 */
	public static void applyToPlayers(Object object, BiConsumer<Game, Object> loop) {
		games.forEach((playerName, playerGame) -> loop.accept(playerGame, object));
	}
	
	/**
	 * Gets the best rank from playerRanks map
	 * 
	 * @return the best rank
	 */
	public static int getBestRank() {
		int cntRank = 1;

		while (cntRank <= playersRanks.size() && playersRanks.containsValue(cntRank)) {
			cntRank++;
		}

		return cntRank;
	}

	/**
	 * Gets the worst rank from playerRanks map
	 * 
	 * @return the worst rank
	 */
	public static int getWorstRank() {
		int cntRank = playersRanks.size();

		while (cntRank > 1 && playersRanks.containsValue(cntRank)) {
			cntRank--;
		}

		return cntRank;
	}
	
	/**
	 * Gets the lang for a specific player
	 * 
	 * @param player	The player
	 * 
	 * @return the lang as String the player chosen
	 */
	public static String getPlayerLang(String player) {
		return games.get(player).getConfigLang();
	}
	
	/**
	 * Display the player in game list
	 * 
	 * @param player	the player that ask for display
	 */
	public static void displayList(Player player) {
		if (games != null && games.size() == 0) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NO_PLAYERS", Config.getLang()));
		} else if (games != null){
			StringBuilder sb = new StringBuilder();
			int i = 0;

			for (String playerName : games.keySet()) {
				if (i == 0) {
					sb.append(playerName);
				} else {
					sb.append(", ").append(playerName);
				}

				i++;
			}

			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("PLAYER_LIST", Config.getLang()) + " " + sb.toString());
		}
	}
	
	/**
	 * Gets the inventory with all players heads and game infos
	 * 
	 * @return the inventory
	 */
	public static Inventory getPlayersHeadsInventory() {
		return playersHeads;
	}
	
	/**
	 * Setups the playersRanks map
	 */
	public static void setupPlayersRanks() {
		playersRanks.clear();
		
		if (Config.getTeam()) {
			TeamManager.getInstance().getTeams().forEach(team -> playersRanks.put(team.getName(), 0));
		} else {
			games.forEach((playerName, playerGame) -> playersRanks.put(playerName, 0));
		}
	}
	
	/**
	 * Adds a player to the ranks map
	 * 
	 * @param player	The player to add
	 */
	public static void addToPlayersRanks(String player) {
		Game game = games.get(player);
		
		if (game != null) {
			if (Config.getTeam() && !playersRanks.containsKey(game.getTeamName())) {
				playersRanks.put(game.getTeamName(), 0);
			} else if (!Config.getTeam()) {
				playersRanks.put(player, 0);
			}
		}
	}
	
	/**
	 * Checks if all players have a team
	 * 
	 * @return True if all players are in a team, False instead
	 */
	public static boolean checkTeams() {
		for (String playerName : games.keySet()) {
			if (!TeamManager.getInstance().isInTeam(playerName)) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Gets the target of a specific player
	 * 
	 * @param player	The player from whom it will take the target
	 * 
	 * @return the player's target
	 */
	public static String getPlayerTarget(String player) {
		return games.get(player).getCurrentTarget();
	}
	
	/**
	 * Gets Player object by player name
	 * 
	 * @param player	The player name that will be searched
	 * 
	 * @return the player object
	 */
	public static Player getPlayer(String player) {
		return games.get(player).getPlayer();
	}

	/**
	 * Gets a JSONObject that represent actual players ranks
	 * 
	 * @return the JSONObject
	 */
	@SuppressWarnings("unchecked")
	public static JSONObject saveRanks() {
		JSONObject rankObj = new JSONObject();
		
		for (Map.Entry<String, Integer> entry : playersRanks.entrySet()) {
			if ((!Config.getTeam() && hasPlayer(entry.getKey())) ||
			(Config.getTeam() && TeamManager.getInstance().hasTeam(entry.getKey()))) {
				rankObj.put(entry.getKey(), entry.getValue());
			}
		}
		
		return rankObj;
	}
	
	public static void loadRanks(Map<String, Integer> ranks) {
		if (playersRanks == null) {
			playersRanks = new HashMap<>();
		}
		
		if (playersRanks.size() != 0) {
			playersRanks.clear();
		}
		
		ranks.forEach((k, v) -> playersRanks.put(k, v));
	}
	
	/**
	 * Loads ranks from JSONObject
	 * 
	 * @param ranksObj	The JSON object that represent saved ranks
	 */
	public static void loadRanks(JSONObject ranksObj) {
		for (Object key : ranksObj.keySet()) {
			String rankName = (String) key;
			int rank = Integer.parseInt(ranksObj.get(key).toString());
			
			playersRanks.put(rankName, rank);
		}
	}
	
	/**
	 * Gets the players ranks map
	 * 
	 * @return the ranks map
	 */
	public static Map<String, Integer> getRanks() {
		return playersRanks;
	}
}
