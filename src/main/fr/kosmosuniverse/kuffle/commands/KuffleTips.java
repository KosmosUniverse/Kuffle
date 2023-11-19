package main.fr.kosmosuniverse.kuffle.commands;

import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleTips extends AKuffleCommand {
	public KuffleTips() {
		super("k-tips", null, null, 0, 1, false);
	}

	@Override
	public boolean runCommand() throws KuffleCommandFalseException {
		if (!GameManager.hasPlayer(player.getName())) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("PLAYER_NOT_IN_LIST", Config.getLang()));
		}
		
		if (args.length != 1) {
			return false;
		}
		
		if (args.length == 1) {
			String sTips = args[0].toLowerCase();
			
			if (!"true".equals(sTips) && !"false".equals(sTips)) {
				return false;
			}
			
			boolean tips = "true".equals(sTips);
			
			GameManager.setPlayerTips(player.getName(), tips);
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TIPS_SET", Config.getLang()).replace("[#]", " [" + tips + "]"));
		}
		
		return true;
	}

}
