package main.fr.kosmosuniverse.kuffle.type;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.parser.ParseException;

import main.fr.kosmosuniverse.kuffle.core.AgeManager;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.CraftManager;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LevelManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.RewardManager;
import main.fr.kosmosuniverse.kuffle.core.ScoreManager;
import main.fr.kosmosuniverse.kuffle.core.TargetManager;
import main.fr.kosmosuniverse.kuffle.core.VersionManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
import main.fr.kosmosuniverse.kuffle.listeners.PlayerEvents;
import main.fr.kosmosuniverse.kuffle.utils.FilesConformity;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleType {
	protected static Type type = Type.UNKNOWN;
	private static PlayerEvents playerEvents;
	
	public enum Type {
		UNKNOWN,
		ITEMS,
		BLOCKS
	}
	
	/**
	 * Constructor
	 * 
	 * @param plugin	The plugin instance to get the plugin folder
	 * 
	 * @throws KuffleFileLoadException if files load fails
	 */
	public KuffleType(JavaPlugin plugin) throws KuffleFileLoadException {
		LogManager.setupInstanceGame(plugin.getDataFolder().getPath() + File.separator + "KuffleGamelogs.txt");
		LogManager.setupInstanceSystem(plugin.getDataFolder().getPath() + File.separator + "KuffleSystemlogs.txt");
		
		try {
			LangManager.setupTargetsLangs(FilesConformity.getContent("targets_langs.json"));
			LangManager.setupMsgsLangs(FilesConformity.getContent("msgs_langs.json"));
		} catch (IllegalArgumentException | ParseException e) {
			Utils.logException(e);
			LangManager.clear();
			
			throw new KuffleFileLoadException("Langs load failed !");
		}
		
		try {
			VersionManager.setupVersions("versions.json");
		} catch (IllegalArgumentException | ParseException e) {
			Utils.logException(e);
			VersionManager.clear();
			
			throw new KuffleFileLoadException("Versions load failed !");
		}
		
		try {
			AgeManager.setupAges(FilesConformity.getContent("ages.json"));
		} catch (IllegalArgumentException | ParseException e) {
			Utils.logException(e);
			AgeManager.clear();
			
			throw new KuffleFileLoadException("Ages load failed !");
		}
		
		try {
			LevelManager.setupLevels(FilesConformity.getContent("levels.json"));
		} catch (IllegalArgumentException | ParseException e) {
			Utils.logException(e);
			LevelManager.clear();
			
			throw new KuffleFileLoadException("Levels load failed !");
		}
		
		Config.setupConfig(plugin.getConfig());
		
		playerEvents = new PlayerEvents(plugin.getDataFolder());
		plugin.getServer().getPluginManager().registerEvents(playerEvents, plugin);
	}
	
	/**
	 * Setup all type related resources
	 *
	 * @param plugin	The plugin itself
	 * 
	 * @throws KuffleFileLoadException if targets, sbbts or rewards files loading fails
	 */
	protected void setupTypeResources(JavaPlugin plugin) throws KuffleFileLoadException {
	}
	
	/**
	 * Setups Kuffle specific type
	 * 
	 * @param plugin	The plugin itself
	 * 
	 * @throws KuffleFileLoadException if file loading fails
	 */
	protected void setupType(JavaPlugin plugin) throws KuffleFileLoadException {
		try {
			setupTypeResources(plugin);
		} catch (KuffleFileLoadException e) {
			type = Type.UNKNOWN;
			
			TargetManager.clear();
			RewardManager.clear();
			
			throw e;
		}
		
		CraftManager.setupCrafts(type);
		ScoreManager.setupPlayerScores();
		GameManager.setupGame();
	}
	
	/**
	 * Clear all the Objects
	 */
	public final void clear() {
		clearType();
		
		Config.clear();
		LevelManager.clear();
		AgeManager.clear();
		VersionManager.clear();
		LangManager.clear();
	}
	
	public final void clearType() {
		GameManager.clear();
		ScoreManager.clear();
		CraftManager.clear();
		RewardManager.clear();
		TargetManager.clear();
		
		type = Type.UNKNOWN;
	}
}
