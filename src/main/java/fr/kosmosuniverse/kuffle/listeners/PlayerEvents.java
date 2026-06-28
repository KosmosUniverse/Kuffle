package fr.kosmosuniverse.kuffle.listeners;

import fr.kosmosuniverse.kuffle.KuffleMain;
import fr.kosmosuniverse.kuffle.core.*;
import fr.kosmosuniverse.kuffle.datamanagers.LangManager;
import fr.kosmosuniverse.kuffle.states.States;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

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
	
		/*if (PartyTmp.getInstance().getGameState().getState() != States.RUNNING) {
			return;
		}
		
		if (!Utils.fileExists(KuffleMain.getInstance().getDataFolder().getPath(), player.getName() + ".k")) {
			return;
		}

		PartyTmp.getInstance().getPlayers().addPlayer(player.getName());

		try {
			PartyTmp.getInstance().getGameManager().loadPlayerGame(KuffleMain.getInstance().getDataFolder().getPath(), player);
		} catch (IOException | ClassNotFoundException e) {
			LogManager.getInstanceSystem().writeMsg(player, "Cannot reload your game, please contact an administrator.");
			Utils.logException(e);
			return;
		}

		CraftManager.discoverCrafts(player);
		PartyTmp.getInstance().getPlayers().getList().forEach(p -> Objects.requireNonNull(Bukkit.getPlayer(p)).sendMessage(LangManager.getMsgLang("GAME_RELOADED", PartyTmp.getInstance().getGameManager().getPlayerLang(p)).replace("%s", player.getName())));
		PartyTmp.getInstance().getSpectators().getList().forEach(p -> Objects.requireNonNull(Bukkit.getPlayer(p)).sendMessage(LangManager.getMsgLang("GAME_RELOADED", PartyTmp.getInstance().getGameManager().getPlayerLang(p)).replace("%s", player.getName())));
		LogManager.getInstanceSystem().logMsg(KuffleMain.getInstance().getName(), "<" + player.getName() + "> game is reloaded !");*/
	}

	/**
	 * Event triggered at player disconnection during game, it saves this player game
	 * 
	 * @param event	The PlayerQuitEvent
	 */
	@EventHandler
	public void onPlayerDisconnectEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		/*if (PartyTmp.getInstance().getPlayers().has(player.getName())) {
			PartyTmp.getInstance().getPlayers().removePlayer(player.getName());
		}

		if (PartyTmp.getInstance().getSpectators().has(player.getName())) {
			PartyTmp.getInstance().getSpectators().removePlayer(player.getName());
		}

		if (PartyTmp.getInstance().getGameState().getState() == States.NOT_RUNNING) {
			return ;
		}

		CraftManager.undiscoverCrafts(player);
		Save.savePlayer(KuffleMain.getInstance().getDataFolder().getPath(), PartyTmp.getInstance().getGameManager().getPlayerData(player.getName()));
		PartyTmp.getInstance().getGameManager().stopPlayer(player.getName(), PartyTmp.getInstance().getGameManager().getPlayerData(player.getName()));
		PartyTmp.getInstance().getGameManager().getPlayersData().remove(player.getName());
		PartyTmp.getInstance().getPlayers().updatePlayersHeads(PartyTmp.getInstance().getGameManager().getPlayersData().entrySet()
				.stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> (e.getValue().getCurrentTarget() != null ? e.getValue().getCurrentTarget() : "null"))));
		PartyTmp.getInstance().getPlayers().getList().forEach(p -> Objects.requireNonNull(Bukkit.getPlayer(p)).sendMessage(LangManager.getMsgLang("PLAYER_GAME_SAVED", PartyTmp.getInstance().getGameManager().getPlayerLang(p)).replace("%s", player.getName())));
		PartyTmp.getInstance().getSpectators().getList().forEach(p -> Objects.requireNonNull(Bukkit.getPlayer(p)).sendMessage(LangManager.getMsgLang("PLAYER_GAME_SAVED", PartyTmp.getInstance().getGameManager().getPlayerLang(p)).replace("%s", player.getName())));

		if (PartyTmp.getInstance().getPlayers().getList().isEmpty()) {
			if (Config.getTeam()) {
				TeamManager.getInstance().saveTeams();
			}
			
			//CommandUtils.savePartyTmp();
			
			LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang("ALL_DISCONNECTED", Config.getLang()));
			LogManager.getInstanceGame().logSystemMsg(LangManager.getMsgLang("ALL_DISCONNECTED", Config.getLang()));
		}*/
	}
	
	/**
	 * Event triggered when player tries to change game mode
	 * 
	 * @param event	The PlayerGameModeChangeEvent
	 */
	@EventHandler
	public void onGameModeChangeEvent(PlayerGameModeChangeEvent event) {
		if (PartyTmp.getInstance().getGameState().getState() != States.RUNNING) {
			return ;
		}
		
		Player player = event.getPlayer();
		GameMode gm = event.getNewGameMode();
		
		if (PartyTmp.getInstance().getSpectators().has(player.getName()) && gm != GameMode.SPECTATOR) {
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
		if (PartyTmp.getInstance().getGameState().getState() != States.RUNNING) {
			return ;
		}
		
		Player player = event.getEntity();
		
		if (!PartyTmp.getInstance().getPlayers().has(player.getName())) {
			return ;
		}
		
		Location deathLoc = player.getLocation();
		event.setKeepInventory(true);
		
		if (!event.getDrops().isEmpty()) {
			event.getDrops().clear();
		}
		
		LogManager.getInstanceGame().logMsg(player.getName(), "just died.");

		PartyTmp.getInstance().getGameManager().playerDied(player.getName(), deathLoc);
	}
	
	/**
	 * Event triggered at player respawn, it teleports him to his death location
	 * 
	 * @param event	The PlayerRespawnEvent
	 */
	@EventHandler
	public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
		if (PartyTmp.getInstance().getGameState().getState() != States.RUNNING) {
			return ;
		}
		
		Player player = event.getPlayer();
		
		if (!PartyTmp.getInstance().getPlayers().has(player.getName())) {
			return ;
		}
		
		LogManager.getInstanceGame().logMsg(player.getName(), "just respawned.");

		event.setRespawnLocation(PartyTmp.getInstance().getGameManager().getPlayerSpawnLoc(player.getName()));
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
			if (Config.getLevel().isLosable()) {
				PartyTmp.getInstance().getGameManager().sendMsgToPlayer(player.getName(), (receiverLang) -> ChatColor.RED + LangManager.getMsgLang("YOU_LOSE", receiverLang));
			} else {
				PartyTmp.getInstance().getGameManager().teleportAutoBack(player);
			}
		}, 20);
	}

	@EventHandler
	public void onPlayerInvincibilityPeriod(EntityTargetLivingEntityEvent e) {
		if (PartyTmp.getInstance().getGameState().getState() == States.NOT_RUNNING
				|| !(e.getTarget() instanceof Player)) {
			return ;
		}

		Player p = (Player) e.getTarget();

		if (PartyTmp.getInstance().getPlayers().has(p.getName())
				&& PartyTmp.getInstance().getGameManager().isPlayerDead(p.getName())) {
			e.setCancelled(true);
		}
	}
	
	/**
	 * If game is paused, player can't move
	 * 
	 * @param event	The PlayerMoveEvent to cancel if game is paused
	 */
	@EventHandler
	public void onPauseEvent(PlayerMoveEvent event) {
		if (PartyTmp.getInstance().getGameState().getState() != States.PAUSED) {
			return ;
		}

		event.setCancelled(true);
	}
}
