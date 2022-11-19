package main.fr.kosmosuniverse.kuffle.listeners;

import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.AgeManager;
import main.fr.kosmosuniverse.kuffle.core.CraftManager;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.TargetManager;
import main.fr.kosmosuniverse.kuffle.crafts.ACraft;
import main.fr.kosmosuniverse.kuffle.multiblock.AMultiblock;
import main.fr.kosmosuniverse.kuffle.multiblock.MultiblockManager;
import main.fr.kosmosuniverse.kuffle.type.KuffleType;
import net.md_5.bungee.api.ChatColor;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class InventoryListeners implements Listener {
	
	/**
	 * Triggered on item clicked in an inventory
	 * 
	 * @param event	The InventoryClickEvent
	 */
	@EventHandler
	public void onItemClick(InventoryClickEvent event) {	
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		Inventory current = event.getClickedInventory();
		ACraft craft;
		AMultiblock multiblock;
		Inventory inv;
		
		if (item == null) {
			return;
		}
		
		if (event.getView().getTitle().contains(ChatColor.BLACK + "AllCustomCrafts")) {
			event.setCancelled(true);

			if ((inv = CraftManager.getInventory(current, item)) != null) {
				player.openInventory(inv);
			} else if ((craft = CraftManager.getCraftByItem(item)) != null &&
					(inv = craft.getInventory()) != null) {
				player.openInventory(inv);
			}
		} else if (event.getView().getTitle().equals(ChatColor.BLACK + "AllMultiBlocks")) {
			event.setCancelled(true);
			
			if ((multiblock = MultiblockManager.searchMultiBlockByItem(item)) != null) {
				if ((inv = multiblock.getInventory(current, item, MultiblockManager.getMultiblocksInventories(), true)) != null) {
					player.openInventory(inv);
				}
			}
		} else if ((craft = CraftManager.getCraftByInventoryName(event.getView().getTitle())) != null) {
			event.setCancelled(true);
			
			if (item.getItemMeta().getDisplayName().equals("<- Back")) {
				player.openInventory(CraftManager.getCraftsInventory(craft));
			}
		} else if (KuffleMain.type.getType() == KuffleType.Type.BLOCKS && (multiblock = MultiblockManager.searchMultiBlockByInventoryName(event.getView().getTitle())) != null) {
			event.setCancelled(true);
			
			if ((inv = multiblock.getInventory(current, item, MultiblockManager.getMultiblocksInventories(), false)) != null) {
				player.openInventory(inv);
			}
		} else if (event.getView().getTitle().equals(ChatColor.BLACK + "Players")) {
			event.setCancelled(true);
			
			playersInventory(player, item);
		} else if (event.getView().getTitle().contains(" Targets ")) {
			itemsInventory(event);
		}
	}
	
	/**
	 * Teleports a player that has finished its game to another player 
	 * 
	 * @param player	The player that click in the inventory (that will be teleported)
	 * @param item		The clicked item to determine if he clicked on another player head or not
	 */
	private void playersInventory(Player player, ItemStack item) {
		if (GameManager.hasPlayerFinished(player.getName()) && 
				item.getType() == Material.PLAYER_HEAD && item.hasItemMeta() &&
				!item.getItemMeta().getDisplayName().equals(player.getName())) {
			player.setGameMode(GameMode.SPECTATOR);
			GameManager.teleportPlayerToPlayer(player, item.getItemMeta().getDisplayName());
		}
	}
	
	/**
	 * If player clicked on previous or next in an inventory it will opened the appropriate inv
	 * 
	 * @param event	The InventoryCLickEvent
	 */
	private void itemsInventory(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		
		String age = getInvAgeName(event.getView().getTitle());
		
		if (age != null && event.getView().getTitle().contains(age)) {
			event.setCancelled(true);
			
			if (item.getItemMeta().getDisplayName().equals("<- Previous")) {
				player.openInventory(TargetManager.getAgeInv(age, event.getClickedInventory(), -1));
			} else if (item.getItemMeta().getDisplayName().equals("Next ->")) {
				player.openInventory(TargetManager.getAgeInv(age, event.getClickedInventory(), 1));
			}
		}
	}
	
	/**
	 * Search for the Age name that is linked to the current opened inventory
	 * 
	 * @param invName	The current inventory name
	 * 
	 * @return the actual age name, null if not found
	 */
	private String getInvAgeName(String invName) {
		String name = null;
		List<String> ageNames = AgeManager.getAgesNameList();
		
		for (String age : ageNames) {
			if (invName.contains(age)) {
				name = age;
				break;
			}
		}
		
		return name;
	}
}
