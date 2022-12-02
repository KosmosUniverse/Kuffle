package main.fr.kosmosuniverse.kuffle.listeners;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

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

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.CraftManager;
import main.fr.kosmosuniverse.kuffle.core.GameHolder;
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
		
		if (!Utils.fileExists(KuffleMain.getInstance().getDataFolder().getPath(), player.getName() + ".k")) {
			return;
		}

		try {
			GameManager.loadPlayerGame(KuffleMain.getInstance().getDataFolder().getPath(), player);
		} catch (IOException | ClassNotFoundException e) {
			LogManager.getInstanceSystem().writeMsg(player, "Cannot reload your game, please contact an administartor.");
			Utils.logException(e);
			return;
		}
		
		for (ACraft item : CraftManager.getRecipeList()) {
			player.discoverRecipe(new NamespacedKey(KuffleMain.getInstance(), item.getName()));
		}
		
		GameManager.sendMsgToPlayers(LangManager.getMsgLang("GAME_RELOADED", GameManager.getPlayerLang(player.getName())).replace("%s", player.getName()));
		LogManager.getInstanceSystem().logMsg(KuffleMain.getInstance().getName(), "<" + player.getName() + "> game is reloaded !");
	}
	
	/**
	 * Event triggered at player disconnection during game, it saves this player game
	 * 
	 * @param event	The PlayerQuitEvent
	 */
	@EventHandler
	public void onPlayerDisconnectEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		if (!KuffleMain.gameStarted || !GameManager.hasPlayer(player.getName())) {
			return ;
		}
		
		for (ACraft item : CraftManager.getRecipeList()) {
			player.undiscoverRecipe(new NamespacedKey(KuffleMain.getInstance(), item.getName()));
		}
		
		GameManager.savePlayer(KuffleMain.getInstance().getDataFolder().getPath(), player.getName());
				
		GameManager.stopPlayer(player.getName());
		GameManager.removePlayer(player.getName());
		GameManager.updatePlayersHeads();
		GameManager.sendMsgToPlayers(LangManager.getMsgLang("PLAYER_GAME_SAVED", Config.getLang()).replace("%s", player.getName()));
		
		if (GameManager.getGames().size() == 0) {
			if (Config.getTeam()) {
				TeamManager.getInstance().saveTeams(KuffleMain.getInstance().getDataFolder().getPath());
			}
			
			GameHolder holder = new GameHolder(Config.getHolder(), KuffleMain.type.getType().toString(), GameManager.getRanks(), KuffleMain.type.getXpMap());
			
			try (FileOutputStream fos = new FileOutputStream(KuffleMain.getInstance().getDataFolder().getPath() + File.separator + "Game.k")) {
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(holder);
				oos.flush();
				oos.close();
			} catch (IOException e) {
				Utils.logException(e);
			}
			
			/*try (FileWriter writer = new FileWriter(KuffleMain.getInstance().getDataFolder().getPath() + File.separator + "Games.k");) {				
				JSONObject global = new JSONObject();

				global.put("type", KuffleMain.type.getType().toString());
				global.put("config", Config.saveConfig());
				global.put("ranks", GameManager.saveRanks());
				global.put("xpMax", KuffleMain.type.saveXpMax());
				
				writer.write(global.toJSONString());
				
				global.clear();
			} catch (IOException e) {
				LogManager.getInstanceSystem().logSystemMsg(e.getMessage());
			}*/

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
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
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
