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
	
	public KuffleTypeDecorator(KuffleType _type) {
		type = _type;
	}
	
	@Override
	public KuffleType clearType() {
		GameManager.clear();
		ScoreManager.clear();
		CraftManager.clear();
		RewardManager.clear();
		TargetManager.clear();
		
		KuffleMain.current.getCommand("k-agetargets").setExecutor(null);
		KuffleMain.current.getCommand("k-crafts").setExecutor(null);
		
		KuffleMain.current.getCommand("k-agetargets").setTabCompleter(null);
		
		return type;
	}
}
