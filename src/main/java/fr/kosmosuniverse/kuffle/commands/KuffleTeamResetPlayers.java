package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.core.*;
import fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;

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
		if (Party.getInstance().getStatus() == GameStatus.RUNNING) {
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
