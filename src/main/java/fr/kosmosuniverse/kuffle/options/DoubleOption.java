package fr.kosmosuniverse.kuffle.options;

import fr.kosmosuniverse.kuffle.core.PlayerData;
import fr.kosmosuniverse.kuffle.datamanagers.LangManager;

/**
 * @author KosmosUniverse
 */
public class DoubleOption extends OptionDecorator {
    protected DoubleOption(GameOption option) {
        super(option);
    }

    @Override
    public String newTarget(PlayerData playerData) {
        String target = super.newTarget(playerData);

        return target + "/" + super.newTarget(playerData);
    }

    @Override
    public String getTargetDisplay(PlayerData playerData) {
        String[] targets = playerData.getCurrentTarget().split("/");

        return LangManager.getTargetLang(targets[0], playerData.getConfigLang()) + "/" +
                LangManager.getTargetLang(targets[1], playerData.getConfigLang());
    }

    @Override
    public boolean checkPlayerTarget(String target, String targetToTest) {
        if (target == null) {
            return false;
        }

        String[] targets = target.split("/");

        return super.checkPlayerTarget(targets[0], targetToTest) || super.checkPlayerTarget(targets[1], targetToTest);
    }

    @Override
    public String getTargetMsg(PlayerData playerData) {
        return LangManager.getMsgLang("TARGET_DOUBLE", playerData.getConfigLang()).replace("[#]", playerData.getCurrentTargetDisplay().split("/")[0]).replace("[##]", playerData.getCurrentTargetDisplay().split("/")[1]);
    }

    @Override
    public String getSkipStr(PlayerData playerData) {
        return LangManager.getMsgLang("ITEMS_SKIP", playerData.getConfigLang()).replace("[#]", "[" + playerData.getCurrentTarget().split("/")[0] + "]").replace("[##]", "[" + playerData.getCurrentTarget().split("/")[1] + "]");
    }
}
