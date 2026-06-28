package fr.kosmosuniverse.kuffle.options;

import fr.kosmosuniverse.kuffle.core.PlayerData;
import fr.kosmosuniverse.kuffle.datamanagers.targets.TargetManager;
import fr.kosmosuniverse.kuffle.utils.Pair;

/**
 * @author KosmosUniverse
 */
public class SameOption extends OptionDecorator {
    protected SameOption(GameOption option) {
        super(option);
    }

    @Override
    public void initOption() {
        super.initOption();

        TargetManager.shuffleTargets();
    }

    @Override
    public String newTarget(PlayerData playerData) {
        Pair target = TargetManager.nextTarget(playerData.getAlreadyGot(),
                playerData.getAge().getName(),
                playerData.getSameIdx());

        playerData.setSameIdx((int) target.getKey());

        return (String) target.getValue();
    }
}
