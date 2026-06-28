package fr.kosmosuniverse.kuffle.core;

import fr.kosmosuniverse.kuffle.KuffleMain;
import fr.kosmosuniverse.kuffle.datamanagers.LangManager;
import fr.kosmosuniverse.kuffle.datamanagers.crafts.CraftManager;
import fr.kosmosuniverse.kuffle.event.NewTargetEvent;
import fr.kosmosuniverse.kuffle.event.NextAgeEvent;
import fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
import fr.kosmosuniverse.kuffle.mode.BaseMode;
import fr.kosmosuniverse.kuffle.mode.GameMode;
import fr.kosmosuniverse.kuffle.mode.Mode;
import fr.kosmosuniverse.kuffle.mode.ModeFactory;
import fr.kosmosuniverse.kuffle.options.BaseOption;
import fr.kosmosuniverse.kuffle.options.GameOption;
import fr.kosmosuniverse.kuffle.options.OptionFactory;
import fr.kosmosuniverse.kuffle.states.*;
import fr.kosmosuniverse.kuffle.utils.ItemMaker;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.EnumMap;
import java.util.Objects;

/**
 * @author KosmosUniverse
 */
public class PartyTmp {
    @Getter
    private static PartyTmp instance;

    /**
     * Create Party
     *
     * @param plugin Plugin to init
     *
     * @return True if everything well loaded, False instead
     */
    public static boolean createParty(JavaPlugin plugin) {
        instance = new PartyTmp();

        return instance.init(plugin);
    }

    private EnumMap<States, GameState> states;

    /**
     * Change Party State
     *
     * @param newState The new state of the party
     */
    public void setState(States newState) {
        gameState = states.get(newState);
    }

    @Getter
    private GameMode gameMode;
    @Getter
    private GameState gameState;
    @Getter
    private GameOption options;
    @Getter
    private PartyList players;
    @Getter
    private PartyList spectators;
    @Getter
    private GameManager gameManager;

    /**
     * Initialize Party base Mode
     *
     * @param plugin Plugin to init
     *
     * @return True if everything well loaded, False instead
     */
    private boolean init(JavaPlugin plugin) {
        try {
            gameMode = new BaseMode(plugin);

            if (Config.getStartMode() != Mode.NO_MODE) {
                gameMode = ModeFactory.create(Config.getStartMode(), gameMode, plugin);
            }
        }  catch (KuffleFileLoadException e) {
            LogManager.getInstanceSystem().logSystemMsg(e.getMessage());

            return false;
        }

        return true;
    }

    /**
     * Clear All
     */
    public void clear() {
        gameMode.clear();

        if (states != null) {
            states.clear();
        }
    }

    /**
     * Change mode
     *
     * @param mode The new mode
     *
     * @throws KuffleFileLoadException The exception raised by data loading
     */
    public void setMode(Mode mode) throws KuffleFileLoadException {
        gameMode = ModeFactory.create(mode, gameMode, KuffleMain.getInstance());
    }

    /**
     * Setup Party
     */
    public void setupParty() {
        states = new EnumMap<>(States.class);

        states.put(States.NOT_RUNNING, new NotRunningState());
        states.put(States.RUNNING, new RunningState());
        states.put(States.PAUSED, new PausedState());

        gameState = states.get(States.NOT_RUNNING);
        players = new PartyList();
        spectators = new PartyList();
        gameManager = new GameManager();
        options = new BaseOption();
    }

    /**
     * Clear Party
     */
    public void clearParty() {
        if (players != null) {
            players.clear();
        }

        if (spectators != null) {
            spectators.clear();
        }

        if (gameManager != null) {
            gameManager.clear();
        }
    }

    /**
     * Init Party Game
     */
    public void initGame() {
        gameManager.init(players.getList());
        options = OptionFactory.createOptionsFromConfig(options, Config.getHolder());
        options.initOption();

        ScoreManagerTmp.createInstance(gameMode.getMode());
    }

    /**
     * Clear Party Game
     */
    public void clearGame() {
        gameManager.clearInit();
        ScoreManagerTmp.clear();
    }

    /**
     * Check Party state for config update
     *
     * @return True if party state allow config change, False instead
     */
    public boolean checkConfigUpdatability() {
        return gameState.checkConfigUpdatability();
    }

    /**
     * Launch Party
     *
     * @param player The player that started the party
     */
    public boolean launch(Player player) {
        return gameState.launch(this, player);
    }

    /**
     * Pause Party
     */
    public boolean pause() {
        return gameState.pause(this);
    }

    /**
     * Resume Party
     */
    public boolean resume() {
        return gameState.resume(this);
    }

    /**
     * Stop Party
     */
    public boolean stop() {
        return gameState.stop(this);
    }

    /**
     * Check launch conditions
     *
     * @param player Player trying launch the game
     *
     * @return True if Party can be launched, False instead
     */
    public boolean launchChecks(Player player) {
        if (players.getList().isEmpty()) {
            LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NO_PLAYERS", Config.getLang()));

            return false;
        }

        return options.launchChecks(player);
    }

    /**
     * Launching Game
     *
     * @param player The player launching the game
     */
    public void launchGame(Player player) {
        players.createPlayersInventory();
        spectators.createPlayersInventory();

        ScoreManagerTmp.getInstance().setupScores();

        setupSpectators();

        sendAll(LangManager.getMsgLang("GAME_STARTED", Config.getLang()));
        LogManager.getInstanceGame().logSystemMsg(LangManager.getMsgLang("GAME_STARTED", Config.getLang()));

        int spread = spreadAndSpawn(player);

        Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
            CraftManager.enableCrafts();

            titleAll(ChatColor.BOLD + String.valueOf(ChatColor.RED) + "5" + ChatColor.RESET);
        }, spread);

        Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
            discoverCrafts();

            titleAll(ChatColor.BOLD + String.valueOf(ChatColor.GOLD) + "4" + ChatColor.RESET);
        }, 20 + spread);

        Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
            gameManager.setupPlayers();

            titleAll(ChatColor.BOLD + String.valueOf(ChatColor.YELLOW) + "3" + ChatColor.RESET);
        }, 40 + spread);

        Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
            gameManager.setupPlayersScores();

            titleAll(ChatColor.BOLD + String.valueOf(ChatColor.GREEN) + "2" + ChatColor.RESET);
        }, 60 + spread);

        Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
            giveStartBox();

            titleAll(ChatColor.BOLD + String.valueOf(ChatColor.BLUE) + "1" + ChatColor.RESET);
        }, 80 + spread);

        Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
            gameManager.startGame();
            options.launch();

            titleAll(ChatColor.BOLD + String.valueOf(ChatColor.DARK_PURPLE) + "GO !!!" + ChatColor.RESET);
        }, 100 + spread);
    }

    /**
     * Pausing Game
     */
    public void pauseGame() {
        gameManager.pauseGame();
        options.pause();
    }

    /**
     * Resuming Game
     */
    public void resumeGame() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () ->
                        titleAll(ChatColor.BOLD + String.valueOf(ChatColor.RED) + "3" + ChatColor.RESET)
                , 20);
        Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () ->
                        titleAll(ChatColor.BOLD + String.valueOf(ChatColor.YELLOW) + "2" + ChatColor.RESET)
                , 40);
        Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () ->
                        titleAll(ChatColor.BOLD + String.valueOf(ChatColor.GREEN) + "1" + ChatColor.RESET)
                , 60);
        Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () ->
                    titleAll(ChatColor.BOLD + String.valueOf(ChatColor.DARK_PURPLE) + LangManager.getMsgLang("GAME_RESUMED", Config.getLang()) + ChatColor.RESET)
                , 80);

        gameManager.resumeGame();
        options.resume();
    }

    /**
     * Stoping Game
     */
    public void stopGame() {
        options.stop();
        gameManager.stopGame();
    }

    /**
     * Send all players and spectators chat
     *
     * @param msg The message to send
     */
    public void sendAll(String msg) {
        players.getList().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(player -> player.sendMessage(msg));

        spectators.getList().stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(player -> player.sendMessage(msg));
    }

    /**
     * Title all players and spectators
     *
     * @param msg The title
     */
    public void titleAll(String msg) {
        players.getList()
                .stream()
                .filter(playerName -> Bukkit.getPlayer(playerName) != null)
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(player -> ActionBar.sendRawTitle(msg, player));
        spectators.getList()
                .stream()
                .filter(specName -> Bukkit.getPlayer(specName) != null)
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(spec -> ActionBar.sendRawTitle(msg, spec));
    }

    /**
     * Give saturation to all players
     */
    public void setSaturation() {
        players.getList()
                .stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(player -> player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 10, false, false, false)));
    }

    /**
     * Check player a target
     *
     * @param player The player to check
     * @param target The target to check
     *
     * @return True if the target is good for that player, False instead
     */
    public boolean checkPlayerTarget(Player player, String target) {
        return options.checkPlayerTarget(gameManager.getPlayerData(player.getName()).getCurrentTarget(), target);
    }

    /**
     * Target Found for a player
     *
     * @param player The player that found the target
     */
    public void targetFound(Player player) {
        boolean ret = gameManager.targetFound(player.getName());

        if (!ret) {
            return ;
        }

        options.targetFound();

        if (options.checkAge(gameManager.getPlayerData(player.getName()))) {
            Bukkit.getPluginManager().callEvent(new NextAgeEvent(player));
        } else {
            Bukkit.getPluginManager().callEvent(new NewTargetEvent(player));
        }
    }

    /**
     * Give a new target
     *
     * @param player The player that is getting a new target
     */
    public void newTarget(Player player) {
        String newTarget = options.newTarget(gameManager.getPlayerData(player.getName()));
        String targetDisplay = options.getTargetDisplay(gameManager.getPlayerData(player.getName()));

        gameManager.setCurrentTarget(player.getName(), newTarget, targetDisplay);
    }

    /**
     * SBTT used by a player
     *
     * @param player The player that used the SBTT
     */
    public void sbttFound(Player player) {
        boolean ret = gameManager.sbttFound(player.getName());

        if (!ret) {
            return ;
        }

        options.targetFound();

        if (options.checkAge(gameManager.getPlayerData(player.getName()))) {
            Bukkit.getPluginManager().callEvent(new NextAgeEvent(player));
        } else {
            String newTarget = options.newTarget(gameManager.getPlayerData(player.getName()));
            String targetDisplay = options.getTargetDisplay(gameManager.getPlayerData(player.getName()));

            gameManager.setCurrentTarget(player.getName(), newTarget, targetDisplay);
        }
    }

    /**
     * Updates the player name display in tab
     */
    public void updatePlayerListName(String playerName) {
        options.updatePlayerListName(gameManager.getPlayerData(playerName));
    }

    private void setupSpectators() {
        spectators.getList()
                .stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(this::setupSingleSpec);
    }

    /**
     * Sets players spawn location after spreading if on in config
     *
     * @param sender The player that made start command
     *
     * @return 20 is players have been spread, 0 instead
     */
    private int spreadAndSpawn(Player sender) {
        if (Config.getSpread()) {
            SpreadPlayer.spreadPlayers(sender, Config.getSpreadDistance(), Config.getSpreadRadius(), players.getList());

            gameManager.setPlayersSpawnLoc();

            return 20;
        } else {
            Location spawnLoc = sender.getLocation().getWorld().getSpawnLocation();

            spawnLoc.getBlock().setType(Material.BEDROCK);
            gameManager.setPlayersSpawnLoc(spawnLoc);

            return 0;
        }
    }

    private void setupSingleSpec(Player player) {
        player.setGameMode(org.bukkit.GameMode.SPECTATOR);
        player.setScoreboard(ScoreManagerTmp.getInstance().getScoreboard());
    }

    private void discoverCrafts() {
        players.getList()
                .stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(CraftManager::discoverCrafts);
    }

    private void giveStartBox() {
        players.getList()
                .stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(player -> {
                    ItemStack box = ItemMaker.newItem(Material.WHITE_SHULKER_BOX).addName("Start Box").addLore("Owner:" + player.getName()).getItem();
                    player.getInventory().addItem(box);
                });
    }
}
