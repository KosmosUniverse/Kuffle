package main.fr.kosmosuniverse.kuffle.listeners;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.CraftManager;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.TeamManager;
import main.fr.kosmosuniverse.kuffle.core.VersionManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleEventNotUsableException;
import main.fr.kosmosuniverse.kuffle.utils.ItemUtils;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class PlayerInteract implements Listener  {
	private static final String CORAL_COMPASS = "CoralCompass";
	private static final String COMPASS_CLASS = "org.bukkit.inventory.meta.CompassMeta";
	
	protected Map<Location, String> shulkers = new HashMap<>();
	
	/**
	 * Delete the player's item from its hand
	 * 
	 * @param player	The player
	 * @param hand		The hand that hold the item to delete
	 */
	protected void consumeItem(Player player, EquipmentSlot hand) {
		if (hand == EquipmentSlot.HAND) {
			player.getInventory().setItemInMainHand(null);	
		} else if (hand == EquipmentSlot.OFF_HAND) {
			player.getInventory().setItemInOffHand(null);
		}
	}
		
	/**
	 * Manages the common behavior of player left click
	 * 
	 * @param event	The PlayerInteractEvent
	 * 
	 * @return True if it was managed by the method, False instead
	 * 
	 * @throws KuffleEventNotUsableException if event is not usable by Kuffle plugin
	 * @throws ClassNotFoundException 	 	NMS Exception
	 * @throws SecurityException 		 	NMS Exception
	 * @throws NoSuchMethodException  		NMS Exception
	 * @throws InvocationTargetException  	NMS Exception
	 * @throws IllegalArgumentException  	NMS Exception
	 * @throws IllegalAccessException 	 	NMS Exception
	 */
	protected boolean onRightClickGeneric(PlayerInteractEvent event) throws KuffleEventNotUsableException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		boolean ret = true;
		
		if (!KuffleMain.getInstance().isStarted()) {
			throw new KuffleEventNotUsableException("Not a good event type to use here.");
		}
		
		Player player = event.getPlayer();
		
		if (!GameManager.hasPlayer(player.getName())) {
			ret = false;
		}
		
		Action action = event.getAction();
		
		if (ret && action != Action.RIGHT_CLICK_AIR) {
			ret = false;
		}
		
		if (ret && (!event.hasItem() || event.getItem() == null)) {
			ret = false;
		}
		
		if (!ret) {
			throw new KuffleEventNotUsableException("Not a good event type to use here.");
		}
		
		ret = false;
		
		ItemStack item = event.getItem();
		
		if (VersionManager.isVersionValid("1.17", null) && ItemUtils.itemComparison(item, CraftManager.findItemByName(CORAL_COMPASS))) {
			if (!Boolean.valueOf((Class.forName(COMPASS_CLASS).getClass().getMethod("hasLodestone").invoke(Class.forName(COMPASS_CLASS).cast(item.getItemMeta())).toString()))) {
				coralCompass(player, item);
				
			} else {
				LogManager.getInstanceGame().writeMsg(player, LangManager.getMsgLang("COMPASS_PAIRED", GameManager.getPlayerLang(player.getName())));
			}
			
			ret = true;
		}
		
		return ret;
	}
	
	/**
	 * Manages the behavior when player drink milk to reload its game effects
	 * 
	 * @param event	The PlayerItemConsumeEvent
	 */
	@EventHandler
	public void onDrinkMilk(PlayerItemConsumeEvent event) {
		if (!KuffleMain.getInstance().isStarted()) {
			return ;
		}
		
		Player player = event.getPlayer();
		
		if (GameManager.hasPlayer(player.getName()) &&
				event.getItem().getType() == Material.MILK_BUCKET) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () ->
				GameManager.reloadPlayerEffects(player.getName())
			, 20);
		}
	}
	
	/**
	 * Manages the common behavior of player shulker placing
	 * 
	 * @param event	The BlockPlaceEvent
	 */
	@EventHandler
	public void onPlaceShulkerGeneric(BlockPlaceEvent event) {
		if (!KuffleMain.getInstance().isStarted() || (!Config.getPassiveAll() && !Config.getPassiveTeam())) {
			return ;
		}
		
		Player player = event.getPlayer();
		Block block = event.getBlock();
		Location location = block.getLocation();

		if (!GameManager.hasPlayer(player.getName())) {
			return ;
		}
		
		if (block.getType().name().toLowerCase().contains("shulker_box")) {
			shulkers.put(location, player.getName());
		}
	}

	/**
	 * Manages the common behavior of player shulker opening
	 * 
	 * @param event	The PlayerInteractEvent
	 */
	@EventHandler
	public void onInteractShulkerGeneric(PlayerInteractEvent event) {
		if (!KuffleMain.getInstance().isStarted() ||
				(!Config.getPassiveAll() && !Config.getPassiveTeam())) {
			return ;
		}
		
		Player player = event.getPlayer();
		Action action = event.getAction();
		Block block = event.getClickedBlock();
		
		if (action != Action.RIGHT_CLICK_BLOCK || block == null || !block.getType().toString().toLowerCase().contains("shulker_box")) {
			return ;
		}
		
		String placerName = shulkers.get(block.getLocation());
		
		if (placerName != null && !placerName.equals(player.getName()) &&
				(Config.getPassiveAll() && !GameManager.hasSpectator(player) ||
						(Config.getTeam() && Config.getPassiveTeam() &&
								!TeamManager.getInstance().sameTeam(placerName, player.getName())))) {
			event.setCancelled(true);
		}
	}
	
	/**
	 * Manages the common behavior of player shulker breaking
	 * 
	 * @param event	The BlockBreakEvent
	 */
	@EventHandler
	public void onBreakShulkerGeneric(BlockBreakEvent event) {
		if (!KuffleMain.getInstance().isStarted() ||
				(!Config.getPassiveAll() && !Config.getPassiveTeam())) {
			return ;
		}
		
		Player player = event.getPlayer();
		Block block = event.getBlock();
		Location location = block.getLocation();
		
		if (!shulkers.containsKey(location)) {
			return;
		}
		
		String placerName = shulkers.get(location);
		
		if (placerName != null && !placerName.equals(player.getName()) && (Config.getPassiveAll() ||
				(Config.getTeam() && Config.getPassiveTeam() &&	!TeamManager.getInstance().sameTeam(placerName, player.getName())))) {
			event.setCancelled(true);
		} else {
			shulkers.remove(location);	
		}
	}
	
	/**
	 * Manages the common behavior of Kuffle sign breaking
	 * 
	 * @param event	The BlockBreakEvent
	 */
	@EventHandler
	public void onBreakSignGeneric(BlockBreakEvent event) {
		if (!KuffleMain.getInstance().isStarted()) {
			return ;
		}
		
		Block block = event.getBlock();
		
		if (block.getType() == Material.OAK_SIGN && Utils.checkSign(block.getLocation())) {
			event.setCancelled(true);
		}
	}
	
	/**
	 * Manages the common behavior of player crafting
	 * 
	 * @param event	The CraftItemEvent
	 */
	@EventHandler
	public void onCraftGeneric(CraftItemEvent event) {
		if (!KuffleMain.getInstance().isStarted()) {
			return ;
		}
		
		ItemStack item = event.getInventory().getResult();
		Player player = (Player) event.getWhoClicked();

		if (ItemUtils.itemComparison(item, CraftManager.findItemByName(CORAL_COMPASS))) {
			int xpAmount = KuffleMain.getInstance().getType().getXpActivable(CORAL_COMPASS);
			
			if (player.getLevel() < xpAmount) {
				event.setCancelled(true);
				player.sendMessage("You need " + xpAmount + " xp levels to craft this item.");
			} else {
				player.setLevel(player.getLevel() - xpAmount);
				xpAmount = (xpAmount - 5) < 5 ? 5 : (xpAmount - 5);
				KuffleMain.getInstance().getType().setXpActivable(CORAL_COMPASS, xpAmount);
				
				LogManager.getInstanceGame().logMsg(player.getName(), "Crafted CoralCompass.");
			}
		}
	}
	
	/**
	 * Manages the common behavior of player hitting another player
	 * 
	 * @param event	The EntityDamageByEntityEvent
	 */
	@EventHandler
	public void onPlayerHitPlayerGeneric(EntityDamageByEntityEvent event) {
		if (!KuffleMain.getInstance().isStarted() && !Config.getPassiveAll()) {
			return ;
		}
		
		Entity tmpDamager = event.getDamager();	
		Entity tmpDamagee = event.getEntity();
		
		if (!(tmpDamager instanceof Player) || !(tmpDamagee instanceof Player)) {
			return;
		}
		
		Player damager = (Player) tmpDamager;
		Player damagee = (Player) tmpDamagee;
		
		if (!GameManager.hasPlayer(damager.getName()) || !GameManager.hasPlayer(damagee.getName())) {
			return ;
		}
		
		if (Config.getPassiveAll() || (Config.getPassiveTeam() && Config.getTeam() &&
						!TeamManager.getInstance().sameTeam(damager.getName(), damagee.getName()))) {
			event.setCancelled(true);
		}
	}
	
	/**
	 * Manages the common behavior of firework throwing
	 * 
	 * @param event	The PlayerInteractEvent
	 */
	@EventHandler
	public void onFireWorkThrowGeneric(PlayerInteractEvent event) {
		if (!KuffleMain.getInstance().isStarted()) {
			return ;
		}
		
		ItemStack item;
		Action action = event.getAction();
		Player player = event.getPlayer();

		if (!GameManager.hasPlayer(player.getName())) {
			return ;
		}
		
		if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
			return ;
		}
		
		if (event.getItem() != null && event.getItem().getType() == Material.FIREWORK_ROCKET) {
			item = event.getItem();
			
			if (item.getAmount() == 1) {
				item.setAmount(64);
				player.getInventory().setItemInMainHand(item);
			}
		} else if (player.getInventory().getItemInOffHand() != null && player.getInventory().getItemInOffHand().getType() == Material.FIREWORK_ROCKET) {
			item = player.getInventory().getItemInOffHand();
			
			if (item.getAmount() == 1) {
				item.setAmount(64);
				player.getInventory().setItemInOffHand(item);
			}
		}
	}
	
	/**
	 * Manages coral compass search
	 * 
	 * @param player	The player that searching warm ocean
	 * @param compass	The compass item
	 * 
	 * @throws ClassNotFoundException  		NMS Exception
	 * @throws SecurityException  			NMS Exception
	 * @throws NoSuchMethodException  		NMS Exception
	 * @throws InvocationTargetException  	NMS Exception
	 * @throws IllegalArgumentException  	NMS Exception
	 * @throws IllegalAccessException  		NMS Exception
	 */
	private void coralCompass(Player player, ItemStack compass) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		Location tmp = player.getLocation();
		
		if (findCoralBiome(tmp, compass)) {
			LogManager.getInstanceGame().writeMsg(player, LangManager.getMsgLang("WARM_FOUND", GameManager.getPlayerLang(player.getName())));
		} else {
			LogManager.getInstanceGame().writeMsg(player, LangManager.getMsgLang("WARM_NOT_FOUND", GameManager.getPlayerLang(player.getName())));
		}
	}
	
	/**
	 * Search for a warm ocean in a 10 chunk square centered in loc
	 * 
	 * @param loc		Location of the square center
	 * @param compass	The compass item
	 * 
	 * @return True if warm ocean is found, False instead
	 * 
	 * @throws ClassNotFoundException  		NMS Exception
	 * @throws SecurityException  			NMS Exception
	 * @throws NoSuchMethodException  		NMS Exception
	 * @throws InvocationTargetException  	NMS Exception
	 * @throws IllegalArgumentException 	NMS Exception
	 * @throws IllegalAccessException		NMS Exception
	 */
	private boolean findCoralBiome(Location loc, ItemStack compass) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		Chunk baseChunk = loc.getChunk();

		for (int radius = 0; radius <= 10; radius++) {
			Location found = searchWarmRadius(baseChunk, radius);
			
			if (found != null) {
				Class.forName(COMPASS_CLASS).getMethod("setLodestone", Boolean.class).invoke(Class.forName(COMPASS_CLASS).cast(compass.getItemMeta()), found);
				ItemMeta itM = compass.getItemMeta();
				itM.getLore().add("Location : " + found.getBlockX() + ", " + found.getBlockY() + ", " + found.getBlockZ());
				compass.setItemMeta(itM);
				
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Search for a warm ocean in radius centered on baseChunk
	 * 
	 * @param baseChunk	The center chunk for the research
	 * @param radius	The radius around baseChunk to search for
	 * 
	 * @return True if Warm ocean found, False instead
	 */
	private Location searchWarmRadius(Chunk baseChunk, int radius) {
		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				if (Math.abs(x) == radius || Math.abs(z) == radius) {
					Chunk currentChunk = baseChunk.getWorld().getChunkAt(baseChunk.getX() + x, baseChunk.getZ() + z);
					Location blockLoc = currentChunk.getBlock(8, 0, 8).getLocation();
					Biome biome = baseChunk.getWorld().getBiome(blockLoc.getBlockX(), blockLoc.getBlockY(), blockLoc.getBlockZ());
					
					if (biome == Biome.WARM_OCEAN || biome == Biome.DEEP_WARM_OCEAN) {
						blockLoc.getBlock().setType(Material.matchMaterial("LODESTONE"));
						return blockLoc;
					}
				}
			}
		}
		
		return null;
	}
}
