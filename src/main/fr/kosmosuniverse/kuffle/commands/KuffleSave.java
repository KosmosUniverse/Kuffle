package main.fr.kosmosuniverse.kuffle.commands;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.CraftManager;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.ScoreManager;
import main.fr.kosmosuniverse.kuffle.core.TeamManager;
import main.fr.kosmosuniverse.kuffle.type.KuffleType;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleSave implements CommandExecutor {
	private File dataFolder;
	
	/**
	 * Constructor
	 * 
	 * @param folder	The Kuffle plugin folder
	 */
	public KuffleSave(File folder) {
		dataFolder = folder;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		LogManager.getInstanceSystem().logMsg(player.getName(), LangManager.getMsgLang("CMD_PERF", Config.getLang()).replace("<#>", "<k-save>"));
		
		if (!player.hasPermission("k-save")) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_ALLOWED", Config.getLang()));
			
			return false;
		}
		
		if (!KuffleMain.gameStarted) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_NOT_LAUNCHED", Config.getLang()));
			return false;
		}
		
		KuffleMain.paused = true;

		GameManager.savePlayers(dataFolder.getPath());
				
		if (Config.getTeam()) {
			try (FileWriter writer = new FileWriter(dataFolder.getPath() + File.separator + "Teams.k");) {				
				writer.write(TeamManager.saveTeams());
			} catch (IOException e) {
				LogManager.getInstanceSystem().logSystemMsg(e.getMessage());
			}
		}
		
		try (FileWriter writer = new FileWriter(dataFolder.getPath() + File.separator + "Games.k");) {				
			JSONObject global = new JSONObject();

			global.put("config", Config.saveConfig());
			global.put("ranks", GameManager.saveRanks());
			global.put("xpMax", KuffleMain.type.getPlayerInteract().saveXpMax());
			
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
		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_SAVED", Config.getLang()));
		
		return true;
	}
}
