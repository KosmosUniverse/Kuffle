package main.fr.kosmosuniverse.kuffle.commands;

import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.TeamManager;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleTeamDelete extends AKuffleCommand {
	public KuffleTeamDelete() {
		super("k-team-delete", null, false, 1, 1, true);
	}

	@Override
	public boolean runCommand() {
		if (GameManager.getGames().size() > 0) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_LAUNCHED", Config.getLang()));
			return true;
		}
		
		if (!TeamManager.getInstance().hasTeam(args[0])) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_NOT_EXISTS", Config.getLang()).replace("<#>", "<" + args[0] + ">"));
			return true;
		}
		
		TeamManager.getInstance().deleteTeam(args[0]);
		
		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_DELETED", Config.getLang()).replace("<#>", "<" + args[0] + ">"));
		
		return true;
	}
}
