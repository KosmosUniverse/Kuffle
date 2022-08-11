package main.fr.kosmosuniverse.kuffle;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import main.fr.kosmosuniverse.kuffle.commands.*;
import main.fr.kosmosuniverse.kuffle.core.*;
import main.fr.kosmosuniverse.kuffle.crafts.ACrafts;
import main.fr.kosmosuniverse.kuffle.listeners.*;
import main.fr.kosmosuniverse.kuffle.tabcompleters.*;
import main.fr.kosmosuniverse.kuffle.type.KuffleType;
import main.fr.kosmosuniverse.kuffle.utils.FilesConformity;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

public class KuffleMain extends JavaPlugin {
	public static KuffleType type = null;
	public static Map<String, Map<String, RewardElem>> allRewards;
	public static Map<String, Map<String, String>> allItemsLangs;
	public static Map<String, Map<String, String>> allLangs;

	public static Map<String, List<String>> allItems = new HashMap<>();
	public static Map<String, List<String>> allSbtts = new HashMap<>();
	public static Map<String, List<Inventory>> itemsInvs;

	public static Map<String, Game> games = new HashMap<>();
	public static Map<String, Integer> playerRank = new HashMap<>();
	public static Map<Integer, String> versions;

	public static List<String> langs;
	public static List<Age> ages;
	public static List<Level> levels;

	public static KuffleMain current;
	public static GameLoop loop;
	public static Config config;
	public static Logs gameLogs;
	public static Logs systemLogs;
	public static TeamManager teams;
	public static CraftManager crafts = null;
	public static ScoreManager scores;
	public static Inventory playersHeads;
	public static PlayerInteract playerInteract;
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
		
		if (!setup(this)) {
			this.getPluginLoader().disablePlugin(this);
		}
		
		loaded = true;

		systemLogs.logMsg(this.getName(), Utils.getLangString(null, "ON"));
	}

	@Override
	public void onDisable() {
		if (loaded) {
			killAll();
		}

		systemLogs.logMsg(this.getName(), Utils.getLangString(null, "OFF"));
	}
	
	private static boolean setup(JavaPlugin plugin) {
		current = (KuffleMain) plugin;
		
		gameLogs = new Logs(plugin.getDataFolder().getPath() + File.separator + "KuffleItemsGamelogs.txt");
		systemLogs = new Logs(plugin.getDataFolder().getPath() + File.separator + "KuffleItemsSystemlogs.txt");
		
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
	}

	public static void addRecipe(Recipe recipe) {
		current.getServer().addRecipe(recipe);
	}

	public static void removeRecipe(String name) {
		NamespacedKey n = new NamespacedKey(current, name);

		for (String playerName : games.keySet()) {
			games.get(playerName).getPlayer().undiscoverRecipe(n);
		}

		current.getServer().removeRecipe(n);
	}

	private void killAll() {
		if (allRewards != null) {
			allRewards.forEach((k, v) -> v.clear());

			allRewards.clear();
		}

		if (allItems != null) {
			allItems.forEach((k, v) -> v.clear());

			allItems.clear();
		}

		if (allItemsLangs != null) {
			allItemsLangs.forEach((k, v) -> v.clear());

			allItemsLangs.clear();
		}

		if (itemsInvs != null) {
			itemsInvs.forEach((k, v) -> v.clear());

			itemsInvs.clear();
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
				removeRecipe(craft.getName());
			}

			crafts.clear();
		}

		if (config != null) {
			config.clear();
		}
	}
	
	public static void updatePlayersHead() {
		Inventory newInv = Bukkit.createInventory(null, Utils.getNbInventoryRows(games.size()), "§8Players");
		
		for (String playerName : games.keySet()) {
			newInv.addItem(Utils.getHead(games.get(playerName).getPlayer(), games.get(playerName).getItemDisplay()));
		}
		
		playersHeads.clear();
		playersHeads = newInv;
	}

	public static void updatePlayersHeadData(String player, String currentItem) {
		ItemMeta itM;

		if (playersHeads == null) {
			return;
		}

		for (ItemStack item : playersHeads) {
			if (item != null) {
				itM = item.getItemMeta();

				if (itM.getDisplayName().equals(player)) {
					List<String> lore = new ArrayList<>();

					if (currentItem != null) {
						lore.add(currentItem);
					}

					itM.setLore(lore);
					item.setItemMeta(itM);
				}
			}
		}
	}
}
