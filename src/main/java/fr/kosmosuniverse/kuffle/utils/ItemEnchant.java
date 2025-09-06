package fr.kosmosuniverse.kuffle.utils;

import lombok.Getter;
import org.bukkit.enchantments.Enchantment;

/**
 * @author KosmosUniverse
 */

@Getter
public class ItemEnchant {
    private final Enchantment enchant;
    private final int level;

    public ItemEnchant(Enchantment enchant, int level) {
        this.enchant = enchant;
        this.level = level;
    }
}