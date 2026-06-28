package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.core.Config;
import fr.kosmosuniverse.kuffle.core.PartyTmp;
import fr.kosmosuniverse.kuffle.datamanagers.LangManager;
import fr.kosmosuniverse.kuffle.core.LogManager;

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
			if (PartyTmp.getInstance().getSpectators().has(player.getName())) {
				PartyTmp.getInstance().getSpectators().addPlayer(player.getName());
			} else {
				PartyTmp.getInstance().getSpectators().removePlayer(player.getName());
			}
		} else if (args.length == 1) {
			return doOneArg(args[0]);
		}

		return true;
	}

	/**
	 * Empty the spectators list
	 * 
	 * @param firstArg	The first arg (must be "reset")
	 * 
	 * @return True if success, False instead
	 */
	private boolean doOneArg(String firstArg) {
		if ("display".equals(firstArg)) {
			String str = PartyTmp.getInstance().getSpectators().getDisplayString();

			if (str.isEmpty()) {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NO_SPEC", Config.getLang()));
			} else {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("SPEC_LIST", Config.getLang()) + " " + str);
			}

			return true;
		} else if ("reset".equals(firstArg)) {
			if (!player.hasPermission("k-op")) {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_ALLOWED", Config.getLang()));
				return true;
			}

			PartyTmp.getInstance().getSpectators().clear();
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("SPEC_LIST_RESET", Config.getLang()));
			
			return true;
		}
		
		return false;
	}
}
