package main.fr.kosmosuniverse.kuffle.type;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.CraftManager;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.RewardManager;
import main.fr.kosmosuniverse.kuffle.core.ScoreManager;
import main.fr.kosmosuniverse.kuffle.core.TargetManager;

/**
 * 
 * @author KosmosUniverse
 *
 */
public abstract class KuffleTypeDecorator extends KuffleType {
	protected KuffleType type;
	
	protected KuffleTypeDecorator(KuffleType kuffleType) {
		type = kuffleType;
	}
	
	@Override
	public KuffleType clearType() {
		GameManager.clear();
		ScoreManager.clear();
		CraftManager.clear();
		RewardManager.clear();
		TargetManager.clear();
		
		KuffleMain.getInstance().getCommand("k-agetargets").setExecutor(null);
		KuffleMain.getInstance().getCommand("k-crafts").setExecutor(null);
		
		KuffleMain.getInstance().getCommand("k-agetargets").setTabCompleter(null);
		
		return type;
	}
}
