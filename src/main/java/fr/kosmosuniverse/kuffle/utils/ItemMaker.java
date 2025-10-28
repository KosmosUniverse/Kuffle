package fr.kosmosuniverse.kuffle.utils;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author KosmosUniverse
 */
public class ItemMaker {
    @Getter
    final ItemStack item;

    public ItemMaker(Material material) {
        this.item = new ItemStack(material);
    }

    public ItemMaker(ItemStack item) {
        this.item = item.clone();
    }

    public static ItemMaker newItem(Material material) {
        return new ItemMaker(material);
    }
    public static ItemMaker newItem(ItemStack item) {
        return new ItemMaker(item);
    }

    public ItemMaker addQuantity(int quantity) {
        item.setAmount(quantity);

        return this;
    }

    public ItemMaker addName(String name) {
        if (name == null) {
            return this;
        }

        ItemMeta itM = item.getItemMeta();

        assert itM != null;
        itM.setDisplayName(name);
        item.setItemMeta(itM);

        return this;
    }

    public ItemMaker setLores(List<String> lores) {
        ItemMeta itM = item.getItemMeta();

        assert itM != null;
        itM.setLore(new ArrayList<>());
        item.setItemMeta(itM);
        return addLores(lores);
    }

    public ItemMaker addLores(String... lores) {
        if (lores == null) {
            return this;
        }

        ItemMeta itM = item.getItemMeta();

        assert itM != null;
        if (itM.getLore() == null) {
            itM.setLore(Arrays.asList(lores));
        } else {
            List<String> l = itM.getLore();
            l.addAll(Arrays.asList(lores));

            itM.setLore(l);
        }

        item.setItemMeta(itM);

        return this;
    }

    public ItemMaker addLore(String lore) {
        if (lore == null) {
            return this;
        }

        ItemMeta itM = item.getItemMeta();

        assert itM != null;
        if (itM.getLore() == null) {
            itM.setLore(Collections.singletonList(lore));
        } else {
            List<String> l = itM.getLore();
            l.add(lore);

            itM.setLore(l);
        }

        item.setItemMeta(itM);

        return this;
    }

    public ItemMaker addLores(List<String> lores) {
        if (lores == null) {
            return this;
        }

        ItemMeta itM = item.getItemMeta();

        assert itM != null;
        if (itM.getLore() == null) {
            itM.setLore(lores);
        } else {
            List<String> l = itM.getLore();
            l.addAll(lores);

            itM.setLore(l);
        }

        item.setItemMeta(itM);

        return this;
    }

    public ItemMaker addEnchants(List<ItemEnchant> enchants) {
        if (enchants == null) {
            return this;
        }

        enchants.forEach(e -> item.addUnsafeEnchantment(e.getEnchant(), e.getLevel()));

        return this;
    }

    public ItemMaker addTag(String tag, String value) {
        if (tag == null) {
            return this;
        }

        ItemMeta itM = item.getItemMeta();

        if (itM == null) {
            return this;
        }

        itM.getPersistentDataContainer().set(NamespacedKey.minecraft(tag), PersistentDataType.STRING, value);

        item.setItemMeta(itM);

        return this;
    }
}
