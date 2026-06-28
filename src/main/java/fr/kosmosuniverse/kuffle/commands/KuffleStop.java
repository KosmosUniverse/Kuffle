package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.core.Config;
import fr.kosmosuniverse.kuffle.core.PartyTmp;
import fr.kosmosuniverse.kuffle.datamanagers.LangManager;
import fr.kosmosuniverse.kuffle.core.LogManager;

public class KuffleStop extends AKuffleCommand {
	public KuffleStop() {
		super("k-stop", null, true, 0, 0, false);
	}

	@Override
	public boolean runCommand() {
		PartyTmp.getInstance().stop();
		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_STOPPED", Config.getLang()));
		
		return true;
	}
}
