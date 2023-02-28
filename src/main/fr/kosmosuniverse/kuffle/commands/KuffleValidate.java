package main.fr.kosmosuniverse.kuffle.commands;

import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;

public class KuffleValidate extends AKuffleCommand {
	public KuffleValidate() {
		super("k-validate", null, true, 1, 1, false);
	}

	@Override
	public boolean runCommand() throws KuffleCommandFalseException {
		if (!GameManager.hasPlayer(args[0])) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("VALIDATE_PLAYER_ITEM", Config.getLang()));
			throw new KuffleCommandFalseException();
		}

		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("ITEM_VALIDATED", Config.getLang()).replace("[#]", " [" + GameManager.getPlayerTarget(args[0]) + "] ").replace("<#>", "<" + args[0] + ">"));			
		GameManager.playerFoundTarget(args[0]);
		
		return true;
	}
}
