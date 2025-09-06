package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.core.Config;
import fr.kosmosuniverse.kuffle.core.LangManager;
import fr.kosmosuniverse.kuffle.core.LogManager;
import fr.kosmosuniverse.kuffle.core.Party;
import fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;

public class KuffleValidate extends AKuffleCommand {
	public KuffleValidate() {
		super("k-validate", null, true, 1, 1, false);
	}

	@Override
	public boolean runCommand() throws KuffleCommandFalseException {
		if (!Party.getInstance().getPlayers().has(args[0])) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("VALIDATE_PLAYER_ITEM", Config.getLang()));
			throw new KuffleCommandFalseException();
		}

		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("ITEM_VALIDATED", Config.getLang()).replace("[#]", " [" + Party.getInstance().getGames().getGames().get(args[0]).getCurrentTarget() + "] ").replace("<#>", "<" + args[0] + ">"));
		Party.getInstance().getGames().playerFoundTarget(args[0]);
		
		return true;
	}
}
