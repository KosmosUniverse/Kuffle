package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.potion.PotionEffect;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.ScoreManager;
import main.fr.kosmosuniverse.kuffle.core.TeamManager;

public class KuffleStop extends AKuffleCommand {
	public KuffleStop() {
		super("k-stop", null, true, 0, 0, false);
	}

	@Override
	public boolean runCommand() {
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
