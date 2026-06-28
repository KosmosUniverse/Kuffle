package fr.kosmosuniverse.kuffle.options;

import fr.kosmosuniverse.kuffle.core.PlayerData;
import fr.kosmosuniverse.kuffle.datamanagers.crafts.CraftManager;
import fr.kosmosuniverse.kuffle.datamanagers.targets.TargetManager;

import java.util.ArrayList;

/**
 * @author KosmosUniverse
 */
public class SbttOption extends OptionDecorator {
    protected SbttOption(GameOption option) {
        super(option);
    }

    @Override
    public void initOption() {
        super.initOption();

        CraftManager.setupCraftTemplates();
    }

    @Override
    public String newSbtt(PlayerData playerData) {
        return TargetManager.newSbtt(new ArrayList<>(), playerData.getAge().getName());
    }
}
