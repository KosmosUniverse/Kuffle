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
import main.fr.kosmosuniverse.kuffle.type.KuffleType;

public class KuffleValidate implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		if (KuffleMain.type.getType() == KuffleType.Type.UNKNOWN) {
			LogManager.getInstanceSystem().writeMsg(player, "Kuffle type not configured, please set it with /k-set-type");
			return true;
		}
		
		if (!KuffleMain.gameStarted) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_NOT_LAUNCHED", Config.getLang()));
			return true;
		}
		
		
		if (args.length != 1) {
			return false;
		}
		
		if (msg.equalsIgnoreCase("k-validate")) {
			LogManager.getInstanceSystem().logMsg(player.getName(), LangManager.getMsgLang("CMD_PERF", Config.getLang()).replace("<#>", "<k-validate>"));
			
			if (!player.hasPermission("k-validate")) {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_ALLOWED", Config.getLang()));
				return false;
			}
			
			if (!GameManager.hasPlayer(args[0])) {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("VALIDATE_PLAYER_ITEM", Config.getLang()));
				return true;
			}

			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("ITEM_VALIDATED", Config.getLang()).replace("[#]", " [" + GameManager.getPlayerTarget(args[0]) + "] ").replace("<#>", "<" + args[0] + ">"));			
			GameManager.playerFoundTarget(args[0]);
		} else if (msg.equalsIgnoreCase("k-validate-age")) {
			LogManager.getInstanceSystem().logMsg(player.getName(), LangManager.getMsgLang("CMD_PERF", Config.getLang()).replace("<#>", "<k-validate-age>"));
			
			if (!player.hasPermission("k-validate-age")) {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_ALLOWED", Config.getLang()));
				return false;
			}
			
			if (!GameManager.hasPlayer(args[0])) {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("VALIDATE_PLAYER_AGE", Config.getLang()));	
				
				return true;
			}
			
			if (GameManager.getPlayerAge(args[0]).number == -1) {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_ALREADY_FINISHED", Config.getLang()).replace("<#>", "<" + args[0] + ">"));
				
				return true;
			}

			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("AGE_VALIDATED", Config.getLang()).replace("[#]", "[" + GameManager.getPlayerAge(args[0]) + "]").replace("<#>", "<" + args[0] + ">"));
			GameManager.nextPlayerAge(args[0]);
		}
		
		return true;
	}

}
