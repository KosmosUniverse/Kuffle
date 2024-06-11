package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;

public class KuffleSkip extends AKuffleCommand {
	public KuffleSkip() {
		super("k-skip", null, true, 0, 0, false);
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
		
		GameManager.skipPlayerTarget(playerTarget, true);
	}

	@Override
	public boolean runCommand() throws KuffleCommandFalseException {
		if (!Config.getSkip()) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("CONFIG_DISABLED", Config.getLang()));
			throw new KuffleCommandFalseException();
		}
		
		if (!GameManager.hasPlayer(player.getName())) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_PLAYING", Config.getLang()));
			throw new KuffleCommandFalseException();
		}
		
		doSkip(player, name, player.getName());
		
		return true;
	}
}
