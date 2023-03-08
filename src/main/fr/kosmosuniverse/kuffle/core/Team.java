package main.fr.kosmosuniverse.kuffle.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.utils.SerializeUtils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class Team implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private List<Player> players = new ArrayList<>();
	private String name;
	private ChatColor color;
	
	/**
	 * Constructor
	 * 
	 * @param teamName	The team name
	 */
	public Team(String teamName) {
		ChatColor[] colors = ChatColor.values();
		SecureRandom random = new SecureRandom();
		
		name = teamName;
		color = colors[random.nextInt(colors.length)];
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
		return players;
	}
	
	/**
	 * Gets the team Name
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the team color
	 * 
	 * @return color
	 */
	public ChatColor getColor() {
		return color;
	}
	
	/**
	 * Sets team color
	 * 
	 * @param teamColor	New team color
	 */
	public void setColor(ChatColor teamColor) {
		color = teamColor;
	}
	
	/**
	 * Adds a player to the team
	 * 
	 * @param player	The player to add
	 */
	public void addPlayer(Player player) {
		players.add(player);
	}
	
	/**
	 * Removes a player from the team
	 * 
	 * @param player	The player to remove
	 */
	public void removePlayer(Player player) {
		players.remove(player);
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
		
		sb.append("" + ChatColor.RESET);
		
		return sb.toString();
	}
	
	/**
	 * Defines what will be stored about this Team in the Teams save file
	 * 
	 * @param oStream	The ObjectOoutputStream
	 * 
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream oStream) throws IOException {
		oStream.writeUTF(name == null ? "$null$" : name);
		oStream.writeObject(color);
		oStream.writeInt(players.size());
		
		for (Player player : players) {
			oStream.writeUTF(player.getName());
		}
	}
	
	/**
	 * Read Team info from Teams input stream
	 * 
	 * @param iStream	The stream that contains all Team infos
	 * 
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private void readObject(ObjectInputStream iStream) throws ClassNotFoundException, IOException {
		name = SerializeUtils.readString(iStream);
		color = (ChatColor) iStream.readObject();
		
		int size = iStream.readInt();
		List<String> names = new ArrayList<>();
		players = new ArrayList<>();
		
		for (int i = 0; i < size; i++) {
			names.add(SerializeUtils.readString(iStream));
		}
		
		for (String pname : names) {
			if (GameManager.hasPlayer(pname)) {
				players.add(GameManager.getPlayer(pname));
			} else {
				LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang("PLAYER_NOT_EXISTS", Config.getLang()).replace("<#>", "<" + pname + ">"));
			}
		}
		
		names.clear();
	}
}
