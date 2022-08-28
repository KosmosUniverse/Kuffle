package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.AgeManager;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

public class KuffleValidate implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		if (!KuffleMain.gameStarted) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_NOT_LAUNCHED", Config.getLang()));
			return true;
		}
		
		if (args.length != 1) {
			return false;
		}
		
		if (msg.equalsIgnoreCase("ki-validate")) {
			LogManager.getInstanceSystem().logMsg(player.getName(), LangManager.getMsgLang("CMD_PERF", Config.getLang()).replace("<#>", "<ki-validate>"));
			
			if (!player.hasPermission("ki-validate")) {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_ALLOWED", Config.getLang()));
				return false;
			}
			
			if (!GameManager.hasPlayer(args[0])) {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("VALIDATE_PLAYER_ITEM", Config.getLang()));
				return true;
			}

			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("ITEM_VALIDATED", Config.getLang()).replace("[#]", " [" + GameManager.getPlayerTarget(playerTarget) + "] ").replace("<#>", "<" + args[0] + ">"));			
			GameManager.playerFoundTarget(args[0]);
		} else if (msg.equalsIgnoreCase("ki-validate-age")) {
			LogManager.getInstanceSystem().logMsg(player.getName(), LangManager.getMsgLang("CMD_PERF", Config.getLang()).replace("<#>", "<ki-validate-age>"));
			
			if (!player.hasPermission("ki-validate-age")) {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_ALLOWED", Config.getLang()));
				return false;
			}
			
			if (!GameManager.hasPlayer(args[0])) {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("VALIDATE_PLAYER_AGE", Config.getLang()));	
				
				return true;
			}
			
			if (KuffleMain.games.get(args[0]).getAge() == -1) {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_ALREADY_FINISHED", Config.getLang()).replace("<#>", "<" + args[0] + ">"));
				
				return true;
			}
			
			String tmp = AgeManager.getAgeByNumber(KuffleMain.ages, KuffleMain.games.get(args[0]).getAge()).name;
			
			KuffleMain.games.get(args[0]).setItemCount(KuffleMain.config.getItemPerAge() + 1);
			KuffleMain.games.get(args[0]).setCurrentItem(null);
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("AGE_VALIDATED", Config.getLang()).replace("[#]", "[" + tmp + "]").replace("<#>", "<" + args[0] + ">"));
		}
		
		return true;
	}

}
