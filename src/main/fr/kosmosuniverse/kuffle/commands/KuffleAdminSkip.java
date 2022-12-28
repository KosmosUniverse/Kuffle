package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;

public class KuffleAdminSkip extends AKuffleCommand {
	public KuffleAdminSkip() {
		super("k-adminskip", null, true, 1, 1, false);
	}
	
	private void doSkip(Player player, String cmd, String playerTarget) {
		if (!player.hasPermission(cmd)) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_ALLOWED", Config.getLang()));
			return ;
		}
		
		if (!GameManager.hasPlayer(playerTarget)) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("PLAYER_NOT_IN_GAME", Config.getLang()));
			return ;
		}
		
		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("ITEM_SKIPPED", Config.getLang()).replace("[#]", " [" + GameManager.getPlayerTarget(playerTarget) + "] ").replace("<#>", " <" + playerTarget + ">"));
		GameManager.skipPlayerTarget(playerTarget, false);
	}

	@Override
	public boolean runCommand() {
		if (!GameManager.hasPlayer(player.getName())) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_PLAYING", Config.getLang()));
			return true;
		}
		
		doSkip(player, name, args[0]);
		
		return true;
	}
}
