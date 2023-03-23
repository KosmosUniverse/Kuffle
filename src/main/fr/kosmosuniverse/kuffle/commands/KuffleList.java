package main.fr.kosmosuniverse.kuffle.commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

public class KuffleList extends AKuffleCommand {
	public KuffleList() {
		super("k-list", true, false, 0, 2, false);
	}

	@Override
	public boolean runCommand() {
		if (args.length == 0) {
			GameManager.displayList(player);

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

	private boolean resetList(Player player, String firstArg) {
		if (firstArg.equals("reset")) {
			GameManager.resetList();
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("LIST_RESET", Config.getLang()));
			
			return true;
		}
		
		return false;
	}
	
	private void addAllList(Player player) {
		int cnt = GameManager.addPlayers(new ArrayList<>(Bukkit.getOnlinePlayers()));
		
		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("ADDED_LIST", Config.getLang()).replace("%i", "" + cnt));
	}
	
	private void addOneList(Player player, String playerName) {
		Player retComp;

		if ((retComp = Utils.searchPlayerByName(playerName)) != null) {
			GameManager.addPlayer(player, retComp);
		} else {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("PLAYER_NOT_EXISTS", Config.getLang()).replace("<#>", playerName));
		}
	}
	
	private void removeList(Player player, String playerName) {
		if (GameManager.removePlayer(playerName)) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("REMOVED_LIST", Config.getLang()));
		} else {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("PLAYER_NOT_IN_GAME", Config.getLang()));		
		}
	}
}
