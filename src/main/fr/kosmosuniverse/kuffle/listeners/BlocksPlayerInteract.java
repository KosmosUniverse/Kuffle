package main.fr.kosmosuniverse.kuffle.listeners;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleEventNotUsableException;
import main.fr.kosmosuniverse.kuffle.type.KuffleType;

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
	 * @param event	The event called on player interaction
	 * 
	 * @throws ClassNotFoundException 	 	NMS Exception
	 * @throws SecurityException 		 	NMS Exception
	 * @throws NoSuchMethodException 	 	NMS Exception
	 * @throws InvocationTargetException  	NMS Exception
	 * @throws IllegalArgumentException  	NMS Exception
	 * @throws IllegalAccessException 	 	NMS Exception
	 */
	@EventHandler
	public void onLeftClick(PlayerInteractEvent event) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		if (KuffleMain.getInstance().getType().getType() != KuffleType.Type.BLOCKS) {
			return ;
		}
		
		try {
			onLeftClickGeneric(event);
		} catch (KuffleEventNotUsableException e) {
		}	
	}
}