package fr.kosmosuniverse.kuffle.datamanagers.targets;

import fr.kosmosuniverse.kuffle.datamanagers.age.Age;
import fr.kosmosuniverse.kuffle.datamanagers.age.AgeManager;
import fr.kosmosuniverse.kuffle.utils.ItemMaker;
import fr.kosmosuniverse.kuffle.utils.ItemsUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author KosmosUniverse
 */
public class TargetInventories {
    private final Map<String, Inventory> targetsInvs;
    private int cnt = 0;

    public TargetInventories() {
        targetsInvs = new HashMap<>();
    }

    public void clear() {
        targetsInvs.clear();
    }

    public Inventory getMainInv() {
        return targetsInvs.get("Main Target Inv");
    }

    public Inventory getInv(String invName) {
        return targetsInvs.get(invName);
    }

    public boolean hasInv(String invName) {
        return getInv(invName) != null;
    }

    private void setupFirstRow(Inventory inv, String prevInv, String nextInv, boolean mainMenuShortcut) {
        for (int i = 0; i < 9; i++) {
            if (i == 0) {
                inv.setItem(i, prevInv != null ? ItemMaker.newItem(ItemsUtils.getBackPane()).addTag("invname", prevInv).getItem() : ItemsUtils.getQuitPane());
            } else if (i == 8) {
                inv.setItem(i, nextInv != null ? ItemMaker.newItem(ItemsUtils.getNextPane()).addTag("invname", nextInv).getItem() : ItemsUtils.getLimitPane());
            } else if (i == 4 && mainMenuShortcut) {
                inv.setItem(i, ItemMaker.newItem(Material.MAGENTA_STAINED_GLASS_PANE).addName("Go to Main Menu").addTag("invname", "Main Target Inv").getItem());
            } else {
                inv.setItem(i, ItemsUtils.getLimitPane());
            }
        }
    }

    private void setupFirstRow(Inventory inv, String prevInv, String nextInv) {
        setupFirstRow(inv, prevInv, nextInv, true);
    }

    public void setupInventories(Map<String, List<String>> targets) {
        createMainInv(targets.keySet());
        targets.forEach(this::createAgeTargetInvs);
    }

    private void createMainInv(Set<String> ages) {
        Inventory mainInv = Bukkit.createInventory(null, 18, "Main Target Inv");

        setupFirstRow(mainInv, null, null, false);

        AgeManager.getAges()
                .stream()
                .filter(age -> ages.contains(age.getName()))
                .sorted(Comparator.comparingInt(Age::getNumber))
                .forEach(age -> mainInv.addItem(ItemMaker.newItem(age.getBox())
                        .addName(age.getColor() + age.getName().replace("_", " "))
                        .addTag("invname", age.getName() + " Targets 1")
                        .getItem()));

        targetsInvs.put("Main Target Inv", mainInv);
    }

    private void createAgeTargetInvs(String age, List<String> ageTargets) {
        cnt = 0;
        List<String> tmpAgeTargets = new ArrayList<>(ageTargets);
        enrichAgeTarget(tmpAgeTargets);
        tmpAgeTargets.sort(Comparator.naturalOrder());

        createAgeTargetInv("Main Target Inv", age, 1, tmpAgeTargets);
    }

    private static void enrichAgeTarget(List<String> ageTargets) {
        List<String> any = new ArrayList<>();
        List<String> toRemove = new ArrayList<>();

        ageTargets.stream().filter(target -> target.startsWith("*")).forEach(target -> {
            String cleanTarget = target.replace("*", "").toUpperCase();

            toRemove.add(target);
            Arrays.stream(Material.values()).filter(material -> material.toString().contains(cleanTarget)).forEach(material -> any.add(material.toString()));
        });

        if (!any.isEmpty()) {
            ageTargets.addAll(any);
        }

        if (!toRemove.isEmpty()) {
            ageTargets.removeAll(toRemove);
        }
    }

    private void createAgeTargetInv(String prevInv, String age, int invNb, List<String> ageTargets) {
        int rest = ageTargets.size() - cnt;
        int nbInvRest = (rest / 45) + (rest % 45 != 0 ? 1 : 0);
        int invSize = nbInvRest > 1 ? 54 : ((rest / 9) + (rest % 9 != 0 ? 1 : 0) + 1) * 9;
        int invCnt = 0;

        Inventory ageInv = Bukkit.createInventory(null, invSize, age + " Targets " + invNb);

        setupFirstRow(ageInv, prevInv, nbInvRest > 1 ? age + " Targets " + (invNb + 1) : null);

        while (cnt < ageTargets.size() && invCnt < (invSize - 9)) {
            try {
                ageInv.addItem(getMaterial(ageTargets.get(cnt)));
            } catch (Exception e) {
                ageInv.addItem(ItemMaker.newItem(Material.BARRIER).addName(ageTargets.get(cnt)).getItem());
            }

            cnt += 1;
            invCnt += 1;
        }

        targetsInvs.put(age + " Targets " + invNb, ageInv);

        if (nbInvRest > 1) {
            createAgeTargetInv(age + " Targets " + invNb, age, invNb + 1, ageTargets);
        }
    }

    private static ItemStack getMaterial(String target) {
        for (Material mat : Material.values()) {
            if (mat.toString().equals(target.toUpperCase())) {
                return new ItemStack(mat);
            } else if (mat.toString().contains(target.toUpperCase()) &&
                    target.toUpperCase().contains(mat.toString())) {
                return ItemMaker.newItem(mat).addName(target).getItem();
            }
        }

        return ItemMaker.newItem(Material.GRAY_STAINED_GLASS_PANE).addName(target).getItem();
    }
}
