package fr.kosmosuniverse.kuffle.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import fr.kosmosuniverse.kuffle.utils.SerializeUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;

/**
 * 
 * @author KosmosUniverse
 *
 */
@Getter
public class Team implements Serializable {
	/**
	 * Serialize UUID
	 */
	private static final long serialVersionUID = 1L;
	
	private List<String> players = new ArrayList<>();
	@Setter
	private String name;
	@Setter
	private ChatColor color;
	@Setter
	private Inventory inv;
	
	/**
	 * Constructor
	 * 
	 * @param teamName	The team name
	 */
	public Team(String teamName, List<ChatColor> usedColors) {
		SecureRandom random = new SecureRandom();
		List<ChatColor> colors = new ArrayList<>();

		Arrays.asList(ChatColor.values()).forEach(c -> {
			if (!usedColors.contains(c)) {
				colors.add(c);
			}
		});
		
		name = teamName;
		color = colors.get(random.nextInt(colors.size()));
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
	 * Setups team inventory
	 */
	public void setupInv() {
		inv = Bukkit.createInventory(null, Config.getTeamInvSize() * 9, net.md_5.bungee.api.ChatColor.BLACK + name);
	}
	
	/**
	 * Checks if a specific player is in this team
	 * 
	 * @param player	The player to check
	 * 
	 * @return True if the player is in the team, False instead
	 */
	public boolean hasPlayer(String player)	{
		for (String item : players) {
			if (item.equals(player)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Adds a player to the team
	 * 
	 * @param playerName	The player to add
	 */
	public void addPlayer(String playerName) {
		players.add(playerName);
	}
	
	/**
	 * Removes a player from the team
	 * 
	 * @param playerName	The player to remove
	 */
	public void removePlayer(String playerName) {
		players.remove(playerName);
	}
	
	/**
	 * Gets the stringifies team
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(color).append(name);
		sb.append("\n  Color: ").append(color.name());
		sb.append("\n  Players:");
		if (players.size() == 0) {
			sb.append(" NONE.");
		} else {
			for (String item : players) {
				sb.append("\n    - ").append(item);
			}
		}
		
		sb.append(ChatColor.RESET);
		
		return sb.toString();
	}
	
	/**
	 * Defines what will be stored about this Team in the Teams save file
	 * 
	 * @param oStream	The ObjectOutputStream
	 * 
	 * @throws IOException classic stream exception
	 */
	private void writeObject(ObjectOutputStream oStream) throws IOException {
		oStream.writeUTF(name == null ? "$null$" : name);
		oStream.writeObject(color);

		if (Config.getTeam() && Config.getTeamInv()) {
			oStream.writeObject(inv);
		}

		oStream.writeInt(players.size());
		
		for (String player : players) {
			oStream.writeUTF(player);
		}
	}
	
	/**
	 * Read Team info from Teams input stream
	 * 
	 * @param iStream	The stream that contains all Team info
	 * 
	 * @throws ClassNotFoundException stream read cast exception
	 * @throws IOException	classic stream exception
	 */
	private void readObject(ObjectInputStream iStream) throws ClassNotFoundException, IOException {
		name = SerializeUtils.readString(iStream);
		color = (ChatColor) iStream.readObject();

		if (Config.getTeam() && Config.getTeamInv()) {
			inv = (Inventory) iStream.readObject();
		}

		int size = iStream.readInt();
		List<String> names = new ArrayList<>();
		players = new ArrayList<>();
		
		for (int i = 0; i < size; i++) {
			names.add(SerializeUtils.readString(iStream));
		}

		List<String> playersName = Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());

		for (String name : names) {
			if (playersName.contains(name) &&
					Party.getInstance().getPlayers().has(name)) {
				players.add(name);
			} else {
				LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang("PLAYER_NOT_EXISTS", Config.getLang()).replace("<#>", "<" + name + ">"));
			}
		}
		
		names.clear();
	}
}
