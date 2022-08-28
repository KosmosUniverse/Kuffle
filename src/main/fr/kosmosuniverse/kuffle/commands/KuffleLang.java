package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleLang implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		if (args.length > 1) {
			return false; 
		}
		
		Player player = (Player) sender;
		
		LogManager.getInstanceSystem().logMsg(player.getName(), LangManager.getMsgLang("CMD_PERF", Config.getLang()).replace("<#>", "<ki-lang>"));
		
		if (!player.hasPermission("ki-lang")) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_ALLOWED", Config.getLang()));
			return false;
		}
		
		if (!KuffleMain.gameStarted) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_PLAYING", Config.getLang()));
			return true;
		}
		
		if (!GameManager.hasPlayer(player.getName())) {
			return true;
		}
		
		if (args.length == 0) {
			LogManager.getInstanceSystem().writeMsg(player, GameManager.getPlayerLang(player.getName()));
		} else if (args.length == 1) {
			String lang = args[0].toLowerCase();
			
			if (LangManager.hasLang(lang)) {
				GameManager.setPlayerLang(player.getName(), lang);
				
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("LANG_SET", Config.getLang()).replace("[#]", " [" + lang + "]"));
			} else {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("REQ_LANG_NOT_AVAIL", Config.getLang()));
			}
		}
		
		return true;
	}

}
