package fr.kosmosuniverse.kuffle.listeners;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import fr.kosmosuniverse.kuffle.KuffleMain;
import fr.kosmosuniverse.kuffle.core.*;
import fr.kosmosuniverse.kuffle.utils.CommandUtils;
import fr.kosmosuniverse.kuffle.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
	
		if (Party.getInstance().getStatus() != GameStatus.RUNNING) {
			return;
		}
		
		if (!Utils.fileExists(KuffleMain.getInstance().getDataFolder().getPath(), player.getName() + ".k")) {
			return;
		}

		Party.getInstance().getPlayers().addPlayer(player.getName());

		try {
			Party.getInstance().getGames().loadPlayerGame(KuffleMain.getInstance().getDataFolder().getPath(), player);
		} catch (IOException | ClassNotFoundException e) {
			LogManager.getInstanceSystem().writeMsg(player, "Cannot reload your game, please contact an administrator.");
			Utils.logException(e);
			return;
		}
		
		CraftManager.discoverCrafts(player);
		Party.getInstance().getPlayers().getList().forEach(p -> Objects.requireNonNull(Bukkit.getPlayer(p)).sendMessage(LangManager.getMsgLang("GAME_RELOADED", Party.getInstance().getGames().getGames().get(p).getConfigLang()).replace("%s", player.getName())));
		Party.getInstance().getSpectators().getList().forEach(p -> Objects.requireNonNull(Bukkit.getPlayer(p)).sendMessage(LangManager.getMsgLang("GAME_RELOADED", Party.getInstance().getGames().getGames().get(p).getConfigLang()).replace("%s", player.getName())));
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

		if (Party.getInstance().getPlayers().has(player.getName())) {
			Party.getInstance().getPlayers().removePlayer(player.getName());
		}

		if (Party.getInstance().getSpectators().has(player.getName())) {
			Party.getInstance().getSpectators().removePlayer(player.getName());
		}

		if (Party.getInstance().getStatus() == GameStatus.NOT_RUNNING) {
			return ;
		}

		CraftManager.undiscoverCrafts(player);
		Party.getInstance().getGames().savePlayer(KuffleMain.getInstance().getDataFolder().getPath(), player.getName(), Party.getInstance().getGames().getGames().get(player.getName()));
		Party.getInstance().getGames().stopPlayer(player.getName(), Party.getInstance().getGames().getGames().get(player.getName()));
		Party.getInstance().getGames().getGames().remove(player.getName());
		Party.getInstance().getPlayers().updatePlayersHeads(Party.getInstance().getGames().getGames().entrySet()
				.stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> (e.getValue().getCurrentTarget() != null ? e.getValue().getCurrentTarget() : "null"))));
		Party.getInstance().getPlayers().getList().forEach(p -> Objects.requireNonNull(Bukkit.getPlayer(p)).sendMessage(LangManager.getMsgLang("PLAYER_GAME_SAVED", Party.getInstance().getGames().getGames().get(p).getConfigLang()).replace("%s", player.getName())));
		Party.getInstance().getSpectators().getList().forEach(p -> Objects.requireNonNull(Bukkit.getPlayer(p)).sendMessage(LangManager.getMsgLang("PLAYER_GAME_SAVED", Party.getInstance().getGames().getGames().get(p).getConfigLang()).replace("%s", player.getName())));

		if (Party.getInstance().getPlayers().getList().isEmpty()) {
			if (Config.getTeam()) {
				TeamManager.getInstance().saveTeams(KuffleMain.getInstance().getDataFolder().getPath());
			}
			
			CommandUtils.saveParty();
			
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang("ALL_DISCONNECTED", Config.getLang()));
			LogManager.getInstanceGame().logSystemMsg(LangManager.getMsgLang("ALL_DISCONNECTED", Config.getLang()));
		}
	}
	
	/**
	 * Event triggered when player tries to change game mode
	 * 
	 * @param event	The PlayerGameModeChangeEvent
	 */
	@EventHandler
	public void onGameModeChangeEvent(PlayerGameModeChangeEvent event) {
		if (Party.getInstance().getStatus() != GameStatus.RUNNING) {
			return ;
		}
		
		Player player = event.getPlayer();
		GameMode gm = event.getNewGameMode();
		
		if (Party.getInstance().getSpectators().has(player.getName()) && gm != GameMode.SPECTATOR) {
			event.setCancelled(true);
			player.sendMessage(LangManager.getMsgLang("NOT_CHANGE_GM", Config.getLang()));
		}
	}
	
	/**
	 * Event triggered at player death, it sets player death
	 * 
	 * @param event	The PlayerDeathEvent
	 */
	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		if (Party.getInstance().getStatus() != GameStatus.RUNNING) {
			return ;
		}
		
		Player player = event.getEntity();
		
		if (!Party.getInstance().getPlayers().has(player.getName())) {
			return ;
		}
		
		Location deathLoc = player.getLocation();
		event.setKeepInventory(true);
		
		if (event.getDrops().size() > 0) {	
			event.getDrops().clear();
		}
		
		LogManager.getInstanceGame().logMsg(player.getName(), "just died.");

		Party.getInstance().getGames().playerDied(player.getName(), deathLoc);
	}
	
	/**
	 * Event triggered at player respawn, it teleports him to his death location
	 * 
	 * @param event	The PlayerRespawnEvent
	 */
	@EventHandler
	public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
		if (Party.getInstance().getStatus() != GameStatus.RUNNING) {
			return ;
		}
		
		Player player = event.getPlayer();
		
		if (!Party.getInstance().getPlayers().has(player.getName())) {
			return ;
		}
		
		LogManager.getInstanceGame().logMsg(player.getName(), "just respawned.");

		event.setRespawnLocation(Party.getInstance().getGames().getGames().get(player.getName()).getSpawnLoc());
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
			if (Config.getLevel().isLosable()) {
				player.sendMessage(ChatColor.RED + LangManager.getMsgLang("YOU_LOSE", Party.getInstance().getGames().getGames().get(player.getName()).getConfigLang()));
			} else {
				Party.getInstance().getGames().teleportAutoBack(player);
				player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 1));
				player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 999999, 10));
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
		if (Party.getInstance().getStatus() != GameStatus.PAUSED) {
			return ;
		}

		event.setCancelled(true);
	}
}
