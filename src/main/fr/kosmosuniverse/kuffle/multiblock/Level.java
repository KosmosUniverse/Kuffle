package main.fr.kosmosuniverse.kuffle.multiblock;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class Level {
	private double levelNb;
	private int length;
	private List<List<Pattern>> levelNS = new ArrayList<>();
	private List<List<Pattern>> levelEW = null;
	
	/**
	 * Constructor
	 * 
	 * @param lNb		The level
	 * @param lNb	The level size
	 * @param patterns	Pattern list
	 */
	public Level(double lNb, int levelLength, Pattern ... patterns) {
		levelNb = lNb;
		length = levelLength;
		List<Pattern> tmp = new ArrayList<>();
		int i = 0;
		
		for (Pattern p : patterns) {
			if (i == length) {
				i = 0;
				levelNS.add(new ArrayList<Pattern>(tmp));
				tmp.clear();
			}
			
			tmp.add(p);
			i++;
		}
		
		levelNS.add(new ArrayList<Pattern>(tmp));
		tmp.clear();
		
		if (length > 1) {
			levelEW = new ArrayList<>();
			turnLevel();
		}
	}
	
	/**
	 * Turn @levelNS and save it into @levelEW
	 */
	public void turnLevel() {
		List<Pattern> tmp = new ArrayList<>();
		
		for (List<Pattern> row: levelNS) {
			for (Pattern p : row) {
				tmp.add(new Pattern(p.getMaterial(), p.getX(), p.getY(), p.getZ()));
			}
			
			levelEW.add(new ArrayList<Pattern>(tmp));
			tmp.clear();
		}
		
		int i = 0;
		int j = 1;
		
		for (List<Pattern> row: levelEW) {
			for (Pattern p : row) {
				p.setMaterial(levelNS.get(i).get(length - j).getMaterial());
				i++;
			}
			
			i = 0;
			j++;
		}
	}
	
	/**
	 * Clears the level 
	 */
	public void clear() {
		levelNS.forEach(level -> level.clear());
		levelNS.clear();
		
		if (levelEW != null) {
			levelEW.forEach(level -> level.clear());
			levelEW.clear();
		}
	}

	/**
	 * Checks if level is present at this Location
	 * 
	 * @param loc		The center
	 * @param direction	1 for NS and -1 for SN
	 * 
	 * @return True if level is present, False instead
	 */
	public boolean checkRowsNS(Location loc, double direction) {
		Location tmp;
		
		for (List<Pattern> row : levelNS) {
			for (Pattern p : row) {
				tmp = loc.clone();
				tmp.add(p.getX() * direction, p.getY(), p.getZ() * direction);
				
				if (tmp.getBlock().getType() != p.getMaterial() &&
						(p.getMaterial() != Material.AIR ||
						(tmp.getBlock().getType() != Material.CAVE_AIR && tmp.getBlock().getType() != Material.VOID_AIR))) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Checks if level is present at this Location
	 * 
	 * @param loc		The center
	 * @param direction	1 for EW and -1 for WE
	 * 
	 * @return True if level is present, False instead
	 */
	public boolean checkRowsEW(Location loc, double direction) {
		if (levelEW == null) {
			return false;
		}
		
		Location tmp;
		
		for (List<Pattern> row : levelEW) {
			for (Pattern p : row) {
				tmp = loc.clone();
				tmp.add(p.getX() * direction, p.getY(), p.getZ() * direction);
				if (tmp.getBlock().getType() != p.getMaterial() &&
						(p.getMaterial() != Material.AIR ||
						(tmp.getBlock().getType() != Material.CAVE_AIR && tmp.getBlock().getType() != Material.VOID_AIR))) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Gets level index
	 * 
	 * @return the @levelNb
	 */
	public double getLevelNb() {
		return (levelNb);
	}
	
	/**
	 * Generate level at @loc Location
	 * 
	 * @param loc	The level center
	 */
	public void spawnLevel(Location loc) {
		levelNS.forEach(level -> {
			Location tmp = loc.clone();
			level.forEach(pattern -> {
				tmp.add(pattern.getX(), pattern.getY(), pattern.getZ());
				tmp.getBlock().setType(pattern.getMaterial());				
			});
		});
		
		if (levelEW != null) { 
			levelEW.forEach(level -> {
				Location tmp = loc.clone();
				level.forEach(pattern -> {
					tmp.add(pattern.getX(), pattern.getY(), pattern.getZ());
					tmp.getBlock().setType(pattern.getMaterial());				
				});
			});
		}
	}
}
