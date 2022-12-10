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
public class KuffleTeamCreate implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		Player player = null;
		
		try {
			player = CommandUtils.initCommand(sender, "k-team-create", true, true, false);
		} catch (KuffleCommandFalseException e) {
			return false;
		}
		
		if (!Config.getTeam()) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_ENABLE", Config.getLang()));
			return true;
		}
		
		if (GameManager.getGames().size() > 0) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_LAUNCHED", Config.getLang()));
			return true;
		}
		
		if (args.length < 1 || args.length > 2) {
			return false;
		}
		
		if (TeamManager.getInstance().hasTeam(args[0])) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_EXISTS", Config.getLang()).replace("<#>", "<" + args[0] + ">"));
			return true;
		}
		
		if (args.length == 1) {
			TeamManager.getInstance().createTeam(args[0]);
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_CREATED", Config.getLang()).replace("<#>", "<" + args[0] + ">"));
		} else if (args.length == 2) {
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
			TeamManager.getInstance().createTeam(args[0], tmp);
			
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_CREATED", Config.getLang()).replace("<#>", "<" + args[0] + ">"));
		}
		
		return true;
	}

}
