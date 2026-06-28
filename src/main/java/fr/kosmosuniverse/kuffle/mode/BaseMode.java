package fr.kosmosuniverse.kuffle.mode;

import fr.kosmosuniverse.kuffle.KuffleMain;
import fr.kosmosuniverse.kuffle.commands.*;
import fr.kosmosuniverse.kuffle.core.*;
import fr.kosmosuniverse.kuffle.datamanagers.crafts.CraftManager;
import fr.kosmosuniverse.kuffle.datamanagers.LangManager;
import fr.kosmosuniverse.kuffle.datamanagers.VersionManager;
import fr.kosmosuniverse.kuffle.datamanagers.age.AgeManager;
import fr.kosmosuniverse.kuffle.datamanagers.level.LevelManager;
import fr.kosmosuniverse.kuffle.datamanagers.reward.RewardManager;
import fr.kosmosuniverse.kuffle.datamanagers.targets.TargetManager;
import fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
import fr.kosmosuniverse.kuffle.listeners.*;
import fr.kosmosuniverse.kuffle.tabcompleters.*;
import fr.kosmosuniverse.kuffle.utils.FilesConformity;
import fr.kosmosuniverse.kuffle.utils.Utils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

/**
 * @author KosmosUniverse
 */
public class BaseMode implements GameMode {

    protected PlayerInteract playerInteract = null;
    protected KuffleSetMode kuffleSetMode = null;
    protected KuffleAbandon kuffleAbandon = null;
    protected KuffleSetModeTab kuffleSetModeTab = null;

    /**
     * Constructor
     *
     * @param plugin	The plugin instance to get the plugin folder
     *
     * @throws KuffleFileLoadException if files load fails
     */
    public BaseMode(JavaPlugin plugin) throws KuffleFileLoadException {
        LogManager.setupInstanceGame(plugin.getDataFolder().getPath() + File.separator + "KuffleGamelogs.txt");
        LogManager.setupInstanceSystem(plugin.getDataFolder().getPath() + File.separator + "KuffleSystemlogs.txt");

        setupManagers();
        Config.setupConfig(plugin.getConfig());
        setupListeners(plugin);
        setupCommands(plugin);
        setupCommendTabs(plugin);
    }

    @Override
    public Mode getMode() {
        return Mode.NO_MODE;
    }

    @Override
    public void clear() {
        if (kuffleSetModeTab != null) {
            kuffleSetModeTab.clear();
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

    @Override
    public void clearMode() {
        CraftManager.clear();
        RewardManager.clear();
        TargetManager.clear();

        Objects.requireNonNull(KuffleMain.getInstance().getCommand("k-agetargets")).setExecutor(null);
        Objects.requireNonNull(KuffleMain.getInstance().getCommand("k-crafts")).setExecutor(null);

        Objects.requireNonNull(KuffleMain.getInstance().getCommand("k-agetargets")).setTabCompleter(null);
    }

    @Override
    public void setupSbtt() {
        throw new UnsupportedOperationException("No mode selected");
    }

    @Override
    public void clearSbtt() {
        throw new UnsupportedOperationException("No mode selected");
    }

    @Override
    public void initMode(JavaPlugin plugin) throws KuffleFileLoadException {
        try {
            TargetManager.setup(getMode(), FilesConformity.getContent("targets.json"));
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
            int badCrafts = CraftManager.setupCrafts(getMode(), FilesConformity.getContent("crafts.json"));

            if (badCrafts > 0) {
                LogManager.getInstanceSystem().logSystemMsg("[WARNING] : Some crafts could not be load, check Kuffle system logs for more information.");
            }
        } catch (IllegalArgumentException e) {
            Utils.logException(e);
            CraftManager.clear();

            throw new KuffleFileLoadException("Crafts load failed !");
        }

        Utils.setupLists();

        Objects.requireNonNull(plugin.getCommand("k-agetargets")).setExecutor(new KuffleAgeTargets());
        Objects.requireNonNull(plugin.getCommand("k-crafts")).setExecutor(new KuffleCrafts());
        Objects.requireNonNull(plugin.getCommand("k-give")).setExecutor(new KuffleGive());

        Objects.requireNonNull(plugin.getCommand("k-give")).setTabCompleter(new KuffleGiveTab());

        plugin.getServer().getPluginManager().registerEvents(new InventoryListeners(), plugin);
    }

    @Override
    public void setPlayerInteractListener(PlayerInteract listener) {
        playerInteract = listener;
    }

    @Override
    public PlayerInteract getPlayerInteractListener() {
        return playerInteract;
    }

    private void setupManagers() throws KuffleFileLoadException {
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
    }

    private void setupListeners(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new PlayerEvents(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ItemEvent(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ConfigInventoriesListener(), plugin);
    }

    private void setupCommands(JavaPlugin plugin) {
        kuffleSetMode = new KuffleSetMode();
        kuffleSetModeTab = new KuffleSetModeTab();
        kuffleAbandon = new KuffleAbandon();
        Objects.requireNonNull(plugin.getCommand("k-config")).setExecutor(new KuffleConfig());
        Objects.requireNonNull(plugin.getCommand("k-list")).setExecutor(new KuffleList());
        Objects.requireNonNull(plugin.getCommand("k-spectate")).setExecutor(new KuffleSpectate());
        Objects.requireNonNull(plugin.getCommand("k-save")).setExecutor(new KuffleSave());
        Objects.requireNonNull(plugin.getCommand("k-load")).setExecutor(new KuffleLoad(plugin.getDataFolder()));
        Objects.requireNonNull(plugin.getCommand("k-start")).setExecutor(new KuffleStart());
        Objects.requireNonNull(plugin.getCommand("k-stop")).setExecutor(new KuffleStop());
        Objects.requireNonNull(plugin.getCommand("k-pause")).setExecutor(new KufflePause());
        Objects.requireNonNull(plugin.getCommand("k-resume")).setExecutor(new KuffleResume());
        Objects.requireNonNull(plugin.getCommand("k-set-mode")).setExecutor(kuffleSetMode);
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
        Objects.requireNonNull(plugin.getCommand("k-results")).setExecutor(new KuffleResults());
        Objects.requireNonNull(plugin.getCommand("k-team")).setExecutor(new KuffleTeam());
        Objects.requireNonNull(plugin.getCommand("k-team-inv")).setExecutor(new KuffleTeamInv());
    }

    private void setupCommendTabs(JavaPlugin plugin) {
        Objects.requireNonNull(plugin.getCommand("k-config")).setTabCompleter(new KuffleConfigTab());
        Objects.requireNonNull(plugin.getCommand("k-list")).setTabCompleter(new KuffleListTab());
        Objects.requireNonNull(plugin.getCommand("k-spectate")).setTabCompleter(new KuffleSpectateTab());
        Objects.requireNonNull(plugin.getCommand("k-lang")).setTabCompleter(new KuffleLangTab());
        Objects.requireNonNull(plugin.getCommand("k-tips")).setTabCompleter(new KuffleTipsTab());
        Objects.requireNonNull(plugin.getCommand("k-adminskip")).setTabCompleter(new KuffleCurrentGamePlayerTab());
        Objects.requireNonNull(plugin.getCommand("k-validate")).setTabCompleter(new KuffleCurrentGamePlayerTab());
        Objects.requireNonNull(plugin.getCommand("k-validate-age")).setTabCompleter(new KuffleCurrentGamePlayerTab());
        //Objects.requireNonNull(plugin.getCommand("k-add-during-game")).setTabCompleter(new KuffleAddDuringGameTab());
        Objects.requireNonNull(plugin.getCommand("k-set-mode")).setTabCompleter(kuffleSetModeTab);
        Objects.requireNonNull(plugin.getCommand("k-restoreinv")).setTabCompleter(new KuffleCurrentGamePlayerTab());
        Objects.requireNonNull(plugin.getCommand("k-results")).setTabCompleter(new KuffleResultsTab());
        Objects.requireNonNull(plugin.getCommand("k-team")).setTabCompleter(new KuffleTeamTab());
    }
}
