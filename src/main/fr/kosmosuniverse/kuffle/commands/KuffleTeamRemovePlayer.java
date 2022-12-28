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
public class KuffleTeamRemovePlayer extends AKuffleCommand {
	public KuffleTeamRemovePlayer() {
		super("k-team-remove-player", null, false, 2, 2, true);
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
		
		if (!TeamManager.getInstance().getTeam(args[0]).hasPlayer(args[1])) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_NO_PLAYER", Config.getLang()));
			return true;
		}
		
		TeamManager.getInstance().removePlayer(args[0], GameManager.getPlayer(args[1]));
		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_REMOVED", Config.getLang()).replace("<#>", "<" + args[1] + ">").replace("<##>", "<" + args[0] + ">"));
		
		return true;
	}
}
