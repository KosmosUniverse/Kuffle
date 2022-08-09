package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.AgeManager;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

public class KuffleValidate implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		if (!KuffleMain.gameStarted) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "GAME_NOT_LAUNCHED"));
			return true;
		}
		
		if (args.length != 1) {
			return false;
		}
		
		if (msg.equalsIgnoreCase("ki-validate")) {
			KuffleMain.systemLogs.logMsg(player.getName(), Utils.getLangString(player.getName(), "CMD_PERF").replace("<#>", "<ki-validate>"));
			
			if (!player.hasPermission("ki-validate")) {
				KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NOT_ALLOWED"));
				return false;
			}
			
			if (!KuffleMain.games.containsKey(args[0])) {
				KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "VALIDATE_PLAYER_ITEM"));	
				return true;
			}

			String tmp = KuffleMain.games.get(args[0]).getCurrentItem();
			
			KuffleMain.games.get(args[0]).found();
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "ITEM_VALIDATED").replace("[#]", " [" + tmp + "] ").replace("<#>", "<" + args[0] + ">"));			
		}
		
		if (msg.equalsIgnoreCase("ki-validate-age")) {
			KuffleMain.systemLogs.logMsg(player.getName(), Utils.getLangString(player.getName(), "CMD_PERF").replace("<#>", "<ki-validate-age>"));
			
			if (!player.hasPermission("ki-validate-age")) {
				KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NOT_ALLOWED"));
				return false;
			}
			
			if (!KuffleMain.games.containsKey(args[0])) {
				KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "VALIDATE_PLAYER_AGE"));	
				
				return true;
			}
			
			if (KuffleMain.games.get(args[0]).getAge() == -1) {
				KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "GAME_ALREADY_FINISHED").replace("<#>", "<" + args[0] + ">"));
				
				return true;
			}
			
			String tmp = AgeManager.getAgeByNumber(KuffleMain.ages, KuffleMain.games.get(args[0]).getAge()).name;
			
			KuffleMain.games.get(args[0]).setItemCount(KuffleMain.config.getItemPerAge() + 1);
			KuffleMain.games.get(args[0]).setCurrentItem(null);
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "AGE_VALIDATED").replace("[#]", "[" + tmp + "]").replace("<#>", "<" + args[0] + ">"));
		}
		
		return true;
	}

}
