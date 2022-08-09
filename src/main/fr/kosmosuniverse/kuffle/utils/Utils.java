package main.fr.kosmosuniverse.kuffle.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Age;
import main.fr.kosmosuniverse.kuffle.core.AgeManager;
import main.fr.kosmosuniverse.kuffle.core.Game;
import main.fr.kosmosuniverse.kuffle.core.ItemManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
//import main.fr.kosmosuniverse.kuffle.crafts.activables.Template;
import main.fr.kosmosuniverse.kuffle.core.Logs;

/**
 * 
 * @author KosmosUniverse
 *
 */
public final class Utils {
	
	/**
	 * Private Utils constructor
	 * @throws IllegalStateException
	 */
	private Utils() {
		throw new IllegalStateException("Utility class");
	}
	
	/**
	 * Read file from stream and return String that represent the whole file
	 * 
	 * @param in	InputStream of the file
	 * 
	 * @return a String that contains the whole file content
	 * @throws IOException if InputStream.readLine() fails
	 */
	public static String readFileContent(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + System.lineSeparator());
        }

        return sb.toString();
	}

	/**
	 * Checks if a file exists at a specific path
	 * 
	 * @param path		The path to check
	 * @param fileName	The file name
	 * 
	 * @return True if the file exists, False instead
	 */
	public static boolean fileExists(String path, String fileName) {
		File tmp = new File(path + File.separator + fileName);

		return tmp.exists();
	}
	
	/**
	 * Checks if a path exists
	 * 
	 * @param path		The path to check
	 * 
	 * @return True if the path exists, False instead
	 */
	public static boolean fileExists(String path) {
		File tmp = new File(path);

		return tmp.exists();
	}

	/**
	 * Delete a file located at a specific path
	 * 
	 * @param path		The file location
	 * @param fileName	The file to delete
	 * 
	 * @return True if file is deleted, False instead
	 */
	public static boolean fileDelete(String path, String fileName) {
		try {
			Files.delete(Paths.get(path + File.separator + fileName));
		} catch (IOException e) {
			KuffleMain.systemLogs.logSystemMsg(e.getMessage());
			return false;
		}
		
		return true;
	}

	/**
	 * Checks if a file exists for the current version
	 * 
	 * @param fileName	The file to check
	 * 
	 * @return The file name if found, null instead
	 */
	public static String findFileExistVersion(String fileName) {
		String version = getVersion();
		String file = fileName.replace("%v", version);
		int versionNb = findVersionNumber(version);

		if (versionNb == -1) {
			return null;
		}

		while (KuffleMain.current.getResource(file)  == null && versionNb > 0) {
			versionNb -= 1;
			version = KuffleMain.versions.get(versionNb);
			file = fileName.replace("%v", version);
		}

		if (KuffleMain.current.getResource(file)  == null) {
			return null;
		}

		return file;
	}

	/**
	 * Get the number of players that have not finished the game.
	 * 
	 * @return the number of player that are still playing
	 */
	public static int playerLasts() {
		int notEnded = 0;

		for (String playerName : KuffleMain.games.keySet()) {
			if (!KuffleMain.games.get(playerName).getFinished()) {
				notEnded++;
			}
		}

		return notEnded;
	}

	/**
	 * Force the end of the game for all player that does not have finished yet
	 * 
	 * @param gameRank	The game rank for forced finished players
	 */
	public static void forceFinish(int gameRank) {
		for (String playerName : KuffleMain.games.keySet()) {
			if (!KuffleMain.games.get(playerName).getFinished()) {
				KuffleMain.games.get(playerName).finish(gameRank);
			}
		}
	}

	/**
	 * Load the game for a specific Player if he has a saved game
	 * 
	 * @param player	The player whose game will be loaded
	 */
	public static void loadGame(Player player) {
		JSONParser parser = new JSONParser();
		Game tmpGame = new Game(player);

		tmpGame.setup();

		try (FileReader reader = new FileReader(KuffleMain.current.getDataFolder().getPath() + File.separator + player.getName() + ".ki")) {
			JSONObject mainObject = (JSONObject) parser.parse(reader);

			tmpGame.setAge(Integer.parseInt(((Long) mainObject.get("age")).toString()));
			tmpGame.setCurrentItem((String) mainObject.get("current"));
			tmpGame.setTimeShuffle(System.currentTimeMillis() - (Long) mainObject.get("interval"));
			tmpGame.setTime(Integer.parseInt(((Long) mainObject.get("time")).toString()));
			tmpGame.setDead((boolean) mainObject.get("isDead"));
			tmpGame.setFinished((boolean) mainObject.get("finished"));
			tmpGame.setLose((boolean) mainObject.get("lose"));
			tmpGame.setItemCount(Integer.parseInt(((Long) mainObject.get("itemCount")).toString()));
			tmpGame.setTeamName((String) mainObject.get("teamName"));
			tmpGame.setAlreadyGot((JSONArray) mainObject.get("alreadyGot"));
			tmpGame.setSpawnLoc((JSONObject) mainObject.get("spawn"));
			tmpGame.setDeathLoc((JSONObject) mainObject.get("death"));
			tmpGame.setSameIdx(Integer.parseInt(((Long) mainObject.get("sameIdx")).toString()));
			tmpGame.setTimes((JSONObject) mainObject.get("times"));
			tmpGame.setDeathCount(Integer.parseInt(mainObject.get("deathCount").toString()));
			tmpGame.setSkipCount(Integer.parseInt(mainObject.get("skipCount").toString()));

			if (fileExists(KuffleMain.current.getDataFolder().getPath(), player.getName() + ".yml")) {
				tmpGame.loadInventory();
				fileDelete(KuffleMain.current.getDataFolder().getPath(), player.getName() + ".yml");
			}

			if (tmpGame.getDead()) {
				//KuffleMain.playerEvents.teleportAutoBack(tmpGame);
			}

			KuffleMain.games.put(player.getName(), tmpGame);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Calculates the amount of inventory rows from a specific number of slot
	 * 
	 * @param quantity	Slot quantity
	 * 
	 * @return the amount of rows
	 */
	public static int getNbInventoryRows(int quantity) {
		int rows = quantity / 9;
		
		if (quantity % 9 == 0) {
			rows = rows * 9;
		} else {
			rows = (rows + 1) * 9;
		}
		
		return rows;
	}

	/**
	 * Get the Head of a specific player
	 * 
	 * @param player The player of whom we take the head
	 * 
	 * @return the ItemStack corresponding to the specified player head
	 */
	public static ItemStack getHead(Player player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skull = (SkullMeta) item.getItemMeta();

        skull.setDisplayName(player.getName());
        skull.setOwningPlayer(player);
        item.setItemMeta(skull);

        return item;
    }
	
	/**
	 * Get the Head of a specific player and set its lore to a specific item
	 * 
	 * @param player		The player of whom we take the head
	 * @param currentItem	The item in which we will get the lore
	 * 
	 * @return the ItemStack corresponding to the specified player head
	 */
	public static ItemStack getHead(Player player, String currentItem) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        
        if (currentItem != null) {        
	        ItemMeta itM = item.getItemMeta();
	        
	        List<String> lore = new ArrayList<>();
	        lore.add(currentItem);
	
			itM.setLore(lore);
			item.setItemMeta(itM);
		}
		
		SkullMeta skull = (SkullMeta) item.getItemMeta();

        skull.setDisplayName(player.getName());
        skull.setOwningPlayer(player);
        item.setItemMeta(skull);

        return item;
    }

	/**
	 * Get the current Minecraft version
	 * 
	 * @return the version as a String
	 */
	public static String getVersion() {
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

		version = version.split("v")[1];
		version = version.split("_")[0] + "." + version.split("_")[1];

		return version;
	}

	/**
	 * Get ChatColor from a color String
	 * 
	 * @param color	String that represent a color
	 * 
	 * @return the ChatColor object if found, null instead
	 */
	public static ChatColor findChatColor(String color) {
		for (ChatColor item : ChatColor.values()) {
			if (item.name().equals(color)) {
				return item;
			}
		}

		return null;
	}

	/**
	 * Get the currently playing players list
	 * 
	 * @return the player list
	 */
	public static List<Player> getPlayerList() {
		List<Player> players = new ArrayList<>();

		for (String playerName : KuffleMain.games.keySet()) {
			players.add(KuffleMain.games.get(playerName).getPlayer());
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

		for (String playerName : KuffleMain.games.keySet()) {
			players.add(playerName);
		}

		return players;
	}

	/**
	 * Get the OverWorld
	 * 
	 * @return the world if found, null instead
	 */
	public static World findNormalWorld() {
		for (World w : Bukkit.getWorlds()) {
			if (!w.getName().contains("nether") && !w.getName().contains("the_end")) {
				return w;
			}
		}

		return null;
	}

	/**
	 * Checks if the specified effect exists
	 * 
	 * @param effect	The effect to check
	 * 
	 * @return True if effect exists, False instead
	 */
	public static boolean checkEffect(String effect) {
		for (PotionEffectType potion : PotionEffectType.values()) {
			if (potion.getName().equalsIgnoreCase(effect)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Setups the template items
	 */
	public static void setupTemplates() {
		/*List<Template> templates = new ArrayList<>();

		for (int i = 0; i < KuffleMain.config.getMaxAges(); i++)  {
			String name = AgeManager.getAgeByNumber(KuffleMain.ages, i).name;

			name = name.replace("_Age", "");
			templates.add(new Template(name, getMaterials(AgeManager.getAgeByNumber(KuffleMain.ages, i).name)));
		}

		for (Template t : templates) {
			KuffleMain.crafts.addCraft(t);
			KuffleMain.addRecipe(t.getRecipe());
		}*/
	}

	/**
	 * Removes the template items
	 */
	public static void removeTemplates() {
		for (int i = 0; i < KuffleMain.config.getMaxAges(); i++)  {
			String name = AgeManager.getAgeByNumber(KuffleMain.ages, i).name;
			
			name = name.replace("_Age", "");
			name = name + "Template";

			KuffleMain.removeRecipe(name);
			KuffleMain.crafts.removeCraft(name);
		}
	}

	/**
	 * Get Material list for templates in a specific Age
	 * 
	 * @param age	The specific Age
	 * 
	 * @return the material list
	 */
	public static List<Material> getMaterials(String age) {
		List<Material> compose = new ArrayList<>();
		List<String> done = new ArrayList<>();

		for (int cnt = 0; cnt < KuffleMain.config.getSBTTAmount(); cnt++) {
			done.add(ItemManager.newItem(done, KuffleMain.allSbtts.get(age)));
		}

		for (String item : done) {
			compose.add(Material.matchMaterial(item));
		}

		done.clear();
		return compose;
	}

	/**
	 * Reloads the templates
	 * 
	 * @param name	The old template name
	 * @param age	The Age for the new template
	 */
	public static void reloadTemplate(String name, String age) {
		/*KuffleMain.crafts.removeCraft(name);
		KuffleMain.removeRecipe(name);

		String tmp = age;

		tmp = tmp.replace("_Age", "");

		Template t = new Template(tmp, getMaterials(age));

		KuffleMain.crafts.addCraft(t);
		KuffleMain.addRecipe(t.getRecipe());

		KuffleMain.games.forEach((playerName, game) ->
			game.getPlayer().discoverRecipe(new NamespacedKey(KuffleMain.current, t.getName()))
		);*/
	}

	/**
	 * Get the time display from second amount
	 * 
	 * @param sec	The amount of seconds to convert
	 * 
	 * @return a String that represent time like "xxhxxmxxs"
	 */
	public static String getTimeFromSec(long sec) {
		StringBuilder sb = new StringBuilder();

		if (sec != 0 && sec >= 3600) {
			sb.append(sec / 3600);
			sb.append("h");

			sec = sec % 3600;
		}

		if (sec != 0 && sec >= 60) {
			sb.append(sec / 60);
			sb.append("m");

			sec = sec % 60;
		}

		sb.append(sec);
		sb.append("s");

		return sb.toString();
	}

	/**
	 * Compares two items by Material and if true, by name and/or by lore
	 * 
	 * @param first				The first item
	 * @param second			The second item
	 * @param hasItemMeta		If True, it compares names and/or lores
	 * @param hasDisplayName	If True, it compares names
	 * @param hasLore			If True, it compares lores
	 * 
	 * @return True if both items are the same, False instead
	 */
	public static boolean compareItems(ItemStack first, ItemStack second, boolean hasItemMeta, boolean hasDisplayName, boolean hasLore) {
		if ((first.getType() != second.getType()) || (first.hasItemMeta() != second.hasItemMeta()) || (first.hasItemMeta() != hasItemMeta)) {
			return false;
		}

		if (!hasItemMeta) {
			return true;
		}

		ItemMeta firstMeta = first.getItemMeta();
		ItemMeta secondMeta = second.getItemMeta();

		if (firstMeta.hasDisplayName() != secondMeta.hasDisplayName()) {
			return false;
		}

		if (firstMeta.hasDisplayName() != hasDisplayName) {
			return false;
		}

		if (!hasDisplayName) {
			return true;
		}

		if (!firstMeta.getDisplayName().equals(secondMeta.getDisplayName())) {
			return false;
		}

		if (firstMeta.hasLore() != secondMeta.hasLore()) {
			return false;
		}

		if (firstMeta.hasLore() != hasLore) {
			return false;
		}

		if (!hasLore) {
			return true;
		}

		List<String> firstLore = firstMeta.getLore();
		List<String> secondLore = secondMeta.getLore();

		if (firstLore.size() != secondLore.size()) {
			return false;
		}

		for (int i = 0; i < firstLore.size(); i++) {
			if (!firstLore.get(i).equals(secondLore.get(i))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Get the tag translated in the player's config language
	 * 
	 * @param player	The player of whom we take the language
	 * @param tag		The tag as String to translate
	 * 
	 * @return the translated tag as String
	 */
	public static String getLangString(String player, String tag) {
		if (KuffleMain.gameStarted && player != null && KuffleMain.games.containsKey(player)) {
			return (LangManager.findDisplay(KuffleMain.allLangs, tag, KuffleMain.games.get(player).getLang()));
		} else {
			return (LangManager.findDisplay(KuffleMain.allLangs, tag, KuffleMain.config.getLang()));
		}
	}

	/**
	 * Loads the file that contains all supported Minecraft versions
	 * 
	 * @param file	The file path to load
	 * 
	 * @return a map that contains all version as String ordered by key
	 */
	public static Map<Integer, String> loadVersions(String file) {
		Map<Integer, String> versions = null;

		try {
			InputStream in = KuffleMain.current.getResource(file);
			String content = Utils.readFileContent(in);

			JSONParser parser = new JSONParser();
			JSONObject result = ((JSONObject) parser.parse(content));

			in.close();

			versions = new HashMap<>();

			for (Object key : result.keySet()) {
				versions.put(Integer.parseInt(result.get(key).toString()), (String) key);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}

		return versions;
	}

	/**
	 * Finds version key from version value
	 * 
	 * @param version	The version value
	 * 
	 * @return the key if found, -1 instead
	 */
	public static int findVersionNumber(String version) {
		for (int key : KuffleMain.versions.keySet()) {
			if (KuffleMain.versions.get(key).equals(version)) {
				return key;
			}
		}

		return -1;
	}

	/**
	 * Logs and Prints game end result tab
	 */
	public static void printGameEnd() {
		KuffleMain.games.forEach((playerName, game) -> {
			logPlayer(playerName);

			KuffleMain.games.forEach((playerToSend, gameToSend) ->
				printPlayer(playerName, playerToSend)
			);
		});
	}

	/**
	 * Prints the game end result tab from a specific player
	 * 
	 * @param playerName	The player name whose game will be printed
	 * @param toSend		The player name that will receive this print
	 */
	public static void printPlayer(String playerName, String toSend) {
		long total = 0;
		Game game = KuffleMain.games.get(playerName);

		KuffleMain.games.get(toSend).getPlayer().sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + playerName + ChatColor.RESET + ":");
		KuffleMain.games.get(toSend).getPlayer().sendMessage(ChatColor.BLUE + Utils.getLangString(toSend, "DEATH_COUNT").replace("%i", "" + ChatColor.RESET + game.getDeathCount()));
		KuffleMain.games.get(toSend).getPlayer().sendMessage(ChatColor.BLUE + Utils.getLangString(toSend, "SKIP_COUNT").replace("%i", "" + ChatColor.RESET + game.getSkipCount()));
		KuffleMain.games.get(toSend).getPlayer().sendMessage(ChatColor.BLUE + Utils.getLangString(toSend, "TEMPLATE_COUNT").replace("%i", "" + ChatColor.RESET + game.getSbttCount()));
		KuffleMain.games.get(toSend).getPlayer().sendMessage(ChatColor.BLUE + Utils.getLangString(toSend, "TIME_TAB"));

		for (int i = 0; i < KuffleMain.config.getMaxAges(); i++) {
			Age age = AgeManager.getAgeByNumber(KuffleMain.ages, i);

			if (game.getAgeTime(age.name) == -1) {
				KuffleMain.games.get(toSend).getPlayer().sendMessage(Utils.getLangString(toSend, "FINISH_ABANDON").replace("%s", age.color + age.name.replace("_Age", "") + ChatColor.RESET));
			} else {
				KuffleMain.games.get(toSend).getPlayer().sendMessage(Utils.getLangString(toSend, "FINISH_TIME").replace("%s", age.color + age.name.replace("_Age", "") + ChatColor.BLUE).replace("%t", ChatColor.RESET + Utils.getTimeFromSec(game.getAgeTime(age.name) / 1000)));
				total += game.getAgeTime(age.name) / 1000;
			}
		}

		KuffleMain.games.get(toSend).getPlayer().sendMessage(ChatColor.BLUE + Utils.getLangString(toSend, "FINISH_TOTAL").replace("%t", ChatColor.RESET + Utils.getTimeFromSec(total)));

	}

	/**
	 * Logs the game end result tab from a specific player
	 * 
	 * @param playerName	The player name whose game will be printed
	 */
	public static void logPlayer(String playerName) {
		long total = 0;
		Game game = KuffleMain.games.get(playerName);
		StringBuilder sb = new StringBuilder();

		sb.append("\n");
		sb.append(ChatColor.GOLD + "" + ChatColor.BOLD + playerName + ChatColor.RESET + ":" + "\n");
		sb.append(ChatColor.BLUE + Utils.getLangString(playerName, "DEATH_COUNT").replace("%i", "" + ChatColor.RESET + game.getDeathCount()) + "\n");
		sb.append(ChatColor.BLUE + Utils.getLangString(playerName, "SKIP_COUNT").replace("%i", "" + ChatColor.RESET + game.getSkipCount()) + "\n");
		sb.append(ChatColor.BLUE + Utils.getLangString(playerName, "TEMPLATE_COUNT").replace("%i", "" + ChatColor.RESET + game.getSbttCount()) + "\n");
		sb.append(ChatColor.BLUE + Utils.getLangString(playerName, "TIME_TAB") + "\n");

		for (int i = 0; i < KuffleMain.config.getMaxAges(); i++) {
			Age age = AgeManager.getAgeByNumber(KuffleMain.ages, i);

			if (game.getAgeTime(age.name) == -1) {
				sb.append(Utils.getLangString(playerName, "FINISH_ABANDON").replace("%s", age.color + age.name.replace("_Age", "") + ChatColor.RESET) + "\n");
			} else {
				sb.append(Utils.getLangString(playerName, "FINISH_TIME").replace("%s", age.color + age.name.replace("_Age", "") + ChatColor.BLUE).replace("%t", ChatColor.RESET + Utils.getTimeFromSec(game.getAgeTime(age.name) / 1000)) + "\n");
				total += game.getAgeTime(age.name) / 1000;
			}
		}

		sb.append(ChatColor.BLUE + Utils.getLangString(playerName, "FINISH_TOTAL").replace("%t", ChatColor.RESET + Utils.getTimeFromSec(total)) + "\n");

		Logs.getInstanceSystem(null).logSystemMsg(sb.toString());
	}
	
	/**
	 * Logs an Exception
	 * 
	 * @param e	The exception to log its StackTrace
	 */
	public static void logException(Exception e) {
		StringBuilder sb = new StringBuilder();
		
		for (StackTraceElement ste : e.getStackTrace()) {
			sb.append(ste).append("\n");
		}
		
		Logs.getInstanceSystem(null).logSystemMsg(sb.toString());
	}
}
