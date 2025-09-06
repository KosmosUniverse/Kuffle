package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.core.*;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleResume extends AKuffleCommand {
	public KuffleResume() {
		super("k-resume", null, true, 0, 0, false);
	}

	@Override
	public boolean runCommand() {
		if (Party.getInstance().getStatus() != GameStatus.PAUSED) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_ALREADY_RUNNING", Config.getLang()));
			return false;
		}
		
		Party.getInstance().resume();

		return true;
	}

}
