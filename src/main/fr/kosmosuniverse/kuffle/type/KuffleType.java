package main.fr.kosmosuniverse.kuffle.type;

import java.io.File;
import java.util.Map;

import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.parser.ParseException;

import main.fr.kosmosuniverse.kuffle.core.AgeManager;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.CraftManager;
import main.fr.kosmosuniverse.kuffle.core.Game;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LevelManager;
import main.fr.kosmosuniverse.kuffle.core.Logs;
import main.fr.kosmosuniverse.kuffle.core.RewardManager;
import main.fr.kosmosuniverse.kuffle.core.ScoreManager;
import main.fr.kosmosuniverse.kuffle.core.TargetManager;
import main.fr.kosmosuniverse.kuffle.core.VersionManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
import main.fr.kosmosuniverse.kuffle.utils.FilesConformity;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public abstract class KuffleType {
	protected Map<String, Game> games = null;
	protected Map<String, Integer> playerRank = null;
	protected Type type = Type.UNKNOWN;
	
	public Inventory playersHeads = null;
	
	/**
	 * Setup all type related resources
	 */
	protected abstract void setupTypeResources(JavaPlugin plugin) throws KuffleFileLoadException ;
	
	/**
	 * Constructor
	 * 
	 * @param plugin	The plugin instance to get the plugin folder
	 * 
	 * @throws KuffleFileLoadException if files load fails
	 */
	public KuffleType(JavaPlugin plugin) throws KuffleFileLoadException {
		Logs.getInstanceGame(plugin.getDataFolder().getPath() + File.separator + "KuffleGamelogs.txt");
		Logs.getInstanceSystem(plugin.getDataFolder().getPath() + File.separator + "KuffleSystemlogs.txt");
		
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
	}
	
	/**
	 * Clear all the Objects
	 */
	protected final void clear() {
		RewardManager.clear();
		TargetManager.clear();
		Config.clear();
		LevelManager.clear();
		LangManager.clear();
		AgeManager.clear();
		VersionManager.clear();
		TargetManager.clear();
		RewardManager.clear();
		CraftManager.clear();
		ScoreManager.clear();
		
		if (playerRank != null) {
			playerRank.clear();
		}

		if (games != null) {
			games.clear();
		}
	}
	
	public enum Type {
		UNKNOWN,
		ITEMS,
		BLOCKS
	}
	
	/**
	 * Get all the games
	 * 
	 * @return the games map
	 */
	public final Map<String, Game> getGames() {
		return games;
	}
	
	/**
	 * Get all the playersRanks
	 * 
	 * @return the players ranks map
	 */
	public final Map<String, Integer> getPlayerRanks() {
		return playerRank;
	}
}
