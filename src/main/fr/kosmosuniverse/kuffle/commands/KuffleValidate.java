package main.fr.kosmosuniverse.kuffle.commands;

import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;

public class KuffleValidate extends AKuffleCommand {
	public KuffleValidate() {
		super("k-validate", null, true, 1, 1, false);
	}

	@Override
	public boolean runCommand() {
		LogManager.getInstanceSystem().logMsg(player.getName(), LangManager.getMsgLang("CMD_PERF", Config.getLang()).replace("<#>", "<k-validate>"));
		
		if (!player.hasPermission("k-validate")) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_ALLOWED", Config.getLang()));
			return false;
		}
		
		if (!GameManager.hasPlayer(args[0])) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("VALIDATE_PLAYER_ITEM", Config.getLang()));
			return true;
		}

		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("ITEM_VALIDATED", Config.getLang()).replace("[#]", " [" + GameManager.getPlayerTarget(args[0]) + "] ").replace("<#>", "<" + args[0] + ">"));			
		GameManager.playerFoundTarget(args[0]);
		
		return true;
	}
}
