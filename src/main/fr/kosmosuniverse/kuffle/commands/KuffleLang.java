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
public class KuffleLang extends AKuffleCommand {
	public KuffleLang() {
		super("k-lang", null, true, 0, 1, false);
	}

	@Override
	public boolean runCommand() throws KuffleCommandFalseException {
		if (!GameManager.hasPlayer(player.getName())) {
			throw new KuffleCommandFalseException();
		}
		
		if (args.length == 0) {
			LogManager.getInstanceSystem().writeMsg(player, GameManager.getPlayerLang(player.getName()));
		} else if (args.length == 1) {
			String lang = args[0].toLowerCase();
			
			if (LangManager.hasLang(lang)) {
				GameManager.setPlayerLang(player.getName(), lang);
				
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("LANG_SET", Config.getLang()).replace("[#]", " [" + lang + "]"));
			} else {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("REQ_LANG_NOT_AVAIL", Config.getLang()));
			}
		}
		
		return true;
	}

}
