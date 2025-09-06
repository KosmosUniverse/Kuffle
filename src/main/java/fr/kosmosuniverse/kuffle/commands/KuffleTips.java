package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.core.Config;
import fr.kosmosuniverse.kuffle.core.LangManager;
import fr.kosmosuniverse.kuffle.core.LogManager;
import fr.kosmosuniverse.kuffle.core.Party;
import fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleTips extends AKuffleCommand {
	public KuffleTips() {
		super("k-tips", null, true, 0, 1, false);
	}

	@Override
	public boolean runCommand() throws KuffleCommandFalseException {
		if (!Party.getInstance().getPlayers().has(player.getName())) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("PLAYER_NOT_IN_LIST", Config.getLang()));
		}
		
		if (args.length != 1) {
			return false;
		}

		String sTips = args[0].toLowerCase();

		if (!"true".equals(sTips) && !"false".equals(sTips)) {
			return false;
		}

		boolean tips = "true".equals(sTips);

		Party.getInstance().getGames().getGames().get(player.getName()).setTips(tips);

		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TIPS_SET", Config.getLang()).replace("[#]", " [" + tips + "]"));

		return true;
	}

}
