package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.ActionBar;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

public class KuffleResume implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		KuffleMain.systemLogs.logMsg(player.getName(), Utils.getLangString(player.getName(), "CMD_PERF").replace("<#>", "<ki-resume>"));
		
		if (!player.hasPermission("ki-resume")) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NOT_ALLOWED"));
			return false;
		}
		
		if (!KuffleMain.gameStarted) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "GAME_NOT_LAUNCHED"));
			return false;
		}
		
		if (!KuffleMain.paused) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "GAME_ALREADY_RUNNING"));
			return false;
		}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () ->
			KuffleMain.games.forEach((playerName, game) ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.RED + "3" + ChatColor.RESET, game.getPlayer())
			)
		, 20);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () ->
			KuffleMain.games.forEach((playerName, game) ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.YELLOW + "2" + ChatColor.RESET, game.getPlayer())
			)
		, 40);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () ->
			KuffleMain.games.forEach((playerName, game) ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.GREEN + "1" + ChatColor.RESET, game.getPlayer())
			)
		, 60);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () -> {
			KuffleMain.paused = false;
			
			KuffleMain.games.forEach((playerName, game) -> {
				game.resume();
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + Utils.getLangString(player.getName(), "GAME_RESUMED") + ChatColor.RESET, game.getPlayer());
				game.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
			});
		}, 80);

		return true;
	}

}
