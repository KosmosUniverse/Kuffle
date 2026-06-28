package fr.kosmosuniverse.kuffle.core;

import fr.kosmosuniverse.kuffle.KuffleMain;
import fr.kosmosuniverse.kuffle.datamanagers.LangManager;
import fr.kosmosuniverse.kuffle.datamanagers.age.Age;
import fr.kosmosuniverse.kuffle.datamanagers.age.AgeManager;
import fr.kosmosuniverse.kuffle.datamanagers.results.ResultManager;
import fr.kosmosuniverse.kuffle.datamanagers.reward.RewardManager;
import fr.kosmosuniverse.kuffle.event.NextAgeEvent;
import fr.kosmosuniverse.kuffle.ranks.Ranks;
import fr.kosmosuniverse.kuffle.utils.StringFunction;
import fr.kosmosuniverse.kuffle.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author KosmosUniverse
 */
public class GameManager {
    private final Map<String, PlayerData> playersData;
    @Getter
    private final Map<String, Integer> xpActivables;
    @Getter
    @Setter
    private Ranks ranks;
    @Getter
    private final GameLoop gameLoop;

    public GameManager() {
        playersData = new HashMap<>();
        xpActivables = new HashMap<>();
        gameLoop = new GameLoop();
        ranks = new Ranks();
    }

    public void clear() {
        clearInit();
        ranks.clear();
    }

    public void init(List<String> players) {
        players.forEach(playerName -> playersData.put(playerName, new PlayerData(playerName)));

        xpActivables.put("EndTeleporter", Config.getXpEnd());
        xpActivables.put("OverworldTeleporter", Config.getXpOverworld());
        xpActivables.put("CoralCompass", Config.getXpCoral());
    }

    public void loadPlayer(PlayerData playerData) {
        playersData.put(playerData.getPlayerName(), playerData);
    }

    public void clearInit() {
        playersData.clear();
        xpActivables.clear();
    }

    public void startGame() {
        gameLoop.startRunnable();
    }

    public void pauseGame() {
        playersData.values().forEach(PlayerData::pause);
    }

    public void resumeGame() {
        playersData.values().forEach(PlayerData::resume);
    }

    public void stopGame() {
        gameLoop.kill();
        processGameResults();
    }

    public PlayerData getPlayerData(String playerName) {
        return playersData.get(playerName);
    }

    public void setupPlayers() {
        playersData.values().forEach(PlayerData::setup);
    }

    /**
     * Setup Scores for all players
     */
    public void setupPlayersScores() {
        playersData.keySet().forEach(this::setupPlayerScores);
    }

    /**
     * Setup Scores for a Player
     *
     * @param playerName    The player for whom score is set up
     */
    public void setupPlayerScores(String playerName) {
        ScoreManagerTmp.getInstance().setupPlayerScore(playersData.get(playerName));
    }

    public void setPlayersSpawnLoc() {
        playersData.forEach((playerName, playerData) -> {
            Bukkit.getPlayer(playerName).setBedSpawnLocation(Bukkit.getPlayer(playerName).getLocation(), true);
            playerData.setSpawnLoc(Bukkit.getPlayer(playerName).getLocation());
            playerData.getSpawnLoc().add(0, -1, 0).getBlock().setType(Material.BEDROCK);
        });
    }

    public void setPlayersSpawnLoc(Location loc) {
        playersData.forEach((playerName, playerData) -> {
            Bukkit.getPlayer(playerName).setBedSpawnLocation(loc, true);
            playerData.setSpawnLoc(loc);
        });
    }

    /**
     * Run the game
     */
    private void runLoop() {
        playersData.forEach((playerName, playerData) -> {
            if (playerData.isFinished()) {
                playerData.updateBossBar();
            } else {
                ActionBar.sendMessage(playerData.getActionBarStr(), Bukkit.getPlayer(playerData.getPlayerName()));
            }
        });
    }

    /**
     * Target Found for a player
     *
     * @param playerName The player that found the target
     */
    public boolean targetFound(String playerName) {
        return playersData.get(playerName).foundTarget();
    }

    /**
     * SBTT used by a player
     *
     * @param playerName The player that used the SBTT
     */
    public boolean sbttFound(String playerName) {
        return playersData.get(playerName).foundSbtt();
    }

    /**
     * Set the specified target to the player
     *
     * @param playerName    The Player that receive the target
     * @param target        The new target to set
     * @param targetDisplay The display version of the target
     */
    public void setCurrentTarget(String playerName, String target, String targetDisplay) {
        playersData.get(playerName).setCurrentTarget(target, targetDisplay);
    }

    public String getPlayerTarget(String playerName) {
        return playersData.get(playerName).getCurrentTarget();
    }

    public Age getPlayerAge(String playerName) {
        return playersData.get(playerName).getAge();
    }

    /**
     * Player is getting to the next age
     *
     * @param playerName The player that is moving Age
     */
    public void nextPlayerAge(String playerName) {
        playersData.get(playerName).nextAge();
    }

    public long getPlayersNotFinished() {
        return playersData.size() - (playersData.values()
                .stream()
                .filter(PlayerData::isFinished)
                .count());
    }

    public boolean checkTeamFinished(Team team) {
        return team.getPlayers()
                .stream()
                .anyMatch(teamPlayer -> playersData.get(teamPlayer).getAge() != Config.getLastAge() ||
                        !playersData.get(teamPlayer).getState().isWaiting());
    }

    /**
     * Check if teammates have finished their age
     *
     * @param playerData	The player data
     *
     * @return True if all team player have finished their age, False instead
     */
    public boolean checkTeamMates(PlayerData playerData) {
        Team team = TeamManager.getInstance().findTeamByPlayer(playerData.getPlayerName());

        long stillPlaying = team.getPlayers()
                .stream()
                .filter(playerName -> playersData.get(playerName).getState().isWaiting())
                .count();

        return stillPlaying == 0;
    }

    /**
     * Move all the team members to the next age
     *
     * @param teamName The player name
     */
    public void teamNextAge(String teamName) {
        Team team = TeamManager.getInstance().getTeam(teamName);

        team.getPlayers()
                .stream()
                .map(Bukkit::getPlayer)
                .filter(Objects::nonNull)
                .forEach(player -> Bukkit.getPluginManager().callEvent(new NextAgeEvent(player)));
    }

    public List<String> getPlayerNotFinishedList() {
        return playersData.entrySet()
                .stream()
                .filter(e -> !e.getValue().isFinished())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Player is finishing
     *
     * @param playerName The player that finishes
     */
    public void finishPlayer(String playerName) {
        playersData.get(playerName).finish();
    }

    /**
     * Force abandon for the rest of the players
     */
    public void forceLastFinish() {
        playersData.entrySet()
                .stream()
                .filter(entry -> !entry.getValue().isFinished())
                .forEach(entry -> entry.getValue().abandon());
    }

    /**
     * Send Tips to the player
     */
    public void sendTips(PlayerData playerData) {
        String ageName = playerData.getAge().getName().replace("_Age", "").toUpperCase();

        Bukkit.getPlayer(playerData.getPlayerName()).sendMessage(net.md_5.bungee.api.ChatColor.BLUE + LangManager.getMsgLang("TIPS_" + ageName, playerData.getConfigLang()) + net.md_5.bungee.api.ChatColor.RESET);

        if (playerData.getAge().getNumber() == 2) {
            Bukkit.getPlayer(playerData.getPlayerName()).sendMessage(net.md_5.bungee.api.ChatColor.BLUE + LangManager.getMsgLang("TIPS_END_TELEPORTER", playerData.getConfigLang()) + net.md_5.bungee.api.ChatColor.RESET);
        } else if (playerData.getAge().getNumber() == 3) {
            Bukkit.getPlayer(playerData.getPlayerName()).sendMessage(net.md_5.bungee.api.ChatColor.BLUE + LangManager.getMsgLang("TIPS_OVERWORLD_TELEPORTER", playerData.getConfigLang()) + net.md_5.bungee.api.ChatColor.RESET);
        }
    }

    public void getPlayerStoredInv(Player sender, String target) {
        List<ItemStack> items = playersData.get(target).getDeathInv();

        if (items == null || items.isEmpty()) {
            LogManager.getInstanceGame().writeMsg(sender, target + " does not have any items in its saved inventory.");
            return ;
        }

        Inventory inv = Bukkit.createInventory(null, 54, ChatColor.BLACK + target + " Inventory");

        items.forEach(inv::addItem);
        sender.openInventory(inv);
    }

    public void setPlayerTipsState(String playerName, boolean isTips) {
        playersData.get(playerName).setTips(isTips);
    }

    public String getPlayerLang(String playerName) {
        return playersData.get(playerName).getConfigLang();
    }

    /**
     * Gives effects to a player depending on his current Age
     */
    public void reloadPlayerEffects(String playerName) {
        if (Config.getSaturation()) {
            Bukkit.getPlayer(playerName).addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 10, false, false, false));
        }

        if (Config.getRewards() && (playersData.get(playerName).getAge().getNumber() - 1) >= 0) {
            RewardManager.givePlayerRewardEffect(Bukkit.getPlayer(playerName), AgeManager.getAgeByNumber(playersData.get(playerName).getAge().getNumber() - 1).getName());
        }
    }

    public boolean isPlayerDead(String playerName) {
        return playersData.get(playerName).getState().isDead();
    }

    /**
     * Player died
     *
     * @param playerName	The dead player
     * @param deathLoc	The death Location
     */
    public void playerDied(String playerName, Location deathLoc) {
        if (Config.getLevel().isLosable()) {
            playerLose(playerName);
        } else {
            playersData.get(playerName).died(deathLoc);
            Objects.requireNonNull(Bukkit.getPlayer(playerName)).getInventory().clear();
        }
    }

    public Location getPlayerSpawnLoc(String playerName) {
        return playersData.get(playerName).getSpawnLoc();
    }

    /**
     * Teleport a specific player to his death location
     */
    public void teleportAutoBack(Player player) {
        sendMsgToPlayer(player.getName(), (lang) -> LangManager.getMsgLang("TP_BACK", lang).replace("%i", String.valueOf(Config.getLevel().getSeconds())));

        Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
            Location loc = playersData.get(player.getName()).getDeathLoc();

            if (loc == null) {
                return;
            }

            if (Objects.requireNonNull(loc.getWorld()).getName().contains("the_end") && loc.getY() < 0) {
                Utils.changeLocForEnd(loc);
            }

            Utils.createSafeBox(loc, player.getName());

            player.teleport(loc);

            playersData.get(player.getName()).revive();
            reloadPlayerEffects(player.getName());

            for (Entity e : player.getNearbyEntities(3.0, 3.0, 3.0)) {
                if (e.getType() != EntityType.DROPPED_ITEM &&
                        e.getType() != EntityType.PLAYER) {
                    e.remove();
                }
            }
        }, (Config.getLevel().getSeconds() * 20L));
    }

    public void sendMsgToPlayer(String playerName, StringFunction func) {
        playersData.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals(playerName))
                .forEach(entry -> Bukkit.getPlayer(entry.getKey()).sendMessage(func.run(entry.getValue().getConfigLang())));
    }

    public void sendMsgToAll(StringFunction func) {
        playersData.forEach((key, value) -> Bukkit.getPlayer(key).sendMessage(func.run(value.getConfigLang())));
    }

    /**
     * Save Game results and create inventories
     */
    public void processGameResults() {
        ResultManager.getInstance().saveGameResults(playersData);
        ResultManager.getInstance().createInventories();

        if (Config.getLogGameResult()) {
            playersData.values().forEach(playerData -> LogManager.getInstanceGame().logSystemMsg(logString(playerData)));
        }
    }

    /**
     * Logs the game end result tab from a specific player
     *
     * @return the log String
     */
    public String logString(PlayerData playerData) {
        long total = 0;
        String lang = Config.getLang();
        StringBuilder sb = new StringBuilder();

        sb.append(playerData.getPlayerName()).append(":").append("\n");
        sb.append(LangManager.getMsgLang("DEATH_COUNT", lang).replace("%i", String.valueOf(playerData.getDeathCount()))).append("\n");
        sb.append(LangManager.getMsgLang("SKIP_COUNT", lang).replace("%i", String.valueOf(playerData.getSkipCount()))).append("\n");
        sb.append(LangManager.getMsgLang("TEMPLATE_COUNT", lang).replace("%i", String.valueOf(playerData.getSbttCount()))).append("\n");
        sb.append(LangManager.getMsgLang("TIME_TAB", lang)).append("\n");

        boolean abandon = false;

        for (int i = 0; i < (Config.getLastAge().getNumber() + 1); i++) {
            Age tmpAge = AgeManager.getAgeByNumber(i);

            if (playerData.getAgeTimes().get(tmpAge.getName()) == 0) {
                sb.append(LangManager.getMsgLang("FINISH_ABANDON", lang).replace("%s", tmpAge.getName().replace("_Age", ""))).append("\n");
                abandon = true;
            } else if (playerData.getAgeTimes().get(tmpAge.getName()) < 0) {
                sb.append(LangManager.getMsgLang("ABANDON_AFTER", lang).replace("%s", tmpAge.getName().replace("_Age", "")).replace("%t", Utils.getTimeFromSec((playerData.getAgeTimes().get(tmpAge.getName()) * -1) / 1000))).append("\n");
                total += (playerData.getAgeTimes().get(tmpAge.getName()) * -1) / 1000;
                abandon = true;
            } else {
                sb.append(LangManager.getMsgLang("FINISH_TIME", lang).replace("%s", tmpAge.getName().replace("_Age", "")).replace("%t", Utils.getTimeFromSec(playerData.getAgeTimes().get(tmpAge.getName()) / 1000))).append("\n");
                total += playerData.getAgeTimes().get(tmpAge.getName()) / 1000;
            }
        }

        if (abandon) {
            sb.append(LangManager.getMsgLang("FINISH_TOTAL", lang).replace("%t", LangManager.getMsgLang("ABANDONED", lang)));
            sb.append(" (").append(Utils.getTimeFromSec(total)).append(")");
        } else {
            sb.append(LangManager.getMsgLang("FINISH_TOTAL", lang).replace("%t", Utils.getTimeFromSec(total)));
        }

        return sb.toString();
    }

    public void playerLose(String playerName) {
        playersData.get(playerName).abandon();
    }

    public void teamLose(String teamName) {
        TeamManager.getInstance().getTeam(teamName).getPlayers().forEach(this::playerLose);
    }

    public void skipPlayerTarget(String playerName, boolean penalty) {
        if (playersData.get(playerName).skip(penalty)) {
            PartyTmp.getInstance().getOptions().targetSkipped(playersData.get(playerName));
        }
    }

    /**
     * Teleports a specific player to a target player
     *
     * @param player		The player to teleport
     * @param targetPlayer	The target for teleportation
     */
    public void teleportPlayerToPlayer(Player player, String targetPlayer) {
        player.teleport(Bukkit.getPlayer(targetPlayer));
    }
}
