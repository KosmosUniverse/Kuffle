package main.fr.kosmosuniverse.kuffle.type;

import org.bukkit.plugin.java.JavaPlugin;

import main.fr.kosmosuniverse.kuffle.core.CraftManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
import main.fr.kosmosuniverse.kuffle.listeners.ItemsPlayerInteract;

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
	public KuffleItems(KuffleType _type, JavaPlugin plugin) throws KuffleFileLoadException {
		super(_type);
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
