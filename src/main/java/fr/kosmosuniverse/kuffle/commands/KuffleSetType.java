package fr.kosmosuniverse.kuffle.commands;

import java.util.UUID;

import fr.kosmosuniverse.kuffle.KuffleMain;
import fr.kosmosuniverse.kuffle.core.LogManager;
import fr.kosmosuniverse.kuffle.core.Party;
import fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
import fr.kosmosuniverse.kuffle.type.KuffleType;
import fr.kosmosuniverse.kuffle.utils.Pair;
import fr.kosmosuniverse.kuffle.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleSetType extends AKuffleCommand {
	private Pair confirm = null;
	
	public KuffleSetType() {
		super("k-set-type", null, false, 1, 1, false);
	}

	@Override
	public boolean runCommand() throws KuffleCommandFalseException {
		KuffleType.Type type;
		
		try {
			type = KuffleType.Type.valueOf(args[0].toUpperCase());
		} catch (IllegalArgumentException e) {
			LogManager.getInstanceSystem().writeMsg(player, "[ERROR] Unknown Kuffle Type");
			throw new KuffleCommandFalseException();
		}
		
		if (Party.getInstance().getType().getType() == type) {
			LogManager.getInstanceSystem().writeMsg(player, "Kuffle Type is already set as [" + type.name() + "]");
			throw new KuffleCommandFalseException();
		}
		
		if (confirm == null) {				
			firstSubmit(player, name+args[0]);
		} else {
			confirmSubmit(player, name+args[0], type);
		}

		return true;
	}

	private void firstSubmit(Player player, String key) {
		LogManager.getInstanceSystem().writeMsg(player, "[Warning] : Change Kuffle type takes few seconds to reload resource files.");

		if (Party.getInstance().getType().getType() != KuffleType.Type.NO_TYPE) {
			LogManager.getInstanceSystem().writeMsg(player, "[Warning] : Kuffle type is already set. This action will unload current Kuffle type {" + Party.getInstance().getType().getType() + "}.");
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

	private void confirmSubmit(Player player, String key, KuffleType.Type type) {
		if ((confirm.getKey()) != player.getUniqueId()) {
			LogManager.getInstanceSystem().writeMsg(player, "Please wait because another player in setting the kuffle game type.");
			return ;
		} else if (!confirm.getValue().toString().equals(key)) {
			LogManager.getInstanceSystem().writeMsg(player, "Please send the exact same command as before or wait for the end of the 10s to choose another Kuffle Type.");
			return ;
		}

		confirm = null;

		try {
			Party.getInstance().setType(player, type);
			LogManager.getInstanceSystem().writeMsg(player, "Kuffle type set as [" + type.name() + "].");
		} catch (KuffleFileLoadException e) {
			Utils.logException(e);
			LogManager.getInstanceSystem().writeMsg(player, "File load fails, type cleared.");
		}
	}
}
