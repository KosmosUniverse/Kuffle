package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

public class KuffleStop implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		KuffleMain.systemLogs.logMsg(player.getName(), Utils.getLangString(player.getName(), "CMD_PERF").replace("<#>", "<ki-stop>"));
		
		if (!player.hasPermission("ki-stop")) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NOT_ALLOWED"));
			return false;
		}
		
		if (!KuffleMain.gameStarted) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "GAME_NOT_LAUNCHED"));
			return false;
		}
		
		KuffleMain.games.forEach((playerName, game) -> {
			for (PotionEffect pe : game.getPlayer().getActivePotionEffects()) {
				game.getPlayer().removePotionEffect(pe.getType());
			}
			
			game.resetBar();
		});

		Utils.removeTemplates();
		KuffleMain.scores.clear();
		
		if (KuffleMain.config.getTeam()) {
			KuffleMain.teams.resetAll();
		}
		
		KuffleMain.games.clear();
		KuffleMain.loop.kill();
		
		KuffleMain.gameStarted = false;
		KuffleMain.paused = false;
		KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "GAME_STOPPED"));
		
		return true;
	}

}
