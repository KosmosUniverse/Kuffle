package fr.kosmosuniverse.kuffle.core;

import fr.kosmosuniverse.kuffle.KuffleMain;
import fr.kosmosuniverse.kuffle.utils.ItemMaker;
import fr.kosmosuniverse.kuffle.utils.ItemsUtils;
import fr.kosmosuniverse.kuffle.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.io.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author KosmosUniverse
 */
public class ResultManager {
    private ResultData rd = null;
    private static ResultManager instance;
    private final Map<String, Inventory> invs = new HashMap<>();

    public static synchronized ResultManager getInstance() {
        if (instance == null) {
            instance = new ResultManager();
        }

        return instance;
    }

    public boolean isResultsLoaded() {
        return rd != null;
    }

    public Inventory getMainResults() {
        return invs.get("Party Results");
    }

    public boolean hasInv(String invName) {
        return invs.containsKey(invName);
    }

    public Inventory getInv(String invName) {
        return invs.get(invName);
    }

    /**
     * Save in variable and file the Game results
     */
    public void saveGameResults(Map<String, PlayerData> games) {
        rd = new ResultData();

        rd.setTeam(Config.getTeam());
        rd.setSkip(Config.getSkip());
        rd.setSbtt(Config.getSBTT());
        rd.setLastAge(Config.getLastAge().getNumber());

        if (Config.getTeam()) {
            TeamManager.getInstance().getTeams().forEach(t -> {
                rd.addTeam(t.getName(), t.getPlayers());
                rd.addTeamDeath(t.getName(), games.entrySet().stream()
                        .filter(e -> t.getPlayers().contains(e.getKey()))
                        .mapToInt(e -> e.getValue().getDeathCount())
                        .sum());
                if (Config.getSkip()) {
                    rd.addTeamSkip(t.getName(), games.entrySet().stream()
                            .filter(e -> t.getPlayers().contains(e.getKey()))
                            .mapToInt(e -> e.getValue().getSkipCount())
                            .sum());
                }

                if (Config.getSBTT()) {
                    rd.addTeamSbtt(t.getName(), games.entrySet().stream()
                            .filter(e -> t.getPlayers().contains(e.getKey()))
                            .mapToInt(e -> e.getValue().getSbttCount())
                            .sum());
                }

                rd.addTeamTimes(t.getName(), getTeamTimesFromPlayerTimes(games.entrySet().stream()
                        .filter(e -> t.getPlayers().contains(e.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getAgeTimes()))));
            });
        }

        games.forEach((key, value) -> {
            rd.addPlayerTimes(key, value.getAgeTimes());
            rd.addPlayerDeath(key, value.getDeathCount());

            if (Config.getSkip()) {
                rd.addPlayerSkip(key, value.getSkipCount());
            }

            if (Config.getSBTT()) {
                rd.addPlayerSbtt(key, value.getSkipCount());
            }
        });

        try (FileOutputStream fos = new FileOutputStream(KuffleMain.getInstance().getDataFolder() + File.separator + "results.k")) {
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(rd);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            Utils.logException(e);
        }
    }

    /**
     * Load in variable Game results from file
     */
    public boolean loadGameResult(String filename) {
        try (FileInputStream fis = new FileInputStream(KuffleMain.getInstance().getDataFolder() + File.separator + filename + ".k")) {
            ObjectInputStream ois = new ObjectInputStream(fis);
            rd = (ResultData) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            Utils.logException(e);
            rd = null;
        }

        if (rd == null) {
            LogManager.getInstanceSystem().logSystemMsg("Cannot load Results.");
            return false;
        } else {
            //printResults();

            createInventories();
            LogManager.getInstanceSystem().logSystemMsg("Results data loaded.");
            return true;
        }
    }

    private Map<String, Long> getTeamTimesFromPlayerTimes(Map<String, Map<String, Long>> playersAgeTimes) {
        Map<String, Long> result = new HashMap<>();

        for (AtomicInteger i = new AtomicInteger(0); i.get() < Config.getLastAge().getNumber(); i.incrementAndGet()) {
            result.put(AgeManager.getAgeByNumber(i.get()).getName(), playersAgeTimes.values().stream()
                    .mapToLong(stringLongMap -> stringLongMap.get(AgeManager.getAgeByNumber(i.get()).getName()))
                    .max().orElse(-1));
        }

        return result;
    }

    public void createInventories() {
        createMainInv();
        createCountingInv("Death", rd.getPlayersDeath(), rd.isTeam() ? rd.getTeamDeath() : null);

        if (rd.isSkip()) {
            createCountingInv("Skip", rd.getPlayersSkip(), rd.isTeam() ? rd.getTeamSkip() : null);
        }

        if (rd.isSbtt()) {
            createCountingInv("Sbtt", rd.getPlayersSbtt(), rd.isTeam() ? rd.getTeamSbtt() : null);
        }

        createTimesInv();
    }

    private void createMainInv() {
        Inventory main = Bukkit.createInventory(null, 9, "Party Results");

        main.addItem(ItemMaker.newItem(Material.SKELETON_SKULL).addName("Death Board").addTag("invname", "Death Board").getItem());

        if (rd.isSkip()) {
            main.addItem(ItemMaker.newItem(Material.BARRIER).addName("Skip Board").addTag("invname", "Skip Board").getItem());
        }

        if (rd.isSbtt()) {
            main.addItem(ItemMaker.newItem(Material.EMERALD).addName("SBTTs Board").addTag("invname", "Sbtts Board").getItem());
        }

        main.addItem(ItemMaker.newItem(Material.CLOCK).addName("Times Board").addTag("invname", "Times Board").getItem());

        invs.put("Party Results", main);
    }

    private void createCountingInv(String invName, Map<String, Integer> playerData, Map<String, Integer> teamData) {
        Inventory cntInv = Bukkit.createInventory(null, 18, invName + " Board");

        setupFirstRow(cntInv, "Party Results");

        cntInv.addItem(ItemMaker.newItem(Material.PLAYER_HEAD)
                .addName("Player " + invName + " Ranks")
                .addTag("invname", "Player " + invName + " Ranks")
                .getItem());

        if (rd.isTeam()) {
            cntInv.addItem(ItemMaker.newItem(Material.PLAYER_HEAD)
                    .addName("Team " + invName + " Ranks")
                    .addTag("invname", "Team " + invName + " Ranks")
                    .getItem());
        }

        invs.put(invName + " Board", cntInv);

        Inventory playerInv = Bukkit.createInventory(null, Utils.getNbInventoryRows(playerData.size()) + 9, "Player " + invName + " Ranks");
        AtomicInteger aInt = new AtomicInteger(1);

        setupFirstRow(playerInv, invName + " Board");

        playerData.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(e -> {
                    playerInv.addItem(ItemMaker.newItem(Utils.getHead(Bukkit.getPlayer(e.getKey())))
                            .addName(aInt.get() + "# " + e.getKey())
                            .addLore(invName + " : " + e.getValue())
                            .getItem());
                    aInt.incrementAndGet();
                });

        invs.put("Player " + invName + " Ranks", playerInv);

        if (rd.isTeam()) {
            Inventory teamInv = Bukkit.createInventory(null, Utils.getNbInventoryRows(teamData.size()) + 9, "Team " + invName + " Ranks");
            aInt.set(1);

            setupFirstRow(teamInv, invName + " Board");

            teamData.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .forEach(e -> {
                        teamInv.addItem(ItemMaker.newItem(Utils.getHead(Bukkit.getPlayer(e.getKey())))
                                .addName(aInt.get() + "# " + e.getKey())
                                .addLore(invName + " : " + e.getValue())
                                .getItem());
                        aInt.incrementAndGet();
                    });

            invs.put("Team " + invName + " Ranks", teamInv);
        }
    }

    private void createTimesInv() {
        Inventory inv = Bukkit.createInventory(null, 18, "Times Board");

        setupFirstRow(inv, "Party Results");

        inv.addItem(ItemMaker.newItem(Material.PLAYER_HEAD)
                .addName("Players Times")
                .addTag("invname", "Players Times Board")
                .getItem());

        if (rd.isTeam()) {
            inv.addItem(ItemMaker.newItem(Material.PLAYER_HEAD)
                    .addName("Teams Times")
                    .addTag("invname", "Teams Times Board")
                    .getItem());
        }

        inv.addItem(ItemMaker.newItem(Material.PLAYER_HEAD)
                .addName("Player Ages Times")
                .addTag("invname", "Player Ages Times Board")
                .getItem());

        if (rd.isTeam()) {
            inv.addItem(ItemMaker.newItem(Material.PLAYER_HEAD)
                    .addName("Team Ages Times")
                    .addTag("invname", "Team Ages Times Board")
                    .getItem());
        }

        invs.put("Times Board", inv);

        createPlayersTimeInv();

        if (rd.isTeam()) {
            createTeamsTimeInv();
        }

        createAgesTimes("Player", rd.getPlayersTimes());

        if (rd.isTeam()) {
            createAgesTimes("Team", rd.getTeamTimes());
        }
    }

    private void setupFirstRow(Inventory inv, String prevInv) {
        for (int i = 0; i < 9; i++) {
            if (i == 0) {
                inv.setItem(i, prevInv != null ? ItemMaker.newItem(ItemsUtils.getBackPane()).addTag("invname", prevInv).getItem() : ItemsUtils.getLimitPane());
            } else {
                inv.setItem(i, ItemsUtils.getLimitPane());
            }
        }
    }

    private void createPlayersTimeInv() {
        Inventory playersInv = Bukkit.createInventory(null, Utils.getNbInventoryRows(rd.getPlayersTimes().size()) + 9, "Players Times Board");

        setupFirstRow(playersInv, "Times Board");

        rd.getPlayersTimes()
                .keySet()
                .forEach(playerName -> playersInv.addItem(ItemMaker.newItem(Utils.getHead(Bukkit.getPlayer(playerName)))
                        .addName(playerName)
                        .addTag("invname", playerName + " Times")
                        .getItem()));

        invs.put("Players Times Board", playersInv);

        rd.getPlayersTimes().forEach((playerName, playerTimes) -> createPersonalTimes(playerName + " Times", playerTimes, "Players Times Board"));
    }

    private void createTeamsTimeInv() {
        Inventory teamInv = Bukkit.createInventory(null, Utils.getNbInventoryRows(rd.getTeamTimes().size()) + 9, "Teams Times Board");

        setupFirstRow(teamInv, "Times Board");

        rd.getTeamTimes()
                .keySet()
                .forEach(teamName -> teamInv.addItem(ItemMaker.newItem(Material.PLAYER_HEAD)
                        .addName(teamName)
                        .addTag("invname", teamName + " Times")
                        .getItem()));

        invs.put("Teams Times Board", teamInv);

        rd.getTeamTimes().forEach((teamName, teamTimes) -> createPersonalTimes(teamName + " Times", teamTimes, "Teams Times Board"));
    }

    private void createPersonalTimes(String invName, Map<String, Long> times, String prevInv) {
        Inventory inv = Bukkit.createInventory(null, 18, invName);

        setupFirstRow(inv, prevInv);

        Map<String, Long> tmp = times.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (e.getValue() >= 0 ? e.getValue() : Long.MAX_VALUE)));

        AgeManager.getAges().stream()
                .filter(a -> a.getNumber() != -1)
                .sorted(Comparator.comparing(Age::getNumber))
                .filter(a -> a.getNumber() <= rd.getLastAge())
                .forEach(a -> inv.addItem(ItemMaker.newItem(a.getBox())
                        .addName(a.getColor() + a.getName().replace("_", " "))
                        .addLore("Time : " + (tmp.get(a.getName()) != Long.MAX_VALUE ? Utils.getTimeFromSec(tmp.get(a.getName()) / 1000) : "Abandoned"))
                        .getItem()));

        tmp.clear();

        invs.put(invName, inv);
    }

    private void createAgesTimes(String invName, Map<String, Map<String, Long>> datas) {
        Inventory inv = Bukkit.createInventory(null, 18, invName + " Ages Times Board");

        setupFirstRow(inv, "Times Board");

        AgeManager.getAges()
                .stream()
                .filter(a -> a.getNumber() != -1)
                .sorted(Comparator.comparing(Age::getNumber))
                .filter(a -> a.getNumber() <= rd.getLastAge())
                .forEach(a -> {
                    inv.addItem(ItemMaker.newItem(a.getBox())
                                    .addName(a.getColor() + a.getName().replace("_", " "))
                                    .addTag("invname", invName + " " + a.getName().replace("_", " ") + " Times")
                                    .getItem());
                    createAgeTimes(invName, a.getName(), datas);
                });

        invs.put(invName + " Ages Times Board", inv);
    }

    private void createAgeTimes(String prevInvName, String ageName, Map<String, Map<String, Long>> datas) {
        Inventory inv = Bukkit.createInventory(null, Utils.getNbInventoryRows(datas.size()) + 9, prevInvName + " " + ageName.replace("_", " ") + " Times");

        setupFirstRow(inv, prevInvName + " Ages Times Board");

        AtomicInteger aInt = new AtomicInteger(1);

        datas.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (e.getValue().get(ageName) >= 0 ? e.getValue().get(ageName) : Long.MAX_VALUE)))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(e -> inv.addItem(ItemMaker.newItem(Utils.getHead(Bukkit.getPlayer(e.getKey())))
                        .addName(aInt.getAndIncrement() + "# : " + e.getKey())
                        .addLore("Time : " + (e.getValue() != Long.MAX_VALUE ? Utils.getTimeFromSec(e.getValue() / 1000) : "Abandoned"))
                        .getItem()));

        invs.put(prevInvName + " " + ageName.replace("_", " ") + " Times", inv);
    }
}
