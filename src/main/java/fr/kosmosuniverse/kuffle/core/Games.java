package fr.kosmosuniverse.kuffle.core;

import fr.kosmosuniverse.kuffle.KuffleMain;
import fr.kosmosuniverse.kuffle.utils.Utils;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.*;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author KosmosUniverse
 */
public class Games {
    private final Map<String, PlayerData> games;
    @Getter
    private GameLoop gameLoop;

    /**
     * Constructor
     */
    public Games() {
        games = new HashMap<>();
    }

    /**
     * Gets the unmodifiable Games map
     *
     * @return Games map
     */
    public Map<String, PlayerData> getGames() {
        return games;
    }

    /**
     * Clears games map
     */
    public void clear() {
        games.forEach((key, value) -> value.clear());
        games.clear();
        if (gameLoop != null) {
            gameLoop.kill();
        }
    }

    /**
     * Initialize the games values
     */
    public void init() {
        if (games.isEmpty()) {
            Party.getInstance().getPlayers().getList().forEach(playerName -> games.put(playerName, new PlayerData()));
        }

        gameLoop = new GameLoop();
    }

    /**
     * Gives effects to a player depending on his current Age
     */
    public void reloadPlayerEffects(String playerName) {
        if (Config.getSaturation()) {
            Objects.requireNonNull(Bukkit.getPlayer(playerName)).addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 10, false, false, false));
        }

        if (Config.getRewards() && (games.get(playerName).getAge() - 1) >= 0) {
            RewardManager.givePlayerRewardEffect(Bukkit.getPlayer(playerName), AgeManager.getAgeByNumber(games.get(playerName).getAge() - 1).getName());
        }
    }

    public void playerFoundTarget(String playerName) {
        playerFound(playerName);
    }

    public void playerFoundSbtt(String playerName) {
        games.get(playerName).incrementSbtt();
        LogManager.getInstanceGame().logMsg(playerName, LangManager.getMsgLang("TARGET_FOUND", games.get(playerName).getConfigLang()).replace("[#]", "[" + games.get(playerName).getCurrentTarget() + "]"));
        playerFound(playerName);
    }

    private void playerFound(String playerName) {
        games.get(playerName).incrementTarget();
        Objects.requireNonNull(Bukkit.getPlayer(playerName)).playSound(Objects.requireNonNull(Bukkit.getPlayer(playerName)).getLocation(), Sound.BLOCK_BELL_USE, 1f, 1f);
        games.get(playerName).getScore().setScore(games.get(playerName).getTargetCount());
        updatePlayerBar(playerName);
        games.get(playerName).setCurrentTarget(null);
    }

    /**
     * Checks if a specific target is the target of this player
     *
     * @param target	The target to check
     *
     * @return True if @target is the @player's target
     */
    public boolean checkPlayerTarget(String playerName, ItemStack target) {
        boolean ret = false;

        if (games.get(playerName).getCurrentTarget() != null) {
            if (Config.getDouble()) {
                String[] targets = games.get(playerName).getCurrentTarget().split("/");

                ret = targets[0].equals(target.getType().name().toLowerCase()) ||
                        targets[1].equals(target.getType().name().toLowerCase()) ||
                        (targets[0].startsWith("*") && target.getType().name().toLowerCase().contains(targets[0].replace("*", ""))) ||
                        (targets[1].startsWith("*") && target.getType().name().toLowerCase().contains(targets[1].replace("*", "")));
            } else {
                ret = games.get(playerName).getCurrentTarget().equals(target.getType().name().toLowerCase()) ||
                        (games.get(playerName).getCurrentTarget().startsWith("*") && target.getType().name().toLowerCase().contains(games.get(playerName).getCurrentTarget().replace("*", "")));
            }
        }

        return ret;
    }

    /**
     * Checks if player has already finished
     *
     * @param player	The player
     *
     * @return True if player has finished, False instead
     */
    public boolean hasPlayerFinished(String player) {
        return games.get(player).isFinished();
    }

    /**
     * Teleports a specific player to a target player
     *
     * @param player		The player to teleport
     * @param targetPlayer	The target for teleportation
     */
    public void teleportPlayerToPlayer(Player player, String targetPlayer) {
        player.teleport(Objects.requireNonNull(Bukkit.getPlayer(targetPlayer)));
    }

    /**
     * Updates the player name display in tab
     */
    public void updatePlayerListName(String playerName) {
        if (Config.getTeam()) {
            Team team = TeamManager.getInstance().getTeamByPlayer(playerName);
            Objects.requireNonNull(Bukkit.getPlayer(playerName)).setPlayerListName("[" + team.getColor() + team.getName() + ChatColor.RESET + "] - " + AgeManager.getAgeByNumber(games.get(playerName).getAge()).getColor() + playerName);
        } else {
            Objects.requireNonNull(Bukkit.getPlayer(playerName)).setPlayerListName(AgeManager.getAgeByNumber(games.get(playerName).getAge()).getColor() + playerName);
        }
    }

    /**
     * Updates the player boss bar
     */
    public void updatePlayerBar(String playerName) {
        if (games.get(playerName).isLose()) {
            games.get(playerName).getAgeDisplay().setProgress(0.0);
            games.get(playerName).getAgeDisplay().setTitle(LangManager.getMsgLang("GAME_DONE", games.get(playerName).getConfigLang()).replace("%i", String.valueOf(Party.getInstance().getRanks().getRank(playerName) + 1)));

            return ;
        }

        if (games.get(playerName).isFinished()) {
            games.get(playerName).getAgeDisplay().setProgress(1.0);
            games.get(playerName).getAgeDisplay().setTitle(LangManager.getMsgLang("GAME_DONE", games.get(playerName).getConfigLang()).replace("%i", String.valueOf(Party.getInstance().getRanks().getRank(playerName) + 1)));

            return ;
        }

        double calc = ((double) games.get(playerName).getTargetCount()) / Config.getTargetPerAge();
        calc = Math.min(calc, 1.0);
        games.get(playerName).getAgeDisplay().setProgress(calc);
        games.get(playerName).getAgeDisplay().setTitle(AgeManager.getAgeByNumber(games.get(playerName).getAge()).getName().replace("_", " ") + ": " + games.get(playerName).getTargetCount());
    }

    /**
     * Send Tips to the player
     */
    public void sendTips(String playerName) {
        if (Party.getInstance().getGames().getGames().get(playerName).isTips() &&
                Party.getInstance().getGames().getGames().get(playerName).getAge() <= Config.getLastAge().getNumber()) {
            String ageName = AgeManager.getAgeByNumber(Party.getInstance().getGames().getGames().get(playerName).getAge()).getName().replace("_Age", "").toUpperCase();

            Objects.requireNonNull(Bukkit.getPlayer(playerName)).sendMessage(ChatColor.BLUE + LangManager.getMsgLang("TIPS_" + ageName, Party.getInstance().getGames().getGames().get(playerName).getConfigLang()) + ChatColor.RESET);

            if (Party.getInstance().getGames().getGames().get(playerName).getAge() == 2) {
                Objects.requireNonNull(Bukkit.getPlayer(playerName)).sendMessage(ChatColor.BLUE + LangManager.getMsgLang("TIPS_END_TELEPORTER", Party.getInstance().getGames().getGames().get(playerName).getConfigLang()) + ChatColor.RESET);
            } else if (Party.getInstance().getGames().getGames().get(playerName).getAge() == 3) {
                Objects.requireNonNull(Bukkit.getPlayer(playerName)).sendMessage(ChatColor.BLUE + LangManager.getMsgLang("TIPS_OVERWORLD_TELEPORTER", Party.getInstance().getGames().getGames().get(playerName).getConfigLang()) + ChatColor.RESET);
            }
        }
    }

    /**
     * Fore finish for last player
     */
    public void finishLast() {
        if (getNbPlayerStillPlaying() == 1) {
            games.entrySet().stream()
                    .filter(entry -> !entry.getValue().isFinished())
                    .forEach(entry -> Party.getInstance().getGames().playerLose(entry.getKey()));
        }
    }

    /**
     * Get the number of players that have not finished the game.
     *
     * @return the number of player that are still playing
     */
    public int getNbPlayerStillPlaying() {
        if (!Config.getTeam()) {
            return (int) (games.size() - games.entrySet().stream()
                                .filter(entry -> entry.getValue().isFinished())
                                .count());
        } else {
            List<String> teams = new ArrayList<>();

            games.entrySet().stream()
                    .filter(entry -> entry.getValue().isFinished() && !teams.contains(TeamManager.getInstance().getTeamByPlayer(entry.getKey()).getName()))
                    .forEach(entry -> teams.add(TeamManager.getInstance().getTeamByPlayer(entry.getKey()).getName()));

            int ret = TeamManager.getInstance().getTeams().size() - teams.size();

            teams.clear();

            return ret;
        }
    }

    /**
     * Sets the game lose for all players of a specific team
     *
     * @param teamName	The team that will lose
     */
    public void teamLose(String teamName) {
        Party.getInstance().getPlayers().getList().stream()
                .filter(playerName -> TeamManager.getInstance().getTeam(teamName).hasPlayer(playerName))
                .forEach(this::playerLose);
    }

    public void playerLose(String playerName) {
        Party.getInstance().getRanks().abandonRank(playerName);
        games.get(playerName).setLose(true);
        playerFinish(playerName);
        Party.getInstance().getPlayers().updatePlayersHeads(games.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getCurrentTarget())));
    }
    public void playerFinish(String playerName) {
        Party.getInstance().getRanks().finishRank(playerName);
        games.get(playerName).setFinished(true);
        games.get(playerName).getAgeDisplay().setTitle(LangManager.getMsgLang("GAME_DONE", games.get(playerName).getConfigLang()).replace("%i", String.valueOf(Party.getInstance().getRanks().getGameRank(playerName))));

        if (games.get(playerName).isLose()) {
            games.get(playerName).getAgeDisplay().setProgress(0.0f);
        } else {
            games.get(playerName).getAgeDisplay().setProgress(1.0f);
        }

        for (PotionEffect pe : Objects.requireNonNull(Bukkit.getPlayer(playerName)).getActivePotionEffects()) {
            Objects.requireNonNull(Bukkit.getPlayer(playerName)).removePotionEffect(pe.getType());
        }

        if (games.get(playerName).isLose()) {
            games.get(playerName).getAgeTimes().put(AgeManager.getAgeByNumber(games.get(playerName).getAge()).getName(), (System.currentTimeMillis() - games.get(playerName).getTimeStartAge()) * -1);
            games.get(playerName).setAge(games.get(playerName).getAge() + 1);

            for (int cnt = games.get(playerName).getAge(); cnt < (Config.getLastAge().getNumber() + 1); cnt++) {
                games.get(playerName).getAgeTimes().put(AgeManager.getAgeByNumber(cnt).getName(), (long) -1);
            }
        } else {
            games.get(playerName).getAgeTimes().put(AgeManager.getAgeByNumber(games.get(playerName).getAge()).getName(), (System.currentTimeMillis() - games.get(playerName).getTimeStartAge()));
        }

        games.get(playerName).setAge(-1);

        updatePlayerListName(playerName);

        if (Config.getPrintTab()) {
            Objects.requireNonNull(Bukkit.getPlayer(playerName)).sendMessage(playerString(playerName, games.get(playerName).getConfigLang()));
        }

        LogManager.getInstanceGame().logSystemMsg(logString(playerName));
    }

    /**
     * Prints the game end result tab from a specific player
     *
     * @param playerName	the player to display
     *
     * @return the player string
     */
    public String playerString(String playerName, String receiverLang) {
        long total = 0;
        StringBuilder sb = new StringBuilder();

        sb.append(ChatColor.GOLD).append(ChatColor.BOLD).append(playerName).append(ChatColor.RESET).append(":").append("\n");
        sb.append(ChatColor.BLUE).append(LangManager.getMsgLang("DEATH_COUNT", receiverLang).replace("%i", String.valueOf(ChatColor.RESET) + games.get(playerName).getDeathCount())).append("\n");
        sb.append(ChatColor.BLUE).append(LangManager.getMsgLang("SKIP_COUNT", receiverLang).replace("%i", String.valueOf(ChatColor.RESET) + games.get(playerName).getSkipCount())).append("\n");
        sb.append(ChatColor.BLUE).append(LangManager.getMsgLang("TEMPLATE_COUNT", receiverLang).replace("%i", String.valueOf(ChatColor.RESET) + games.get(playerName).getSbttCount())).append("\n");
        sb.append(ChatColor.BLUE).append(LangManager.getMsgLang("TIME_TAB", receiverLang)).append("\n");

        boolean abandon = false;

        for (int i = 0; i < (Config.getLastAge().getNumber() + 1); i++) {
            Age tmpAge = AgeManager.getAgeByNumber(i);

            if (games.get(playerName).getAgeTimes().get(tmpAge.getName()) == -1) {
                sb.append(LangManager.getMsgLang("FINISH_ABANDON", receiverLang).replace("%s", tmpAge.getColor() + tmpAge.getName().replace("_Age", "") + ChatColor.BLUE)).append("\n");
                abandon = true;
            } else if (games.get(playerName).getAgeTimes().get(tmpAge.getName()) < 0) {
                sb.append(LangManager.getMsgLang("ABANDON_AFTER", receiverLang).replace("%s", tmpAge.getColor() + tmpAge.getName().replace("_Age", "") + ChatColor.BLUE).replace("%t", ChatColor.RESET + Utils.getTimeFromSec((games.get(playerName).getAgeTimes().get(tmpAge.getName()) * -1) / 1000))).append("\n");
                total += (games.get(playerName).getAgeTimes().get(tmpAge.getName()) * -1) / 1000;
                abandon = true;
            } else {
                sb.append(LangManager.getMsgLang("FINISH_TIME", receiverLang).replace("%s", tmpAge.getColor() + tmpAge.getName().replace("_Age", "") + ChatColor.BLUE).replace("%t", ChatColor.RESET + Utils.getTimeFromSec(games.get(playerName).getAgeTimes().get(tmpAge.getName()) / 1000))).append("\n");
                total += games.get(playerName).getAgeTimes().get(tmpAge.getName()) / 1000;
            }
        }

        if (abandon) {
            sb.append(ChatColor.BLUE).append(LangManager.getMsgLang("FINISH_TOTAL", receiverLang).replace("%t", ChatColor.RESET + LangManager.getMsgLang("ABANDONED", receiverLang)));
            sb.append(" (").append(Utils.getTimeFromSec(total)).append(")");
        } else {
            sb.append(ChatColor.BLUE).append(LangManager.getMsgLang("FINISH_TOTAL", receiverLang).replace("%t", ChatColor.RESET + Utils.getTimeFromSec(total)));
        }

        return sb.toString();
    }

    /**
     * Logs the game end result tab from a specific player
     *
     * @return the log String
     */
    public String logString(String playerName) {
        long total = 0;
        String lang = Config.getLang();
        StringBuilder sb = new StringBuilder();

        sb.append(playerName).append(":").append("\n");
        sb.append(LangManager.getMsgLang("DEATH_COUNT", lang).replace("%i", String.valueOf(games.get(playerName).getDeathCount()))).append("\n");
        sb.append(LangManager.getMsgLang("SKIP_COUNT", lang).replace("%i", String.valueOf(games.get(playerName).getSkipCount()))).append("\n");
        sb.append(LangManager.getMsgLang("TEMPLATE_COUNT", lang).replace("%i", String.valueOf(games.get(playerName).getSbttCount()))).append("\n");
        sb.append(LangManager.getMsgLang("TIME_TAB", lang)).append("\n");

        boolean abandon = false;

        for (int i = 0; i < (Config.getLastAge().getNumber() + 1); i++) {
            Age tmpAge = AgeManager.getAgeByNumber(i);

            if (games.get(playerName).getAgeTimes().get(tmpAge.getName()) == -1) {
                sb.append(LangManager.getMsgLang("FINISH_ABANDON", lang).replace("%s", tmpAge.getName().replace("_Age", ""))).append("\n");
                abandon = true;
            } else if (games.get(playerName).getAgeTimes().get(tmpAge.getName()) < 0) {
                sb.append(LangManager.getMsgLang("ABANDON_AFTER", lang).replace("%s", tmpAge.getName().replace("_Age", "")).replace("%t", Utils.getTimeFromSec((games.get(playerName).getAgeTimes().get(tmpAge.getName()) * -1) / 1000))).append("\n");
                total += (games.get(playerName).getAgeTimes().get(tmpAge.getName()) * -1) / 1000;
                abandon = true;
            } else {
                sb.append(LangManager.getMsgLang("FINISH_TIME", lang).replace("%s", tmpAge.getName().replace("_Age", "")).replace("%t", Utils.getTimeFromSec(games.get(playerName).getAgeTimes().get(tmpAge.getName()) / 1000))).append("\n");
                total += games.get(playerName).getAgeTimes().get(tmpAge.getName()) / 1000;
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

    /**
     * Set player BossBar color randomly
     *
     * @param playerName	The playerName for which it will change game bossBar color
     */
    public void playerRandomBarColor(String playerName) {
        BarColor[] colors = BarColor.values();
        SecureRandom random = new SecureRandom();

        games.get(playerName).getAgeDisplay().setColor(colors[random.nextInt(colors.length)]);
    }

    /**
     * Reset player's BossBar
     *
     * @param playerName	The player's name
     */
    public void resetPlayerBar(String playerName) {
        if (games.get(playerName).getAgeDisplay() != null &&
                games.get(playerName).getAgeDisplay().getPlayers().size() != 0) {
            games.get(playerName).getAgeDisplay().removeAll();
        }
    }

    /**
     * Player goes to the next Age
     *
     * @param playerName	The name of the player that is moving to the next Age
     */
    public void nextPlayerAge(String playerName) {
        nextAge(playerName, games.get(playerName));

        if (games.get(playerName).getAge() < (Config.getLastAge().getNumber() + 1)) {
            games.forEach((k, v) -> Objects.requireNonNull(Bukkit.getPlayer(k)).sendMessage(LangManager.getMsgLang("AGE_MOVED", v.getConfigLang()).replace("<#>", ChatColor.BLUE + "<" + ChatColor.GOLD + playerName + ChatColor.BLUE + ">").replace("<##>", "<" + AgeManager.getAgeByNumber(games.get(playerName).getAge()).getColor() + AgeManager.getAgeByNumber(games.get(playerName).getAge()).getName().replace("_Age", "") + ChatColor.BLUE + ">")));
            Party.getInstance().getSpectators().getList().forEach(p -> Objects.requireNonNull(Bukkit.getPlayer(p)).sendMessage(LangManager.getMsgLang("AGE_MOVED", Config.getLang()).replace("<#>", ChatColor.BLUE + "<" + ChatColor.GOLD + playerName + ChatColor.BLUE + ">").replace("<##>", "<" + AgeManager.getAgeByNumber(games.get(playerName).getAge()).getColor() + AgeManager.getAgeByNumber(games.get(playerName).getAge()).getName().replace("_Age", "") + ChatColor.BLUE + ">")));
        }

        sendTips(playerName);
    }

    /**
     * Player goes to the next Age
     */
    private void nextAge(String playerName, PlayerData playerData) {
        if (Config.getRewards()) {
            if (playerData.getAge() > 0) {
                RewardManager.removePreviousRewardEffects(AgeManager.getAgeByNumber(playerData.getAge() - 1).getName(), Bukkit.getPlayer(playerName));
            }

            if (playerData.getAge() < Config.getLastAge().getNumber()) {
                RewardManager.givePlayerReward(AgeManager.getAgeByNumber(playerData.getAge()).getName(), Bukkit.getPlayer(playerName));
            }
        }

        long ageDuration = System.currentTimeMillis() - playerData.getTimeStartAge();

        playerData.getAgeTimes().put(AgeManager.getAgeByNumber(playerData.getAge()).getName(), ageDuration);
        playerData.setTotalTime(playerData.getTotalTime() + (ageDuration / 1000));

        Objects.requireNonNull(Bukkit.getPlayer(playerName)).sendMessage(LangManager.getMsgLang("TIME_AGE", playerData.getConfigLang()).replace("%t", Utils.getTimeFromSec(playerData.getTotalTime())));
        LogManager.getInstanceGame().logSystemMsg(LangManager.getMsgLang("AGE_VALIDATED", "en").replace("[#]", "[" + AgeManager.getAgeByNumber(playerData.getAge()).getName() + "]").replace("<#>", "<" + playerName + ">"));

        playerData.setTimeStartAge(System.currentTimeMillis());
        playerData.clearAlreadyGot();
        playerData.setCurrentTarget(null);
        playerData.setTargetCount(1);
        playerData.setSameIdx(0);
        playerData.setAge(playerData.getAge() + 1);
        Objects.requireNonNull(Bukkit.getPlayer(playerName)).playSound(Objects.requireNonNull(Bukkit.getPlayer(playerName)).getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1f, 1f);
        playerData.getScore().setScore(playerData.getTargetCount());

        if (playerData.getAge() == (Config.getLastAge().getNumber() + 1)) {
            return ;
        }

        updatePlayerListName(playerName);
        updatePlayerBar(playerName);
    }

    /**
     * Skip target for a specific player
     *
     * @param playerName The player form whom the target will be skipped
     * @param malus      If True a malus of 1 target is applied
     */
    public void skipPlayerTarget(String playerName, boolean malus) {
        if (malus) {
            if ((games.get(playerName).getAge()) < Config.getSkipAge().getNumber()) {
                LogManager.getInstanceGame().writeMsg(Objects.requireNonNull(Bukkit.getPlayer(playerName)), LangManager.getMsgLang("CANT_SKIP_AGE", games.get(playerName).getConfigLang()));

                return;
            }

            if (games.get(playerName).getTargetCount() == 1) {
                LogManager.getInstanceGame().writeMsg(Objects.requireNonNull(Bukkit.getPlayer(playerName)), LangManager.getMsgLang("CANT_SKIP_FIRST", games.get(playerName).getConfigLang()));

                return;
            }

            if (games.get(playerName).getTargetCount() > Config.getTargetPerAge()) {
                LogManager.getInstanceGame().writeMsg(Objects.requireNonNull(Bukkit.getPlayer(playerName)), LangManager.getMsgLang("CANT_SKIP_FINISHED", games.get(playerName).getConfigLang()));

                return;
            }

            games.get(playerName).setTargetCount(games.get(playerName).getTargetCount() - 1);
            games.get(playerName).setSkipCount(games.get(playerName).getSkipCount() + 1);

            if (games.get(playerName).getCurrentTarget().contains("/")) {
                LogManager.getInstanceGame().writeMsg(Objects.requireNonNull(Bukkit.getPlayer(playerName)), LangManager.getMsgLang("ITEMS_SKIP", games.get(playerName).getConfigLang()).replace("[#]", "[" + games.get(playerName).getCurrentTarget().split("/")[0] + "]").replace("[##]", "[" + games.get(playerName).getCurrentTarget().split("/")[1] + "]"));
            } else {
                LogManager.getInstanceGame().writeMsg(Objects.requireNonNull(Bukkit.getPlayer(playerName)), LangManager.getMsgLang("ITEM_SKIP", games.get(playerName).getConfigLang()).replace("[#]", "[" + games.get(playerName).getCurrentTarget() + "]"));
            }

        }

        games.get(playerName).getScore().setScore(games.get(playerName).getTargetCount());
        games.get(playerName).setCurrentTarget(null);
        updatePlayerBar(playerName);

    }

    /**
     * Finish an Age for a player
     *
     * @param player	The player
     */
    public void finishPlayerAge(String player) {
        games.get(player).setTargetCount(Config.getTargetPerAge() + 1);
        games.get(player).setCurrentTarget(null);
        games.get(player).getScore().setScore(games.get(player).getTargetCount());
        updatePlayerBar(player);
    }

    /**
     * Saves players data into files
     *
     * @param path	The location in which files will be generated
     */
    public void savePlayers(String path) {
        games.forEach((playerName, playerData) -> {
            savePlayer(path, playerName, playerData);

            stopPlayer(playerName, playerData);
        });

        Party.getInstance().getPlayers().getList().forEach(games::remove);
    }

    /**
     * Save Player game in a file
     *
     * @param path	File Path
     * @param playerName	Player name
     */
    public void savePlayer(String path, String playerName, PlayerData playerData) {
        try (FileOutputStream fos = new FileOutputStream(path + File.separator + playerName + ".k")) {
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(playerData);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            Utils.logException(e);
        }
    }

    /**
     * Clears Player's data
     *
     * @param playerName	The player name
     * @param playerData	The player data to clear
     */
    public void stopPlayer(String playerName, PlayerData playerData) {
        for (PotionEffect pe : Objects.requireNonNull(Bukkit.getPlayer(playerName)).getActivePotionEffects()) {
            Objects.requireNonNull(Bukkit.getPlayer(playerName)).removePotionEffect(pe.getType());
        }

        resetPlayerBar(playerName);
        playerData.clear();
    }

    public void loadPlayers(String path) throws RuntimeException {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        players.stream().filter(player -> Utils.fileExists(path, player.getName() + ".k")).forEach(player -> {
            try {
                Party.getInstance().getPlayers().addPlayer(player.getName());
                loadPlayerGame(path, player);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        players.clear();
    }

    /**
     * Loads a new Game for a specific Player
     *
     * @param player	The player that will be loaded
     *
     * @throws IOException if FileReader fails
     * @throws ClassNotFoundException Classic IStream exception
     */
    public void loadPlayerGame(String path, Player player) throws IOException, ClassNotFoundException {
        loadPlayer(path, player);

        GameLoop.updatePlayerDisplayTarget(games.get(player.getName()));
        Party.getInstance().getPlayers().updatePlayersHeads();

        if (games.get(player.getName()).isDead()) {
            teleportAutoBack(player);
        }

        updatePlayerBar(player.getName());
        reloadPlayerEffects(player.getName());
        updatePlayerListName(player.getName());
    }

    public void loadPlayer(String path, Player player) throws IOException, ClassNotFoundException {
        try (FileInputStream fos = new FileInputStream(path + File.separator + player.getName() + ".k")) {
            ObjectInputStream ois = new ObjectInputStream(fos);

            PlayerData playerData = (PlayerData) ois.readObject();
            ois.close();

            playerData.setup(player);
            games.put(player.getName(), playerData);
        }

        ScoreManager.setupPlayerScores(player.getName());

        games.get(player.getName()).getScore().setScore(games.get(player.getName()).getTargetCount());
    }

    /**
     * Teleport a specific player to his death location
     */
    public void teleportAutoBack(Player player) {
        player.sendMessage(LangManager.getMsgLang("TP_BACK", games.get(player.getName()).getConfigLang()).replace("%i", String.valueOf(Config.getLevel().getSeconds())));

        Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
            Location loc = games.get(player.getName()).getDeathLoc();

            if (loc == null) {
                return;
            }

            if (Objects.requireNonNull(loc.getWorld()).getName().contains("the_end") && loc.getY() < 0) {
                Utils.changeLocForEnd(loc);
            }

            Utils.createSafeBox(loc, player.getName());

            player.teleport(loc);

            restorePlayerInv(player);

            for (PotionEffect p : player.getActivePotionEffects()) {
                player.removePotionEffect(p.getType());
            }

            reloadPlayerEffects(player.getName());

            for (Entity e : player.getNearbyEntities(3.0, 3.0, 3.0)) {
                if (e.getType() != EntityType.DROPPED_ITEM &&
                        e.getType() != EntityType.PLAYER) {
                    e.remove();
                }
            }
        }, (Config.getLevel().getSeconds() * 20L));
    }

    /**
     * Give to a player the content of its saved inventory
     */
    public void restorePlayerInv(Player player) {
        for (ItemStack item : games.get(player.getName()).getDeathInv()) {
            HashMap<Integer, ItemStack> ret = player.getInventory().addItem(item);

            if (!ret.isEmpty()) {
                for (Integer cnt : ret.keySet()) {
                    player.getWorld().dropItem(player.getLocation(), ret.get(cnt));
                }
            }

            ret.clear();
        }

        games.get(player.getName()).setDeathLoc(null);
        games.get(player.getName()).setDead(false);
    }

    /**
     * Player died
     *
     * @param playerName	The dead player
     * @param deathLoc	The death Location
     */
    public void playerDied(String playerName, Location deathLoc) {
        games.get(playerName).setDeathCount(games.get(playerName).getDeathCount() + 1);

        if (Config.getLevel().isLosable()) {
            playerLose(playerName);
        } else {
            storePlayerInv(playerName);
            Objects.requireNonNull(Bukkit.getPlayer(playerName)).getInventory().clear();
        }

        games.get(playerName).setDeathLoc(deathLoc);
        games.get(playerName).setDead(true);
    }

    /**
     * Store actual player inventory into another one
     */
    public void storePlayerInv(String playerName) {
        games.get(playerName).clearDeathInv();
        games.get(playerName).saveDeathInv(Arrays.stream(Objects.requireNonNull(Bukkit.getPlayer(playerName)).getInventory().getContents())
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }

    /**
     * Logs and Prints game end result tab
     */
    public void printGameEnd() {
        games.forEach((senderName, senderData) -> {
            games.forEach((playerName, playerData) ->
                    Objects.requireNonNull(Bukkit.getPlayer(playerName)).sendMessage(playerString(senderName, playerData.getConfigLang())));

            logString(senderName);
        });

        games.forEach((senderName, senderData) ->
                Party.getInstance().getSpectators().getList().forEach(playerName ->
                        Objects.requireNonNull(Bukkit.getPlayer(playerName)).sendMessage(playerString(senderName, Config.getLang()))));
    }
}
