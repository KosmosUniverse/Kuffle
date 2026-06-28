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
public class KuffleLang extends AKuffleCommand {
	public KuffleLang() {
		super("k-lang", null, null, 0, 1, false);
	}

	@Override
	public boolean runCommand() {
		if (!PartyTmp.getInstance().getPlayers().has(player.getName())) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("PLAYER_NOT_IN_LIST", Config.getLang()));
		}
		
		if (args.length == 0) {
			LogManager.getInstanceSystem().writeMsg(player, PartyTmp.getInstance().getGameManager().getPlayerLang(player.getName()));
		} else if (args.length == 1) {
			String lang = args[0].toLowerCase();
			
			if (LangManager.hasLang(lang)) {
				PartyTmp.getInstance().getGameManager().getPlayerLang(player.getName());
				
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("LANG_SET", Config.getLang()).replace("[#]", " [" + lang + "]"));
			} else {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("REQ_LANG_NOT_AVAIL", Config.getLang()));
			}
		}
		
		return true;
	}

}
