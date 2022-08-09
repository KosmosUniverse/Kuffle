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

public class ManageTeams {
	private List<Team> teams = null;

	public void createTeam(String name) {
		if (teams == null) {
			teams = new ArrayList<>();
		}
		
		teams.add(new Team(name));
	}
	
	public void createTeam(String name, ChatColor color) {
		if (teams == null) {
			teams = new ArrayList<>();
		}
		
		teams.add(new Team(name, color));
	}
	
	public boolean hasTeam(String team) {
		if (teams != null) {
			for (Team item : teams) {
				if (item.name.equals(team)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void deleteTeam(String name) {
		if (teams != null) {
			for (Team item : teams) {
				if (item.name.equals(name)) {
					item.players.clear();
					teams.remove(item);
					return ;
				}
			}
		}
	}
	
	public void changeTeamColor(String name, ChatColor color) {
		if (teams != null) {
			for (Team item : teams) {
				if (item.name.equals(name)) {
					item.color = color;
					return ;
				}
			}
		}
	}
	
	public void affectPlayer(String teamName, Player player) {
		if (teams != null) {
			for (Team item : teams) {
				if (item.name.equals(teamName)) {
					item.players.add(player);
					return ;
				}
			}
		}
	}
	
	public void removePlayer(String teamName, Player player) {
		if (teams != null) {
			for (Team item : teams) {
				if (item.name.equals(teamName)) {
					item.players.remove(player);
					return ;
				}
			}
		}
	}
	
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
	
	public List<String> getTeamColors() {
		List<String> teamColors = new ArrayList<>();
		
		if (teams != null) {
			for (Team item : teams)  {
				teamColors.add(item.color.name());
			}
		}
		
		return teamColors;
	}
	
	public int getMaxTeamSize() {
		int max = 0;
		
		for (Team teamItem : teams) {
			max = teamItem.players.size() < max ? max : teamItem.players.size();
		}
		
		return max;
	}
	
	public String findTeamByPlayer(String player) {
		if (teams != null) {
			for (Team teamItem : teams) {
				for (Player playerItem : teamItem.players) {
					if (playerItem.getDisplayName().equals(player)) {
						return teamItem.name;
					}
				}
			}
		}
		
		return null;
	}
	
	public void resetAll() {
		if (teams != null) {
			for (Team item : teams) {
				item.players.clear();
			}
			
			teams.clear();
			teams = null;
		}
	}
	
	public String printTeam(String teamName) {
		if (teams != null) {
			for (Team item : teams) {
				if (item.name.equals(teamName)) {
					return item.toString();
				}
			}
		}
		
		return null;
	}
	
	public String toString() {
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
	
	@SuppressWarnings("unchecked")
	public String saveTeams() {
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
	
	public void loadTeams(JSONObject global, Map<String, Game> games) {
		for (Object key : global.keySet()) {
			String name = key.toString();
			JSONObject tmp = (JSONObject) global.get(key);
			ChatColor color = Utils.findChatColor(tmp.get("color").toString());
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
	
	public List<Team> getTeams() {
		return teams;
	}
	
	public Team getTeam(String name) {
		for (Team item : teams) {
			if (item.name.equals(name)) {
				return item;
			}
		}
		
		return null;
	}
}
