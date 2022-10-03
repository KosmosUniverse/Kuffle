package main.fr.kosmosuniverse.kuffle.commands;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.ActionBar;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.CraftManager;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.ScoreManager;
import main.fr.kosmosuniverse.kuffle.core.TeamManager;
import main.fr.kosmosuniverse.kuffle.type.KuffleType;
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
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		LogManager.getInstanceSystem().logMsg(player.getName(), LangManager.getMsgLang("CMD_PERF", Config.getLang()).replace("<#>", "<k-load>"));
		
		if (!player.hasPermission("k-load")) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_ALLOWED", Config.getLang()));
			return false;
		}
		
		if (KuffleMain.type.getType() == KuffleType.Type.NO_TYPE) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("KUFFLE_TYPE_NOT_CONFIG", Config.getLang()));
			return true;
		}
		
		if (KuffleMain.gameStarted) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_LAUNCHED", Config.getLang()));
			return true;
		} else if (GameManager.getGames().size() != 0) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("LIST_NOT_EMPTY", Config.getLang()) + ".");
			return true;
		}
		
		JSONParser parser = new JSONParser();
		JSONObject mainObject;
		
		if (Utils.fileExists(dataFolder.getPath(), GAME_FILE)) {
			try (FileReader reader = new FileReader(dataFolder.getPath() + File.separator + GAME_FILE)) {
				mainObject = (JSONObject) parser.parse(reader);
				Config.loadConfig((JSONObject) mainObject.get("config"));
				GameManager.loadRanks((JSONObject) mainObject.get("ranks"));
				KuffleMain.type.loadXpMax((JSONObject) mainObject.get("xpMax"));
				mainObject.clear();
			} catch (IOException | ParseException e) {
				Utils.logException(e);
			}
		}
		
		try {
			GameManager.loadPlayers(dataFolder.getPath());
		} catch (IOException | ParseException e) {
			Utils.logException(e);
		}
		
		GameManager.updatePlayersHeads();
		
		if (Config.getTeam()) {
			loadTeams(parser);
		}
		
		KuffleMain.paused = true;
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () -> {
			GameManager.applyToPlayers((game) ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.RED + "5" + ChatColor.RESET, game.player)
			);
			
			if (Config.getSBTT()) {
				CraftManager.setupCraftTemplates();
			}
		}, 20);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () -> {
			GameManager.applyToPlayers((game) -> {
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.GOLD + "4" + ChatColor.RESET, game.player);
				
				if (Config.getSaturation()) {
					game.player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 10, false, false, false));
				}
			});
		}, 40);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () ->
			GameManager.applyToPlayers((game) ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.YELLOW + "3" + ChatColor.RESET, game.player)
			)
		, 60);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () ->
			GameManager.applyToPlayers((game) ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.GREEN + "2" + ChatColor.RESET, game.player)
			)
		, 80);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () -> {
			ScoreManager.setupPlayersScores();
				
			GameManager.applyToPlayers((game) -> {
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.BLUE + "1" + ChatColor.RESET, game.player);
				//game.load();
			});
		}, 100);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () -> {
			GameManager.applyToPlayers((game) ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + "GO!" + ChatColor.RESET, game.player)
			);
			
			GameManager.updatePlayersHeads();
			KuffleMain.loop.startRunnable();
			KuffleMain.gameStarted = true;
			KuffleMain.paused = false;
			
		}, 120);
		
		return true;
	}
	
	/**
	 * Loads Teams from Teams file
	 * 
	 * @param parser	The JSONParser object that will parse Teams file
	 */
	private void loadTeams(JSONParser parser) {
		JSONObject mainObject;
		
		try (FileReader reader = new FileReader(dataFolder.getPath() + File.separator + "Teams.k")) {
			mainObject = (JSONObject) parser.parse(reader);
			TeamManager.loadTeams(mainObject, GameManager.getGames());
			mainObject.clear();
		} catch (IOException | ParseException e) {
			LogManager.getInstanceSystem().logSystemMsg(e.getMessage());
		}
	}
}
