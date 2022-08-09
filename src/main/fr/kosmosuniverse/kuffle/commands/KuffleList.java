package main.fr.kosmosuniverse.kuffle.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Game;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

public class KuffleList implements CommandExecutor {
	private static final String NO_PLAYERS = "NO_PLAYERS";
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;

		Player player = (Player) sender;

		KuffleMain.systemLogs.logMsg(player.getName(), Utils.getLangString(player.getName(), "CMD_PERF").replace("<#>", "<ki-list>"));

		if (!player.hasPermission("ki-list")) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NOT_ALLOWED"));

			return false;
		}
		
		if (args.length != 0 && KuffleMain.gameStarted) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "GAME_LAUNCHED"));
			return true;
		}

		if (args.length == 0) {
			displayList(player);

			return true;
		} else if (args.length == 1) {
			return resetList(player, args[0]);
		} else if (args.length != 2) {
			return false;
		}
		
		if (args[0].equals("add")) {
			if (args[1].equals("@a")) {
				addAllList(player);
			} else {
				addOneList(player, args[1]);
			}
		} else if (args[0].equals("remove")) {
			removeList(player, args[1]);
		}

		return true;
	}
	
	private void displayList(Player player) {
		if (KuffleMain.games.size() == 0) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), NO_PLAYERS));
		} else {
			StringBuilder sb = new StringBuilder();
			int i = 0;

			for (String playerName : KuffleMain.games.keySet()) {
				if (i == 0) {
					sb.append(playerName);
				} else {
					sb.append(", ").append(playerName);
				}

				i++;
			}

			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "PLAYER_LIST") + " " + sb.toString());
		}
	}

	private boolean resetList(Player player, String firstArg) {
		if (firstArg.equals("reset")) {
			if (KuffleMain.games.size() == 0) {
				KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), NO_PLAYERS));

				return false;
			}

			KuffleMain.games.clear();
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "LIST_RESET"));
			
			return true;
		}
		
		return false;
	}
	
	private void addAllList(Player player) {
		int cnt = 0;
		List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

		for (Player p : players) {
			if (!KuffleMain.games.containsKey(p.getName())) {
				KuffleMain.games.put(p.getName(), new Game(p));
				cnt++;
			}
		}

		KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "ADDED_LIST").replace("%i", "" + cnt));
	}
	
	private void addOneList(Player player, String playerName) {
		Player retComp;

		if ((retComp = searchPlayerByName(playerName)) != null) {
			if (!KuffleMain.games.containsKey(retComp.getName())) {
				KuffleMain.games.put(retComp.getName(), new Game(retComp));
				KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "ADDED_ONE_LIST"));
			} else {
				KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "PLAYER_ALREADY_LIST"));
			}
		} else {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "PLAYER_NOT_EXISTS").replace("<#>", playerName));
		}
	}
	
	private void removeList(Player player, String playerName) {
		if (KuffleMain.games.size() == 0) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), NO_PLAYERS));
		} else if (KuffleMain.games.containsKey(playerName)) {
			KuffleMain.games.remove(playerName);
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "REMOVED_LIST"));
		} else {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "PLAYER_NOT_IN_GAME"));		
		}
	}
	
	static Player searchPlayerByName(String name) {
		List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
		Player retPlayer = null;

		for (Player player : players) {
			if (player.getName().contains(name)) {
				retPlayer = player;
			}
		}

		players.clear();

		return retPlayer;
	}

}
