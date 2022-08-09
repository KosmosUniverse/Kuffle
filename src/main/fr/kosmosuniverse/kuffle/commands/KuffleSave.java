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
import main.fr.kosmosuniverse.kuffle.utils.Utils;

public class KuffleSave implements CommandExecutor {
	private File dataFolder;
	
	public KuffleSave(File folder) {
		dataFolder = folder;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		KuffleMain.systemLogs.logMsg(player.getName(), Utils.getLangString(player.getName(), "CMD_PERF").replace("<#>", "<ki-save>"));
		
		if (!player.hasPermission("ki-save")) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NOT_ALLOWED"));
			
			return false;
		}
		
		if (!KuffleMain.gameStarted) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "GAME_NOT_LAUNCHED"));
			return false;
		}
		
		KuffleMain.paused = true;
		
		for (String playerName : KuffleMain.games.keySet()) {
			try (FileWriter writer = new FileWriter(dataFolder.getPath() + File.separator + playerName + ".ki");) {				
				writer.write(KuffleMain.games.get(playerName).save());
			} catch (IOException e) {
				KuffleMain.systemLogs.logSystemMsg(e.getMessage());
			}
			
			KuffleMain.games.get(playerName).stop();
		}
		
		if (KuffleMain.config.getTeam()) {
			try (FileWriter writer = new FileWriter(dataFolder.getPath() + File.separator + "Teams.ki");) {				
				writer.write(KuffleMain.teams.saveTeams());
			} catch (IOException e) {
				KuffleMain.systemLogs.logSystemMsg(e.getMessage());
			}
		}
		
		try (FileWriter writer = new FileWriter(dataFolder.getPath() + File.separator + "Games.ki");) {				
			JSONObject global = new JSONObject();

			global.put("config", KuffleMain.config.saveConfig());
			global.put("ranks", saveRanks());
			global.put("xpMax", KuffleMain.playerInteract.saveXpMax());
			
			writer.write(global.toJSONString());
			
			global.clear();
		} catch (IOException e) {
			KuffleMain.systemLogs.logSystemMsg(e.getMessage());
		}
		
		Utils.removeTemplates();
		KuffleMain.scores.clear();
		KuffleMain.games.clear();
		KuffleMain.loop.kill();
		KuffleMain.paused = false;
		KuffleMain.gameStarted = false;
		KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "GAME_SAVED"));
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject saveRanks() {
		JSONObject rankObj = new JSONObject();
		
		for (String name : KuffleMain.playerRank.keySet()) {
			if ((!KuffleMain.config.getTeam() && KuffleMain.games.containsKey(name)) ||
			(KuffleMain.config.getTeam() && KuffleMain.teams.hasTeam(name))) {
				rankObj.put(name, KuffleMain.playerRank.get(name));
			}
		}
		
		return rankObj;
	}
}
