package fr.kosmosuniverse.kuffle.utils;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author KosmosUniverse
 */
public abstract class Inventorisable<T> {
    private final Map<String, Inventory> invs;
    private final String mainInvName;
    private final boolean mainInvShortcut;
    @Getter
    private final String className;

    protected abstract ItemStack convertObjectIntoItemStack(T object);

    public static final class TmpPair {
        @Getter
        private final ItemStack item;
        @Getter
        private final String name;

        public TmpPair(ItemStack item, String name) {
            this.item = item;
            this.name = name;
        }
    }

    public Inventorisable(String mainInvName, boolean mainInvShortcut, String className) {
        invs = new HashMap<>();
        this.mainInvName = mainInvName;
        this.mainInvShortcut = mainInvShortcut;
        this.className = className;
    }

    public Inventory getMainInv() {
        return invs.get(mainInvName);
    }

    public Inventory getInv(String name) {
        return invs.get(name);
    }

    public void clear() {
        invs.clear();
    }

    protected int calcInvAmnt(int itemAmnt) {
        return (itemAmnt / 45) + (itemAmnt % 45 != 0 ? 1 : 0);
    }

    protected int calcInvRows(int itemAmnt) {
        int tmpSize = ((itemAmnt / 9) + (itemAmnt % 9 != 0 ? 1 : 0) + 1) * 9;

        return Math.min(tmpSize, 54);
    }

    protected void setupFirstRow(Inventory inv, String prevInv, String nextInv, String mainInv) {
        for (int i = 0; i < 9; i++) {
            if (i == 0) {
                inv.setItem(i, prevInv != null ? ItemMaker.newItem(ItemsUtils.getBackPane())
                        .addTag("invname", prevInv)
                        .addTag("class name", className)
                        .getItem() : ItemsUtils.getQuitPane());
            } else if (i == 4 && mainInv != null) {
                inv.setItem(i, ItemMaker.newItem(Material.MAGENTA_STAINED_GLASS_PANE)
                        .addName("Back to Main Menu")
                        .addTag("invname", mainInv)
                        .addTag("classname", className)
                        .getItem());
            } else if (i == 8 && nextInv != null) {
                inv.setItem(i, ItemMaker.newItem(ItemsUtils.getNextPane())
                        .addTag("invname", nextInv)
                        .addTag("classname", className)
                        .getItem());
            } else {
                inv.setItem(i, ItemsUtils.getLimitPane());
            }
        }
    }

    protected List<List<T>> splitList(List<T> items, int length) {
        List<List<T>> splittedList = new ArrayList<>();
        int hi;

        for (int lo = 0; lo < items.size(); lo = hi) {
            hi = lo + length;

            if (hi > items.size()) {
                hi = items.size();
            }

            splittedList.add(new ArrayList<>(items.subList(lo, hi)));
        }

        return splittedList;
    }

    public void createInventoriesFromList(String invNameTemplate, List<T> items) {
        int nbInv = calcInvAmnt(items.size());

        if (nbInv > 1) {
            List<List<T>> splittedList = splitList(items, 45);

            for (int i = 0; i < splittedList.size(); i++) {
                createInventoryWithObjectList(invNameTemplate.replace("<#>", "" + i),
                        splittedList.get(i),
                        i > 0 ? invNameTemplate.replace("<#>", "" + (i - 1)) : mainInvName,
                        i < splittedList.size() - 1 ? invNameTemplate.replace("<#>", "" + (i + 1)) : null);
            }
        } else {
            createInventoryWithObjectList(invNameTemplate == null ? mainInvName : invNameTemplate, items, null, null);
        }
    }

    public void createInventoriesFromMap(String invNameTemplate, Map<String, ItemStack> categories, Map<String, List<T>> items) {
        List<TmpPair> tmpPairs = convertMapToList(categories);

        createInventoryWithItemList(mainInvName, tmpPairs.stream()
                .map(pair -> ItemMaker.newItem(pair.getItem()).addName(pair.getName()).getItem())
                .collect(Collectors.toList()), null, null);

        for (Map.Entry<String, List<T>> entry : items.entrySet()) {
            createInventoriesFromList(entry.getKey(), entry.getValue());
        }
    }

    private List<TmpPair> convertMapToList(Map<String, ItemStack> categories) {
        List<TmpPair> tmpPairs = new ArrayList<>();

        for (Map.Entry<String, ItemStack> entry : categories.entrySet()) {
            tmpPairs.add(new TmpPair(entry.getValue(), entry.getKey()));
        }

        return tmpPairs;
    }

    private void createInventoryWithObjectList(String invName, List<T> items, String prevInv, String nextInv) {
        createInventoryWithItemList(invName, items.stream().map(this::convertObjectIntoItemStack).collect(Collectors.toList()), prevInv, nextInv);
    }

    private void createInventoryWithItemList(String invName, List<ItemStack> items, String prevInv, String nextInv) {
        int invSize = calcInvRows(items.size());

        Inventory inv = Bukkit.createInventory(null, invSize, invName);

        setupFirstRow(inv, prevInv, nextInv, mainInvShortcut ? mainInvName : null);

        for (ItemStack item : items) {
            inv.addItem(item);
        }

        invs.put(invName, inv);
    }
}
