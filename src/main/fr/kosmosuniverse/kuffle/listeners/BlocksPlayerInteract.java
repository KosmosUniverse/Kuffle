package main.fr.kosmosuniverse.kuffle.listeners;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleEventNotUsableException;
import main.fr.kosmosuniverse.kuffle.type.KuffleType;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

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
		if (KuffleMain.getInstance().getType().getType() != KuffleType.Type.BLOCKS) {
			return ;
		}
		
		try {
			onLeftClickGeneric(event);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | ClassNotFoundException | KuffleEventNotUsableException e) {
			Utils.logException(e);
		}
	}
}