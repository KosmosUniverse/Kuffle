package main.fr.kosmosuniverse.kuffle.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Team {
	public List<Player> players = new ArrayList<>();
	public String name;
	public ChatColor color;
	
	public Team(String teamName) {
		ChatColor[] colors = ChatColor.values();
		
		name = teamName;
		color = colors[ThreadLocalRandom.current().nextInt(colors.length)];
	}
	
	public Team(String teamName, ChatColor teamColor) {
		name = teamName;
		color = teamColor;
	}
	
	public boolean hasPlayer(String player)	{
		for (Player item : players) {
			if (item.getName().equals(player)) {
				return true;
			}
		}
		
		return false;
	}
	
	public List<String> getPlayersName() {
		List<String> names = new ArrayList<>();
		
		for (Player item : players) {
			names.add(item.getName());
		}
		
		return names;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(color + name);
		sb.append("\n  Color: " + color.name());
		sb.append("\n  Players:");
		if (players.size() == 0) {
			sb.append(" NONE.");
		} else {
			for (Player item : players) {
				sb.append("\n    - " + item.getName());
			}
		}
		
		sb.append("§r");
		
		return sb.toString();
	}
}
