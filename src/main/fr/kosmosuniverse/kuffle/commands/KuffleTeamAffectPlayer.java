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
public class KuffleTeamAffectPlayer extends AKuffleCommand {
	public KuffleTeamAffectPlayer() {
		super("k-team-affect-player", null, true, 2, 2, true);
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
		
		if (TeamManager.getInstance().getTeam(args[0]).getPlayers().size() == Config.getTeamSize()) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_FULL", Config.getLang()));
			return true;
		}
		
		if (!GameManager.hasPlayer(args[1])) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("PLAYER_NOT_IN_GAME", Config.getLang()));
			return true;
		}
		
		if (TeamManager.getInstance().getTeam(args[0]).hasPlayer(args[1])) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_PLAYER", Config.getLang()));
			return true;
		}
		
		TeamManager.getInstance().affectPlayer(args[0], GameManager.getPlayer(args[1]));
		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_ADD_PLAYER", Config.getLang()).replace("<#>", "<" + args[1] + ">").replace("<##>", "<" + args[0] + ">"));
		
		return true;
	}
}
