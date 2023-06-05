package main.fr.kosmosuniverse.kuffle.commands;

import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.TeamManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;

public class KuffleTeamInv extends AKuffleCommand {

	public KuffleTeamInv() {
		super("k-team-inv", null, true, 0, 0, true);
	}
	
	@Override
	protected boolean runCommand() throws KuffleCommandFalseException {
		if (!Config.getTeamInv()) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_INV", Config.getLang()));
			return true;
		}
		
		player.openInventory(TeamManager.getInstance().getTeamInventory(GameManager.getPlayerTeamName(player.getName())));
		
		return true;
	}

}
