package main.fr.kosmosuniverse.kuffle.commands;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.TeamManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleTeamResetPlayers extends AKuffleCommand {
	public KuffleTeamResetPlayers() {
		super("k-team-reset-players", null, false, 1, 1, true);
	}

	@Override
	public boolean runCommand() throws KuffleCommandFalseException {
		if (KuffleMain.getInstance().isStarted()) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_LAUNCHED", Config.getLang()));
			throw new KuffleCommandFalseException();
		}
		
		if (!TeamManager.getInstance().hasTeam(args[0])) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_NOT_EXISTS", Config.getLang()).replace("<#>", "<" + args[0] + ">"));
			throw new KuffleCommandFalseException();
		}
		
		TeamManager.getInstance().getTeam(args[0]).getPlayers().clear();
		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_RESETED", Config.getLang()).replace("<#>", "<" + args[0] + ">"));
		
		return true;
	}
}
