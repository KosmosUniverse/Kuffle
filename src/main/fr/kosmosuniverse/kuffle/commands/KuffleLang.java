package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

public class KuffleLang implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		if (args.length > 1) {
			return false; 
		}
		
		Player player = (Player) sender;
		
		KuffleMain.systemLogs.logMsg(player.getName(), Utils.getLangString(player.getName(), "CMD_PERF").replace("<#>", "<ki-lang>"));
		
		if (!player.hasPermission("ki-lang")) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NOT_ALLOWED"));
			return false;
		}
		
		if (!KuffleMain.gameStarted) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NOT_PLAYING"));
			return true;
		}
		
		if (!KuffleMain.games.containsKey(player.getName())) {
			return true;
		}
		
		if (args.length == 0) {
			KuffleMain.systemLogs.writeMsg(player, KuffleMain.games.get(player.getName()).getLang());
		} else if (args.length == 1) {
			String lang = args[0].toLowerCase();
			
			if (KuffleMain.langs.contains(lang)) {
				KuffleMain.games.get(player.getName()).setLang(lang);
				
				KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "LANG_SET").replace("[#]", " [" + lang + "]"));
			} else {
				KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "REQ_LANG_NOT_AVAIL"));
			}
		}
		
		return true;
	}

}
