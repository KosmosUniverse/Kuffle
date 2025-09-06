package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.core.*;
import fr.kosmosuniverse.kuffle.utils.CommandUtils;

import java.io.File;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleSave extends AKuffleCommand {
	private final File dataFolder;
	
	/**
	 * Constructor
	 * 
	 * @param folder	The Kuffle plugin folder
	 */
	public KuffleSave(File folder) {
		super("k-save", null, true, 0, 0, false);
		dataFolder = folder;
	}

	@Override
	public boolean runCommand() {
		Party.getInstance().pause();
		Party.getInstance().getGames().savePlayers(dataFolder.getPath());
				
		if (Config.getTeam()) {
			TeamManager.getInstance().saveTeams(dataFolder.getPath());
		}
		
		CommandUtils.saveParty();
		
		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_SAVED", Config.getLang()));
		
		return true;
	}
}
