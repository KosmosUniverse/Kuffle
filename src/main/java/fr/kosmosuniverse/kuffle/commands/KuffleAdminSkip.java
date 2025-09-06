package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.core.Config;
import fr.kosmosuniverse.kuffle.core.LangManager;
import fr.kosmosuniverse.kuffle.core.LogManager;
import fr.kosmosuniverse.kuffle.core.Party;
import fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import org.bukkit.entity.Player;

public class KuffleAdminSkip extends AKuffleCommand {
	public KuffleAdminSkip() {
		super("k-adminskip", null, true, 1, 1, false);
	}

	@Override
	public boolean runCommand() throws KuffleCommandFalseException {
		if (!Party.getInstance().getPlayers().has(player.getName())) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_PLAYING", Config.getLang()));
			throw new KuffleCommandFalseException();
		}

		doSkip(player, name, args[0]);

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
		
		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("ITEM_SKIPPED", Config.getLang()).replace("[#]", " [" + Party.getInstance().getGames().getGames().get(playerTarget).getCurrentTarget() + "] ").replace("<#>", " <" + playerTarget + ">"));
		Party.getInstance().getGames().skipPlayerTarget(playerTarget, false);
	}
}
