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
public class KufflePause implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		Player player = null;
		
		try {
			player = CommandUtils.initCommand(sender, "k-pause", false, true, true);
		} catch (KuffleCommandFalseException e) {
			return false;
		}
		
		if (KuffleMain.getInstance().isPaused()) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_ALREADY_PAUSED", Config.getLang()));
			return false;
		}
		
		KuffleMain.getInstance().setPaused(true);
		
		GameManager.applyToPlayers(game -> {
			ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + LangManager.getMsgLang("GAME_PAUSED", game.getConfigLang()) + ChatColor.RESET, game.getPlayer());
			game.pausePlayer();
			game.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 10, false, false, false));			
		});
		
		return true;
	}

}
