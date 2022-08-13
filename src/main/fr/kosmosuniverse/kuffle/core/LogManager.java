package main.fr.kosmosuniverse.kuffle.core;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.bukkit.entity.Player;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class LogManager {
	private static LogManager instanceSystem = null;
	private static LogManager instanceGame = null;
	private String path = "";
	private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	
	/**
	 * Constructor
	 * 
	 * @param pathFile	Log file path
	 */
	public LogManager(String pathFile) {
		path = pathFile;
		Path pPath = Paths.get(pathFile);
		
		try {
			if (!Files.exists(pPath)) {
				Files.createFile(pPath);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the system LogManager
	 * 
	 * @return system log manager
	 */
	public static LogManager getInstanceSystem() {
		return instanceSystem;
	}
	
	/**
	 * Returns the game LogManager
	 * 
	 * @return game log manager
	 */
	public static LogManager getInstanceGame() {
		return instanceGame;
	}
	
	/**
	 * Setups system LogManager
	 * 
	 * @param pathFile	System log file path
	 */
	public static void setupInstanceSystem(String pathFile) {
		instanceSystem = new LogManager(pathFile);
	}
	
	/**
	 * Setups game LogManager
	 * 
	 * @param pathFile	Game log file path
	 */
	public static void setupInstanceGame(String pathFile) {
		instanceGame = new LogManager(pathFile);
	}
	
	/**
	 * Log a generic message
	 * 
	 * @param msg	The message to log
	 */
	public void logSystemMsg(String msg) {		
		try (FileWriter writer = new FileWriter(path, true)) { 
			LocalDateTime now = LocalDateTime.now();  
			
			writer.write(dtf.format(now) + " : [SYSTEM] -> " + msg + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Log a message sent by player or a player's command
	 * 
	 * @param name	The player name from whom the message comes
	 * @param msg	The message to log
	 */
	public void logMsg(String name, String msg) {
		try (FileWriter writer = new FileWriter(path, true)) {
			LocalDateTime now = LocalDateTime.now();  
			
			writer.write(dtf.format(now) + " : [" + name + "] -> " + msg + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Log and send a message to a specific player
	 * 
	 * @param to	The player to send the message
	 * @param msg	The message to send
	 */
	public void writeMsg(Player to, String msg) {
		to.sendMessage(msg);
		
		try (FileWriter writer = new FileWriter(path, true)) {
			LocalDateTime now = LocalDateTime.now();  
			
			writer.write(dtf.format(now) + " : [" + to.getName() + "] -> " + msg + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
