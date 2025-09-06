package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.core.*;
import fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import fr.kosmosuniverse.kuffle.utils.CommandUtils;
import org.bukkit.ChatColor;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleTeamColor extends AKuffleCommand {
	public KuffleTeamColor() {
		super("k-team-color", null, false, 2, 2, true);
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
		if (TeamManager.getInstance().getTeam(args[0]).hasPlayer(args[1])) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_PLAYER", Config.getLang()));
			throw new KuffleCommandFalseException();
		}
		
		ChatColor tmp = CommandUtils.checkTeamColor(player, args[1]);
		
		if (tmp == null) {
			throw new KuffleCommandFalseException();
		}

		String tmpColor = TeamManager.getInstance().getTeam(args[0]).getColor().name();
		
		TeamManager.getInstance().changeTeamColor(args[0], tmp);	
		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("COLOR_CHANGED", Config.getLang()).replace("[#]", "[" + tmpColor + "]").replace("[##]", "[" + tmp.name() + "]").replace("<#>",	"<" + args[0] + ">"));
		
		return true;
	}
}
