package fr.kosmosuniverse.kuffle.core;

import fr.kosmosuniverse.kuffle.utils.Utils;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author KosmosUniverse
 */
public class PartyList {
    private final List<String> list;
    @Getter
    private Inventory playerHeads;

    /**
     * Constructor
     */
    public PartyList() {
        list = new ArrayList<>();
    }

    /**
     * Gets players list as unmodifiable list
     *
     * @return unmodifiable player list
     */
    public List<String> getList() {
        return list;
    }

    /**
     * Add a Player to the party
     *
     * @param playerName The name of the player to add
     *
     * @return True if the player have been added, False instead
     */
    public boolean addPlayer(String playerName) {
        boolean ret = Bukkit.getOnlinePlayers().stream().anyMatch(p -> p.getName().equals(playerName));

        if (ret) {
            list.add(playerName);
        }

        return ret;
    }

    /**
     * Method to add multiple players to the party
     *
     * @param playersName List of the player Names
     *
     * @return The amount of players added to the party
     */
    public int addPlayers(List<String> playersName) {
        AtomicInteger cnt = new AtomicInteger();

        playersName.stream()
                .filter(playerName -> !list.contains(playerName))
                .forEach(playerName -> {
                    if (addPlayer(playerName)) {
                        cnt.incrementAndGet();
                    }
                });

        return cnt.get();
    }

    /**
     * Removes the player from the party
     *
     * @param playerName The player to remove
     *
     * @return True if the player have been removed, False instead
     */
    public boolean removePlayer(String playerName) {
        boolean ret = list.contains(playerName);

        if (ret) {
            ret = list.remove(playerName);
        }

        return ret;
    }

    /**
     * Removes a list of players from party
     *
     * @param playersName Players to remove
     *
     * @return The amount of player removed
     */
    public int removePlayers(List<String> playersName) {
        AtomicInteger cnt = new AtomicInteger();

        list.stream().filter(list::contains)
                .forEach(playerName -> {
                    if (removePlayer(playerName)) {
                        cnt.incrementAndGet();
                    }
                });

        return cnt.get();
    }

    /**
     * Clears the player list
     */
    public void clear() {
        list.clear();
    }

    /**
     * Check if a player is part of the party
     *
     * @param playerName The player to check
     *
     * @return True if the player is present, False instead
     */
    public boolean has(String playerName) {
        return list.contains(playerName);
    }

    /**
     * Gets the display String
     *
     * @return Party list as String
     */
    public String getDisplayString() {
        return list.isEmpty() ? "" : String.join(", ", list);
    }

    /**
     * Updates players heads in playersHeads inventory
     */
    public void updatePlayersHeads(Map<String, String> playerTargets) {
        int slots = Utils.getNbInventoryRows(list.size());
        Inventory newInv = Bukkit.createInventory(null, slots == 0 ? 9 : slots, ChatColor.BLACK + "Players");

        list.forEach(name -> newInv.addItem(Utils.getHead(name, playerTargets.get(name))));

        if (playerHeads != null) {
            playerHeads.clear();
        }

        playerHeads = newInv;
    }

    /**
     * Updates players heads in playersHeads inventory
     */
    public void updatePlayersHeads() {
        int slots = Utils.getNbInventoryRows(list.size());
        Inventory newInv = Bukkit.createInventory(null, slots == 0 ? 9 : slots, ChatColor.BLACK + "Players");

        list.forEach(name -> newInv.addItem(Utils.getHead(name, null)));

        if (playerHeads != null) {
            playerHeads.clear();
        }

        playerHeads = newInv;
    }
}
