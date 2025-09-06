package fr.kosmosuniverse.kuffle.commands;

import java.util.stream.Collectors;

import fr.kosmosuniverse.kuffle.core.Config;
import fr.kosmosuniverse.kuffle.core.LangManager;
import fr.kosmosuniverse.kuffle.core.LogManager;
import fr.kosmosuniverse.kuffle.core.Party;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class KuffleList extends AKuffleCommand {
	public KuffleList() {
		super("k-list", true, false, 0, 2, false);
	}

	@Override
	public boolean runCommand() {
		if (args.length == 0) {
			String str = Party.getInstance().getPlayers().getDisplayString();

			if (str.isEmpty()) {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NO_PLAYERS", Config.getLang()));
			} else {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("PLAYER_LIST", Config.getLang()) + " " + str);
			}

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
			Party.getInstance().getPlayers().clear();
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("LIST_RESET", Config.getLang()));
			
			return true;
		}
		
		return false;
	}
	
	private void addAllList(Player player) {
		int cnt = Party.getInstance().getPlayers().addPlayers(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
		
		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("ADDED_LIST", Config.getLang()).replace("%i", String.valueOf(cnt)));
	}
	
	private void addOneList(Player player, String playerName) {
		if (Party.getInstance().getPlayers().has(playerName)) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("PLAYER_ALREADY_LIST", Config.getLang()));
		} else if (Party.getInstance().getPlayers().addPlayer(playerName)) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("ADDED_ONE_LIST", Config.getLang()));
		} else {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("PLAYER_NOT_EXISTS", Config.getLang()).replace("<#>", playerName));
		}
	}
	
	private void removeList(Player player, String playerName) {
		if (Party.getInstance().getPlayers().removePlayer(playerName)) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("REMOVED_LIST", Config.getLang()));
		} else {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("PLAYER_NOT_IN_GAME", Config.getLang()));		
		}
	}
}
