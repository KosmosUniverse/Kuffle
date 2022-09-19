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

public class KuffleSkip implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		LogManager.getInstanceSystem().logMsg(player.getName(), LangManager.getMsgLang("CMD_PERF", Config.getLang()).replace("<#>", "<k-skip>"));
		
		if (KuffleMain.type.getType() == KuffleType.Type.UNKNOWN) {
			LogManager.getInstanceSystem().writeMsg(player, "Kuffle type not configured, please set it with /k-set-type");
			return true;
		}
		
		if (!KuffleMain.gameStarted) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_NOT_LAUNCHED", Config.getLang()));
			
			return false;
		}

		if (!Config.getSkip() && !msg.equals("k-adminskip")) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("CONFIG_DISABLED", Config.getLang()));
			
			return false;
		}
		
		if (!GameManager.hasPlayer(player.getName())) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_PLAYING", Config.getLang()));
			return true;
		}
		
		if (msg.equals("k-skip")) {
			if (args.length != 0) {
				return false;
			}
			
			doSkip(player, msg, player.getName());
		} else if (msg.equals("k-adminskip")) {
			if (args.length != 1) {
				return false;
			}
			
			doSkip(player, msg, args[0]);
		}
		
		return true;
	}
	
	private void doSkip(Player player, String cmd, String playerTarget) {
		if (!player.hasPermission(cmd)) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_ALLOWED", Config.getLang()));
			return ;
		}
		
		if (!GameManager.hasPlayer(playerTarget)) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("PLAYER_NOT_IN_GAME", Config.getLang()));
			return ;
		}
		
		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("ITEM_SKIPPED", Config.getLang()).replace("[#]", " [" + GameManager.getPlayerTarget(playerTarget) + "] ").replace("<#>", " <" + playerTarget + ">"));
		GameManager.skipPlayerTarget(playerTarget, "k-skip".equals(cmd));

		
		return ;
	}
}
