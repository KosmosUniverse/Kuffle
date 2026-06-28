package fr.kosmosuniverse.kuffle.datamanagers.results;

import fr.kosmosuniverse.kuffle.KuffleMain;
import fr.kosmosuniverse.kuffle.core.Config;
import fr.kosmosuniverse.kuffle.core.LogManager;
import fr.kosmosuniverse.kuffle.core.PlayerData;
import fr.kosmosuniverse.kuffle.core.TeamManager;
import fr.kosmosuniverse.kuffle.datamanagers.age.Age;
import fr.kosmosuniverse.kuffle.datamanagers.age.AgeManager;
import fr.kosmosuniverse.kuffle.utils.ItemMaker;
import fr.kosmosuniverse.kuffle.utils.ItemsUtils;
import fr.kosmosuniverse.kuffle.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
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
        rd = new ResultData(Config.getTeam(), Config.getSkip(), Config.getSBTT(), Config.getLastAge().getNumber());

        if (rd.isTeam()) {
            TeamManager.getInstance().getTeams().forEach(t -> {
                rd.getTeamsData().addDeath(t.getName(), games.entrySet().stream()
                        .filter(e -> t.getPlayers().contains(e.getKey()))
                        .mapToInt(e -> e.getValue().getDeathCount())
                        .sum());
                if (rd.isSkip()) {
                    rd.getTeamsData().addSkip(t.getName(), games.entrySet().stream()
                            .filter(e -> t.getPlayers().contains(e.getKey()))
                            .mapToInt(e -> e.getValue().getSkipCount())
                            .sum());
                }

                if (rd.isSbtt()) {
                    rd.getTeamsData().addSbtt(t.getName(), games.entrySet().stream()
                            .filter(e -> t.getPlayers().contains(e.getKey()))
                            .mapToInt(e -> e.getValue().getSbttCount())
                            .sum());
                }

                rd.getTeamsData().addTimes(t.getName(), getTeamTimesFromPlayerTimes(games.entrySet().stream()
                        .filter(e -> t.getPlayers().contains(e.getKey()))
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getAgeTimes()))));
            });
        }

        games.forEach((key, value) -> {
            rd.getPlayersData().addTimes(key, value.getAgeTimes());
            rd.getPlayersData().addDeath(key, value.getDeathCount());

            if (rd.isSkip()) {
                rd.getPlayersData().addSkip(key, value.getSkipCount());
            }

            if (rd.isSbtt()) {
                rd.getPlayersData().addSbtt(key, value.getSkipCount());
            }
        });

        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss");

        try (FileOutputStream fos = new FileOutputStream(KuffleMain.getInstance().getDataFolder() + File.separator + "results" + dtf.format(ldt) + ".k")) {
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
        try (FileInputStream fis = new FileInputStream(KuffleMain.getInstance().getDataFolder() + File.separator + filename)) {
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
            try {
                createInventories();
            } catch (Exception ignored) {
                rd.printResult();
            }
            LogManager.getInstanceSystem().logSystemMsg("Results data loaded.");
            return true;
        }
    }

    private Map<String, Long> getTeamTimesFromPlayerTimes(Map<String, Map<String, Long>> playersAgeTimes) {
        Map<String, Long> result = new HashMap<>();

        for (AtomicInteger i = new AtomicInteger(0); i.get() <= rd.getLastAge(); i.incrementAndGet()) {
            result.put(AgeManager.getAgeByNumber(i.get()).getName(), playersAgeTimes.values().stream()
                    .mapToLong(stringLongMap -> stringLongMap.get(AgeManager.getAgeByNumber(i.get()).getName()))
                    .max().orElse(-1));
        }

        return result;
    }

    public void createInventories() {
        createMainInv();
        createCountingInv("Death", rd.getPlayersData().getDeath(), rd.isTeam() ? rd.getTeamsData().getDeath() : null);

        if (rd.isSkip()) {
            createCountingInv("Skip", rd.getPlayersData().getSkip(), rd.isTeam() ? rd.getTeamsData().getSkip() : null);
        }

        if (rd.isSbtt()) {
            createCountingInv("Sbtt", rd.getPlayersData().getSbtt(), rd.isTeam() ? rd.getTeamsData().getSbtt() : null);
        }

        createTimesInv();
        createTotalTimeInv();
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

        main.addItem(ItemMaker.newItem(Material.COMPASS).addName("Total Time Board").addTag("invname", "Total Time Board").getItem());

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
        AtomicInteger rank = new AtomicInteger(0);
        AtomicInteger previousValue = new AtomicInteger(-1);

        setupFirstRow(playerInv, invName + " Board");

        playerData.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(e -> {
                    int tmp = e.getValue() == previousValue.get() ? rank.get() : rank.incrementAndGet();

                    playerInv.addItem(ItemMaker.newItem(Utils.getHead(Bukkit.getPlayer(e.getKey())))
                            .addName(tmp + "# " + e.getKey())
                            .addLore(invName + " : " + e.getValue())
                            .getItem());

                    previousValue.set(e.getValue());
                });

        invs.put("Player " + invName + " Ranks", playerInv);

        if (rd.isTeam()) {
            Inventory teamInv = Bukkit.createInventory(null, Utils.getNbInventoryRows(teamData.size()) + 9, "Team " + invName + " Ranks");
            rank.set(0);
            previousValue.set(-1);

            setupFirstRow(teamInv, invName + " Board");

            teamData.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .forEach(e -> {
                        int tmp = e.getValue() == previousValue.get() ? rank.get() : rank.incrementAndGet();

                        teamInv.addItem(ItemMaker.newItem(Utils.getHead(Bukkit.getPlayer(e.getKey())))
                                .addName(tmp + "# " + e.getKey())
                                .addLore(invName + " : " + e.getValue())
                                .getItem());

                        previousValue.set(e.getValue());
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

        createAgesTimes("Player", rd.getPlayersData().getTimes());

        if (rd.isTeam()) {
            createAgesTimes("Team", rd.getTeamsData().getTimes());
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
        Inventory playersInv = Bukkit.createInventory(null, Utils.getNbInventoryRows(rd.getPlayersData().getTimes().size()) + 9, "Players Times Board");

        setupFirstRow(playersInv, "Times Board");

        rd.getPlayersData().getTimes()
                .keySet()
                .forEach(playerName -> playersInv.addItem(ItemMaker.newItem(Utils.getHead(Bukkit.getPlayer(playerName)))
                        .addName(playerName)
                        .addTag("invname", playerName + " Times")
                        .getItem()));

        invs.put("Players Times Board", playersInv);

        rd.getPlayersData().getTimes().forEach((playerName, playerTimes) -> createPersonalTimes(playerName + " Times", playerTimes, "Players Times Board"));
    }

    private void createTeamsTimeInv() {
        Inventory teamInv = Bukkit.createInventory(null, Utils.getNbInventoryRows(rd.getTeamsData().getTimes().size()) + 9, "Teams Times Board");

        setupFirstRow(teamInv, "Times Board");

        rd.getTeamsData().getTimes()
                .keySet()
                .forEach(teamName -> teamInv.addItem(ItemMaker.newItem(Material.PLAYER_HEAD)
                        .addName(teamName)
                        .addTag("invname", teamName + " Times")
                        .getItem()));

        invs.put("Teams Times Board", teamInv);

        rd.getTeamsData().getTimes().forEach((teamName, teamTimes) -> createPersonalTimes(teamName + " Times", teamTimes, "Teams Times Board"));
    }

    private String createStringTimeFromValue(long time) {
        if (time == 0 || time / 1000 == 0) {
            return "Abandoned";
        } else if (time < 0) {
            return "Abandoned after " + Utils.getTimeFromSec((time * -1) / 1000);
        } else {
            return Utils.getTimeFromSec(time / 1000);
        }
    }

    static class TimeComparator implements Comparator<Map.Entry<String, Long>> {
        @Override
        public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
            return compareValue(o1.getValue(), o2.getValue());
        }

        // Put in asc order but with neg number in desc order and after pos numbers
        private int compareValue(Long time1, Long time2) {
            if (time1 < 0 || time2 < 0) {
                if (time1 < 0 && time2 < 0) {
                    return time1.compareTo(time2);
                } else if (time1 >= 0) {
                    return -1;
                } else {
                    return 1;
                }
            } else if (time1 == 0 || time2 == 0) {
                if (time1 == 0 && time2 == 0) {
                    return 0;
                } else if (time1 > 0) {
                    return -1;
                } else {
                    return 1;
                }
            } else {
                return time1.compareTo(time2);
            }
        }
    }

    private void createPersonalTimes(String invName, Map<String, Long> times, String prevInv) {
        Inventory inv = Bukkit.createInventory(null, 18, invName);

        setupFirstRow(inv, prevInv);

        AgeManager.getAges().stream()
                .filter(a -> a.getNumber() != -1)
                .sorted(Comparator.comparing(Age::getNumber))
                .filter(a -> a.getNumber() <= rd.getLastAge())
                .forEach(a -> inv.addItem(ItemMaker.newItem(a.getBox())
                        .addName(a.getColor() + a.getName().replace("_", " "))
                        .addLore("Time : " + createStringTimeFromValue(times.get(a.getName())))
                        .getItem()));

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
                    createAgeTimes(invName, a.getName(), datas.entrySet()
                            .stream()
                            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(a.getName()))));
                });

        invs.put(invName + " Ages Times Board", inv);
    }

    private void createAgeTimes(String prevInvName, String ageName, Map<String, Long> datas) {
        Inventory inv = Bukkit.createInventory(null, Utils.getNbInventoryRows(datas.size()) + 9, prevInvName + " " + ageName.replace("_", " ") + " Times");

        setupFirstRow(inv, prevInvName + " Ages Times Board");

        AtomicInteger rank = new AtomicInteger(0);
        AtomicLong previousValue = new AtomicLong(-1);

        datas.entrySet()
                .stream()
                .sorted(new TimeComparator())
                .forEach(e -> {
                    int tmp = e.getValue() == previousValue.get() ? rank.get() : rank.incrementAndGet();

                    inv.addItem(ItemMaker.newItem(Utils.getHead(Bukkit.getPlayer(e.getKey())))
                            .addName(tmp + "# : " + e.getKey())
                            .addLore("Time : " + createStringTimeFromValue(e.getValue()))
                            .getItem());

                    previousValue.set(e.getValue());
                });

        invs.put(prevInvName + " " + ageName.replace("_", " ") + " Times", inv);
    }

    private void createTotalTimeInv() {
        Inventory inv = Bukkit.createInventory(null, 18, "Total Time Board");

        setupFirstRow(inv, "Party Results");

        inv.addItem(ItemMaker.newItem(Material.PLAYER_HEAD)
                .addName("Players Total Time")
                .addTag("invname", "Players Total Time Board")
                .getItem());

        if (rd.isTeam()) {
            inv.addItem(ItemMaker.newItem(Material.PLAYER_HEAD)
                    .addName("Teams Total Time")
                    .addTag("invname", "Teams Total Time Board")
                    .getItem());

            createTotalTimeSpecificInv("Teams", rd.getTeamsData().getTimes().entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey,
                            e -> e.getValue()
                                    .values()
                                    .stream()
                                    .mapToLong(l -> l)
                                    .sum())));
        }

        invs.put("Total Time Board", inv);

        createTotalTimeSpecificInv("Players", rd.getPlayersData().getTimes().entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue()
                                .values()
                                .stream()
                                .mapToLong(l -> l)
                                .sum())));
    }

    private void createTotalTimeSpecificInv(String invName, Map<String, Long> datas) {
        Inventory totalTimeInv = Bukkit.createInventory(null, Utils.getNbInventoryRows(datas.size()) + 9, invName + " Total Time Board");
        AtomicInteger rank = new AtomicInteger(0);
        AtomicLong previousValue = new AtomicLong(-1);

        setupFirstRow(totalTimeInv, "Total Time Board");

        datas.entrySet()
                .stream()
                .sorted(new TimeComparator())
                .forEach(e -> {
                    int tmp = previousValue.get() == e.getValue() ? rank.get() : rank.incrementAndGet();

                    totalTimeInv.addItem(ItemMaker.newItem(Utils.getHead(Bukkit.getPlayer(e.getKey())))
                            .addName(tmp + "# : " + e.getKey())
                            .addLore("Time : " + createStringTimeFromValue(e.getValue()))
                            .getItem());

                    previousValue.set(e.getValue());
                });

        invs.put(invName + " Total Time Board", totalTimeInv);
    }
}
