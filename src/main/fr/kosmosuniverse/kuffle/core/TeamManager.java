package main.fr.kosmosuniverse.kuffle.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class TeamManager {
	private static List<Team> teams = null;

	/**
	 * Create Team with name
	 * 
	 * @param name	Team name
	 */
	public static void createTeam(String name) {
		if (teams == null) {
			teams = new ArrayList<>();
		}
		
		teams.add(new Team(name));
	}
	
	/**
	 * Create Team with name and color
	 * 
	 * @param name	Team name
	 * @param color	Team color
	 */
	public static void createTeam(String name, ChatColor color) {
		if (teams == null) {
			teams = new ArrayList<>();
		}
		
		teams.add(new Team(name, color));
	}
	
	/**
	 * Checks if Team named "name" exists
	 * 
	 * @param teamName	The team name to check
	 * 
	 * @return True if Team exists, False instead
	 */
	public boolean hasTeam(String teamName) {
		if (teams != null) {
			for (Team item : teams) {
				if (item.name.equals(teamName)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Deletes a team by name
	 * 
	 * @param name	Team name to delete
	 */
	public static void deleteTeam(String teamName) {
		if (teams != null) {
			for (Team item : teams) {
				if (item.name.equals(teamName)) {
					item.players.clear();
					teams.remove(item);
					break;
				}
			}
		}
	}
	
	/**
	 * Change Team color
	 * 
	 * @param teamName	The team concerned by color change
	 * @param teamColor	The new color to set
	 */
	public static void changeTeamColor(String teamName, ChatColor teamColor) {
		if (teams != null) {
			for (Team item : teams) {
				if (item.name.equals(teamName)) {
					item.color = teamColor;
					break;
				}
			}
		}
	}
	
	/**
	 * Adds a specific player to a specific team
	 * 
	 * @param teamName	The team in which the player will be added
	 * @param player	The player to add
	 */
	public static void affectPlayer(String teamName, Player player) {
		if (teams != null) {
			for (Team item : teams) {
				if (item.name.equals(teamName)) {
					item.players.add(player);
					break;
				}
			}
		}
	}
	
	/**
	 * Removes a specific player from a specific team
	 * 
	 * @param teamName	The team where the player will be removed
	 * @param player	The player to remove
	 */
	public static void removePlayer(String teamName, Player player) {
		if (teams != null) {
			for (Team item : teams) {
				if (item.name.equals(teamName)) {
					item.players.remove(player);
					break;
				}
			}
		}
	}
	
	/**
	 * Checks if a specific player is in a team
	 * 
	 * @param player	The player to check
	 * 
	 * @return True if the player is in a team, False instead
	 */
	public static boolean isInTeam(String player) {
		if (teams != null) {
			for (Team teamItem : teams) {
				if (teamItem.hasPlayer(player)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Gets used color list
	 * 
	 * @return the list of current team colors
	 */
	public static List<String> getTeamColors() {
		List<String> teamColors = new ArrayList<>();
		
		if (teams != null) {
			for (Team item : teams)  {
				teamColors.add(item.color.name());
			}
		}
		
		return teamColors;
	}
	
	/**
	 * Gets the number of player in the biggest team
	 * 
	 * @return the player number
	 */
	public static int getMaxTeamSize() {
		int max = 0;
		
		for (Team teamItem : teams) {
			max = teamItem.players.size() < max ? max : teamItem.players.size();
		}
		
		return max;
	}
	
	/**
	 * Gets Team that contains a specific player
	 * 
	 * @param player	The player to search for
	 * 
	 * @return The Team object that contains this player, null if player is not in a team
	 */
	public static Team findTeamByPlayer(String player) {
		if (teams != null) {
			for (Team teamItem : teams) {
				for (Player playerItem : teamItem.players) {
					if (playerItem.getDisplayName().equals(player)) {
						return teamItem;
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Clears teams list
	 */
	public static void clear() {
		if (teams != null) {
			teams.forEach((t) -> t.players.clear());			
			teams.clear();
		}
	}
	
	/**
	 * Gets the String representation of a specific Team
	 * 
	 * @param teamName	The team it gets the string
	 * 
	 * @return string that represent Team teamName, null if team not exists
	 */
	public static String printTeam(String teamName) {
		if (teams != null) {
			for (Team item : teams) {
				if (item.name.equals(teamName)) {
					return item.toString();
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Gets a String representation of all teams
	 * 
	 * @return string representation of all teams
	 */
	public static String printTeams() {
		StringBuilder sb = new StringBuilder();
		
		if (teams != null && teams.size() != 0) {
			for (int cnt = 0; cnt < teams.size(); cnt++) {
				sb.append(teams.get(cnt).toString());
				
				if (cnt < teams.size() - 1) {
					sb.append("\n");
				}
			}
		} else {
			sb.append(Utils.getLangString(null, "NO_TEAM"));
		}
		
		return sb.toString();
	}
	
	/**
	 * Gets JSON string of all teams
	 * 
	 * @return the JSON string
	 */
	@SuppressWarnings("unchecked")
	public static String saveTeams() {
		JSONObject global = new JSONObject();
		
		for (Team item : teams) {
			JSONObject tmp = new JSONObject();
			JSONArray players = new JSONArray();
			
			tmp.put("color", item.color.toString());
			
			for (Player p : item.players) {
				players.add(p.getName());
			}
			
			tmp.put("players", players);
			
			global.put(item.name, tmp);
		}
		
		return global.toString();
	}
	
	/**
	 * Loads Teams from JSONObject
	 * 
	 * @param global	JSONObject that represents previously saved teams
	 * @param games		map of currently playing players
	 */
	public static void loadTeams(JSONObject global, Map<String, Game> games) {
		for (Object key : global.keySet()) {
			String name = (String) key;
			JSONObject tmp = (JSONObject) global.get(key);
			ChatColor color = Utils.findChatColor((String) tmp.get("color"));
			JSONArray players = (JSONArray) tmp.get("players");
			
			createTeam(name, color);
			
			if (players != null) {
				for (Object obj : players) {

					Game tmpPlayer = games.get((String) obj);
					
					if (tmpPlayer == null) {
						KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, "PLAYER_NOT_EXISTS").replace("<#>", "<" + (String) obj + ">"));
					} else {
						Player p = tmpPlayer.getPlayer();
						affectPlayer(name, p);	
					}
				}
			}
		}
	}
	
	/**
	 * Gets list of all teams
	 * 
	 * @return the teams list
	 */
	public static List<Team> getTeams() {
		return teams;
	}
	
	/**
	 * Gets Team object from team name
	 * 
	 * @param name	The name of searched teams
	 * 
	 * @return Team object found, null if not found
	 */
	public static Team getTeam(String name) {
		for (Team item : teams) {
			if (item.name.equals(name)) {
				return item;
			}
		}
		
		return null;
	}
}