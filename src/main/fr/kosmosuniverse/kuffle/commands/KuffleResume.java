package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffectType;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.ActionBar;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleResume extends AKuffleCommand {
	public KuffleResume() {
		super("k-resume", null, true, 0, 0, false);
	}

	@Override
	public boolean runCommand() {
		if (!KuffleMain.getInstance().isPaused()) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_ALREADY_RUNNING", Config.getLang()));
			return false;
		}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () ->
			GameManager.applyToPlayers(game ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.RED + "3" + ChatColor.RESET, game.getPlayer()))
		, 20);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () ->
			GameManager.applyToPlayers(game ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.YELLOW + "2" + ChatColor.RESET, game.getPlayer()))
		, 40);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () ->
			GameManager.applyToPlayers(game ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.GREEN + "1" + ChatColor.RESET, game.getPlayer()))
		, 60);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
			KuffleMain.getInstance().setPaused(false);
			
			GameManager.applyToPlayers(game -> {
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + LangManager.getMsgLang("GAME_RESUMED", game.getConfigLang()) + ChatColor.RESET, game.getPlayer());
				game.resumePlayer();
				game.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
			});
		}, 80);

		return true;
	}

}
