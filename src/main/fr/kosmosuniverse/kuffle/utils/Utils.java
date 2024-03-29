package main.fr.kosmosuniverse.kuffle.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffectType;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.VersionManager;

/**
 * 
 * @author KosmosUniverse
 *
 */
public final class Utils {
	private static List<Material> exceptions;
	private static List<Location> signs = null;
	
	/**
	 * Private Utils constructor
	 * 
	 * @throws IllegalStateException
	 */
	private Utils() {
		throw new IllegalStateException("Utility class");
	}
	
	public static void setupLists() {
		exceptions = new ArrayList<>();
		signs = new ArrayList<>();
		
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
	 * Read file from stream and return String that represent the whole file
	 * 
	 * @param in	InputStream of the file
	 * 
	 * @return a String that contains the whole file content
	 * 
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
			logException(e);
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
		String version = VersionManager.getVersion();
		String file = fileName.replace("%v", version);
		int versionNb = VersionManager.getVersionByValue(version);

		if (versionNb == -1) {
			return null;
		}

		while (KuffleMain.getInstance().getResource(file) == null && versionNb > 0) {
			versionNb -= 1;
			version = VersionManager.getVersionByIndex(versionNb);
			file = fileName.replace("%v", version);
		}

		if (KuffleMain.getInstance().getResource(file)  == null) {
			return null;
		}

		return file;
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
		
		if (quantity % 9 == 0 && rows != 0) {
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
	 * Capitalize a string
	 * 
	 * @param str	The string to capitalize
	 * 
	 * @return the capitablized string
	 */
	public static String capitalize(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	/**
	 * Logs an Exception
	 * 
	 * @param e	The exception to log its StackTrace
	 */
	public static void logException(Exception e) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(e.getMessage()).append("\n");
		
		for (StackTraceElement ste : e.getStackTrace()) {
			sb.append(ste).append("\n");
		}
		
		LogManager.getInstanceSystem().logSystemMsg(sb.toString());
	}
	
	/**
	 * Gets the Player object by its name
	 * 
	 * @param name	The player name to search for
	 * 
	 * @return The player Object if found, null instead
	 */
	public static Player searchPlayerByName(String name) {
		List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
		Player retPlayer = null;

		for (Player player : players) {
			if (player.getName().equals(name)) {
				retPlayer = player;
			}
		}

		players.clear();

		return retPlayer;
	}
	
	/**
	 * Changes the location Y value to highest block, Or 61 if no block
	 * 
	 * @param loc
	 */
	public static void changeLocForEnd(Location loc) {
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
	public static void createSafeBox(Location loc, String playerName) {
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
			
			sign.setLine(0, "[" + KuffleMain.getInstance().getName() + "]");
			sign.setLine(1, LangManager.getMsgLang("HERE_DIES", Config.getLang()));
			sign.setLine(2, playerName);
			sign.update(true);
			
			signs.add(loc);
		}
	}
	
	/**
	 * Checks if there is a sign at this location
	 * 
	 * @param loc	The location to check
	 * 
	 * @return True if there is a plugin sign at this location
	 */
	public static boolean checkSign(Location loc) {
		return signs.contains(loc);
	}
}
