package main.fr.kosmosuniverse.kuffle.type;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.parser.ParseException;

import main.fr.kosmosuniverse.kuffle.commands.KuffleAbandon;
import main.fr.kosmosuniverse.kuffle.commands.KuffleAddDuringGame;
import main.fr.kosmosuniverse.kuffle.commands.KuffleAgeTargets;
import main.fr.kosmosuniverse.kuffle.commands.KuffleConfig;
import main.fr.kosmosuniverse.kuffle.commands.KuffleCrafts;
import main.fr.kosmosuniverse.kuffle.commands.KuffleLang;
import main.fr.kosmosuniverse.kuffle.commands.KuffleList;
import main.fr.kosmosuniverse.kuffle.commands.KuffleLoad;
import main.fr.kosmosuniverse.kuffle.commands.KufflePause;
import main.fr.kosmosuniverse.kuffle.commands.KufflePlayers;
import main.fr.kosmosuniverse.kuffle.commands.KuffleResume;
import main.fr.kosmosuniverse.kuffle.commands.KuffleSave;
import main.fr.kosmosuniverse.kuffle.commands.KuffleSetType;
import main.fr.kosmosuniverse.kuffle.commands.KuffleSkip;
import main.fr.kosmosuniverse.kuffle.commands.KuffleStart;
import main.fr.kosmosuniverse.kuffle.commands.KuffleStop;
import main.fr.kosmosuniverse.kuffle.commands.KuffleTeamAffectPlayer;
import main.fr.kosmosuniverse.kuffle.commands.KuffleTeamColor;
import main.fr.kosmosuniverse.kuffle.commands.KuffleTeamCreate;
import main.fr.kosmosuniverse.kuffle.commands.KuffleTeamDelete;
import main.fr.kosmosuniverse.kuffle.commands.KuffleTeamRandomPlayer;
import main.fr.kosmosuniverse.kuffle.commands.KuffleTeamRemovePlayer;
import main.fr.kosmosuniverse.kuffle.commands.KuffleTeamResetPlayers;
import main.fr.kosmosuniverse.kuffle.commands.KuffleTeamShow;
import main.fr.kosmosuniverse.kuffle.commands.KuffleValidate;
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
import main.fr.kosmosuniverse.kuffle.listeners.PlayerEvents;
import main.fr.kosmosuniverse.kuffle.listeners.PlayerInteract;
import main.fr.kosmosuniverse.kuffle.tabcompleters.KuffleAddDuringGameTab;
import main.fr.kosmosuniverse.kuffle.tabcompleters.KuffleAgeTargetsTab;
import main.fr.kosmosuniverse.kuffle.tabcompleters.KuffleConfigTab;
import main.fr.kosmosuniverse.kuffle.tabcompleters.KuffleCurrentGamePlayerTab;
import main.fr.kosmosuniverse.kuffle.tabcompleters.KuffleLangTab;
import main.fr.kosmosuniverse.kuffle.tabcompleters.KuffleListTab;
import main.fr.kosmosuniverse.kuffle.tabcompleters.KuffleSetTypeTab;
import main.fr.kosmosuniverse.kuffle.tabcompleters.KuffleTeamAffectPlayerTab;
import main.fr.kosmosuniverse.kuffle.tabcompleters.KuffleTeamColorTab;
import main.fr.kosmosuniverse.kuffle.tabcompleters.KuffleTeamCreateTab;
import main.fr.kosmosuniverse.kuffle.tabcompleters.KuffleTeamDeleteTab;
import main.fr.kosmosuniverse.kuffle.tabcompleters.KuffleTeamRemovePlayerTab;
import main.fr.kosmosuniverse.kuffle.tabcompleters.KuffleTeamResetPlayersTab;
import main.fr.kosmosuniverse.kuffle.tabcompleters.KuffleTeamShowTab;
import main.fr.kosmosuniverse.kuffle.utils.FilesConformity;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public abstract class KuffleType {
	protected Map<String, Integer> xpActivables = null;
	protected PlayerInteract playerInteractItems = null;
	protected PlayerInteract playerInteractBlocks = null;
	protected KuffleSetType kuffleSetType;
	protected KuffleAgeTargetsTab kuffleAgeTargetsTab = null;
	protected KuffleSetTypeTab kuffleSetTypeTab = null;
	
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
	protected KuffleType() {
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
	protected KuffleType(JavaPlugin plugin) throws KuffleFileLoadException {
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
			LevelManager.getInstance().setupLevels(FilesConformity.getContent("levels.json"));
		} catch (IllegalArgumentException | ParseException e) {
			Utils.logException(e);
			LevelManager.getInstance().clear();
			
			throw new KuffleFileLoadException("Levels load failed !");
		}
		
		Config.setupConfig(plugin.getConfig());
		
		xpActivables = new HashMap<>();
		
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
		LevelManager.getInstance().clear();
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
	
	public Integer getXpActivable(String activable) {
		if (xpActivables != null && xpActivables.containsKey(activable)) {
			return xpActivables.get(activable);
		}
		
		return 0;
	}
	
	public void setXpActivable(String activable, int xp) {
		if (xpActivables != null) {
			xpActivables.put(activable, xp);
		}
	}
	
	public void loadXpMax(Map<String, Integer> xpMax) {
		if (xpActivables == null) {
			xpActivables = new HashMap<>();
		}
		
		if (xpActivables.size() != 0) {
			xpActivables.clear();
		}
		
		xpMax.forEach((k, v) -> xpActivables.put(k, v));
	}
	
	/**
	 * Gets the current xp for all in-game activables
	 * 
	 * @return the xpActivables map 
	 */
	public Map<String, Integer> getXpMap() {
		return xpActivables;
	}
	
	/**
	 * Gets the current type
	 */
	public abstract Type getType();
}
