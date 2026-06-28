package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.core.*;
import fr.kosmosuniverse.kuffle.datamanagers.LangManager;
import fr.kosmosuniverse.kuffle.event.NextAgeEvent;
import fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import org.bukkit.Bukkit;

public class KuffleValidateAge extends AKuffleCommand {
	public KuffleValidateAge() {
		super("k-validate-age", null, true, 1, 1, false);
	}

	@Override
	public boolean runCommand() throws KuffleCommandFalseException {
		if (!PartyTmp.getInstance().getPlayers().has(args[0])) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("VALIDATE_PLAYER_AGE", Config.getLang()));
			throw new KuffleCommandFalseException();
		}
		
		if (PartyTmp.getInstance().getGameManager().getPlayerAge(args[0]).getNumber() == -1) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_ALREADY_FINISHED", Config.getLang()).replace("<#>", "<" + args[0] + ">"));
			throw new KuffleCommandFalseException();
		}

		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("AGE_VALIDATED", Config.getLang()).replace("[#]", "[" + PartyTmp.getInstance().getGameManager().getPlayerAge(args[0]).getName() + "]").replace("<#>", "<" + args[0] + ">"));
		Bukkit.getPluginManager().callEvent(new NextAgeEvent(Bukkit.getPlayer(args[0])));
		
		return true;
	}
}
