package main.fr.kosmosuniverse.kuffle.type;

import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.parser.ParseException;

import main.fr.kosmosuniverse.kuffle.core.CraftManager;
import main.fr.kosmosuniverse.kuffle.core.RewardManager;
import main.fr.kosmosuniverse.kuffle.core.TargetManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
import main.fr.kosmosuniverse.kuffle.listeners.ItemsPlayerInteract;
import main.fr.kosmosuniverse.kuffle.utils.FilesConformity;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleItems extends KuffleType {
	/**
	 * Constructor
	 * 
	 * @param plugin	the plugin itself
	 * 
	 * @throws KuffleFileLoadException if one of the resource file load fails
	 */
	public KuffleItems(JavaPlugin plugin) throws KuffleFileLoadException {
		super(plugin);
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
		
		CraftManager.setupCraftTemplates();
		
		playerInteract = new ItemsPlayerInteract();
		plugin.getServer().getPluginManager().registerEvents(playerInteract, plugin);
	}
	
	@Override
	protected void setupTypeResources(JavaPlugin plugin) throws KuffleFileLoadException  {
		type = Type.ITEMS;
		
		try {
			TargetManager.setupTargets(FilesConformity.getContent("items_1.15.json"));
			TargetManager.setupSbtts(FilesConformity.getContent("sbtts_items_1.15.json"));
		} catch (IllegalArgumentException | ParseException e) {
			Utils.logException(e);
			TargetManager.clear();
			
			throw new KuffleFileLoadException("Items or Sbtts load failed !");
		}
		
		try {
			RewardManager.setupRewards(FilesConformity.getContent("rewards_items_1.15.json"));
		} catch (IllegalArgumentException | ParseException e) {
			Utils.logException(e);
			TargetManager.clear();
			
			throw new KuffleFileLoadException("Rewards load failed !");
		}
	}
}
