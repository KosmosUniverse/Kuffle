package fr.kosmosuniverse.kuffle.type;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import fr.kosmosuniverse.kuffle.commands.*;
import fr.kosmosuniverse.kuffle.core.*;
import fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
import fr.kosmosuniverse.kuffle.listeners.InventoryListeners;
import fr.kosmosuniverse.kuffle.listeners.ItemEvent;
import fr.kosmosuniverse.kuffle.listeners.PlayerEvents;
import fr.kosmosuniverse.kuffle.listeners.PlayerInteract;
import fr.kosmosuniverse.kuffle.tabcompleters.*;
import fr.kosmosuniverse.kuffle.utils.FilesConformity;
import fr.kosmosuniverse.kuffle.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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
	protected KuffleAbandon kuffleAbandon;
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
			LangManager.setupMsgsLangs(FilesConformity.getRawContent("msgs_langs.json"));
		} catch (IllegalArgumentException e) {
			Utils.logException(e);
			LangManager.clear();
			
			throw new KuffleFileLoadException("Langs load failed !");
		}
		
		try {
			VersionManager.setupVersions(FilesConformity.getContent("versions.json"));
		} catch (IllegalArgumentException e) {
			Utils.logException(e);
			VersionManager.clear();
			
			throw new KuffleFileLoadException("Versions load failed !");
		}
		
		try {
			AgeManager.setupAges(FilesConformity.getContent("ages.json"));
		} catch (IllegalArgumentException e) {
			Utils.logException(e);
			AgeManager.clear();
			
			throw new KuffleFileLoadException("Ages load failed !");
		}
		
		try {
			LevelManager.getInstance().setupLevels(FilesConformity.getContent("levels.json"));
		} catch (IllegalArgumentException e) {
			Utils.logException(e);
			LevelManager.getInstance().clear();
			
			throw new KuffleFileLoadException("Levels load failed !");
		}
		
		Config.setupConfig(plugin.getConfig());
		
		kuffleSetType = new KuffleSetType();
		kuffleSetTypeTab = new KuffleSetTypeTab();
		kuffleAbandon = new KuffleAbandon();
		
		// Listeners
		plugin.getServer().getPluginManager().registerEvents(new PlayerEvents(), plugin);
		plugin.getServer().getPluginManager().registerEvents(new ItemEvent(), plugin);
		
		// Commands
		Objects.requireNonNull(plugin.getCommand("k-config")).setExecutor(new KuffleConfig());
		Objects.requireNonNull(plugin.getCommand("k-list")).setExecutor(new KuffleList());
		Objects.requireNonNull(plugin.getCommand("k-spectate")).setExecutor(new KuffleSpectate());
		Objects.requireNonNull(plugin.getCommand("k-save")).setExecutor(new KuffleSave(plugin.getDataFolder()));
		Objects.requireNonNull(plugin.getCommand("k-load")).setExecutor(new KuffleLoad(plugin.getDataFolder()));
		Objects.requireNonNull(plugin.getCommand("k-start")).setExecutor(new KuffleStart());
		Objects.requireNonNull(plugin.getCommand("k-stop")).setExecutor(new KuffleStop());
		Objects.requireNonNull(plugin.getCommand("k-pause")).setExecutor(new KufflePause());
		Objects.requireNonNull(plugin.getCommand("k-resume")).setExecutor(new KuffleResume());
		Objects.requireNonNull(plugin.getCommand("k-set-type")).setExecutor(kuffleSetType);
		Objects.requireNonNull(plugin.getCommand("k-lang")).setExecutor(new KuffleLang());
		Objects.requireNonNull(plugin.getCommand("k-tips")).setExecutor(new KuffleTips());
		Objects.requireNonNull(plugin.getCommand("k-skip")).setExecutor(new KuffleSkip());
		Objects.requireNonNull(plugin.getCommand("k-abandon")).setExecutor(kuffleAbandon);
		Objects.requireNonNull(plugin.getCommand("k-adminskip")).setExecutor(new KuffleAdminSkip());
		Objects.requireNonNull(plugin.getCommand("k-validate")).setExecutor(new KuffleValidate());
		Objects.requireNonNull(plugin.getCommand("k-validate-age")).setExecutor(new KuffleValidateAge());
		Objects.requireNonNull(plugin.getCommand("k-players")).setExecutor(new KufflePlayers());
		//Objects.requireNonNull(plugin.getCommand("k-add-during-game")).setExecutor(new KuffleAddDuringGame());
		Objects.requireNonNull(plugin.getCommand("k-restoreinv")).setExecutor(new KuffleRestoreInv());

		Objects.requireNonNull(plugin.getCommand("k-team-create")).setExecutor(new KuffleTeamCreate());
		Objects.requireNonNull(plugin.getCommand("k-team-delete")).setExecutor(new KuffleTeamDelete());
		Objects.requireNonNull(plugin.getCommand("k-team-color")).setExecutor(new KuffleTeamColor());
		Objects.requireNonNull(plugin.getCommand("k-team-show")).setExecutor(new KuffleTeamShow());
		Objects.requireNonNull(plugin.getCommand("k-team-affect-player")).setExecutor(new KuffleTeamAffectPlayer());
		Objects.requireNonNull(plugin.getCommand("k-team-remove-player")).setExecutor(new KuffleTeamRemovePlayer());
		Objects.requireNonNull(plugin.getCommand("k-team-reset-players")).setExecutor(new KuffleTeamResetPlayers());
		Objects.requireNonNull(plugin.getCommand("k-team-random-player")).setExecutor(new KuffleTeamRandomPlayer());
		Objects.requireNonNull(plugin.getCommand("k-team-inv")).setExecutor(new KuffleTeamInv());
		
		// TabCompleter
		Objects.requireNonNull(plugin.getCommand("k-config")).setTabCompleter(new KuffleConfigTab());
		Objects.requireNonNull(plugin.getCommand("k-list")).setTabCompleter(new KuffleListTab());
		Objects.requireNonNull(plugin.getCommand("k-spectate")).setTabCompleter(new KuffleSpectateTab());
		Objects.requireNonNull(plugin.getCommand("k-lang")).setTabCompleter(new KuffleLangTab());
		Objects.requireNonNull(plugin.getCommand("k-tips")).setTabCompleter(new KuffleTipsTab());
		Objects.requireNonNull(plugin.getCommand("k-adminskip")).setTabCompleter(new KuffleCurrentGamePlayerTab());
		Objects.requireNonNull(plugin.getCommand("k-validate")).setTabCompleter(new KuffleCurrentGamePlayerTab());
		Objects.requireNonNull(plugin.getCommand("k-validate-age")).setTabCompleter(new KuffleCurrentGamePlayerTab());
		//Objects.requireNonNull(plugin.getCommand("k-add-during-game")).setTabCompleter(new KuffleAddDuringGameTab());
		Objects.requireNonNull(plugin.getCommand("k-set-type")).setTabCompleter(kuffleSetTypeTab);
		Objects.requireNonNull(plugin.getCommand("k-restoreinv")).setTabCompleter(new KuffleCurrentGamePlayerTab());
		
		Objects.requireNonNull(plugin.getCommand("k-team-create")).setTabCompleter(new KuffleTeamCreateTab());
		Objects.requireNonNull(plugin.getCommand("k-team-delete")).setTabCompleter(new KuffleTeamDeleteTab());
		Objects.requireNonNull(plugin.getCommand("k-team-color")).setTabCompleter(new KuffleTeamColorTab());
		Objects.requireNonNull(plugin.getCommand("k-team-show")).setTabCompleter(new KuffleTeamShowTab());
		Objects.requireNonNull(plugin.getCommand("k-team-affect-player")).setTabCompleter(new KuffleTeamAffectPlayerTab());
		Objects.requireNonNull(plugin.getCommand("k-team-remove-player")).setTabCompleter(new KuffleTeamRemovePlayerTab());
		Objects.requireNonNull(plugin.getCommand("k-team-reset-players")).setTabCompleter(new KuffleTeamResetPlayersTab());
	}
	
	/**
	 * Setups Kuffle specific type
	 * 
	 * @param plugin	The plugin itself
	 * 
	 * @throws KuffleFileLoadException if file loading fails
	 */
	protected void setupType(Player player, JavaPlugin plugin) throws KuffleFileLoadException {
		try {
			TargetManager.setup(getType(), FilesConformity.getContent("targets.json"));
		} catch (Exception e) {
			Utils.logException(e);
			TargetManager.clear();
			
			throw new KuffleFileLoadException("Targets load failed !", e);
		}
		
		try {
			RewardManager.setupRewards(FilesConformity.getContent("rewards.json"));
		} catch (IllegalArgumentException e) {
			Utils.logException(e);
			RewardManager.clear();
			
			throw new KuffleFileLoadException("Rewards load failed !");
		}
		
		try {
			int badCrafts = CraftManager.setupCrafts(getType(), FilesConformity.getContent("crafts.json"));
			
			if (badCrafts > 0) {
				LogManager.getInstanceSystem().writeMsg(player, "[WARNING] : Some crafts could not be load, check Kuffle system logs for more information.");
			}
		} catch (IllegalArgumentException e) {
			Utils.logException(e);
			CraftManager.clear();
			
			throw new KuffleFileLoadException("Crafts load failed !");
		}
		
		xpActivables = new HashMap<>();
		
		ScoreManager.setupScores(getType());
		Utils.setupLists();
		
		Objects.requireNonNull(plugin.getCommand("k-agetargets")).setExecutor(new KuffleAgeTargets());
		Objects.requireNonNull(plugin.getCommand("k-crafts")).setExecutor(new KuffleCrafts());
		Objects.requireNonNull(plugin.getCommand("k-give")).setExecutor(new KuffleGive());
		
		if (kuffleAgeTargetsTab == null) {
			kuffleAgeTargetsTab = new KuffleAgeTargetsTab();
		}
		
		Objects.requireNonNull(plugin.getCommand("k-agetargets")).setTabCompleter(kuffleAgeTargetsTab);
		Objects.requireNonNull(plugin.getCommand("k-give")).setTabCompleter(new KuffleGiveTab());
		
		plugin.getServer().getPluginManager().registerEvents(new InventoryListeners(), plugin);
	}
	
	/**
	 * Clear all the Objects
	 */
	public final void clear() {
		clearType();
		
		if (xpActivables != null) {
			xpActivables.clear();
		}
		
		if (kuffleAgeTargetsTab != null) {
			kuffleAgeTargetsTab.clear();
		}
		
		if (kuffleSetTypeTab != null) {
			kuffleSetTypeTab.clear();	
		}
		
		if (kuffleAbandon != null) {
			kuffleAbandon.clear();
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

		xpActivables.putAll(xpMax);
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
