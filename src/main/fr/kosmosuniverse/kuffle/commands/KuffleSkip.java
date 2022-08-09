package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

public class KuffleSkip implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		KuffleMain.systemLogs.logMsg(player.getName(), Utils.getLangString(player.getName(), "CMD_PERF").replace("<#>", "<ki-skip>"));
		
		if (!KuffleMain.gameStarted) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "GAME_NOT_LAUNCHED"));
			
			return false;
		}

		if (!KuffleMain.config.getSkip() && !msg.equals("ki-adminskip")) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "CONFIG_DISABLED"));
			
			return false;
		}
		
		if (!KuffleMain.games.containsKey(player.getName())) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NOT_PLAYING"));					
			return true;
		}
		
		if (msg.equals("ki-skip")) {
			if (args.length != 0) {
				return false;
			}
			
			doSkip(player, "ki-skip", player.getName());
		} else if (msg.equals("ki-adminskip")) {
			if (args.length != 1) {
				return false;
			}
			
			doSkip(player, "ki-adminskip", args[0]);
		}
		
		return true;
	}
	
	private void doSkip(Player player, String cmd, String playerTarget) {
		if (!player.hasPermission(cmd)) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NOT_ALLOWED"));
			return ;
		}
		
		if (!KuffleMain.games.containsKey(playerTarget)) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "PLAYER_NOT_IN_GAME"));
			return ;
		}
		
		String tmp = KuffleMain.games.get(playerTarget).getCurrentItem();
		
		KuffleMain.games.get(player.getName()).skip("ki-skip".equals(cmd));
		KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "ITEM_SKIPPED").replace("[#]", " [" + tmp + "] ").replace("<#>", " <" + playerTarget + ">"));				
		
		return ;
	}
}
