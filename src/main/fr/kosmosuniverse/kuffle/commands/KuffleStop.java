package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.CraftManager;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.ScoreManager;
import main.fr.kosmosuniverse.kuffle.core.TeamManager;
import main.fr.kosmosuniverse.kuffle.type.KuffleType;

public class KuffleStop implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		LogManager.getInstanceSystem().logMsg(player.getName(), LangManager.getMsgLang("CMD_PERF", Config.getLang()).replace("<#>", "<k-stop>"));
		
		if (!player.hasPermission("k-stop")) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_ALLOWED", Config.getLang()));
			return false;
		}
		
		if (KuffleMain.type.getType() == KuffleType.Type.UNKNOWN) {
			LogManager.getInstanceSystem().writeMsg(player, "Kuffle type not configured, please set it with /k-set-type");
			return true;
		}
		
		if (!KuffleMain.gameStarted) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_NOT_LAUNCHED", Config.getLang()));
			return false;
		}
		
		GameManager.applyToPlayers((game) -> {
			for (PotionEffect pe : game.player.getActivePotionEffects()) {
				game.player.removePotionEffect(pe.getType());
			}
			
			GameManager.resetPlayerBar(game);
		});

		CraftManager.removeCraftTemplates();
		ScoreManager.clear();
		
		if (Config.getTeam()) {
			TeamManager.clear();
		}
		
		GameManager.clear();
		KuffleMain.loop.kill();
		
		KuffleMain.gameStarted = false;
		KuffleMain.paused = false;
		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_STOPPED", Config.getLang()));
		
		return true;
	}

}
