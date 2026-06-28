package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.core.*;
import fr.kosmosuniverse.kuffle.datamanagers.LangManager;
import fr.kosmosuniverse.kuffle.storage.Save;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleSave extends AKuffleCommand {
	/**
	 * Constructor
	 */
	public KuffleSave() {
		super("k-save", null, true, 0, 0, false);
	}

	@Override
	public boolean runCommand() {
		if (PartyTmp.getInstance().pause()) {
			Save.saveStopParty();
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_SAVED", Config.getLang()));
		}
		
		return true;
	}
}
