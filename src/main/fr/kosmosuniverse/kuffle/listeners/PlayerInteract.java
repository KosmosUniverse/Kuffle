package main.fr.kosmosuniverse.kuffle.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONObject;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.AgeManager;
import main.fr.kosmosuniverse.kuffle.core.Game;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

public class PlayerInteract implements Listener {
	private Map<String, Integer> xpActivables;
	private Map<Location, String> shulkers = new HashMap<>();

	public PlayerInteract() {
		xpActivables = new HashMap<>();
		
		xpActivables.put("EndTeleporter", KuffleMain.config.getXpEnd());
		xpActivables.put("OverworldTeleporter", KuffleMain.config.getXpOverworld());
		xpActivables.put("CoralCompass", KuffleMain.config.getXpCoral());
	}
	
	public void loadXpMax(JSONObject xpMax) {
		xpActivables.clear();
		
		xpActivables.put("EndTeleporter", Integer.parseInt(xpMax.get("xpEnd").toString()));
		xpActivables.put("OverworldTeleporter", Integer.parseInt(xpMax.get("xpOverworld").toString()));
		xpActivables.put("CoralCompass", Integer.parseInt(xpMax.get("xpCoral").toString()));
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject saveXpMax() {
		JSONObject xpMaxObj = new JSONObject();

		xpMaxObj.put("xpEnd", xpActivables.get("EndTeleporter"));
		xpMaxObj.put("xpOverworld", xpActivables.get("OverworldTeleporter"));
		xpMaxObj.put("xpCoral", xpActivables.get("CoralCompass"));
		
		return xpMaxObj;
	}
	
	@EventHandler
	public void onLeftClick(PlayerInteractEvent event) {
		if (!KuffleMain.gameStarted) {
			return ;
		}
		
		if (!event.hasItem()) {
			return ;
		}
		
		Player player = event.getPlayer();
		Action action = event.getAction();
		ItemStack item = event.getItem();
		
		if (action != Action.RIGHT_CLICK_AIR || item == null) {
			return ;
		}
		
		if (Utils.compareItems(item, KuffleMain.crafts.findItemByName("EndTeleporter"), true, true, true)) {
			consumeItem(event);
			
			endTeleporter(player);
			
			return ;
		}
		
		if (Utils.compareItems(item, KuffleMain.crafts.findItemByName("OverworldTeleporter"), true, true, true)) {
			consumeItem(event);
			
			overworldTeleporter(player);
			
			return ;
		}
		
		if (Utils.compareItems(item, KuffleMain.crafts.findItemByName("CoralCompass"), true, true, true)) {
			if (!((CompassMeta) item.getItemMeta()).hasLodestone()) {
				coralCompass(player, item);
			} else {
				KuffleMain.gameLogs.writeMsg(player, "This CoralCompass is already paired to a warm ocean.");
			}
			
			return ;
		}
		
		Game tmpGame = KuffleMain.games.get(player.getName());

		if (item.getItemMeta().getDisplayName().contains("Template")) {
			String name = AgeManager.getAgeByNumber(KuffleMain.ages, tmpGame.getAge()).name;
			name = name.replace("_Age", "");
			name = name + "Template";

			if (Utils.compareItems(item, KuffleMain.crafts.findItemByName(name), true, true, true)) {
				event.setCancelled(true);
				
				consumeItem(event);
				
				tmpGame.foundSBTT();
				KuffleMain.gameLogs.logMsg(tmpGame.getPlayer().getName(), "just used " + name + " !");
				
				return ;
			}
		}
		
		if (tmpGame == null || tmpGame.getCurrentItem() == null) {
			return ;
		}
		
		checkItem(tmpGame, item);
	}
	
	private void consumeItem(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if (event.getHand() == EquipmentSlot.HAND) {
			player.getInventory().setItemInMainHand(null);	
		} else if (event.getHand() == EquipmentSlot.OFF_HAND) {
			player.getInventory().setItemInOffHand(null);
		}
	}
	
	private void checkItem(Game game, ItemStack item) {
		if (!KuffleMain.config.getDouble() && game.getCurrentItem().equals(item.getType().name().toLowerCase())) {
			KuffleMain.gameLogs.logMsg(game.getPlayer().getName(), " validate his item [" + game.getCurrentItem() + "] !");
			game.found();
		} else if (KuffleMain.config.getDouble() &&
				(game.getCurrentItem().split("/")[0].equals(item.getType().name().toLowerCase()) ||
						game.getCurrentItem().split("/")[1].equals(item.getType().name().toLowerCase()))) {
			String tmp = game.getCurrentItem().split("/")[0].equals(item.getType().name().toLowerCase()) ? game.getCurrentItem().split("/")[0] : game.getCurrentItem().split("/")[1];
			KuffleMain.gameLogs.logMsg(game.getPlayer().getName(), " validate his item [" + tmp + "] !");
			game.found();
		}
	}
	
	@EventHandler
	public void onPlaceShulker(BlockPlaceEvent event) {
		if (!KuffleMain.gameStarted || !KuffleMain.config.getPassive()) {
			return ;
		}
		
		Player player = event.getPlayer();
		Block block = event.getBlock();
		Location location = block.getLocation();
		
		if (block.getType().name().toLowerCase().contains("shulker_box")) {
			shulkers.put(location, player.getName());
		}
	}
	
	@EventHandler
	public void onBreakShulker(BlockBreakEvent event) {
		if (!KuffleMain.gameStarted || !KuffleMain.config.getPassive()) {
			return ;
		}
		
		Player player = event.getPlayer();
		Block block = event.getBlock();
		Location location = block.getLocation();
		
		if (!shulkers.containsKey(location)) {
			return;
		}
		
		String placerName = shulkers.get(location);
		
		if (!placerName.equals(player.getName()) &&
				(!KuffleMain.config.getTeam() ||
						!KuffleMain.games.get(player.getName()).getTeamName().equals(KuffleMain.games.get(placerName).getTeamName()))) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBreakSign(BlockBreakEvent event) {
		if (!KuffleMain.gameStarted) {
			return ;
		}
		
		Block block = event.getBlock();
		
		if (block.getType() != Material.OAK_SIGN) {
			return;
		}
		
		Sign sign = (Sign) block.getState();
		
		if (sign == null ||
				!sign.getLine(0).equals("[KuffleItems]") ||
				!sign.getLine(1).equals("Here dies") ||
				!KuffleMain.games.containsKey(sign.getLine(2))) {
			return ;
		}
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void onInteractShulker(PlayerInteractEvent event) {
		if (!KuffleMain.gameStarted || !KuffleMain.config.getPassive()) {
			return ;
		}
		
		Player player = event.getPlayer();
		Action action = event.getAction();
		Block block = event.getClickedBlock();
		
		if (action == Action.RIGHT_CLICK_BLOCK && block != null &&
				block.getType().name().toLowerCase().contains("shulker_box") &&
				!shulkers.containsValue(player.getName()) &&
				(!KuffleMain.config.getTeam() || !KuffleMain.games.get(player.getName()).getTeamName().equals(KuffleMain.games.get(shulkers.get(block.getLocation())).getTeamName()))) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent event) {
		if (!KuffleMain.gameStarted) {
			return ;
		}
		
		ItemStack item = event.getInventory().getResult();
		Player player = (Player) event.getWhoClicked();

		if (Utils.compareItems(item, KuffleMain.crafts.findItemByName("EndTeleporter"), true, true, true)) {
			if (player.getLevel() < xpActivables.get("EndTeleporter")) {
				event.setCancelled(true);
				player.sendMessage("You need " + xpActivables.get("EndTeleporter") + " xp levels to craft this item.");
			} else {
				player.setLevel(player.getLevel() - xpActivables.get("EndTeleporter"));
				KuffleMain.gameLogs.logMsg(player.getName(), "Crafted EndTeleporter.");
			}
		} else if (Utils.compareItems(item, KuffleMain.crafts.findItemByName("OverworldTeleporter"), true, true, true)) {
			if (player.getLevel() < xpActivables.get("OverworldTeleporter")) {
				event.setCancelled(true);
				player.sendMessage("You need " + xpActivables.get("OverworldTeleporter") + " xp levels to craft this item.");
			} else {
				player.setLevel(player.getLevel() - xpActivables.get("OverworldTeleporter"));
				KuffleMain.gameLogs.logMsg(player.getName(), "Crafted OverworldTeleporter.");
			}
		} else if (Utils.compareItems(item, KuffleMain.crafts.findItemByName("CoralCompass"), true, true, true)) {
			if (player.getLevel() < xpActivables.get("CoralCompass")) {
				event.setCancelled(true);
				player.sendMessage("You need " + xpActivables.get("CoralCompass") + " xp levels to craft this item.");
			} else {
				player.setLevel(player.getLevel() - xpActivables.get("CoralCompass"));
				KuffleMain.gameLogs.logMsg(player.getName(), "Crafted CoralCompass.");
			}
		} else if (item.hasItemMeta() &&
				item.getItemMeta().hasDisplayName() &&
				item.getItemMeta().getDisplayName().contains("Template")) {
			String name = AgeManager.getAgeByNumber(KuffleMain.ages, KuffleMain.games.get(player.getName()).getAge()).name;

			name = name.replace("_Age", "");
			name = name + "Template";
			
			Utils.reloadTemplate(name, AgeManager.getAgeByNumber(KuffleMain.ages, KuffleMain.games.get(player.getName()).getAge()).name);
			
			for (String playerName : KuffleMain.games.keySet()) {
				KuffleMain.games.get(playerName).getPlayer().sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + player.getName() + ChatColor.RESET + "" + ChatColor.BLUE + " just crafted Template !");
			}
			KuffleMain.gameLogs.logMsg(player.getName(), "just crafted Template !");
		}
	}
	
	@EventHandler
	public void onPlayerHitPlayer(EntityDamageByEntityEvent event) {
		if (!KuffleMain.gameStarted) {
			return ;
		}
		
		Entity tmpDamager = event.getDamager();	
		Entity tmpDamagee = event.getEntity();
		
		if (!(tmpDamager instanceof Player) || !(tmpDamagee instanceof Player)) {
			return;
		}
		
		Player damager = (Player) tmpDamager;
		Player damagee = (Player) tmpDamagee;
		
		if (!KuffleMain.games.containsKey(damager.getName()) || !KuffleMain.games.containsKey(damagee.getName())) {
			return ;
		}
		
		if (KuffleMain.config.getPassive()) {
			event.setCancelled(true);
			
			return ;
		}
		
		if (KuffleMain.config.getTeam() &&
				KuffleMain.games.get(damager.getName()).getTeamName().equals(KuffleMain.games.get(damagee.getName()).getTeamName())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onFireWorkThrow(PlayerInteractEvent event) {
		if (!KuffleMain.gameStarted) {
			return ;
		}
		
		ItemStack item;
		Action action = event.getAction();
		Player player = event.getPlayer();

		if (!KuffleMain.games.containsKey(player.getName())) {
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
	
	private void endTeleporter(Player player) {
		Location tmp = new Location(Bukkit.getWorld(Utils.findNormalWorld().getName() + "_the_end"), player.getLocation().getX() + 1000, 60.0, player.getLocation().getZ() + 1000);
		
		while (tmp.getBlock().getType() != Material.END_STONE) {
			tmp.add(10, 0, 10);
		}
		
		teleport(tmp, player, "Teleported to the End.");
		
		if (xpActivables.get("EndTeleporter") > 1) {
			xpActivables.put("EndTeleporter", xpActivables.get("EndTeleporter") - 1);
		}
	}
	
	private void overworldTeleporter(Player player) {
		Location tmp = new Location(Bukkit.getWorld(Utils.findNormalWorld().getName()), player.getLocation().getX() - 1000, 80.0, player.getLocation().getZ() - 1000);
		
		teleport(tmp, player, "Teleported to the Overworld.");
		
		if (xpActivables.get("OverworldTeleporter") > 1) {
			int tmpXp = xpActivables.get("OverworldTeleporter") - 2;
			xpActivables.put("OverworldTeleporter", tmpXp < 1 ? 1 : tmpXp);
		}
	}
	
	private void coralCompass(Player player, ItemStack compass) {
		Location tmp = player.getLocation();
		
		if (findCoralBiome(tmp, compass)) {
			KuffleMain.gameLogs.writeMsg(player, "Warm ocean found, follow the compass.");
			
			if (xpActivables.get("CoralCompass") > 1) {
				int tmpXp = xpActivables.get("CoralCompass");
				
				if (tmpXp % 2 == 1) {
					tmpXp++;
				}
				
				tmpXp = tmpXp / 2;
				xpActivables.put("CoralCompass", tmpXp < 1 ? 1 : tmpXp);
			}
		} else {
			KuffleMain.gameLogs.writeMsg(player, "Warm ocean not found, move away and try again.");	
		}
	}
	
	private void teleport(Location loc, Player player, String msg) {
		loc.setY((double) loc.getWorld().getHighestBlockAt(loc).getY());
		
		if (KuffleMain.config.getTeam()) {
			String teamName = KuffleMain.games.get(player.getName()).getTeamName();
			
			for (String playerName : KuffleMain.games.keySet()) {
				if (KuffleMain.games.get(playerName).getTeamName().equals(teamName)) {
					KuffleMain.games.get(playerName).getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 50, false, false, false));
					KuffleMain.games.get(playerName).getPlayer().teleport(loc);
					KuffleMain.games.get(playerName).getPlayer().removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
					KuffleMain.gameLogs.logMsg(playerName, msg);
				}
			}
		} else {
			player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 50, false, false, false));
			player.teleport(loc);
			player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
			KuffleMain.gameLogs.logMsg(player.getName(), msg);
		}
	}
	
	private boolean findCoralBiome(Location loc, ItemStack compass) {
		Chunk baseChunk = loc.getChunk();

		for (int radius = 0; radius <= 10; radius++) {
			Location found = searchWarmRadius(baseChunk, radius, loc);
			
			if (found != null) {
				CompassMeta cm = (CompassMeta) compass.getItemMeta();
				
				cm.setLodestone(found);
				cm.getLore().add("Location : " + found.getBlockX() + ", " + found.getBlockY() + ", " + found.getBlockZ());
				compass.setItemMeta(cm);
				
				return true;
			}
		}
		
		return false;
	}
	
	private Location searchWarmRadius(Chunk baseChunk, int radius, Location loc) {
		for (int x = -radius; x <= radius; x++) {
			for (int z = -radius; z <= radius; z++) {
				if (Math.abs(x) == radius || Math.abs(z) == radius) {
					Chunk currentChunk = loc.getWorld().getChunkAt(baseChunk.getX() + x, baseChunk.getZ() + z);
					Location blockLoc = currentChunk.getBlock(8, 0, 8).getLocation();
					Biome biome = loc.getWorld().getBiome(blockLoc.getBlockX(), blockLoc.getBlockY(), blockLoc.getBlockZ());
					
					if (biome == Biome.WARM_OCEAN || biome == Biome.DEEP_WARM_OCEAN) {
						blockLoc.getBlock().setType(Material.LODESTONE);
						return blockLoc;
					}
				}
			}
		}
		
		return null;
	}
}
