package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.ActionBar;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

public class KufflePause implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		KuffleMain.systemLogs.logMsg(player.getName(), Utils.getLangString(player.getName(), "CMD_PERF").replace("<#>", "<ki-pause>"));
		
		if (!player.hasPermission("ki-pause")) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NOT_ALLOWED"));
			return false;
		}
		
		if (!KuffleMain.gameStarted) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "GAME_NOT_LAUNCHED"));
			return false;
		}
		
		if (KuffleMain.paused) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "GAME_ALREADY_PAUSED"));
			return false;
		}
		
		KuffleMain.paused = true;
		
		KuffleMain.games.forEach((playerName, game) -> {
			game.pause();
			ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + Utils.getLangString(player.getName(), "GAME_PAUSED") + ChatColor.RESET, game.getPlayer());
			game.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 10, false, false, false));			
		});
		
		return true;
	}

}
