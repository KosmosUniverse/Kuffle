package main.fr.kosmosuniverse.kuffle.listeners;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.CraftManager;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.ScoreManager;
import main.fr.kosmosuniverse.kuffle.core.TeamManager;
import main.fr.kosmosuniverse.kuffle.crafts.ACraft;
import main.fr.kosmosuniverse.kuffle.type.KuffleType;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class PlayerEvents implements Listener {
	/**
	 * Event triggered at player connection, if game is started load for this player if he has saved game file
	 * 
	 * @param event	The PlayerJoinEvent
	 */
	@EventHandler
	public void onPlayerConnectEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
	
		if (!KuffleMain.gameStarted) {
			return;
		}
		
		if (!Utils.fileExists(KuffleMain.current.getDataFolder().getPath(), player.getName() + ".k")) {
			return;
		}

		try {
			GameManager.loadPlayerGame(player);
		} catch (IOException | ParseException e) {
			Utils.logException(e);
		}
		
		for (ACraft item : CraftManager.getRecipeList()) {
			player.discoverRecipe(new NamespacedKey(KuffleMain.current, item.getName()));
		}
		
		GameManager.sendMsgToPlayers(LangManager.getMsgLang("GAME_RELOADED", GameManager.getPlayerLang(player.getName())).replace("%s", player.getName()));
		LogManager.getInstanceSystem().logMsg(KuffleMain.current.getName(), "<" + player.getName() + "> game is reloaded !");
	}
	
	/**
	 * Event triggered at player disconnection during game, it saves this player game
	 * 
	 * @param event	The PlayerQuitEvent
	 */
	@SuppressWarnings("unchecked")
	@EventHandler
	public void onPlayerDisconnectEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		if (!KuffleMain.gameStarted || !GameManager.hasPlayer(player.getName())) {
			return ;
		}
		
		for (ACraft item : CraftManager.getRecipeList()) {
			player.undiscoverRecipe(new NamespacedKey(KuffleMain.current, item.getName()));
		}
		
		try (FileWriter writer = new FileWriter(KuffleMain.current.getDataFolder().getPath() + File.separator + player.getName() + ".k")) {			
			writer.write(GameManager.savePlayer(player.getName()));			
			LogManager.getInstanceSystem().logMsg(KuffleMain.current.getName(), "<" + player.getName() + "> game is saved.");
		} catch (IOException e) {
			LogManager.getInstanceSystem().logSystemMsg(e.getMessage());
		}
		
		GameManager.stopPlayer(player.getName());
		GameManager.removePlayer(player.getName());
		GameManager.updatePlayersHeads();
		GameManager.sendMsgToPlayers(LangManager.getMsgLang("PLAYER_GAME_SAVED", Config.getLang()).replace("%s", player.getName()));
		
		if (GameManager.getGames().size() == 0) {
			if (Config.getTeam()) {
				try (FileWriter writer = new FileWriter(KuffleMain.current.getDataFolder().getPath() + File.separator + "Teams.k");) {				
					writer.write(TeamManager.getInstance().saveTeams());
				} catch (IOException e) {
					LogManager.getInstanceSystem().logSystemMsg(e.getMessage());
				}
			}
			
			try (FileWriter writer = new FileWriter(KuffleMain.current.getDataFolder().getPath() + File.separator + "Games.k");) {				
				JSONObject global = new JSONObject();

				global.put("type", KuffleMain.type.getType().toString());
				global.put("config", Config.saveConfig());
				global.put("ranks", GameManager.saveRanks());
				global.put("xpMax", KuffleMain.type.saveXpMax());
				
				writer.write(global.toJSONString());
				
				global.clear();
			} catch (IOException e) {
				LogManager.getInstanceSystem().logSystemMsg(e.getMessage());
			}

			if (KuffleMain.type.getType() == KuffleType.Type.ITEMS) {
				CraftManager.removeCraftTemplates();
			}
			
			ScoreManager.clear();
			GameManager.clear();
			KuffleMain.loop.kill();
			KuffleMain.paused = false;
			KuffleMain.gameStarted = false;
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang("ALL_DISCONNECTED", Config.getLang()));
			LogManager.getInstanceGame().logSystemMsg(LangManager.getMsgLang("ALL_DISCONNECTED", Config.getLang()));
		}
	}
	
	/**
	 * Event triggered at player death, it sets player death
	 * 
	 * @param event	The PlayerDeathEvent
	 */
	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		if (!KuffleMain.gameStarted) {
			return ;
		}
		
		Player player = event.getEntity();
		
		if (!GameManager.hasPlayer(player.getName())) {
			return ;
		}
		
		Location deathLoc = player.getLocation();
		event.setKeepInventory(true);
		
		if (event.getDrops().size() > 0) {	
			event.getDrops().clear();
		}
		
		LogManager.getInstanceGame().logMsg(player.getName(), "just died.");
		
		GameManager.playerDied(player.getName(), deathLoc);
	}
	
	/**
	 * Event triggered at player respawn, it teleports him to his death location
	 * 
	 * @param event	The PlayerRespawnEvent
	 */
	@EventHandler
	public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
		if (!KuffleMain.gameStarted) {
			return ;
		}
		
		Player player = event.getPlayer();
		
		if (!GameManager.hasPlayer(player.getName())) {
			return ;
		}
		
		LogManager.getInstanceGame().logMsg(player.getName(), "just respawned.");

		event.setRespawnLocation(GameManager.getPlayerSpawnLoc(player.getName()));
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () -> {
			if (Config.getLevel().losable) {
				player.sendMessage(ChatColor.RED + LangManager.getMsgLang("YOU_LOSE", GameManager.getPlayerLang(player.getName())));
			} else {
				GameManager.teleportAutoBack(player.getName());
				GameManager.giveEffectsToPlayer(player.getName(), new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 1), new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 10));
			}
		}, 20);
	}
	
	/**
	 * If game is paused, player can't move
	 * 
	 * @param event	The PlayerMoveEvent to cancel if game is paused
	 */
	@EventHandler
	public void onPauseEvent(PlayerMoveEvent event) {
		if (!KuffleMain.gameStarted) {
			return ;
		}
		
		if (KuffleMain.paused) {
			event.setCancelled(true);
		}
	}
}
