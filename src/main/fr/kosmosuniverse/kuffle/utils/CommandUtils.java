package main.fr.kosmosuniverse.kuffle.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.CraftManager;
import main.fr.kosmosuniverse.kuffle.core.GameHolder;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.ScoreManager;
import main.fr.kosmosuniverse.kuffle.core.TeamManager;
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
	
	public static ChatColor checkTeamColor(Player player, String color) {
		ChatColor tmp;
		
		if ((tmp = Utils.findChatColor(color)) == null) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("COLOR_NOT_EXISTS", Config.getLang()).replace("[#]", "[" + color + "]"));
			return null;
		}
		
		List<String> colorUsed = TeamManager.getInstance().getTeamColors();
		
		if (colorUsed.contains(tmp.name())) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("COLOR_ALREADY_USED", Config.getLang()).replace("[#]", "[" + tmp.name() + "]"));
			colorUsed.clear();
			return null;
		}
		
		colorUsed.clear();
		
		return tmp;
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
