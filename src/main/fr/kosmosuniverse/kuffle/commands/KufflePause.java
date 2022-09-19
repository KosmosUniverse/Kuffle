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
import main.fr.kosmosuniverse.kuffle.type.KuffleType;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KufflePause implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		LogManager.getInstanceSystem().logMsg(player.getName(), LangManager.getMsgLang("CMD_PERF", Config.getLang()).replace("<#>", "<k-pause>"));
		
		if (!player.hasPermission("k-pause")) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_ALLOWED", Config.getLang()));
			return false;
		}
		
		if (KuffleMain.type.getType() == KuffleType.Type.NO_TYPE) {
			LogManager.getInstanceSystem().writeMsg(player, "Kuffle type not configured, please set it with /k-set-type");
			return true;
		}
		
		if (!KuffleMain.gameStarted) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_NOT_LAUNCHED", Config.getLang()));
			return false;
		}
		
		if (KuffleMain.paused) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_ALREADY_PAUSED", Config.getLang()));
			return false;
		}
		
		KuffleMain.paused = true;
		
		GameManager.applyToPlayers((game) -> {
			GameManager.pausePlayer(game);
			ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + LangManager.getMsgLang("GAME_PAUSED", game.configLang) + ChatColor.RESET, game.player);
			game.player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 10, false, false, false));			
		});
		
		return true;
	}

}
