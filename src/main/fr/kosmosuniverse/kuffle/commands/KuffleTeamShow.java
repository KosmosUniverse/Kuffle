package main.fr.kosmosuniverse.kuffle.commands;

import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.TeamManager;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleTeamShow extends AKuffleCommand {
	public KuffleTeamShow() {
		super("k-team-show", null, null, 0, 1, true);
	}

	@Override
	public boolean runCommand() {
		if (args.length == 0) {
			LogManager.getInstanceSystem().writeMsg(player, TeamManager.getInstance().printTeams());
		} else if (args.length == 1) {
			if (TeamManager.getInstance().hasTeam(args[0])) {
				LogManager.getInstanceSystem().writeMsg(player, TeamManager.getInstance().printTeam(args[0]));
			} else {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_NOT_EXISTS", Config.getLang()).replace("<#>", "<" + args[0] + ">"));
			}
		}
		
		return true;
	}
}
