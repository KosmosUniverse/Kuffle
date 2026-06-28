package fr.kosmosuniverse.kuffle.options;

import fr.kosmosuniverse.kuffle.core.Config;
import fr.kosmosuniverse.kuffle.core.PartyTmp;
import fr.kosmosuniverse.kuffle.core.PlayerData;
import fr.kosmosuniverse.kuffle.utils.Utils;
import net.md_5.bungee.api.ChatColor;

/**
 * @author KosmosUniverse
 */
public class CoopOption extends OptionDecorator {
    long timer;
    long interval = -1;

    protected CoopOption(GameOption option) {
        super(option);
    }

    @Override
    public void initOption() {
        super.initOption();

        timer = 15 * 60000L;
    }

    @Override
    public void launch() {
        super.launch();

        if (interval == -1) {
            timer = System.currentTimeMillis() + timer;
        } else {
            timer = System.currentTimeMillis() + interval;
        }
    }

    @Override
    public void pause() {
        interval = timer -  System.currentTimeMillis();
    }

    @Override
    public void resume() {
        timer = System.currentTimeMillis() + interval;
    }

    @Override
    public void stop() {
        if (specificEndCondition()) {
            PartyTmp.getInstance().sendAll("Coop Timer hit 0 ! All remaining players are forced abandon. Game is finished.");
            PartyTmp.getInstance().getGameManager().forceLastFinish();
        } else {
            super.stop();
        }
    }

    @Override
    public void targetFound() {
        super.targetFound();

        timer += (Config.getCoopUpdated() * 60000L);
    }

    @Override
    public String getPlayerBarString(PlayerData playerData) {
        String bossBarTitle = super.getPlayerBarString(playerData);

        return getGlobalTimerStrig() + bossBarTitle;
    }

    @Override
    public boolean checkEndCondition() {
        return super.checkEndCondition() || specificEndCondition();
    }

    @Override
    public long getGlobalTimerInterval() {
        return timer - System.currentTimeMillis();
    }

    @Override
    public void setGlobalTimerInterval(long globalTimerInterval) {
        interval = globalTimerInterval;
    }

    private String getGlobalTimerStrig() {
        long rest = timer - System.currentTimeMillis();

        rest = rest <= 0 ? 0 : rest;

        return getColor(rest / 60000L) + Utils.getTimeFromSec(rest / 1000L) + ChatColor.RESET + " - ";
    }

    private ChatColor getColor(long count) {
        ChatColor color;

        if (count < (15 * 15 / 100)) {
            color = ChatColor.RED;
        } else if (count < (15 * 50 / 100)) {
            color = ChatColor.YELLOW;
        } else {
            color = ChatColor.GREEN;
        }

        return color;
    }

    private boolean specificEndCondition() {
        return timer - System.currentTimeMillis() <= 0;
    }

    public void targetSkipped(PlayerData playerData) {
        if (Config.getCoopSkip()) {
            timer = timer - (Config.getCoopUpdated() * 60000L);
        }
    }
}
