package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.core.*;
import fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import org.bukkit.Bukkit;

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
	public boolean runCommand() throws KuffleCommandFalseException {
		if (Party.getInstance().getStatus() == GameStatus.RUNNING) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_LAUNCHED", Config.getLang()));
			throw new KuffleCommandFalseException();
		}
		
		if (!TeamManager.getInstance().hasTeam(args[0])) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_NOT_EXISTS", Config.getLang()).replace("<#>", "<" + args[0] + ">"));
			throw new KuffleCommandFalseException();
		}
		
		if (!TeamManager.getInstance().getTeam(args[0]).hasPlayer(args[1])) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_NO_PLAYER", Config.getLang()));
			throw new KuffleCommandFalseException();
		}
		
		TeamManager.getInstance().removePlayer(args[0], Bukkit.getPlayer(args[1]));
		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_REMOVED", Config.getLang()).replace("<#>", "<" + args[1] + ">").replace("<##>", "<" + args[0] + ">"));
		
		return true;
	}
}
