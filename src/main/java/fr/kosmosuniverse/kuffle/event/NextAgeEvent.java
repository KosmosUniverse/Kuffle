package fr.kosmosuniverse.kuffle.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * @author KosmosUniverse
 */
public class NextAgeEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    @Getter
    private final Player player;

    public NextAgeEvent(Player player) {
        this.player = player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
