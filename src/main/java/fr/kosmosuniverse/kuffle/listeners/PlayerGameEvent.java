package fr.kosmosuniverse.kuffle.listeners;

import fr.kosmosuniverse.kuffle.core.PartyTmp;
import fr.kosmosuniverse.kuffle.event.NewTargetEvent;
import fr.kosmosuniverse.kuffle.event.NextAgeEvent;
import fr.kosmosuniverse.kuffle.event.SbttFoundEvent;
import fr.kosmosuniverse.kuffle.event.TargetFoundEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author KosmosUniverse
 */
public class PlayerGameEvent implements Listener {
    @EventHandler
    public void onTargetFound(TargetFoundEvent event) {
        PartyTmp.getInstance().targetFound(event.getPlayer()); // Call GameManager player sound method
    }

    @EventHandler
    public void onSbttFound(SbttFoundEvent event) {
        PartyTmp.getInstance().sbttFound(event.getPlayer()); // Call GameManager player sound method
    }

    @EventHandler
    public void onNextAge(NextAgeEvent event) {
        PartyTmp.getInstance().getGameManager().nextPlayerAge(event.getPlayer().getName()); // Call GameManager player sound method
    }

    @EventHandler
    public void onFinish(NextAgeEvent event) {
        PartyTmp.getInstance().getGameManager().finishPlayer(event.getPlayer().getName());
    }

    @EventHandler
    public void onNewTarget(NewTargetEvent event) {
        PartyTmp.getInstance().newTarget(event.getPlayer());
    }
}
