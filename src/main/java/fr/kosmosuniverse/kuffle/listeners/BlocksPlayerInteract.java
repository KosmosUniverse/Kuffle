package fr.kosmosuniverse.kuffle.listeners;

import java.lang.reflect.InvocationTargetException;

import fr.kosmosuniverse.kuffle.core.Party;
import fr.kosmosuniverse.kuffle.exceptions.KuffleEventNotUsableException;
import fr.kosmosuniverse.kuffle.type.KuffleType;
import fr.kosmosuniverse.kuffle.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

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
	 */
	@EventHandler
	public void onLeftClick(PlayerInteractEvent event) {
		if (Party.getInstance().getType().getType() != KuffleType.Type.BLOCKS) {
			return ;
		}
		
		try {
			onRightClickGeneric(event);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | ClassNotFoundException e) {
			Utils.logException(e);
		} catch (KuffleEventNotUsableException e) {
			//Generic Method not really used for real exceptions
		}
	}
}