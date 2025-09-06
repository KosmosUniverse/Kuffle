package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.core.Config;
import fr.kosmosuniverse.kuffle.core.LangManager;
import fr.kosmosuniverse.kuffle.core.LogManager;
import fr.kosmosuniverse.kuffle.core.Party;

public class KuffleStop extends AKuffleCommand {
	public KuffleStop() {
		super("k-stop", null, true, 0, 0, false);
	}

	@Override
	public boolean runCommand() {
		Party.getInstance().stop();
		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_STOPPED", Config.getLang()));
		
		return true;
	}
}
