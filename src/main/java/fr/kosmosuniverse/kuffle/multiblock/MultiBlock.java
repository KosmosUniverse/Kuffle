package fr.kosmosuniverse.kuffle.multiblock;

import java.util.ArrayList;
import java.util.Collections;
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
	private final Material core;
	private final List<Level> pattern;
	
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
		pattern.forEach(Level::clear);
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
	 * Gets the multiblock levels
	 * 
	 * @return the pattern list that contains all multiblock levels
	 */
	public List<Level> getLevels() {
		return Collections.unmodifiableList(pattern);
	}

	/**
	 * Checks if multiblock exists at the Location @coreLoc
	 * 
	 * @param coreLoc	The location center
	 * 
	 * @return True if the multiblock is fully formed at @coreLoc Location
	 */
	public boolean checkMultiBlock(Location coreLoc) {
		Location newLoc = new Location(coreLoc.getWorld(), coreLoc.getBlockX(), coreLoc.getBlockY(), coreLoc.getBlockZ());
		
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
		return pattern.stream().allMatch(l -> l.checkRowsNS(loc, direction));
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
		return pattern.stream().allMatch(l -> l.checkRowsEW(loc, direction));
	}
	
	/**
	 * Spawn a multiblock at a @player Location
	 * 
	 * @param player	The player
	 */
	public void spawnMultiBlock(Player player) {
		Location newLoc = new Location(player.getLocation().getWorld(), player.getLocation().getBlockX(), player.getLocation().getBlockY() + 20, player.getLocation().getBlockZ());

		newLoc.getBlock().setType(this.core);
		pattern.forEach(l -> l.spawnLevel(newLoc));
	}
}
