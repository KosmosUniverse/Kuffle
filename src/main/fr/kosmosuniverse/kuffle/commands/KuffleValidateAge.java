package main.fr.kosmosuniverse.kuffle.commands;

import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;

public class KuffleValidateAge extends AKuffleCommand {
	public KuffleValidateAge() {
		super("k-validate-age", null, true, 1, 1, false);
	}

	@Override
	public boolean runCommand() throws KuffleCommandFalseException {
		if (!GameManager.hasPlayer(args[0])) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("VALIDATE_PLAYER_AGE", Config.getLang()));	
			throw new KuffleCommandFalseException();
		}
		
		if (GameManager.getPlayerAge(args[0]).getNumber() == -1) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_ALREADY_FINISHED", Config.getLang()).replace("<#>", "<" + args[0] + ">"));
			throw new KuffleCommandFalseException();
		}

		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("AGE_VALIDATED", Config.getLang()).replace("[#]", "[" + GameManager.getPlayerAge(args[0]).getName() + "]").replace("<#>", "<" + args[0] + ">"));
		GameManager.finishAge(args[0]);
		
		return true;
	}
}
