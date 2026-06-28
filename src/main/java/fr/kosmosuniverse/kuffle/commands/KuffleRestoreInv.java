package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.core.PartyTmp;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleRestoreInv extends AKuffleCommand {
	public KuffleRestoreInv() {
		super("k-restoreinv", true, true, 1, 1, false);
	}

	@Override
	public boolean runCommand() {
		if (args.length != 1) {
			return false;
		}
		
		if (!PartyTmp.getInstance().getPlayers().has(args[0])) {
			return true;
		}
		
		PartyTmp.getInstance().getGameManager().getPlayerStoredInv(player, args[0]);

		return true;
	}
}
