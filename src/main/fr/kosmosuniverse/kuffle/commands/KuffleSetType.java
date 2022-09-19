package main.fr.kosmosuniverse.kuffle.commands;

import java.util.HashMap;
import java.util.Map;
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
import main.fr.kosmosuniverse.kuffle.utils.Utils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleSetType implements CommandExecutor  {
	private Map<UUID, String> confirm = new HashMap<>();
	
	/**
	 * Clears <confirm> map
	 */
	public void clear() {
		confirm.clear();
	}
	
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
		
		if (!confirm.containsKey(player.getUniqueId())) {				
			LogManager.getInstanceSystem().writeMsg(player, "[Warning] : Change Kuffle type takes few seconds to reload resource files.");
			
			if (KuffleMain.type.getType() != KuffleType.Type.UNKNOWN) {
				LogManager.getInstanceSystem().writeMsg(player, "[Warning] : Kuffle type is already set. This action will unload current Kuffle type {" + KuffleMain.type.getType() + "}.");
			}
			
			confirm.put(player.getUniqueId(), msg+args[0]);
			LogManager.getInstanceSystem().writeMsg(player, "Please, re-send the exact same command within 10sec to confirm.");
			Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () -> {
				if (confirm.containsKey(player.getUniqueId())) {
					confirm.remove(player.getUniqueId());
					LogManager.getInstanceSystem().writeMsg(player, "[Warning] : Command /k-set-type cancelled.");
				}
			}, 100);
		} else {
			if (!confirm.get(player.getUniqueId()).equals(msg+args[0])) {
				return true;
			}
			
			confirm.remove(player.getUniqueId());
			
			try {
				changeKuffleType(player, type);
			} catch (KuffleFileLoadException e) {
				Utils.logException(e);
				LogManager.getInstanceSystem().writeMsg(player, "File load fails, type cleared.");
			}
		}

		return true;
	}
	
	private void changeKuffleType(Player player, KuffleType.Type type) throws KuffleFileLoadException {
		KuffleMain.type = KuffleMain.type.clearType();
		
		switch (type) {
			case ITEMS:
				KuffleMain.type = new KuffleItems(KuffleMain.type, KuffleMain.current);
				break;
			case BLOCKS:
				KuffleMain.type = new KuffleBlocks(KuffleMain.type, KuffleMain.current);
				break;
			case UNKNOWN:
			default:
				break;
		}
		
		LogManager.getInstanceSystem().writeMsg(player, "Kuffle type set as [" + type.name() + "].");
	}
}
