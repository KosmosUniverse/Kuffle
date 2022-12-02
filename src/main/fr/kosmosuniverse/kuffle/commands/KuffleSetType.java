package main.fr.kosmosuniverse.kuffle.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
import main.fr.kosmosuniverse.kuffle.type.KuffleBlocks;
import main.fr.kosmosuniverse.kuffle.type.KuffleItems;
import main.fr.kosmosuniverse.kuffle.type.KuffleType;
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
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		LogManager.getInstanceSystem().logMsg(player.getName(), LangManager.getMsgLang("CMD_PERF", Config.getLang()).replace("<#>", "<k-set-type>"));
		
		if (!player.hasPermission("k-set-type")) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_ALLOWED", GameManager.getPlayerLang(player.getName())));
			return false;
		}
		
		if (args.length != 1) {
			return false;
		}
		
		if (KuffleMain.gameStarted) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_LAUNCHED", Config.getLang()));
			return true;
		}
		
		KuffleType.Type type;
		
		try {
			type = KuffleType.Type.valueOf(args[0].toUpperCase());
		} catch (IllegalArgumentException e) {
			LogManager.getInstanceSystem().writeMsg(player, "[ERROR] Unknown Kuffle Type");
			return true;
		}
		
		if (KuffleMain.type.getType() == type) {
			LogManager.getInstanceSystem().writeMsg(player, "Kuffle Type is already set as [" + type.name() + "]");
			return true;
		}
		
		if (confirm == null) {				
			LogManager.getInstanceSystem().writeMsg(player, "[Warning] : Change Kuffle type takes few seconds to reload resource files.");
			
			if (KuffleMain.type.getType() != KuffleType.Type.NO_TYPE) {
				LogManager.getInstanceSystem().writeMsg(player, "[Warning] : Kuffle type is already set. This action will unload getInstance() Kuffle type {" + KuffleMain.type.getType() + "}.");
			}
			
			confirm = new Pair(player.getUniqueId(), msg+args[0]);
			LogManager.getInstanceSystem().writeMsg(player, "Please, re-send the exact same command within 10sec to confirm.");
			Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
				if (confirm != null && ((UUID) confirm.getKey()) == player.getUniqueId()) {
					confirm = null;
					LogManager.getInstanceSystem().writeMsg(player, "[Warning] : Command /k-set-type cancelled.");
				}
			}, 100);
		} else {
			if (((UUID) confirm.getKey()) != player.getUniqueId()) {
				LogManager.getInstanceSystem().writeMsg(player, "Please wait because another player in setting the kuffle game type.");
				return true;
			} else if (!confirm.getValue().toString().equals(msg+args[0])) {
				LogManager.getInstanceSystem().writeMsg(player, "Please send the exact same command as before or wait for the end of the 10s to choose another Kuffle Type.");
				return true;
			}
			
			confirm = null;
			
			try {
				changeKuffleType(player, type);
			} catch (KuffleFileLoadException e) {
				Utils.logException(e);
				LogManager.getInstanceSystem().writeMsg(player, "File load fails, type cleared.");
			}
		}

		return true;
	}
	
	public static void changeKuffleType(Player player, KuffleType.Type type) throws KuffleFileLoadException {
		KuffleMain.type = KuffleMain.type.clearType();
		
		switch (type) {
			case ITEMS:
				KuffleMain.type = new KuffleItems(KuffleMain.type, KuffleMain.getInstance());
				break;
			case BLOCKS:
				KuffleMain.type = new KuffleBlocks(KuffleMain.type, KuffleMain.getInstance());
				break;
			case NO_TYPE:
			default:
				break;
		}
		
		LogManager.getInstanceSystem().writeMsg(player, "Kuffle type set as [" + type.name() + "].");
	}
}
