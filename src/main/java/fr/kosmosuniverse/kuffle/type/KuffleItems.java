package fr.kosmosuniverse.kuffle.type;

import fr.kosmosuniverse.kuffle.core.CraftManager;
import fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
import fr.kosmosuniverse.kuffle.listeners.ItemsPlayerInteract;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleItems extends KuffleTypeDecorator {
	/**
	 * Constructor
	 * 
	 * @param plugin	the plugin itself
	 * 
	 * @throws KuffleFileLoadException if one of the resource file load fails
	 */	
	public KuffleItems(KuffleType type, JavaPlugin plugin) throws KuffleFileLoadException {
		super(type);
		setupKuffleType(plugin);
	}
	
	public void setupSbtt() {
		CraftManager.setupCraftTemplates();
	}
	
	public void clearSbtt() {
		CraftManager.removeCraftTemplates();
	}
	
	/**
	 * Setups the kuffle type
	 * 
	 * @param plugin	the plugin itself
	 * 
	 * @throws KuffleFileLoadException if file loading fails
	 */
	public void setupKuffleType(JavaPlugin plugin) throws KuffleFileLoadException {
		setupType(plugin);
		
		if (playerInteractItems == null) {
			playerInteractItems = new ItemsPlayerInteract();
			plugin.getServer().getPluginManager().registerEvents(playerInteractItems, plugin);
		}
	}
	
	/**
	 * Gets the current type
	 * 
	 * @return Type.ITEMS
	 */
	@Override
	public Type getType() {
		return Type.ITEMS;
	}
}
