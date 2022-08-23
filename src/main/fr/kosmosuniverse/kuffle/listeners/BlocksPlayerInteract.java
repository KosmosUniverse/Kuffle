package main.fr.kosmosuniverse.kuffle.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import main.fr.kosmosuniverse.kuffle.exceptions.KuffleEventNotUsableException;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class BlocksPlayerInteract extends PlayerInteract implements Listener {
	
	/**
	 * Constructor
	 */
	public BlocksPlayerInteract() {
		super();
	}

	/**
	 * Manages the behavior of player left click specific for Blocks Kuffle type
	 * 
	 * @param event
	 */
	@EventHandler
	public void onLeftClick(PlayerInteractEvent event) {
		try {
			if (onLeftClickGeneric(event)) {
				return ;
			}
		} catch (KuffleEventNotUsableException e) {
			return ;
		}	
	}
}