package main.fr.kosmosuniverse.kuffle.listeners;

import java.lang.reflect.InvocationTargetException;

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
	 * @throws ClassNotFoundException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	@EventHandler
	public void onLeftClick(PlayerInteractEvent event) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		try {
			if (onLeftClickGeneric(event)) {
				return ;
			}
		} catch (KuffleEventNotUsableException e) {
			return ;
		}	
	}
}