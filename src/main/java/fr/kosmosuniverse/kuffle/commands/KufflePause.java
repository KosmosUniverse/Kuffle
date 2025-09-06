package fr.kosmosuniverse.kuffle.commands;


import fr.kosmosuniverse.kuffle.core.*;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KufflePause extends AKuffleCommand {
	public KufflePause() {
		super("k-pause", null, true, 0, 0, false);
	}

	@Override
	public boolean runCommand() {
		if (Party.getInstance().getStatus() == GameStatus.PAUSED) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_ALREADY_PAUSED", Config.getLang()));
			return false;
		}

		Party.getInstance().pause();
		
		return true;
	}

}
