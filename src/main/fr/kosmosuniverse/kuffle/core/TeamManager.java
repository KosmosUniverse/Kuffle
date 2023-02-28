package main.fr.kosmosuniverse.kuffle.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.utils.Utils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class TeamManager {
	private static TeamManager instance;
	private List<Team> teams;

	public static synchronized TeamManager getInstance() {
		if (instance == null) {
			instance = new TeamManager();
		}
		
		return instance;
	}
	
	/**
	 * Create Team with name
	 * 
	 * @param name	Team name
	 */
	public void createTeam(String name) {
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
	public void createTeam(String name, ChatColor color) {
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
				if (item.getName().equals(teamName)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Deletes a team by name
	 * 
	 * @param teamName	Team name to delete
	 */
	public void deleteTeam(String teamName) {
		if (teams != null) {
			for (Team item : teams) {
				if (item.getName().equals(teamName)) {
					item.getPlayers().clear();
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
	public void changeTeamColor(String teamName, ChatColor teamColor) {
		if (teams != null) {
			for (Team item : teams) {
				if (item.getName().equals(teamName)) {
					item.setColor(teamColor);
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
	public boolean affectPlayer(String teamName, Player player) {
		boolean ret = false;
		
		if (teams != null) {
			Optional<Team> team = teams.stream()
					.filter(t -> t.getName().equals(teamName))
					.findFirst();
			
			if (team.isPresent() && !team.get().hasPlayer(player.getName())) {
				team.get().addPlayer(player);
				ret = true;
			}
		}
		
		return ret;
	}
	
	/**
	 * Removes a specific player from a specific team
	 * 
	 * @param teamName	The team where the player will be removed
	 * @param player	The player to remove
	 */
	public void removePlayer(String teamName, Player player) {
		if (teams != null) {
			Optional<Team> team = teams.stream()
					.filter(t -> t.getName().equals(teamName))
					.findFirst();
			
			if (team.isPresent() && team.get().hasPlayer(player.getName())) {
				team.get().removePlayer(player);
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
	public boolean isInTeam(String player) {
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
	public List<String> getTeamColors() {
		List<String> teamColors = new ArrayList<>();
		
		if (teams != null) {
			for (Team item : teams)  {
				teamColors.add(item.getColor().name());
			}
		}
		
		return teamColors;
	}
	
	/**
	 * Gets the number of player in the biggest team
	 * 
	 * @return the player number
	 */
	public int getMaxTeamSize() {
		int max = 0;
		
		for (Team teamItem : teams) {
			max = teamItem.getPlayers().size() < max ? max : teamItem.getPlayers().size();
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
	public Team findTeamByPlayer(String player) {
		if (teams == null) {
			return null;
		}
	
		Optional<Team> item = teams.stream()
				.filter(team -> team.hasPlayer(player))
				.findFirst();
		
		return item.isPresent() ? item.get() : null;
	}
	
	/**
	 * Clears teams list
	 */
	public void clear() {
		if (teams != null) {
			teams.forEach(t -> t.getPlayers().clear());			
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
	public String printTeam(String teamName) {
		if (teams != null) {
			for (Team item : teams) {
				if (item.getName().equals(teamName)) {
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
	public String printTeams() {
		StringBuilder sb = new StringBuilder();
		
		if (teams != null && teams.size() != 0) {
			for (int cnt = 0; cnt < teams.size(); cnt++) {
				sb.append(teams.get(cnt).toString());
				
				if (cnt < teams.size() - 1) {
					sb.append("\n");
				}
			}
		} else {
			sb.append(LangManager.getMsgLang("NO_TEAM", Config.getLang()));
		}
		
		return sb.toString();
	}
	
	/**
	 * Gets JSON string of all teams
	 * 
	 * @param path	The path to the Kuffle plugin folder
	 */
	public void saveTeams(String path) {
		try (FileOutputStream fos = new FileOutputStream(path + File.separator + "Teams.k")) {
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			
			oos.writeInt(teams.size());
			
			for (Team team : teams) {
				oos.writeObject(team);
			}
			
			oos.flush();
			oos.close();
		} catch (IOException e) {
			Utils.logException(e);
		}
	}
	
	/**
	 * Loads Teams from JSONObject
	 * 
	 * @param path	The path to the Kuffle plugin folder
	 * 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ClassNotFoundException 
	 */
	public void loadTeams(String path) throws IOException, ClassNotFoundException {
		try (FileInputStream fos = new FileInputStream(path + File.separator + "Teams.k")) {
			ObjectInputStream ois = new ObjectInputStream(fos);
			
			if (teams == null) {
				teams = new ArrayList<>();
			}
			
			if (teams.size() != 0) {
				clear();
			}
			
			int size = ois.readInt();
			
			for (int i = 0; i < size; i++) {
				teams.add((Team) ois.readObject());
			}

			ois.close();
		}
	}
	
	/**
	 * Gets list of all teams
	 * 
	 * @return the teams list
	 */
	public List<Team> getTeams() {
		return teams;
	}
	
	/**
	 * Gets Team object from team name
	 * 
	 * @param name	The name of searched teams
	 * 
	 * @return Team object found, null if not found
	 */
	public Team getTeam(String name) {
		Optional<Team> item = teams.stream()
				.filter(team -> team.getName().equals(name))
				.findFirst();
		
		return item.isPresent() ? item.get() : null;
	}
	
	/**
	 * Checks if two players are in the same team
	 * 
	 * @param player1	The player based on the team search
	 * @param player2	The team is check for this player
	 * 
	 * @return True if both players are in the same team, False instead
	 */
	public boolean sameTeam(String player1, String player2) {
		Team team = findTeamByPlayer(player1);
		boolean ret = false;
		
		if (team != null && team.hasPlayer(player2)) {
			ret = true;
		}
		
		return ret;
	}
	
	/**
	 * Gets Teams name
	 * 
	 * @return a List of all teams name or null if teams is null
	 */
	public List<String> getTeamsName() {
		if (teams == null) {
			return Collections.emptyList();
		}
		
		return teams.stream()
				.map(team -> team.getName())
				.collect(Collectors.toList());
	}
}
