package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.KuffleMain;
import fr.kosmosuniverse.kuffle.core.LogManager;
import fr.kosmosuniverse.kuffle.core.PartyTmp;
import fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
import fr.kosmosuniverse.kuffle.mode.Mode;
import fr.kosmosuniverse.kuffle.utils.Pair;
import fr.kosmosuniverse.kuffle.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleSetMode extends AKuffleCommand {
	private Pair confirm = null;
	
	public KuffleSetMode() {
		super("k-set-mode", null, false, 1, 1, false);
	}

	@Override
	public boolean runCommand() throws KuffleCommandFalseException {
		Mode mode;
		
		try {
			mode = Mode.valueOf(args[0].toUpperCase());
		} catch (IllegalArgumentException e) {
			LogManager.getInstanceSystem().writeMsg(player, "[ERROR] Unknown Kuffle Type");
			return false;
		}
		
		if (PartyTmp.getInstance().getGameMode().getMode() == mode) {
			LogManager.getInstanceSystem().writeMsg(player, "Kuffle Type is already set as [" + mode.name() + "]");
			return true;
		}

		if (confirm == null) {				
			firstSubmit(player, name+args[0]);
		} else {
			confirmSubmit(player, name+args[0], mode);
		}

		return true;
	}

	private void firstSubmit(Player player, String key) {
		LogManager.getInstanceSystem().writeMsg(player, "[Warning] : Change Kuffle type takes few seconds to reload resource files.");

		if (PartyTmp.getInstance().getGameMode().getMode() != Mode.NO_MODE) {
			LogManager.getInstanceSystem().writeMsg(player, "[Warning] : Kuffle type is already set. This action will unload current Kuffle type {" + PartyTmp.getInstance().getGameMode().getMode() + "}.");
		}

		confirm = new Pair(player.getUniqueId(), key);
		LogManager.getInstanceSystem().writeMsg(player, "Please, re-send the exact same command within 10sec to confirm.");
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
			if (confirm != null && (confirm.getKey()) == player.getUniqueId()) {
				confirm = null;
				LogManager.getInstanceSystem().writeMsg(player, "[Warning] : Command /k-set-type cancelled.");
			}
		}, 200);
	}

	private void confirmSubmit(Player player, String key, Mode mode) {
		if ((confirm.getKey()) != player.getUniqueId()) {
			LogManager.getInstanceSystem().writeMsg(player, "Please wait because another player in setting the kuffle game type.");
			return ;
		} else if (!confirm.getValue().toString().equals(key)) {
			LogManager.getInstanceSystem().writeMsg(player, "Please send the exact same command as before or wait for the end of the 10s to choose another Kuffle Type.");
			return ;
		}

		confirm = null;

		try {
			PartyTmp.getInstance().setMode(mode);
			LogManager.getInstanceSystem().writeMsg(player, "Kuffle type set as [" + mode.name() + "].");
		} catch (KuffleFileLoadException e) {
			Utils.logException(e);
			LogManager.getInstanceSystem().writeMsg(player, "File load fails, type cleared.");
		}
	}
}
