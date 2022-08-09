package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleConfigException;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

public class KuffleConfig implements CommandExecutor {
	private static final String CONFIG_SET = "CONFIG_SET";
	private static final String CONFIG_NOT_SET = "CONFIG_NOT_SET";
	
	private enum ResultType {
		GOOD,
		BAD,
		INVALID
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		if (args.length % 2 == 1) {
			return false;
		}
		
		Player player = (Player) sender;
		
		KuffleMain.systemLogs.logMsg(player.getName(), Utils.getLangString(player.getName(), "CMD_PERF").replace("<#>", "<ki-config>"));
		
		if (args.length == 0) {
			player.sendMessage(KuffleMain.config.displayConfig());
			return true;
		}
		
		if (!player.hasPermission("ki-op")) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NOT_ALLOWED"));
			return true;
		}
		
		String before = "";
		
		for (int i = 0; i < args.length; i++) {
			if (i % 2 == 0) {
				before = args[i].toUpperCase();
			} else {
				ResultType ret = invokeMethod(player, before, args[i]);
				
				if (ret == ResultType.GOOD) {
					KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), CONFIG_SET).replace("[#]", before).replace("[##]", "[" + args[i] + "]"));	
				} else if (ret == ResultType.BAD) {
					KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), CONFIG_NOT_SET).replace("[#]", before).replace("[##]", "[" + args[i] + "]"));
				}
			}
		}
		
		return true;
	}
	
	private ResultType invokeMethod(Player player, String before, String current) {
		ResultType ret = ResultType.GOOD;
		
		if (!KuffleMain.config.hasKey(before)) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "KEY_NOT_REC").replace("[#]", "[" + before + "]"));
			ret = ResultType.INVALID;
		} else {
			try {
				KuffleMain.config.setElem(before, current);
			} catch (KuffleConfigException e) {
				player.sendMessage(e.getMessage());
				ret = ResultType.BAD;
			}
		}
		
		return ret;
	}
}
