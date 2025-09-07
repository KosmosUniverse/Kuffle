package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.core.Config;
import fr.kosmosuniverse.kuffle.core.LangManager;
import fr.kosmosuniverse.kuffle.core.LogManager;
import fr.kosmosuniverse.kuffle.core.TeamManager;

public class KuffleTeamInv extends AKuffleCommand {

	public KuffleTeamInv() {
		super("k-team-inv", null, true, 0, 0, true);
	}
	
	@Override
	protected boolean runCommand() {
		if (!Config.getTeamInv()) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_INV", Config.getLang()));
		} else {
			player.openInventory(TeamManager.getInstance().getTeamInventory(TeamManager.getInstance().getTeamByPlayer(player.getName()).getName()));
		}
		return true;
	}

}
