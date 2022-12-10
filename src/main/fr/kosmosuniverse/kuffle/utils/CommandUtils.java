package main.fr.kosmosuniverse.kuffle.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.CraftManager;
import main.fr.kosmosuniverse.kuffle.core.GameHolder;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.ScoreManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import main.fr.kosmosuniverse.kuffle.type.KuffleType;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class CommandUtils {
	/**
	 * Private Utils constructor
	 * 
	 * @throws IllegalStateException
	 */
	private CommandUtils() {
		throw new IllegalStateException("Utility class");
	}
	
	/**
	 * Initialize command and return sender as Player
	 * 
	 * @param sender		The command sender that has to be a Player
	 * @param commandName	The command name
	 * @param isTyped		If True, will check if type is set up
	 * @param isStarted		If True, will check if game is started
	 * 
	 * @return the Player that made this command
	 * 
	 * @throws KuffleCommandFalseException if something is not good in initialization
	 */
	public static Player initCommand(CommandSender sender, String commandName, boolean isTyped, boolean checkStarted, boolean isStarted) throws KuffleCommandFalseException {
		if (!(sender instanceof Player)) {
			throw new KuffleCommandFalseException();
		}
		
		Player player = (Player) sender;

		LogManager.getInstanceSystem().logMsg(player.getName(), LangManager.getMsgLang("CMD_PERF", Config.getLang()).replace("<#>", "<" + commandName + ">"));

		if (!player.hasPermission(commandName)) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_ALLOWED", Config.getLang()));
			throw new KuffleCommandFalseException();
		}
		
		if (isTyped) {
			isTyped(player);
		}
		
		if (checkStarted) {
			isStarted(player, isStarted);
		}
		
		return player;
 	}
	
	/**
	 * Check if the game is already started
	 * 
	 * @param player	The player that made the command
	 * 
	 * @throws KuffleCommandFalseException thrown at command fail
	 */
	private static void isStarted(Player player, boolean isStarted) throws KuffleCommandFalseException {
		if (KuffleMain.getInstance().isStarted() != isStarted) {
			if (isStarted)
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_LAUNCHED", Config.getLang()));
			else
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_NOT_LAUNCHED", Config.getLang()));			
			
			throw new KuffleCommandFalseException();
		}
	}
	
	/**
	 * Check if the kuffle type is already set
	 * 
	 * @param player	The player that made the command
	 * 
	 * @throws KuffleCommandFalseException thrown at command fail
	 */
	private static void isTyped(Player player) throws KuffleCommandFalseException {
		if (KuffleMain.getInstance().getType().getType() == KuffleType.Type.NO_TYPE) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("KUFFLE_TYPE_NOT_CONFIG", Config.getLang()));
			throw new KuffleCommandFalseException();
		}
	}
	
	/**
	 * Saves the game
	 */
	public static void saveParty() {
		GameHolder holder = new GameHolder(Config.getHolder(),
				KuffleMain.getInstance().getType().getType().toString(),
				GameManager.getRanks(), KuffleMain.getInstance().getType().getXpMap());
		
		try (FileOutputStream fos = new FileOutputStream(KuffleMain.getInstance().getDataFolder().getPath() + File.separator + "Game.k")) {
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(holder);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			Utils.logException(e);
		}
		
		if (KuffleMain.getInstance().getType().getType() == KuffleType.Type.ITEMS) {
			CraftManager.removeCraftTemplates();
		}
		
		ScoreManager.clear();
		GameManager.clear();
		KuffleMain.getInstance().getGameLoop().kill();
		KuffleMain.getInstance().setPaused(false);
		KuffleMain.getInstance().setStarted(false);
	}
}
