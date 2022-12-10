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
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import main.fr.kosmosuniverse.kuffle.utils.CommandUtils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleResume implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		Player player = null;
		
		try {
			player = CommandUtils.initCommand(sender, "k-resume", false, true, true);
		} catch (KuffleCommandFalseException e) {
			return false;
		}
		
		if (!KuffleMain.getInstance().isPaused()) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_ALREADY_RUNNING", Config.getLang()));
			return false;
		}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () ->
			GameManager.applyToPlayers(game ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.RED + "3" + ChatColor.RESET, game.player))
		, 20);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () ->
			GameManager.applyToPlayers(game ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.YELLOW + "2" + ChatColor.RESET, game.player))
		, 40);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () ->
			GameManager.applyToPlayers(game ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.GREEN + "1" + ChatColor.RESET, game.player))
		, 60);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
			KuffleMain.getInstance().setPaused(false);
			
			GameManager.applyToPlayers(game -> {
				GameManager.resumePlayer(game);
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + LangManager.getMsgLang("GAME_RESUMED", game.configLang) + ChatColor.RESET, game.player);
				game.player.removePotionEffect(PotionEffectType.INVISIBILITY);
			});
		}, 80);

		return true;
	}

}
