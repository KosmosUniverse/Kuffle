package main.fr.kosmosuniverse.kuffle.commands;

import java.io.File;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.TeamManager;
import main.fr.kosmosuniverse.kuffle.utils.CommandUtils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleSave extends AKuffleCommand {
	private File dataFolder;
	
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
		KuffleMain.getInstance().setPaused(true);

		GameManager.savePlayers(dataFolder.getPath());
				
		if (Config.getTeam()) {
			TeamManager.getInstance().saveTeams(dataFolder.getPath());
		}
		
		CommandUtils.saveParty();
		
		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_SAVED", Config.getLang()));
		
		return true;
	}
}
