package fr.kosmosuniverse.kuffle.core;

import fr.kosmosuniverse.kuffle.KuffleMain;
import fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
import fr.kosmosuniverse.kuffle.type.KuffleBlocks;
import fr.kosmosuniverse.kuffle.type.KuffleItems;
import fr.kosmosuniverse.kuffle.type.KuffleNoType;
import fr.kosmosuniverse.kuffle.type.KuffleType;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

/**
 * @author KosmosUniverse
 */
public class Party {
    public static Party instance;
    @Getter
    private final PartyList players;
    @Getter
    private final PartyList spectators;
    @Getter
    private final Ranks ranks;
    @Getter
    private final Games games;
    @Getter
    private GameStatus status;
    @Getter
    private KuffleType type;

    /**
     * Constructor
     */
    public Party() throws KuffleFileLoadException {
        players = new PartyList();
        spectators = new PartyList();
        ranks = new Ranks();
        games = new Games();
        status = GameStatus.NOT_RUNNING;
        type = new KuffleNoType(KuffleMain.getInstance());
    }

    /**
     * Gets Party instance
     *
     * @return Party instance
     */
    public static synchronized Party getInstance() {
        if (instance == null) {
            try {
                instance = new Party();
            } catch (KuffleFileLoadException e) {
                throw new RuntimeException(e);
            }
        }

        return instance;
    }

    public void clear() {
        players.clear();
        spectators.clear();
        ranks.clear();
        games.clear();
    }

    public void setType(Player player, KuffleType.Type newType) throws KuffleFileLoadException {
        type = type.clearType();

        switch (newType) {
            case ITEMS:
                type = new KuffleItems(player, type, KuffleMain.getInstance());
                break;
            case BLOCKS:
                type = new KuffleBlocks(player, type, KuffleMain.getInstance());
                break;
            case NO_TYPE:
            default:
                break;
        }
    }

    public void setup() {
        games.init();
    }

    public void launch() {
        status = GameStatus.RUNNING;

        games.getGameLoop().startRunnable();

        spectators.getList().forEach(spec -> {
            Objects.requireNonNull(Bukkit.getPlayer(spec)).setGameMode(GameMode.SPECTATOR);
            Objects.requireNonNull(Bukkit.getPlayer(spec)).setScoreboard(ScoreManager.getScoreboard());
        });
    }

    public void stop() {
        status = GameStatus.NOT_RUNNING;

        games.getGames().forEach((playerName, playerData) -> {
            for (PotionEffect pe : Objects.requireNonNull(Bukkit.getPlayer(playerName)).getActivePotionEffects()) {
                Objects.requireNonNull(Bukkit.getPlayer(playerName)).removePotionEffect(pe.getType());
            }

            games.resetPlayerBar(playerName);
        });

        if (Config.getSBTT()) {
            type.clearSbtt();
        }

        CraftManager.disableCrafts();
        ScoreManager.clear();

        if (Config.getTeam()) {
            TeamManager.getInstance().clear();
        }

        games.clear();
        clear();
    }

    public void pause() {
        if (status == GameStatus.RUNNING) {
            status = GameStatus.PAUSED;
            players.getList().forEach(player -> {
                ActionBar.sendRawTitle(ChatColor.BOLD + String.valueOf(ChatColor.DARK_PURPLE) + LangManager.getMsgLang("GAME_PAUSED", games.getGames().get(player).getConfigLang()) + ChatColor.RESET, Objects.requireNonNull(Bukkit.getPlayer(player)));
                Objects.requireNonNull(Bukkit.getPlayer(player)).addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 10, false, false, false));
                games.getGames().get(player).setInterval(System.currentTimeMillis() - games.getGames().get(player).getTimeTarget());
            });
        }
    }

    public void resume() {
        if (status == GameStatus.PAUSED) {
            players.getList().forEach(player -> {
                Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () ->
                        ActionBar.sendRawTitle(ChatColor.BOLD + String.valueOf(ChatColor.RED) + "3" + ChatColor.RESET, Objects.requireNonNull(Bukkit.getPlayer(player)))
                        , 20);
                Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () ->
                        ActionBar.sendRawTitle(ChatColor.BOLD + String.valueOf(ChatColor.YELLOW) + "2" + ChatColor.RESET, Objects.requireNonNull(Bukkit.getPlayer(player)))
                        , 40);
                Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () ->
                                ActionBar.sendRawTitle(ChatColor.BOLD + String.valueOf(ChatColor.GREEN) + "1" + ChatColor.RESET, Objects.requireNonNull(Bukkit.getPlayer(player)))
                        , 60);
                Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
                    status = GameStatus.RESUMED;
                    ActionBar.sendRawTitle(ChatColor.BOLD + String.valueOf(ChatColor.DARK_PURPLE) + LangManager.getMsgLang("GAME_RESUMED", games.getGames().get(player).getConfigLang()) + ChatColor.RESET, Objects.requireNonNull(Bukkit.getPlayer(player)));
                    Objects.requireNonNull(Bukkit.getPlayer(player)).removePotionEffect(PotionEffectType.INVISIBILITY);
                    games.getGames().get(player).setTimeTarget(System.currentTimeMillis() - games.getGames().get(player).getInterval());
                    games.getGames().get(player).setInterval(-1);
                }, 80);
            });
         }
    }
}
