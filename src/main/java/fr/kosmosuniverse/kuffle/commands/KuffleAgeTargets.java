package fr.kosmosuniverse.kuffle.commands;

import java.util.List;

import fr.kosmosuniverse.kuffle.core.*;
import org.bukkit.inventory.Inventory;

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
			if (Party.getInstance().getStatus() == GameStatus.RUNNING) {
				if (!Party.getInstance().getPlayers().has(player.getName())) {
					LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_PLAYING", Party.getInstance().getGames().getGames().get(player.getName()).getConfigLang()));
					return true;
				}
				
				age = AgeManager.getAgeByNumber(Party.getInstance().getGames().getGames().get(player.getName()).getAge()).getName();
			} else {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_NOT_LAUNCHED", Config.getLang()));
				return true;
			}
		} else {
			age = args[0];
			
			if (!AgeManager.ageExists(age)) {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("AGE_NOT_EXISTS", Config.getLang()));
				return false;
			}
		}
		
		List<Inventory> ageItems = TargetManager.getAgeTargetsInvs(age);
		
		player.openInventory(ageItems.get(0));
		
		return true;
	}
}
