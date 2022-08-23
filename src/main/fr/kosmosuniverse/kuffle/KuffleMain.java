package main.fr.kosmosuniverse.kuffle;

import java.io.File;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
import main.fr.kosmosuniverse.kuffle.listeners.PlayerEvents;
import main.fr.kosmosuniverse.kuffle.type.KuffleBlocks;
import main.fr.kosmosuniverse.kuffle.type.KuffleItems;
import main.fr.kosmosuniverse.kuffle.type.KuffleType;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

public class KuffleMain extends JavaPlugin {
	public static KuffleType type = null;

	public static KuffleMain current;
	public static PlayerEvents playerEvents;
	
	public static boolean paused = false;
	public static boolean loaded = false;
	public static boolean gameStarted = false;

	public KuffleMain() {
		super();
	}

	public KuffleMain(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
		super(loader, description, dataFolder, file);
	}

	@Override
	public void onEnable() {
		saveDefaultConfig();
		reloadConfig();
		
		try {
			type = new KuffleType(this);
			((KuffleItems) type).setupKuffleType(this);
			type.clearType();
			((KuffleBlocks) type).setupKuffleType(this);
			type.clearType();
		} catch (KuffleFileLoadException e) {
			this.getPluginLoader().disablePlugin(this);
			Utils.logException(e);
		}
		
		loaded = true;
		current = this;

		LogManager.getInstanceSystem().logMsg(this.getName(), LangManager.getMsgLang("ON", Config.getLang()));
	}

	@Override
	public void onDisable() {
		if (loaded) {
			type.clear();
		}

		LogManager.getInstanceSystem().logMsg(this.getName(), LangManager.getMsgLang("OFF", Config.getLang()));
	}
	
	/*private static boolean setup(JavaPlugin plugin) {
		current = (KuffleMain) plugin;
		
		gameLogs = new LogManager(plugin.getDataFolder().getPath() + File.separator + "KuffleItemsGamelogs.txt");
		systemLogs = new LogManager(plugin.getDataFolder().getPath() + File.separator + "KuffleItemsSystemlogs.txt");
		
		if (((versions = Utils.loadVersions("versions.json")) == null) ||
				((ages = AgeManager.getAges(FilesConformity.getContent("ages.json"))) == null) ||
				((allItems = TargetManager.getAllItems(ages, FilesConformity.getContent("items_%v.json"), plugin.getDataFolder())) == null) ||
				((allSbtts = TargetManager.getAllItems(ages, FilesConformity.getContent("sbtt_%v.json"), plugin.getDataFolder())) == null)) {
			
			return false;
		}

		if ((allRewards = RewardManager.getAllRewards(ages, FilesConformity.getContent("rewards_%v.json"), plugin.getDataFolder())) == null) {
			return false;
		}

		if ((allItemsLangs = LangManager.getAllItemsLang(FilesConformity.getContent("items_lang.json"), plugin.getDataFolder())) == null) {
			return false;
		}

		if ((allLangs = LangManager.getAllItemsLang(FilesConformity.getContent("langs.json"), plugin.getDataFolder())) == null) {
			return false;
		}

		if ((levels = LevelManager.getLevels(FilesConformity.getContent("levels.json"))) == null) {
			return false;
		}

		langs = LangManager.findAllLangs(allItemsLangs);

		loop = new GameLoop();
		config = new Config(plugin.getConfig());

		teams = new TeamManager();
		crafts = new CraftManager();
		itemsInvs = TargetManager.getItemsInvs(allItems);
		scores = new ScoreManager();

		int cnt = 0;

		for (ACrafts item : crafts.getRecipeList()) {
			plugin.getServer().addRecipe(item.getRecipe());
			cnt++;
		}

		systemLogs.logMsg(plugin.getName(), Utils.getLangString(null, "ADD_CRAFTS").replace("%i", "" + cnt));

		playerInteract = new PlayerInteract();
		playerEvents = new PlayerEvents(plugin.getDataFolder());

		plugin.getServer().getPluginManager().registerEvents(playerEvents, current);
		plugin.getServer().getPluginManager().registerEvents(playerInteract, current);
		plugin.getServer().getPluginManager().registerEvents(new InventoryListeners(), current);
		plugin.getServer().getPluginManager().registerEvents(new ItemEvent(), current);
		systemLogs.logMsg(plugin.getName(), Utils.getLangString(null, "ADD_LISTENERS").replace("%i", "4"));

		plugin.getCommand("ki-config").setExecutor(new KuffleConfig());
		plugin.getCommand("ki-list").setExecutor(new KuffleList());
		plugin.getCommand("ki-save").setExecutor(new KuffleSave(plugin.getDataFolder()));
		plugin.getCommand("ki-load").setExecutor(new KuffleLoad(plugin.getDataFolder()));
		plugin.getCommand("ki-start").setExecutor(new KuffleStart());
		plugin.getCommand("ki-stop").setExecutor(new KuffleStop());
		plugin.getCommand("ki-pause").setExecutor(new KufflePause());
		plugin.getCommand("ki-resume").setExecutor(new KuffleResume());
		plugin.getCommand("ki-ageitems").setExecutor(new KuffleAgeItems());
		plugin.getCommand("ki-crafts").setExecutor(new KuffleCrafts());
		plugin.getCommand("ki-lang").setExecutor(new KuffleLang());
		plugin.getCommand("ki-skip").setExecutor(new KuffleSkip());
		plugin.getCommand("ki-abandon").setExecutor(new KuffleAbandon());
		plugin.getCommand("ki-adminskip").setExecutor(new KuffleSkip());
		plugin.getCommand("ki-validate").setExecutor(new KuffleValidate());
		plugin.getCommand("ki-validate-age").setExecutor(new KuffleValidate());
		plugin.getCommand("ki-players").setExecutor(new KufflePlayers());
		plugin.getCommand("ki-add-during-game").setExecutor(new KuffleAddDuringGame());

		plugin.getCommand("ki-team-create").setExecutor(new KuffleTeamCreate());
		plugin.getCommand("ki-team-delete").setExecutor(new KuffleTeamDelete());
		plugin.getCommand("ki-team-color").setExecutor(new KuffleTeamColor());
		plugin.getCommand("ki-team-show").setExecutor(new KuffleTeamShow());
		plugin.getCommand("ki-team-affect-player").setExecutor(new KuffleTeamAffectPlayer());
		plugin.getCommand("ki-team-remove-player").setExecutor(new KuffleTeamRemovePlayer());
		plugin.getCommand("ki-team-reset-players").setExecutor(new KuffleTeamResetPlayers());
		plugin.getCommand("ki-team-random-player").setExecutor(new KuffleTeamRandomPlayer());
		systemLogs.logMsg(plugin.getName(), Utils.getLangString(null, "ADD_CMD").replace("%i", "25"));

		plugin.getCommand("ki-config").setTabCompleter(new KuffleConfigTab());
		plugin.getCommand("ki-list").setTabCompleter(new KuffleListTab());
		plugin.getCommand("ki-lang").setTabCompleter(new KuffleLangTab());
		plugin.getCommand("ki-ageitems").setTabCompleter(new KuffleAgeItemsTab());
		plugin.getCommand("ki-adminskip").setTabCompleter(new KuffleCurrentGamePlayerTab());
		plugin.getCommand("ki-validate").setTabCompleter(new KuffleCurrentGamePlayerTab());
		plugin.getCommand("ki-validate-age").setTabCompleter(new KuffleCurrentGamePlayerTab());
		plugin.getCommand("ki-add-during-game").setTabCompleter(new KuffleAddDuringGameTab());
		
		plugin.getCommand("ki-team-create").setTabCompleter(new KuffleTeamCreateTab());
		plugin.getCommand("ki-team-delete").setTabCompleter(new KuffleTeamDeleteTab());
		plugin.getCommand("ki-team-color").setTabCompleter(new KuffleTeamColorTab());
		plugin.getCommand("ki-team-show").setTabCompleter(new KuffleTeamShowTab());
		plugin.getCommand("ki-team-affect-player").setTabCompleter(new KuffleTeamAffectPlayerTab());
		plugin.getCommand("ki-team-remove-player").setTabCompleter(new KuffleTeamRemovePlayerTab());
		plugin.getCommand("ki-team-reset-players").setTabCompleter(new KuffleTeamResetPlayersTab());
		systemLogs.logMsg(plugin.getName(), Utils.getLangString(null, "ADD_TAB").replace("%i", "13"));

		return true;
	}*/
}
