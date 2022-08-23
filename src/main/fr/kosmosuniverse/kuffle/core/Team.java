package main.fr.kosmosuniverse.kuffle.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class Team {
	public List<Player> players = new ArrayList<>();
	public String name;
	public ChatColor color;
	
	/**
	 * Constructor
	 * 
	 * @param teamName	The team name
	 */
	public Team(String teamName) {
		ChatColor[] colors = ChatColor.values();
		
		name = teamName;
		color = colors[ThreadLocalRandom.current().nextInt(colors.length)];
	}
	
	/**
	 * Constructor
	 * 
	 * @param teamName	The team name
	 * @param teamColor	The team color
	 */
	public Team(String teamName, ChatColor teamColor) {
		name = teamName;
		color = teamColor;
	}
	
	/**
	 * Checks if a specific player is in this team
	 * 
	 * @param player	The player to check
	 * 
	 * @return True if the player is in the team, False instead
	 */
	public boolean hasPlayer(String player)	{
		for (Player item : players) {
			if (item.getName().equals(player)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Get the team players name list
	 * 
	 * @return the players name list
	 */
	public List<String> getPlayersName() {
		List<String> names = new ArrayList<>();
		
		for (Player item : players) {
			names.add(item.getName());
		}
		
		return names;
	}
	
	/**
	 * Gets team players list
	 * 
	 * @return the players list
	 */
	public List<Player> getPlayers() {
		return Collections.unmodifiableList(players);
	}
	
	/**
	 * Gets the stringified team
	 */
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
