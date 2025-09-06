package fr.kosmosuniverse.kuffle.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import fr.kosmosuniverse.kuffle.KuffleMain;
import fr.kosmosuniverse.kuffle.core.*;
import fr.kosmosuniverse.kuffle.type.KuffleType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class CommandUtils {
	/**
	 * Private Utils constructor
	 * 
	 * @throws IllegalStateException Utility class Exception
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
				Party.getInstance().getType().getType().toString(),
				Party.getInstance().getType().getXpMap(), Party.getInstance().getRanks().getPlayerRanks(),
				Config.getTeam() ? Party.getInstance().getRanks().getTeamRanks() : null,
				Party.getInstance().getRanks().getNextRanks());
		
		try (FileOutputStream fos = new FileOutputStream(KuffleMain.getInstance().getDataFolder().getPath() + File.separator + "Game.k")) {
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(holder);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			Utils.logException(e);
		}
		
		if (Party.getInstance().getType().getType() == KuffleType.Type.ITEMS) {
			CraftManager.removeCraftTemplates();
		}
		
		ScoreManager.clear();
		Party.getInstance().stop();
	}
}
