package main.fr.kosmosuniverse.kuffle.tabcompleters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.AgeManager;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.VersionManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import main.fr.kosmosuniverse.kuffle.type.KuffleType;

public class KuffleGiveTab extends AKuffleTabCommand {
	private List<String> list = new ArrayList<>();
	
	public KuffleGiveTab() {
		super("k-give", 3, 4);
		
		list.add("item");
		list.add("reward");
	}

	@Override
	protected void runCommand() throws KuffleCommandFalseException {
		if (currentArgs.length == 1) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!GameManager.hasPlayer(player.getName()) && !GameManager.hasSpectator(player)) {
					ret.add(player.getName());
				}
			}
		} else if (currentArgs.length == 2) {
			ret.addAll(list);
		} else if (currentArgs.length == 3) {
			giveWhat();
		}
	}
	
	/**
	 * make the list of item and reward op can give
	 */
	private void giveWhat() {
		if ("item".equals(currentArgs[1])) {
			ret.addAll(AgeManager.getAgesNameList());
			
			if (KuffleMain.getInstance().getType().getType() == KuffleType.Type.ITEMS) {
				ret.add("EndTeleporter");
				ret.add("OverworldTeleporter");
			}
			
			if (VersionManager.isVersionValid("1.17", null)) {
				ret.add("CoralCompass");
			}
		} else if ("reward".equals(currentArgs[1])) {
			ret.addAll(AgeManager.getAgesNameList());
		}
	}
}
