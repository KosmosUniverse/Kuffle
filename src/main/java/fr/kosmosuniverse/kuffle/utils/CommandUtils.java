package fr.kosmosuniverse.kuffle.utils;

import java.util.List;

import fr.kosmosuniverse.kuffle.core.*;
import fr.kosmosuniverse.kuffle.datamanagers.LangManager;
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
}
