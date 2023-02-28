package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.ChatColor;

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
public class KuffleTeamCreate extends AKuffleCommand {
	public KuffleTeamCreate() {
		super("k-team-create", null, false, 1, 2, true);
	}

	@Override
	public boolean runCommand() throws KuffleCommandFalseException {
		if (GameManager.getGames().size() > 0) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_LAUNCHED", Config.getLang()));
			throw new KuffleCommandFalseException();
		}
		
		if (TeamManager.getInstance().hasTeam(args[0])) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_EXISTS", Config.getLang()).replace("<#>", "<" + args[0] + ">"));
			throw new KuffleCommandFalseException();
		}
		
		if (args.length == 1) {
			TeamManager.getInstance().createTeam(args[0]);
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_CREATED", Config.getLang()).replace("<#>", "<" + args[0] + ">"));
		} else if (args.length == 2) {
			ChatColor tmp = CommandUtils.checkTeamColor(player, args[1]);
			
			if (tmp == null) {
				throw new KuffleCommandFalseException();
			}
			
			TeamManager.getInstance().createTeam(args[0], tmp);
			
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_CREATED", Config.getLang()).replace("<#>", "<" + args[0] + ">"));
		}
		
		return true;
	}
}
