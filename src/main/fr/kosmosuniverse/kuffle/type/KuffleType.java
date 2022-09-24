package main.fr.kosmosuniverse.kuffle.type;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.parser.ParseException;

import main.fr.kosmosuniverse.kuffle.commands.*;
import main.fr.kosmosuniverse.kuffle.tabcompleters.*;
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
import main.fr.kosmosuniverse.kuffle.listeners.InventoryListeners;
import main.fr.kosmosuniverse.kuffle.listeners.ItemEvent;
import main.fr.kosmosuniverse.kuffle.listeners.ItemsPlayerInteract;
import main.fr.kosmosuniverse.kuffle.listeners.PlayerEvents;
import main.fr.kosmosuniverse.kuffle.utils.FilesConformity;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public abstract class KuffleType {
	protected static ItemsPlayerInteract playerInteract;
	protected static KuffleSetType kuffleSetType;
	protected static KuffleAgeTargetsTab kuffleAgeTargetsTab = null;
	protected static KuffleSetTypeTab kuffleSetTypeTab = null;
	
	/**
	 * 
	 * @author KosmosUniverse
	 *
	 */
	public enum Type {
		NO_TYPE,
		ITEMS,
		BLOCKS
	}
	
	/**
	 * Constructor
	 */
	public KuffleType() {
	}
	
	public abstract void setupSbtt();
	
	public abstract void clearSbtt();
	
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
			LangManager.setupMsgsLangs(FilesConformity.getContent("msgs_langs.json"));
		} catch (IllegalArgumentException | ParseException e) {
			Utils.logException(e);
			LangManager.clear();
			
			throw new KuffleFileLoadException("Langs load failed !");
		}
		
		try {
			VersionManager.setupVersions(FilesConformity.getContent("versions.json"));
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
		
		kuffleSetType = new KuffleSetType();
		kuffleSetTypeTab = new KuffleSetTypeTab();
		
		// Listeners
		plugin.getServer().getPluginManager().registerEvents(new PlayerEvents(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new ItemEvent(), plugin);
		
		// Commands
		plugin.getCommand("k-config").setExecutor(new KuffleConfig());
		plugin.getCommand("k-list").setExecutor(new KuffleList());
		plugin.getCommand("k-save").setExecutor(new KuffleSave(plugin.getDataFolder()));
		plugin.getCommand("k-load").setExecutor(new KuffleLoad(plugin.getDataFolder()));
		plugin.getCommand("k-start").setExecutor(new KuffleStart());
		plugin.getCommand("k-stop").setExecutor(new KuffleStop());
		plugin.getCommand("k-pause").setExecutor(new KufflePause());
		plugin.getCommand("k-resume").setExecutor(new KuffleResume());
		plugin.getCommand("k-set-type").setExecutor(kuffleSetType);
		plugin.getCommand("k-lang").setExecutor(new KuffleLang());
		plugin.getCommand("k-skip").setExecutor(new KuffleSkip());
		plugin.getCommand("k-abandon").setExecutor(new KuffleAbandon());
		plugin.getCommand("k-adminskip").setExecutor(new KuffleSkip());
		plugin.getCommand("k-validate").setExecutor(new KuffleValidate());
		plugin.getCommand("k-validate-age").setExecutor(new KuffleValidate());
		plugin.getCommand("k-players").setExecutor(new KufflePlayers());
		plugin.getCommand("k-add-during-game").setExecutor(new KuffleAddDuringGame());

		plugin.getCommand("k-team-create").setExecutor(new KuffleTeamCreate());
		plugin.getCommand("k-team-delete").setExecutor(new KuffleTeamDelete());
		plugin.getCommand("k-team-color").setExecutor(new KuffleTeamColor());
		plugin.getCommand("k-team-show").setExecutor(new KuffleTeamShow());
		plugin.getCommand("k-team-affect-player").setExecutor(new KuffleTeamAffectPlayer());
		plugin.getCommand("k-team-remove-player").setExecutor(new KuffleTeamRemovePlayer());
		plugin.getCommand("k-team-reset-players").setExecutor(new KuffleTeamResetPlayers());
		plugin.getCommand("k-team-random-player").setExecutor(new KuffleTeamRandomPlayer());
		
		// TabCompleters
		plugin.getCommand("k-config").setTabCompleter(new KuffleConfigTab());
		plugin.getCommand("k-list").setTabCompleter(new KuffleListTab());
		plugin.getCommand("k-lang").setTabCompleter(new KuffleLangTab());
		plugin.getCommand("k-adminskip").setTabCompleter(new KuffleCurrentGamePlayerTab());
		plugin.getCommand("k-validate").setTabCompleter(new KuffleCurrentGamePlayerTab());
		plugin.getCommand("k-validate-age").setTabCompleter(new KuffleCurrentGamePlayerTab());
		plugin.getCommand("k-add-during-game").setTabCompleter(new KuffleAddDuringGameTab());
		plugin.getCommand("k-set-type").setTabCompleter(kuffleSetTypeTab);
		
		plugin.getCommand("k-team-create").setTabCompleter(new KuffleTeamCreateTab());
		plugin.getCommand("k-team-delete").setTabCompleter(new KuffleTeamDeleteTab());
		plugin.getCommand("k-team-color").setTabCompleter(new KuffleTeamColorTab());
		plugin.getCommand("k-team-show").setTabCompleter(new KuffleTeamShowTab());
		plugin.getCommand("k-team-affect-player").setTabCompleter(new KuffleTeamAffectPlayerTab());
		plugin.getCommand("k-team-remove-player").setTabCompleter(new KuffleTeamRemovePlayerTab());
		plugin.getCommand("k-team-reset-players").setTabCompleter(new KuffleTeamResetPlayersTab());
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
			TargetManager.setup(FilesConformity.getContent("targets.json"));
		} catch (IllegalArgumentException | ParseException e) {
			Utils.logException(e);
			TargetManager.clear();
			
			throw new KuffleFileLoadException("Targets load failed !", e);
		}
		
		try {
			RewardManager.setupRewards(FilesConformity.getContent("rewards.json"));
		} catch (IllegalArgumentException | ParseException e) {
			Utils.logException(e);
			RewardManager.clear();
			
			throw new KuffleFileLoadException("Rewards load failed !");
		}
		
		try {
			CraftManager.setupCrafts(getType(), FilesConformity.getContent("crafts.json"));
		} catch (IllegalArgumentException | ParseException e) {
			Utils.logException(e);
			CraftManager.clear();
			
			throw new KuffleFileLoadException("Crafts load failed !");
		}
		
		ScoreManager.setupScores(getType());
		GameManager.setupGame();
		
		plugin.getCommand("k-agetargets").setExecutor(new KuffleAgeTargets());
		plugin.getCommand("k-crafts").setExecutor(new KuffleCrafts());
		
		if (kuffleAgeTargetsTab == null) {
			kuffleAgeTargetsTab = new KuffleAgeTargetsTab();
		}
		
		plugin.getCommand("k-agetargets").setTabCompleter(kuffleAgeTargetsTab);
		
		plugin.getServer().getPluginManager().registerEvents(new InventoryListeners(), plugin);
	}
	
	/**
	 * Clear all the Objects
	 */
	public final void clear() {
		clearType();
		
		if (kuffleAgeTargetsTab != null) {
			kuffleAgeTargetsTab.clear();
		}
		
		if (kuffleSetTypeTab != null) {
			kuffleSetTypeTab.clear();	
		}
		
		Config.clear();
		LevelManager.clear();
		AgeManager.clear();
		VersionManager.clear();
		LangManager.clear();
	}
	
	/**
	 * Clears the specific Type and return basic one
	 * 
	 * @return Base Type
	 */
	public abstract KuffleType clearType();
	
	/**
	 * Gets the ItemsPlayerInteract class
	 * 
	 * @return the playerInteract object
	 */
	public ItemsPlayerInteract getPlayerInteract() {
		return playerInteract;
	}
	
	/**
	 * Gets the current type
	 */
	public abstract Type getType();
}
