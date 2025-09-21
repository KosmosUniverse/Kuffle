package fr.kosmosuniverse.kuffle.core;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import fr.kosmosuniverse.kuffle.utils.CommandUtils;
import fr.kosmosuniverse.kuffle.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class TeamManager {
	private static TeamManager instance;
	private List<Team> teams = new ArrayList<>();

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
		teams.add(new Team(name, getCurrentColors()));
	}
	
	/**
	 * Create Team with name and color
	 * 
	 * @param name	Team name
	 * @param color	Team color
	 */
	public void createTeam(String name, ChatColor color) {
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
	 * Gets list of all colors that are currently used by teams
	 * 
	 * @return the color list
	 */
	private List<ChatColor> getCurrentColors() {
		return teams.stream()
				.map(Team::getColor)
				.collect(Collectors.toList());
	}
	
	/**
	 * gets team inventory
	 * 
	 * @param teamName	the team's to check for inventory
	 * 
	 * @return the team inventory
	 */
	public Inventory getTeamInventory(String teamName) {
		return teams.stream().filter(team -> team.getName().equals(teamName)).map(Team::getInv).findFirst().orElse(null);
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
	 * @param teamName The team in which the player will be added
	 * @param playerName   The player to add
	 */
	public void affectPlayer(String teamName, String playerName) {
		if (teams != null) {
			Optional<Team> team = teams.stream()
					.filter(t -> t.getName().equals(teamName))
					.findFirst();
			
			if (team.isPresent() && !team.get().hasPlayer(playerName)) {
				team.get().addPlayer(playerName);
			}
		}

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
				team.get().removePlayer(player.getName());
			}
		}
	}

	public boolean checkPlayerInTeams() {
		return Party.getInstance().getPlayers().getList().stream().allMatch(this::isInTeam);
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
	 * Gets a Team based on its players
	 *
	 * @param playerName The player to search in Team
	 *
	 * @return The Team that contains player or null is not found
	 */
	public Team getTeamByPlayer(String playerName) {
		return teams.stream().filter(team -> team.hasPlayer(playerName)).findAny().orElse(null);
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
			max = Math.max(teamItem.getPlayers().size(), max);
		}
		
		return max;
	}
	
	/**
	 * Setups teams inventory
	 */
	public void setupTeamsInv() {
		teams.forEach(Team::setupInv);
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
		
		return teams.stream()
				.filter(team -> team.hasPlayer(player))
				.findFirst().orElse(null);
	}
	
	/**
	 * Clears teams list
	 */
	public void clear() {
		if (teams != null) {
			teams.stream().filter(team -> team.getInv() != null).forEach(team -> team.getInv().clear());
			teams.forEach(team -> team.getPlayers().clear());
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
		return teams == null ? null : teams.stream().filter(team -> team.getName().equals(teamName)).map(Team::toString).findAny().orElse(null);
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
	 * @throws IOException 				classic stream exception
	 * @throws FileNotFoundException 	no team file found
	 * @throws ClassNotFoundException   read cast exception
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
		return teams.stream()
				.filter(team -> team.getName().equals(name))
				.findFirst().orElse(null);
	}
	
	/**
	 * Checks if two players are in the same team
	 * 
	 * @param player1	The player based on the team search
	 * @param player2	The team to check for this player
	 * 
	 * @return True if both players are in the same team, False instead
	 */
	public boolean notSameTeam(String player1, String player2) {
		Team team = findTeamByPlayer(player1);

		return team == null || !team.hasPlayer(player2);
	}

	public void loadTeamsConfig(Player player, String filePath) throws IOException {
		try (InputStream is = Files.newInputStream(Paths.get(filePath))) {
			String content = Utils.readFileContent(is);

			if (content.isEmpty()) {
				throw new IllegalArgumentException("file's empty");
			}

			JSONTokener tokenizer = new JSONTokener(content);
			JSONObject main = new JSONObject(tokenizer);

			for (String teamName : main.keySet()) {
				JSONObject teamObj = main.getJSONObject(teamName);

				if (!teamObj.has("color") || !teamObj.has("players")) {
					throw new IllegalArgumentException("invalid team");
				}

				ChatColor color = CommandUtils.checkTeamColor(player, teamObj.getString("color"));

				if (color == null) {
					throw new IllegalArgumentException("invalid color");
				}

				createTeam(teamName, color);

				JSONArray playersObj = teamObj.getJSONArray("players");

				for (AtomicInteger i = new AtomicInteger(0); i.get() < playersObj.length(); i.incrementAndGet()) {
					String playerName = playersObj.getString(i.get());

					if (Bukkit.getOnlinePlayers().stream().anyMatch(p -> p.getName().equals(playerName))) {
						affectPlayer(teamName, playerName);
					} else {
						LogManager.getInstanceSystem().writeMsg(player, "Player " + playerName + " is not connected");
					}
				}
			}
		}
	}

	public void saveTeamsConfig(String filePath) throws IOException {
		try (FileWriter fw = new FileWriter(filePath)) {
			JSONObject mainObj = new JSONObject();

			TeamManager.getInstance().getTeams().forEach(t -> {
				JSONObject teamObj = new JSONObject();
				JSONArray playerList = new JSONArray();

				t.getPlayers().forEach(playerList::put);

				teamObj.put("color", t.getColor().name());
				teamObj.put("players", playerList);

				mainObj.put(t.getName(), teamObj);
			});

			fw.write(mainObj.toString(4));
		}
	}
}
