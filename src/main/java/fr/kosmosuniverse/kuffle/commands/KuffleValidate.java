package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.core.Config;
import fr.kosmosuniverse.kuffle.core.PartyTmp;
import fr.kosmosuniverse.kuffle.datamanagers.LangManager;
import fr.kosmosuniverse.kuffle.core.LogManager;
import fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import org.bukkit.Bukkit;

public class KuffleValidate extends AKuffleCommand {
	public KuffleValidate() {
		super("k-validate", null, true, 1, 1, false);
	}

	@Override
	public boolean runCommand() throws KuffleCommandFalseException {
		if (!PartyTmp.getInstance().getPlayers().has(args[0])) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("VALIDATE_PLAYER_ITEM", Config.getLang()));
			throw new KuffleCommandFalseException();
		}

		LogManager.getInstanceSystem()
				.writeMsg(player, LangManager.getMsgLang("ITEM_VALIDATED", Config.getLang()).replace("[#]", " [" + PartyTmp.getInstance().getGameManager().getPlayerTarget(args[0]) + "] ").replace("<#>", "<" + args[0] + ">"));
		PartyTmp.getInstance().targetFound(Bukkit.getPlayer(args[0]));
		
		return true;
	}
}
