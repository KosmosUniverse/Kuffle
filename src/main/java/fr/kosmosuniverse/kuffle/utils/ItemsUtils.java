package fr.kosmosuniverse.kuffle.utils;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
public class ItemsUtils {
    @Getter
    private static final ItemStack emptyPane = ItemMaker.newItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE).addName(" ").getItem();
    @Getter
    private static final ItemStack limitPane = ItemMaker.newItem(Material.LIME_STAINED_GLASS_PANE).addName(" ").getItem();
    @Getter
    private static final ItemStack backPane = ItemMaker.newItem(Material.RED_STAINED_GLASS_PANE).addName("<- Back").getItem();
    @Getter
    private static final ItemStack previousPane = ItemMaker.newItem(Material.RED_STAINED_GLASS_PANE).addName("<- Previous").getItem();
    @Getter
    private static final ItemStack quitPane = ItemMaker.newItem(Material.RED_STAINED_GLASS_PANE).addName("<- Quit").getItem();
    @Getter
    private static final ItemStack nextPane = ItemMaker.newItem(Material.BLUE_STAINED_GLASS_PANE).addName("Next ->").getItem();

    /**
     * Compares two items by Material, itemMeta, DisplayName and Lore
     *
     * @param first				The first item
     * @param second			The second item
     *
     * @return True if both items are the same, False instead
     */
    public static boolean itemComparison(ItemStack first, ItemStack second) {
        boolean retValue;

        retValue = !(first == null || second == null);

        if (retValue) {
            retValue = first.getType() == second.getType();
        }

        if (retValue) {
            retValue = first.hasItemMeta() == second.hasItemMeta();
        }

        if (retValue && first.hasItemMeta()) {
            retValue = Objects.requireNonNull(first.getItemMeta()).hasDisplayName() == Objects.requireNonNull(second.getItemMeta()).hasDisplayName();

            if (retValue && first.getItemMeta().hasDisplayName()) {
                retValue = first.getItemMeta().getDisplayName().equalsIgnoreCase(second.getItemMeta().getDisplayName());
            }
        }

        if (retValue && first.hasItemMeta()) {
            retValue = Objects.requireNonNull(first.getItemMeta()).hasLore() == Objects.requireNonNull(second.getItemMeta()).hasLore();

            if (retValue && first.getItemMeta().hasLore()) {
                retValue = compareLoreElements(first, second);
            }
        }

        return retValue;
    }

    /**
     * Compares two items' lore
     *
     * @param first		The first item
     * @param second	The second item
     *
     * @return True if lore are same, False instead
     */
    private static boolean compareLoreElements(ItemStack first, ItemStack second) {
        List<String> firstLore = Objects.requireNonNull(first.getItemMeta()).getLore();
        List<String> secondLore = Objects.requireNonNull(second.getItemMeta()).getLore();

        if (Objects.requireNonNull(firstLore).size() != Objects.requireNonNull(secondLore).size()) {
            return false;
        }

        for (int i = 0; i < firstLore.size(); i++) {
            if (firstLore.get(i).equals(secondLore.get(i)) && firstLore.get(i).isEmpty()) {
                break;
            }

            if (!firstLore.get(i).equals(secondLore.get(i))) {
                return false;
            }
        }

        return true;
    }
}
