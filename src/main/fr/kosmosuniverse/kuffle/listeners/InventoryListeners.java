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
import main.fr.kosmosuniverse.kuffle.core.Game;
import main.fr.kosmosuniverse.kuffle.crafts.ACrafts;

public class InventoryListeners implements Listener {
	@EventHandler
	public void onItemClick(InventoryClickEvent event) {	
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		ACrafts craft;
		Inventory inv;
		
		if (item == null) {
			return;
		}
		
		if (event.getView().getTitle() == "§8AllCustomCrafts") {
			event.setCancelled(true);
			
			if ((craft = KuffleMain.crafts.findCraftInventoryByItem(item)) != null &&
					(inv = craft.getInventoryRecipe()) != null) {
				player.openInventory(inv);
			}
		} else if (KuffleMain.crafts.findCraftByInventoryName(event.getView().getTitle()) != null) {
			event.setCancelled(true);
			
			if (item.getItemMeta().getDisplayName().equals("<- Back")) {
				player.openInventory(KuffleMain.crafts.getAllCraftsInventory());
			}
		} else if (event.getView().getTitle() == "§8Players") {
			event.setCancelled(true);
			
			playersInventory(player, item);
		} else if (event.getView().getTitle().contains(" Items ")) {
			itemsInventory(event);
		}
	}
	
	private void playersInventory(Player player, ItemStack item) {
		if (item.getType() == Material.PLAYER_HEAD &&
				!item.getItemMeta().getDisplayName().equals(player.getName())) {
			Game tmpGame = KuffleMain.games.get(item.getItemMeta().getDisplayName());
			
			if (tmpGame != null && KuffleMain.games.get(player.getName()).getFinished()) {
				if (player.getGameMode() != GameMode.SPECTATOR) {
					player.setGameMode(GameMode.SPECTATOR);
				}
				player.teleport(tmpGame.getPlayer());	
			}
		}
	}
	
	private void itemsInventory(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		
		for (String age : KuffleMain.itemsInvs.keySet()) {
			if (event.getView().getTitle().contains(age)) {
				event.setCancelled(true);

				List<Inventory> tmpInvs = KuffleMain.itemsInvs.get(age);
				
				if (tmpInvs.size() > 1) {
					int invIdx = tmpInvs.indexOf(event.getClickedInventory());
					
					if (item.getItemMeta().getDisplayName().equals("<- Previous")) {
						player.openInventory(tmpInvs.get(invIdx - 1));
					} else if (item.getItemMeta().getDisplayName().equals("Next ->")) {
						player.openInventory(tmpInvs.get(invIdx + 1));
					}
				}
			}
		}
	}
}
