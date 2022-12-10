package main.fr.kosmosuniverse.kuffle.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
import main.fr.kosmosuniverse.kuffle.type.KuffleBlocks;
import main.fr.kosmosuniverse.kuffle.type.KuffleItems;
import main.fr.kosmosuniverse.kuffle.type.KuffleType;
import main.fr.kosmosuniverse.kuffle.utils.CommandUtils;
import main.fr.kosmosuniverse.kuffle.utils.Pair;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleSetType implements CommandExecutor  {
	private Pair confirm = null;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cnd, String msg, String[] args) {
		Player player;
		
		try {
			player = CommandUtils.initCommand(sender, "k-set-type", false, true, false);
		} catch (KuffleCommandFalseException e1) {
			return false;
		}
		
		if (args.length != 1) {
			return false;
		}
		
		KuffleType.Type type;
		
		try {
			type = KuffleType.Type.valueOf(args[0].toUpperCase());
		} catch (IllegalArgumentException e) {
			LogManager.getInstanceSystem().writeMsg(player, "[ERROR] Unknown Kuffle Type");
			return true;
		}
		
		if (KuffleMain.getInstance().getType().getType() == type) {
			LogManager.getInstanceSystem().writeMsg(player, "Kuffle Type is already set as [" + type.name() + "]");
			return true;
		}
		
		if (confirm == null) {				
			firstSubmit(player, msg+args[0]);
		} else {
			confirmSubmit(player, msg+args[0], type);
		}

		return true;
	}
	
	private void firstSubmit(Player player, String key) {
		LogManager.getInstanceSystem().writeMsg(player, "[Warning] : Change Kuffle type takes few seconds to reload resource files.");
		
		if (KuffleMain.getInstance().getType().getType() != KuffleType.Type.NO_TYPE) {
			LogManager.getInstanceSystem().writeMsg(player, "[Warning] : Kuffle type is already set. This action will unload current Kuffle type {" + KuffleMain.getInstance().getType().getType() + "}.");
		}
		
		confirm = new Pair(player.getUniqueId(), key);
		LogManager.getInstanceSystem().writeMsg(player, "Please, re-send the exact same command within 10sec to confirm.");
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
			if (confirm != null && ((UUID) confirm.getKey()) == player.getUniqueId()) {
				confirm = null;
				LogManager.getInstanceSystem().writeMsg(player, "[Warning] : Command /k-set-type cancelled.");
			}
		}, 100);
	}
	
	private void confirmSubmit(Player player, String key, KuffleType.Type type) {
		if (((UUID) confirm.getKey()) != player.getUniqueId()) {
			LogManager.getInstanceSystem().writeMsg(player, "Please wait because another player in setting the kuffle game type.");
			return ;
		} else if (!confirm.getValue().toString().equals(key)) {
			LogManager.getInstanceSystem().writeMsg(player, "Please send the exact same command as before or wait for the end of the 10s to choose another Kuffle Type.");
			return ;
		}
		
		confirm = null;
		
		try {
			changeKuffleType(player, type);
		} catch (KuffleFileLoadException e) {
			Utils.logException(e);
			LogManager.getInstanceSystem().writeMsg(player, "File load fails, type cleared.");
		}
	}
	
	public static void changeKuffleType(Player player, KuffleType.Type type) throws KuffleFileLoadException {
		KuffleMain.getInstance().setType(KuffleMain.getInstance().getType().clearType());
		
		switch (type) {
			case ITEMS:
				KuffleMain.getInstance().setType(new KuffleItems(KuffleMain.getInstance().getType(), KuffleMain.getInstance()));
				break;
			case BLOCKS:
				KuffleMain.getInstance().setType(new KuffleBlocks(KuffleMain.getInstance().getType(), KuffleMain.getInstance()));
				break;
			case NO_TYPE:
			default:
				break;
		}
		
		LogManager.getInstanceSystem().writeMsg(player, "Kuffle type set as [" + type.name() + "].");
	}
}
