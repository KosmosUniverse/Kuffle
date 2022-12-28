package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.ChatColor;
import org.bukkit.potion.PotionEffect;
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
public class KufflePause extends AKuffleCommand {
	public KufflePause() {
		super("k-pause", null, true, 0, 0, false);
	}

	@Override
	public boolean runCommand() {
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
		
		return true;	}

}
