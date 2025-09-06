package fr.kosmosuniverse.kuffle.tabcompleters;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.kosmosuniverse.kuffle.core.AgeManager;
import fr.kosmosuniverse.kuffle.core.Party;
import fr.kosmosuniverse.kuffle.core.VersionManager;
import fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import fr.kosmosuniverse.kuffle.type.KuffleType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class KuffleGiveTab extends AKuffleTabCommand {
	private final List<String> list = new ArrayList<>();

	public KuffleGiveTab() {
		super("k-give", 3, 4);
		
		list.add("item");
		list.add("reward");
	}

	@Override
	protected void runCommand() throws KuffleCommandFalseException {
		if (currentArgs.length == 1) {
			ret.addAll(Bukkit.getOnlinePlayers().stream().filter(p -> !Party.getInstance().getPlayers().has(p.getName()) && Party.getInstance().getSpectators().has(p.getName())).map(Player::getName).collect(Collectors.toList()));
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
			
			if (Party.getInstance().getType().getType() == KuffleType.Type.ITEMS) {
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
