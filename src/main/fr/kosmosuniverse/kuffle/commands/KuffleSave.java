package main.fr.kosmosuniverse.kuffle.commands;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.TeamManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import main.fr.kosmosuniverse.kuffle.utils.CommandUtils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleSave implements CommandExecutor {
	private File dataFolder;
	
	/**
	 * Constructor
	 * 
	 * @param folder	The Kuffle plugin folder
	 */
	public KuffleSave(File folder) {
		dataFolder = folder;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		Player player = null;
		
		try {
			player = CommandUtils.initCommand(sender, "k-add-during-game", false, true, true);
		} catch (KuffleCommandFalseException e) {
			return false;
		}
		
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
