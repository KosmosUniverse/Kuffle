package main.fr.kosmosuniverse.kuffle.type;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.parser.ParseException;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.AgeManager;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.CraftsManager;
import main.fr.kosmosuniverse.kuffle.core.Game;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LevelManager;
import main.fr.kosmosuniverse.kuffle.core.Logs;
import main.fr.kosmosuniverse.kuffle.core.ManageTeams;
import main.fr.kosmosuniverse.kuffle.core.RewardElem;
import main.fr.kosmosuniverse.kuffle.core.Scores;
import main.fr.kosmosuniverse.kuffle.core.TargetManager;
import main.fr.kosmosuniverse.kuffle.core.VersionManager;
import main.fr.kosmosuniverse.kuffle.crafts.ACrafts;
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
	
	public Logs gameLogs = null;
	public Logs systemLogs = null;
	
	public Config config = null;
	public ManageTeams teams = null;
	public CraftsManager crafts = null;
	public Scores scores = null;
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
		gameLogs = new Logs(plugin.getDataFolder().getPath() + File.separator + "KuffleGamelogs.txt");
		systemLogs = new Logs(plugin.getDataFolder().getPath() + File.separator + "KuffleSystemlogs.txt");
		
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
		
		config = new Config(plugin.getConfig());
		
		//To be put in KuffleItems.java
		try {
			TargetManager.setupTargets(FilesConformity.getContent("items_1.15.json"));
		} catch (IllegalArgumentException | ParseException e) {
			Utils.logException(e);
			TargetManager.clear();
			
			throw new KuffleFileLoadException("Items load failed !");
		}
	}
	
	/**
	 * Clear all the Objects
	 */
	protected void clear() {
		if (allRewards != null) {
			allRewards.forEach((k, v) -> v.clear());

			allRewards.clear();
		}

		if (playerRank != null) {
			playerRank.clear();
		}

		if (games != null) {
			games.clear();
		}

		VersionManager.clear();
		AgeManager.clear();
		LevelManager.clear();
		LangManager.clear();

		if (crafts != null) {
			for (ACrafts craft : crafts.getRecipeList()) {
				KuffleMain.removeRecipe(craft.getName());
			}

			crafts.clear();
		}

		if (config != null) {
			config.clear();
		}
	}
	
	/**
	 * Get all rewards
	 * 
	 * @return the reward map
	 */
	public final Map<String, Map<String, RewardElem>> getRewards() {
		return allRewards;
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
