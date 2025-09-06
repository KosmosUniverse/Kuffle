package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.core.Config;
import fr.kosmosuniverse.kuffle.core.LangManager;
import fr.kosmosuniverse.kuffle.core.LogManager;
import fr.kosmosuniverse.kuffle.core.Party;
import fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import org.bukkit.entity.Player;

public class KuffleSkip extends AKuffleCommand {
	public KuffleSkip() {
		super("k-skip", null, true, 0, 0, false);
	}

	@Override
	public boolean runCommand() throws KuffleCommandFalseException {
		if (!Config.getSkip()) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("CONFIG_DISABLED", Config.getLang()));
			throw new KuffleCommandFalseException();
		}

		if (!Party.getInstance().getPlayers().has(player.getName())) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_PLAYING", Config.getLang()));
			throw new KuffleCommandFalseException();
		}

		doSkip(player, name, player.getName());

		return true;
	}

	private void doSkip(Player player, String cmd, String playerTarget) {
		if (!player.hasPermission(cmd)) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_ALLOWED", Config.getLang()));
			return ;
		}
		
		if (!Party.getInstance().getPlayers().has(playerTarget)) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("PLAYER_NOT_IN_GAME", Config.getLang()));
			return ;
		}

		if (Party.getInstance().getGames().getGames().get(player.getName()).getCurrentTarget() == null) {
			LogManager.getInstanceSystem().writeMsg(player, "Please wait for your target to appear before skipping.");
			return ;
		}

		Party.getInstance().getGames().skipPlayerTarget(playerTarget, true);
	}
}
