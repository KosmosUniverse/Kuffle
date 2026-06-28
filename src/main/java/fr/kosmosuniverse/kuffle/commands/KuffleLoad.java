package fr.kosmosuniverse.kuffle.commands;

import java.io.File;

import fr.kosmosuniverse.kuffle.core.*;
import fr.kosmosuniverse.kuffle.datamanagers.LangManager;
import fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import fr.kosmosuniverse.kuffle.storage.Load;
import fr.kosmosuniverse.kuffle.utils.Utils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleLoad extends AKuffleCommand {
	private final File dataFolder;
	private static final String GAME_FILE = "Game.k";
	
	/**
	 * Constructor
	 * 
	 * @param folder	The Kuffle plugin folder
	 */
	public KuffleLoad(File folder) {
		super("k-load", null, false, 0, 0, false);
		dataFolder = folder;
	}
	
	@Override
	public boolean runCommand() throws KuffleCommandFalseException {
		if (PartyTmp.getInstance().getPlayers() != null &&
				!PartyTmp.getInstance().getPlayers().getList().isEmpty()) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("LIST_NOT_EMPTY", Config.getLang()) + ".");
			throw new KuffleCommandFalseException();
		}

		if (Utils.fileExists(dataFolder.getPath(), GAME_FILE)) {
			throw new KuffleCommandFalseException();
		}

		Load.loadStartParty(player, dataFolder.getPath());

		return true;
	}
}
