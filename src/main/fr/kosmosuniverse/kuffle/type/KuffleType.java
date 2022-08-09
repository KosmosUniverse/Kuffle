package main.fr.kosmosuniverse.kuffle.type;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Age;
import main.fr.kosmosuniverse.kuffle.core.AgeManager;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.CraftsManager;
import main.fr.kosmosuniverse.kuffle.core.Game;
import main.fr.kosmosuniverse.kuffle.core.Level;
import main.fr.kosmosuniverse.kuffle.core.LevelManager;
import main.fr.kosmosuniverse.kuffle.core.Logs;
import main.fr.kosmosuniverse.kuffle.core.ManageTeams;
import main.fr.kosmosuniverse.kuffle.core.RewardElem;
import main.fr.kosmosuniverse.kuffle.core.Scores;
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
	protected Map<String, Map<String, RewardElem>> allRewards = null;
	protected Map<String, Map<String, String>> allTargetLangs = null;
	protected Map<String, Map<String, String>> allMsgLangs = null;
	
	protected Map<String, List<String>> allTargets = null;
	protected Map<String, List<Inventory>> allTargetInvs = null;

	protected Map<String, Game> games = null;
	protected Map<String, Integer> playerRank = null;
	protected Map<Integer, String> versions = null;
	
	protected List<String> langs = null;
	protected List<Age> ages = null;
	protected List<Level> levels = null;
	
	public Logs gameLogs = null;
	public Logs systemLogs = null;
	public ManageTeams teams = null;
	public CraftsManager crafts = null;
	public Scores scores = null;
	public Inventory playersHeads = null;
	public Config config = null;
	
	/**
	 * Setup all type related resources
	 */
	protected abstract void setupTypeResources(JavaPlugin plugin) throws KuffleFileLoadException ;
	
	/**
	 * Constructor
	 * 
	 * @param plugin	The plugin instance to get the plugin folder
	 * 
	 * @throws KuffleFileLoadException if file load fails
	 */
	public KuffleType(JavaPlugin plugin) throws KuffleFileLoadException {
		gameLogs = new Logs(plugin.getDataFolder().getPath() + File.separator + "KuffleGamelogs.txt");
		systemLogs = new Logs(plugin.getDataFolder().getPath() + File.separator + "KuffleSystemlogs.txt");
		
		if (((versions = Utils.loadVersions("versions.json")) == null)) {
			throw new KuffleFileLoadException("KO");
		}
		
		if ((ages = AgeManager.getAges(FilesConformity.getContent("ages.json"))) == null) {
			throw new KuffleFileLoadException("KO");
		}
		
		if ((levels = LevelManager.getLevels(FilesConformity.getContent("levels.json"))) == null) {
			throw new KuffleFileLoadException("KO");
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

		if (allTargets != null) {
			allTargets.forEach((k, v) -> v.clear());

			allTargets.clear();
		}

		if (allTargetLangs != null) {
			allTargetLangs.forEach((k, v) -> v.clear());

			allTargetLangs.clear();
		}

		if (allTargetInvs != null) {
			allTargetInvs.forEach((k, v) -> v.clear());

			allTargetInvs.clear();
		}

		if (playerRank != null) {
			playerRank.clear();
		}

		if (games != null) {
			games.clear();
		}

		if (langs != null) {
			langs.clear();
		}

		if (ages != null) {
			ages.clear();
		}

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
	 * Get all target langs
	 * 
	 * @return the target langs map
	 */
	public final Map<String, Map<String, String>> getTargetLangs() {
		return allTargetLangs;
	}
	
	/**
	 * Get all messages langs
	 * 
	 * @return the messages langs
	 */
	public final Map<String, Map<String, String>> getMsgLangs() {
		return allMsgLangs;
	}
	
	/**
	 * Get all the targets
	 * 
	 * @return the target map
	 */
	public final Map<String, List<String>> getTarget() {
		return allTargets;
	}
	
	/**
	 * Get all the target inventories
	 * 
	 * @return the Inventory map
	 */
	public final Map<String, List<Inventory>> getTargetInvs() {
		return allTargetInvs;
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
	
	/**
	 * Get all the Minecraft versions available with this plugin
	 * 
	 * @return this plugin versions
	 */
	public final Map<Integer, String> getVersions() {
		return versions;
	}
	
	/**
	 * Get all available langs
	 * 
	 * @return the langs list
	 */
	public final List<String> getLangs() {
		return langs;
	}
	
	/**
	 * Get all Ages
	 * 
	 * @return the Ages list
	 */
	public final List<Age> getAges() {
		return ages;
	}
	
	/**
	 * Get all levels
	 * 
	 * @return the levels list
	 */
	public final List<Level> getLevels() {
		return levels;
	}
}
