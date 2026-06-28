package fr.kosmosuniverse.kuffle.listeners;

import java.util.List;
import java.util.Objects;

import fr.kosmosuniverse.kuffle.core.PartyTmp;
import fr.kosmosuniverse.kuffle.states.States;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class ItemEvent implements Listener {
	private static final String BOX = "shulker_box";
	
	/**
	 * Event triggered at item dropped by player, if this item is shulker_box it is invulnerable
	 * 
	 * @param event	The PlayerDropItemEvent
	 */
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		if (PartyTmp.getInstance().getGameState().getState() != States.RUNNING) {
			return ;
		}
		
		Item item = event.getItemDrop();
		Player player = event.getPlayer();
		
		if (!PartyTmp.getInstance().getPlayers().has(player.getName())) {
			return ;
		}
		
		ItemStack itemstack = item.getItemStack();
		
		if (!itemstack.getType().name().toLowerCase().contains(BOX)) {
			return ;
		}
		
		item.setInvulnerable(true);
	}
	
	/**
	 * Event triggered at item drop by block break, if this item is shulker_box it is invulnerable
	 * 
	 * @param event	The BlockDropItemEvent
	 */
	@EventHandler
	public void onBlockBreak(BlockDropItemEvent event) {
		if (PartyTmp.getInstance().getGameState().getState() != States.RUNNING) {
			return ;
		}
		
		List<Item> items = event.getItems();

		if (items.size() != 1) {
			return;
		}
		
		Item item = items.get(0);
		ItemStack itemStack = item.getItemStack();
		
		if (itemStack.getType().name().toLowerCase().contains(BOX)) {
			item.setInvulnerable(true);
		}
	}
	
	/**
	 * Event triggered when item is about to despawn, cancel if item is shulker_box
	 * 
	 * @param event	The ItemDespawnEvent
	 */
	@EventHandler
	public void onItemDespawn(ItemDespawnEvent event) {
		if (PartyTmp.getInstance().getGameState().getState() != States.RUNNING) {
			return ;
		}
		
		Item item = event.getEntity();
		Player player = null;
		
		if (item.getItemStack().hasItemMeta() && Objects.requireNonNull(item.getItemStack().getItemMeta()).hasLore()) {
			ItemMeta itM = item.getItemStack().getItemMeta();
			String lore = Objects.requireNonNull(itM.getLore()).get(0);
			
			if (lore.contains(":")) {
				player = Bukkit.getPlayer(lore.split(":")[1]);
			}
		}
		
		if (player == null || !PartyTmp.getInstance().getPlayers().has(player.getName())) {
			return ;
		}
		
		ItemStack itemStack = item.getItemStack();
		
		if (itemStack.getType().name().toLowerCase().contains(BOX)) {
			event.setCancelled(true);
		}
	}
}
