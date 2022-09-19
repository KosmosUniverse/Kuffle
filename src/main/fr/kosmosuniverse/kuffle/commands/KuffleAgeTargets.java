package main.fr.kosmosuniverse.kuffle.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.AgeManager;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.TargetManager;
import main.fr.kosmosuniverse.kuffle.type.KuffleType;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleAgeTargets implements CommandExecutor  {
	@Override
	public boolean onCommand(CommandSender sender, Command cnd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		LogManager.getInstanceSystem().logMsg(player.getName(), LangManager.getMsgLang("CMD_PERF", Config.getLang()).replace("<#>", "<k-agetargets>"));
		
		if (!player.hasPermission("k-agetargets")) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_ALLOWED", Config.getLang()));
			return false;
		}
		
		if (KuffleMain.type.getType() == KuffleType.Type.UNKNOWN) {
			LogManager.getInstanceSystem().writeMsg(player, "Kuffle type not configured, please set it with /k-set-type");
			return true;
		}
		
		if (args.length > 1) {
			return false;
		}
		
		String age;
		
		if (args.length == 0) {
			if (KuffleMain.gameStarted) {
				if (!GameManager.hasPlayer(player.getName())) {
					LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_PLAYING", GameManager.getPlayerLang(player.getName())));
					return true;
				}
				
				age = GameManager.getPlayerAge(player.getName()).name;
			} else {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_NOT_LAUNCHED", GameManager.getPlayerLang(player.getName())));			
				return true;
			}
		} else {
			age = args[0];
			
			if (!AgeManager.ageExists(age)) {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("AGE_NOT_EXISTS", GameManager.getPlayerLang(player.getName())));
				return false;
			}
		}
		
		List<Inventory> ageItems = TargetManager.getAgeTargetsInvs(age);
		
		player.openInventory(ageItems.get(0));
		
		return true;
	}
}
