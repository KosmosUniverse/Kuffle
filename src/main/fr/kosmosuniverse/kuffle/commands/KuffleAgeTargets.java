package main.fr.kosmosuniverse.kuffle.commands;

import java.util.List;

import org.bukkit.inventory.Inventory;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.AgeManager;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.TargetManager;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleAgeTargets extends AKuffleCommand {
	public KuffleAgeTargets() {
		super("k-agetargets", true, null, 0, 1, false);
	}

	@Override
	public boolean runCommand() {
		String age;
		
		if (args.length == 0) {
			if (KuffleMain.getInstance().isStarted()) {
				if (!GameManager.hasPlayer(player.getName())) {
					LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_PLAYING", GameManager.getPlayerLang(player.getName())));
					return true;
				}
				
				age = GameManager.getPlayerAge(player.getName()).getName();
			} else {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_NOT_LAUNCHED", Config.getLang()));			
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
