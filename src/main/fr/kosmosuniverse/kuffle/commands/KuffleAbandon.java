package main.fr.kosmosuniverse.kuffle.commands;

import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleAbandon extends AKuffleCommand {
	public KuffleAbandon() {
		super("k-abandon", null, true, 0, 0, false);
	}

	@Override
	public boolean runCommand() {
		if (!GameManager.hasPlayer(player.getName())) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_PLAYING", GameManager.getPlayerLang(player.getName())));
		} else {
			GameManager.setLose(player.getName(), true);
		}
		
		return true;
	}
}
