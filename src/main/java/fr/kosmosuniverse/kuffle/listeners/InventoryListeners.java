package fr.kosmosuniverse.kuffle.listeners;

import java.util.Objects;

import fr.kosmosuniverse.kuffle.core.*;
import fr.kosmosuniverse.kuffle.datamanagers.crafts.ACraft;
import fr.kosmosuniverse.kuffle.datamanagers.crafts.CraftManager;
import fr.kosmosuniverse.kuffle.datamanagers.results.ResultManager;
import fr.kosmosuniverse.kuffle.datamanagers.targets.TargetManager;
import fr.kosmosuniverse.kuffle.mode.Mode;
import fr.kosmosuniverse.kuffle.multiblock.MultiblockManager;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.persistence.PersistentDataType;

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
		
		if (item == null) {
			return;
		}

		if (event.getView().getTitle().contains(ChatColor.BLACK + "AllCustomCrafts")) {
			event.setCancelled(true);

			openAllCrafts(current, item, player);
		} else if (PartyTmp.getInstance().getGameMode().getMode() == Mode.BLOCKS &&
			MultiblockManager.hasInv(event.getView().getTitle())) {
			event.setCancelled(true);

			multiblockInventory(player, item);
		} else if ((craft = CraftManager.getCraftByInventoryName(event.getView().getTitle())) != null) {
			event.setCancelled(true);
			
			if (Objects.requireNonNull(item.getItemMeta()).getDisplayName().equals("<- Back")) {
				player.openInventory(CraftManager.getCraftsInventory(craft));
			}
		} else if (event.getView().getTitle().equals(ChatColor.BLACK + "Players")) {
			event.setCancelled(true);
			
			playersInventory(player, item);
		} else if (ResultManager.getInstance().isResultsLoaded() &&
				ResultManager.getInstance().hasInv(event.getView().getTitle())) {
			event.setCancelled(true);

			resultInventory(player, item);
		} else if (TargetManager.hasInv(event.getView().getTitle())) {
			event.setCancelled(true);

			itemsInventory(player, item);
		}
	}
	
	private void openAllCrafts(Inventory current, ItemStack item, Player player) {
		Inventory inv = CraftManager.getInventory(current, item);
		ACraft craft;
		
		if (inv == null) {
			craft = CraftManager.getCraftByItem(item);
			
			if (craft != null) {
				inv = craft.getInv();
			}
		}
		
		if (inv != null) {
			player.openInventory(inv);
		}
	}

	private void multiblockInventory(Player player, ItemStack item) {
		if (!item.hasItemMeta()) {
			return ;
		}

		String invName = Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().get(NamespacedKey.minecraft("invname"), PersistentDataType.STRING);
		String itemName = Objects.requireNonNull(item.getItemMeta()).getDisplayName();

		if (invName != null && MultiblockManager.hasInv(invName)) {
			player.openInventory(MultiblockManager.getInv(invName));
		}  else if ("<- Quit".equals(itemName)) {
			player.closeInventory();
		}
	}

	/**
	 * Teleports a player that has finished its game to another player 
	 * 
	 * @param player	The player that click in the inventory (that will be teleported)
	 * @param item		The clicked item to determine if he clicked on another player head or not
	 */
	private void playersInventory(Player player, ItemStack item) {
		if (PartyTmp.getInstance().getSpectators().has(player.getName()) ||
				(PartyTmp.getInstance().getPlayers().has(player.getName()) &&
						PartyTmp.getInstance().getGameManager().getPlayerData(player.getName()).isFinished() &&
				item.getType() == Material.PLAYER_HEAD && item.hasItemMeta() &&
				!Objects.requireNonNull(item.getItemMeta()).getDisplayName().equals(player.getName()))) {
			player.setGameMode(GameMode.SPECTATOR);
			PartyTmp.getInstance().getGameManager().teleportPlayerToPlayer(player, Objects.requireNonNull(item.getItemMeta()).getDisplayName());
		}
	}

	private void itemsInventory(Player player, ItemStack item) {
		if (!item.hasItemMeta()) {
			return ;
		}

		String invName = Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().get(NamespacedKey.minecraft("invname"), PersistentDataType.STRING);
		String itemName = Objects.requireNonNull(item.getItemMeta()).getDisplayName();

		if (invName != null && TargetManager.hasInv(invName)) {
			player.openInventory(TargetManager.getInv(invName));
		}  else if ("<- Quit".equals(itemName)) {
			player.closeInventory();
		}
	}

	private void resultInventory(Player player, ItemStack item) {
		if (item.hasItemMeta() &&
				Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().has(NamespacedKey.minecraft("invname"), PersistentDataType.STRING)) {
			player.openInventory(ResultManager.getInstance().getInv(item.getItemMeta().getPersistentDataContainer().get(NamespacedKey.minecraft("invname"), PersistentDataType.STRING)));
		}
	}
}
