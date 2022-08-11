package main.fr.kosmosuniverse.kuffle.type;

import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.parser.ParseException;

import main.fr.kosmosuniverse.kuffle.core.RewardManager;
import main.fr.kosmosuniverse.kuffle.core.TargetManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
import main.fr.kosmosuniverse.kuffle.utils.FilesConformity;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

public class KuffleBlocks extends KuffleType {
	public KuffleBlocks(JavaPlugin plugin) throws KuffleFileLoadException {
		super(plugin);
	}

	@Override
	protected void setupTypeResources(JavaPlugin plugin) throws KuffleFileLoadException  {
		try {
			TargetManager.setupTargets(FilesConformity.getContent("blocks_1.15.json"));
			TargetManager.setupSbtts(FilesConformity.getContent("sbtts_blocks_1.15.json"));
		} catch (IllegalArgumentException | ParseException e) {
			Utils.logException(e);
			TargetManager.clear();
			
			throw new KuffleFileLoadException("Items or Sbtts load failed !");
		}
		
		try {
			RewardManager.setupRewards(FilesConformity.getContent("rewards_blocks_1.15.json"));
		} catch (IllegalArgumentException | ParseException e) {
			Utils.logException(e);
			TargetManager.clear();
			
			throw new KuffleFileLoadException("Rewards load failed !");
		}
	}
}
