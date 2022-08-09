package main.fr.kosmosuniverse.kuffle.listeners;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Game;
import main.fr.kosmosuniverse.kuffle.crafts.ACrafts;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

public class PlayerEvents implements Listener {
	private File dataFolder;
	private List<Material> exceptions;
	
	public PlayerEvents(File folder) {
		dataFolder = folder;

		exceptions = new ArrayList<>();
		
		for (Material m : Material.values()) {
			if (m.name().contains("SHULKER_BOX")) {
				exceptions.add(m);
			}
		}
		
		exceptions.add(Material.CRAFTING_TABLE);
		exceptions.add(Material.FURNACE);
		exceptions.add(Material.STONECUTTER);
	}
	
	@EventHandler
	public void onPlayerConnectEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Game tmpGame;

		for (ACrafts item : KuffleMain.crafts.getRecipeList()) {
			player.discoverRecipe(new NamespacedKey(KuffleMain.current, item.getName()));
		}
	
		if (!KuffleMain.gameStarted) {
			return;
		}
		
		if (!Utils.fileExists(dataFolder.getPath(), player.getName() + ".ki")) {
			return;
		}		

		Utils.loadGame(player);
		
		tmpGame = KuffleMain.games.get(player.getName());
		KuffleMain.updatePlayersHead();
		KuffleMain.scores.setupPlayerScores(tmpGame);
		tmpGame.load();
		KuffleMain.updatePlayersHeadData(player.getName(), tmpGame.getItemDisplay());
		
		for (String playerName : KuffleMain.games.keySet()) {
			KuffleMain.games.get(playerName).getPlayer().sendMessage("[" + KuffleMain.current.getName() + "] : <" + player.getName() + "> game is reloaded !");
		}
		
		KuffleMain.systemLogs.logMsg(KuffleMain.current.getName(), "<" + player.getName() + "> game is reloaded !");
	}
	
	@EventHandler
	public void onPlayerDeconnectEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		Game tmpGame;
		
		if (!KuffleMain.gameStarted || !KuffleMain.games.containsKey(player.getName())) {
			return ;
		}
		
		tmpGame = KuffleMain.games.remove(player.getName());

		try (FileWriter writer = new FileWriter(dataFolder.getPath() + File.separator + player.getName() + ".ki")) {			
			Inventory newInv = Bukkit.createInventory(null, 54, "§8Players");
			
			for (ItemStack item : KuffleMain.playersHeads.getContents()) {
				if (item != null && !item.getItemMeta().getDisplayName().equals(player.getName())) {
					newInv.addItem(item);
				}
			}
			
			KuffleMain.playersHeads = newInv;
			
			writer.write(tmpGame.save());
			
			tmpGame.stop();
			
			for (String playerName : KuffleMain.games.keySet()) {
				KuffleMain.games.get(playerName).getPlayer().sendMessage("[" + KuffleMain.current.getName() + "] : <" + player.getName() + "> game is saved.");
			}
			
			KuffleMain.systemLogs.logMsg(KuffleMain.current.getName(), "<" + player.getName() + "> game is saved.");
		} catch (IOException e) {
			KuffleMain.systemLogs.logSystemMsg(e.getMessage());
		}
	}
	
	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		if (!KuffleMain.gameStarted) {
			return ;
		}
		
		Player player = event.getEntity();
		Location deathLoc = player.getLocation();
		event.setKeepInventory(true);
		
		if (event.getDrops().size() > 0) {	
			event.getDrops().clear();
		}
		
		KuffleMain.gameLogs.logMsg(player.getName(), "just died.");
		
		for (String playerName : KuffleMain.games.keySet()) {
			if (playerName.equals(player.getName())) {
				if (KuffleMain.config.getLevel().losable) {
					KuffleMain.games.get(playerName).setLose(true);
				} else {
					KuffleMain.games.get(playerName).setDeathLoc(deathLoc);
					KuffleMain.games.get(playerName).savePlayerInv();
					KuffleMain.games.get(playerName).setDead(true);
				}
				
				return ;
			}
		}
	}
	
	@EventHandler
	public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
		if (!KuffleMain.gameStarted) {
			return ;
		}
		
		Player player = event.getPlayer();
		
		if (!KuffleMain.games.containsKey(player.getName())) {
			return ;
		}
		
		KuffleMain.gameLogs.logMsg(player.getName(), "just respawned.");

		event.setRespawnLocation(KuffleMain.games.get(player.getName()).getSpawnLoc());
		KuffleMain.games.get(player.getName()).getPlayer().getInventory().clear();

		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () -> {
			if (KuffleMain.config.getLevel().losable) {
				player.sendMessage(ChatColor.RED + "YOU LOSE !");
			} else {
				teleportAutoBack(KuffleMain.games.get(player.getName()));
				KuffleMain.games.get(player.getName()).getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 1));
				KuffleMain.games.get(player.getName()).getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 10));
			}
		}, 20);
	}
	
	private void getEndLoc(Location loc) {
		int tmp = loc.getWorld().getHighestBlockYAt(loc);
		
		if (tmp != -1) {
			loc.setY(loc.getWorld().getHighestBlockYAt(loc) + 1);
		} else {
			loc.setY(61);
		}
	}
	
	private void createSafeBox(Location loc, String playerName) {
		Location wall;
		
		for (double x = -2; x <= 2; x++) {
			for (double y = -2; y <= 2; y++) {
				for (double z = -2; z <= 2; z++) {
					wall = loc.clone();
					wall.add(x, y, z);
					
					if (x == 0 && y == -1 && z == 0) {
						setSign(wall, playerName);
					} else if (x <= 1 && x >= -1 && y <= 1 && y >= -1 && z <= 1 && z >= -1) {
						replaceExeption(wall, Material.AIR);
					} else {
						replaceExeption(wall, Material.DIRT);
					}
				}
			}
		}
	}
	
	public void teleportAutoBack(Game tmpGame) {
		tmpGame.getPlayer().sendMessage("You will be tp back to your death spot in " + KuffleMain.config.getLevel().seconds + " seconds.");
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () -> {
			Location loc = tmpGame.getDeathLoc();
				
			if (loc.getWorld().getName().contains("the_end") && loc.getY() < 0) {
				getEndLoc(loc);
			}
			
			createSafeBox(loc, tmpGame.getPlayer().getName());
			
			tmpGame.getPlayer().teleport(loc.add(0, 1, 0));
			
			for (Entity e : tmpGame.getPlayer().getNearbyEntities(3.0, 3.0, 3.0)) {
				if (e.getType() != EntityType.DROPPED_ITEM) {
					e.remove();
				}
			}
			
			tmpGame.restorePlayerInv();

			for (PotionEffect p : tmpGame.getPlayer().getActivePotionEffects()) {
				tmpGame.getPlayer().removePotionEffect(p.getType());
			}
			
			tmpGame.reloadEffects();
		}, (KuffleMain.config.getLevel().seconds * 20));
	}
	
	private void replaceExeption(Location loc, Material m) {
		if (!exceptions.contains(loc.getBlock().getType())) {
			loc.getBlock().setType(m);
		}
	}
	
	private void setSign(Location loc, String playerName) {
		if (!exceptions.contains(loc.getBlock().getType())) {
			loc.getBlock().setType(Material.OAK_SIGN);
			
			Sign sign = (Sign) loc.getBlock().getState();
			
			sign.setLine(0, "[KuffleItems]");
			sign.setLine(1, Utils.getLangString(null, "HERE_DIES"));
			sign.setLine(2, playerName);
			sign.update(true);
		}
	}
	
	@EventHandler
	public void onPauseEvent(PlayerMoveEvent event) {
		if (KuffleMain.paused) {
			event.setCancelled(true);
		}
	}
}
