package main.fr.kosmosuniverse.kuffle.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.ActionBar;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameHolder;
import main.fr.kosmosuniverse.kuffle.core.GameLoop;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.ScoreManager;
import main.fr.kosmosuniverse.kuffle.core.TeamManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
import main.fr.kosmosuniverse.kuffle.type.KuffleType;
import main.fr.kosmosuniverse.kuffle.utils.CommandUtils;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleLoad implements CommandExecutor {
	private File dataFolder;
	private static final String GAME_FILE = "Game.k";
	
	/**
	 * Constructor
	 * 
	 * @param folder	The Kuffle plugin folder
	 */
	public KuffleLoad(File folder) {
		dataFolder = folder;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		Player player;
		
		try {
			player = CommandUtils.initCommand(sender, "k-load", false, true, false);
		} catch (KuffleCommandFalseException e1) {
			return false;
		}
		
		if (GameManager.getGames() != null && GameManager.getGames().size() != 0) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("LIST_NOT_EMPTY", Config.getLang()) + ".");
			return true;
		}
		
		if (Utils.fileExists(dataFolder.getPath(), GAME_FILE) && !loadGameFile(player)) {
			return true;
		}
		
		try {
			GameManager.loadPlayers(dataFolder.getPath());
		} catch (IOException | ClassNotFoundException e) {
			LogManager.getInstanceSystem().writeMsg(player, "Cannot load game, please contact an administrator.");
			Utils.logException(e);
			return true;
		}
		
		GameManager.updatePlayersHeads();
		
		if (Config.getTeam()) {
			try {
				TeamManager.getInstance().loadTeams(dataFolder.getPath());
			} catch (ClassNotFoundException | IOException e) {
				Utils.logException(e);
			}
		}
		
		KuffleMain.getInstance().setPaused(true);
		
		finalSetupAndCountdown();
		
		return true;
	}
	
	private boolean loadGameFile(Player player) {
		try (FileInputStream fis = new FileInputStream(dataFolder.getPath() + File.separator + GAME_FILE)) {
			ObjectInputStream ois = new ObjectInputStream(fis);
			GameHolder holder = (GameHolder) ois.readObject();
			ois.close();
			
			KuffleType.Type type = KuffleType.Type.valueOf(holder.getKuffleType());

			if (KuffleMain.getInstance().getType().getType() != type &&
					KuffleMain.getInstance().getType().getType() != KuffleType.Type.NO_TYPE) {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("WRONG_TYPE", Config.getLang()));
				return false;
			} else if (KuffleMain.getInstance().getType().getType() == KuffleType.Type.NO_TYPE) {
				KuffleSetType.changeKuffleType(player, type);	
			}
			
			Config.loadConfig(holder.getConfig());
			GameManager.loadRanks(holder.getRanks());
			KuffleMain.getInstance().getType().loadXpMax(holder.getXpMap());
			
			holder.clear();
		} catch (IOException | ClassNotFoundException | KuffleFileLoadException e) {
			Utils.logException(e);
		}
		
		return true;
	}
	
	private void finalSetupAndCountdown() {
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
			GameManager.applyToPlayers(game ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.RED + "5" + ChatColor.RESET, game.player)
			);
			
			if (Config.getSBTT()) {
				KuffleMain.getInstance().getType().setupSbtt();
			}
		}, 20);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> 
			GameManager.applyToPlayers(game -> {
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.GOLD + "4" + ChatColor.RESET, game.player);
				
				if (Config.getSaturation())
					game.player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 10, false, false, false));
			})
		, 40);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () ->
			GameManager.applyToPlayers(game ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.YELLOW + "3" + ChatColor.RESET, game.player)
			)
		, 60);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () ->
			GameManager.applyToPlayers(game ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.GREEN + "2" + ChatColor.RESET, game.player))
		, 80);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
			ScoreManager.setupPlayersScores();
				
			GameManager.applyToPlayers(game ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.BLUE + "1" + ChatColor.RESET, game.player));
		}, 100);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
			GameManager.applyToPlayers(game ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + "GO!" + ChatColor.RESET, game.player)
			);
			
			GameManager.updatePlayersHeads();
			
			if (KuffleMain.getInstance().getGameLoop() == null) {
				KuffleMain.getInstance().setGameLoop(new GameLoop());
			}
			
			KuffleMain.getInstance().getGameLoop().startRunnable();
			KuffleMain.getInstance().setStarted(true);
			KuffleMain.getInstance().setPaused(false);
			
		}, 120);
	}
}
