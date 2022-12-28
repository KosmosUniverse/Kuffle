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
public class KuffleTeamResetPlayers extends AKuffleCommand {
	public KuffleTeamResetPlayers(String cmdName, Boolean typed, Boolean started, Integer aMin, Integer aMax,
			boolean team) {
		super("k-team-reset-players", null, false, 1, 1, true);
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
		
		TeamManager.getInstance().getTeam(args[0]).getPlayers().clear();
		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_RESETED", Config.getLang()).replace("<#>", "<" + args[0] + ">"));
		
		return true;
	}
}
