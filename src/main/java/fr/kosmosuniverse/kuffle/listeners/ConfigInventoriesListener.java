package fr.kosmosuniverse.kuffle.listeners;

import fr.kosmosuniverse.kuffle.core.*;
import fr.kosmosuniverse.kuffle.crafts.ACraft;
import fr.kosmosuniverse.kuffle.multiblock.AMultiblock;
import fr.kosmosuniverse.kuffle.multiblock.MultiblockManager;
import fr.kosmosuniverse.kuffle.type.KuffleType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class ConfigInventoriesListener implements Listener {
	
	/**
	 * Triggered on item clicked in an inventory
	 * 
	 * @param event	The InventoryClickEvent
	 */
	@EventHandler
	public void onConfigItemClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		
		if (item == null) {
			return;
		}

		event.setCancelled(false);
		
		if (Config.hasInv(event.getView().getTitle())) {
			event.setCancelled(true);
			configInventory(player, event.getInventory(), item);
		}
	}

	private void configInventory(Player player, Inventory inv, ItemStack item) {
		if (!item.hasItemMeta()) {
			return ;
		}

		String invName = Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().get(NamespacedKey.minecraft("invname"), PersistentDataType.STRING);
		String trigger = Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().get(NamespacedKey.minecraft("trigger"), PersistentDataType.STRING);
		String reload = Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().get(NamespacedKey.minecraft("reload"), PersistentDataType.STRING);

		if (invName != null && Config.hasInv(invName)) {
			player.openInventory(Config.getInv(invName));
		} else if (trigger != null) {
			Config.invTrigger(player, inv, item, trigger);
		} else if (reload != null) {
			Config.reloadInv(reload);
			player.openInventory(Config.getInv(reload));
		}
	}
}
