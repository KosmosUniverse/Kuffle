package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.ScoreManager;
import main.fr.kosmosuniverse.kuffle.core.TeamManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import main.fr.kosmosuniverse.kuffle.utils.CommandUtils;

public class KuffleStop implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		Player player = null;
		
		try {
			player = CommandUtils.initCommand(sender, "k-stop", false, true, true);
		} catch (KuffleCommandFalseException e) {
			return false;
		}
		
		GameManager.applyToPlayers(game -> {
			for (PotionEffect pe : game.getPlayer().getActivePotionEffects()) {
				game.getPlayer().removePotionEffect(pe.getType());
			}
			
			GameManager.resetPlayerBar(game);
		});

		if (Config.getSBTT()) {
			KuffleMain.getInstance().getType().clearSbtt();
		}
		
		ScoreManager.clear();
		
		if (Config.getTeam()) {
			TeamManager.getInstance().clear();
		}
		
		GameManager.clear();
		KuffleMain.getInstance().getGameLoop().kill();
		
		KuffleMain.getInstance().setStarted(false);
		KuffleMain.getInstance().setPaused(false);
		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_STOPPED", Config.getLang()));
		
		return true;
	}

}
