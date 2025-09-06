package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.core.*;
import fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;

public class KuffleValidateAge extends AKuffleCommand {
	public KuffleValidateAge() {
		super("k-validate-age", null, true, 1, 1, false);
	}

	@Override
	public boolean runCommand() throws KuffleCommandFalseException {
		if (!Party.getInstance().getPlayers().has(args[0])) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("VALIDATE_PLAYER_AGE", Config.getLang()));
			throw new KuffleCommandFalseException();
		}
		
		if (Party.getInstance().getGames().getGames().get(args[0]).getAge() == -1) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_ALREADY_FINISHED", Config.getLang()).replace("<#>", "<" + args[0] + ">"));
			throw new KuffleCommandFalseException();
		}

		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("AGE_VALIDATED", Config.getLang()).replace("[#]", "[" + AgeManager.getAgeByNumber(Party.getInstance().getGames().getGames().get(args[0]).getAge()).getName() + "]").replace("<#>", "<" + args[0] + ">"));
		Party.getInstance().getGames().finishPlayerAge(args[0]);
		
		return true;
	}
}
