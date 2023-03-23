package main.fr.kosmosuniverse.kuffle.commands;

import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleSpectate extends AKuffleCommand {
	/**
	 * Constructor
	 */
	public KuffleSpectate() {
		super("k-spectate", true, null, 0, 1, false);
	}

	@Override
	public boolean runCommand() {
		if (args.length == 0) {
			if (!GameManager.hasSpectator(player)) {
				GameManager.addSpectator(player);
			} else {
				GameManager.removeSpectator(player);
			}
		} else if (args.length == 1) {
			return doOneArg(args[0]);
		}

		return true;
	}

	/**
	 * Empty the spectators list
	 * 
	 * @param player	The player that used the command
	 * @param firstArg	The first arg (must be "reset")
	 * 
	 * @return True if success, False instead
	 */
	private boolean doOneArg(String firstArg) {
		if ("display".equals(firstArg)) {
			GameManager.displaySpecList(player);

			return true;
		} else if ("reset".equals(firstArg)) {
			if (!player.hasPermission("k-op")) {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_ALLOWED", Config.getLang()));
				return true;
			}
			
			GameManager.resetSpectators();
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("SPEC_LIST_RESET", Config.getLang()));
			
			return true;
		}
		
		return false;
	}
}
