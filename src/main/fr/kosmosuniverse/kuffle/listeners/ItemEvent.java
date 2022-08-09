package main.fr.kosmosuniverse.kuffle.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import main.fr.kosmosuniverse.kuffle.KuffleMain;

public class ItemEvent implements Listener {
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		if (!KuffleMain.gameStarted) {
			return ;
		}
		
		Item item = event.getItemDrop();
		Player player = event.getPlayer();
		
		if (!KuffleMain.games.containsKey(player.getName())) {
			return ;
		}
		
		ItemStack itemstack = item.getItemStack();
		
		if (!itemstack.getType().name().toLowerCase().contains("shulker_box")) {
			return ;
		}
		
		item.setOwner(player.getUniqueId());
		item.setInvulnerable(true);
	}
	
	@EventHandler
	public void onBlockBreak(BlockDropItemEvent event) {
		if (!KuffleMain.gameStarted) {
			return ;
		}
		
		Player player = event.getPlayer();
		List<Item> items = event.getItems();

		if (items.size() != 1) {
			return;
		}
		
		Item item = items.get(0);
		ItemStack itemStack = item.getItemStack();
		
		if (itemStack.getType().name().toLowerCase().contains("shulker_box")) {
			item.setOwner(player.getUniqueId());
			item.setInvulnerable(true);
		}
	}
	
	@EventHandler
	public void onItemDespawn(ItemDespawnEvent event) {
		if (!KuffleMain.gameStarted) {
			return ;
		}
		
		Item item = event.getEntity();
		
		Player player = Bukkit.getPlayer(item.getOwner());
		
		if (player == null || !KuffleMain.games.containsKey(player.getName())) {
			return ;
		}
		
		ItemStack itemStack = item.getItemStack();
		
		if (itemStack.getType().name().toLowerCase().contains("shulker_box")) {
			event.setCancelled(true);
		}
	}
}
