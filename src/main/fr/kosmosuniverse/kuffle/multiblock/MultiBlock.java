package main.fr.kosmosuniverse.kuffle.multiblock;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class MultiBlock {
	private Material core;
	private List<Level> pattern;
	
	/**
	 * Constructor
	 * 
	 * @param mbCore	The multiblock core
	 */
	public MultiBlock(Material mbCore) {
		core = mbCore;
		pattern = new ArrayList<>();
	}
	
	/**
	 * Clears the multiblock Pattern
	 */
	public void clear() {
		pattern.forEach(level -> level.clear());
		pattern.clear();
	}
	
	/**
	 * Gets multiblock core
	 * 
	 * @return	The core as Material
	 */
	public Material getCore() {
		return core;
	}
	
	/**
	 * Adds a level to the multiblock pattern
	 * 
	 * @param l	The level to add
	 */
	public void addLevel(Level l) {
		pattern.add(l);
	}

	/**
	 * Checks if multiblock exists at the Location @coreLoc
	 * 
	 * @param corelLoc	The location center
	 * @param player	The player
	 * 
	 * @return True if the multiblock is fully formed at @coreLoc Location
	 */
	public boolean checkMultiBlock(Location corelLoc, Player player) {
		Location newLoc = new Location(corelLoc.getWorld(), corelLoc.getBlockX(), corelLoc.getBlockY(), corelLoc.getBlockZ());
		
		return (checkNorthSouth(newLoc, 1) || checkNorthSouth(newLoc, -1) || checkEastWest(newLoc, 1) || checkEastWest(newLoc, -1));
	}

	/**
	 * Checks if multiblock exists in NS direction
	 * 
	 * @param loc		The center
	 * @param direction	1 for NS and -1 for SN
	 * 
	 * @return True if multiblock is present, False instead
	 */
	public boolean checkNorthSouth(Location loc, double direction) {
		for (Level l : pattern) {
			if (!l.checkRowsNS(loc, direction))
				return false;
		}
		
		return true;
	}
	
	/**
	 * Checks if multiblock exists in NS direction
	 * 
	 * @param loc		The center
	 * @param direction	1 for EW and -1 for WE
	 * 
	 * @return True if multiblock is present, False instead
	 */
	public boolean checkEastWest(Location loc, double direction) {
		for (Level l : pattern) {
			if (!l.checkRowsEW(loc, direction))
				return false;
		}
		
		return true;
	}
	
	/**
	 * Spawn a multiblock at a @player Location
	 * 
	 * @param player	The player
	 */
	public void spawnMultiBlock(Player player) {
		Location newLoc = new Location(player.getLocation().getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY() + 20, player.getLocation().getBlockZ());

		newLoc.getBlock().setType(this.core);

		for (Level l : pattern) {
			l.spawnLevel(newLoc);
		}
	}
}
