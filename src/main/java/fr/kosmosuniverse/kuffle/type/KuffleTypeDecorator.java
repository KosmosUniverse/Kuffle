package fr.kosmosuniverse.kuffle.type;

import fr.kosmosuniverse.kuffle.KuffleMain;
import fr.kosmosuniverse.kuffle.core.*;

import java.util.Objects;

/**
 * 
 * @author KosmosUniverse
 *
 */
public abstract class KuffleTypeDecorator extends KuffleType {
	protected final KuffleType type;
	
	protected KuffleTypeDecorator(KuffleType kuffleType) {
		type = kuffleType;
	}
	
	@Override
	public KuffleType clearType() {
		Party.getInstance().clear();
		ScoreManager.clear();
		CraftManager.clear();
		RewardManager.clear();
		TargetManager.clear();
		
		Objects.requireNonNull(KuffleMain.getInstance().getCommand("k-agetargets")).setExecutor(null);
		Objects.requireNonNull(KuffleMain.getInstance().getCommand("k-crafts")).setExecutor(null);
		
		Objects.requireNonNull(KuffleMain.getInstance().getCommand("k-agetargets")).setTabCompleter(null);
		
		return type;
	}
}
