package main.fr.kosmosuniverse.kuffle.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.TeamManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import main.fr.kosmosuniverse.kuffle.utils.CommandUtils;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleTeamColor implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		Player player = null;
		
		try {
			player = CommandUtils.initCommand(sender, "k-team-color", true, true, false);
		} catch (KuffleCommandFalseException e) {
			return false;
		}
		
		if (GameManager.getGames().size() > 0) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_LAUNCHED", Config.getLang()));
			return true;
		}
		
		if (args.length < 1 || args.length > 2) {
			return false;
		}
		
		if (!TeamManager.getInstance().hasTeam(args[0])) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_NOT_EXISTS", Config.getLang()).replace("<#>", "<" + args[0] + ">"));
			return true;
		}
		if (TeamManager.getInstance().getTeam(args[0]).hasPlayer(args[1])) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_PLAYER", Config.getLang()));
			return true;
		}
		
		ChatColor tmp;
		
		if ((tmp = Utils.findChatColor(args[1])) == null) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("COLOR_NOT_EXISTS", Config.getLang()).replace("[#]", "[" + args[1] + "]"));
			return true;
		}
		
		List<String> colorUsed = TeamManager.getInstance().getTeamColors();
		
		if (colorUsed.contains(tmp.name())) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("COLOR_ALREADY_USED", Config.getLang()).replace("[#]", "[" + tmp.name() + "]"));
			colorUsed.clear();
			return true;
		}
		
		colorUsed.clear();

		String tmpColor = TeamManager.getInstance().getTeam(args[0]).getColor().name();
		
		TeamManager.getInstance().changeTeamColor(args[0], tmp);	
		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("COLOR_CHANGED", Config.getLang()).replace("[#]", "[" + tmpColor + "]").replace("[##]", "[" + tmp.name() + "]").replace("<#>",	"<" + args[0] + ">"));
		
		return true;
	}

}
