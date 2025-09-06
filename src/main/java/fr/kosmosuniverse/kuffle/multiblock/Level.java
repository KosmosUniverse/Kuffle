package fr.kosmosuniverse.kuffle.multiblock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 * 
 * @author KosmosUniverse
 *
 */
@Getter
public class Level {
	private final int levelNb;
	private final int length;
	private final List<List<Pattern>> levelNS = new ArrayList<>();
	private List<List<Pattern>> levelEW = null;
	
	/**
	 * Constructor
	 * 
	 * @param lNb			The level
	 * @param levelLength	The level size
	 * @param patterns		Pattern list
	 */
	public Level(int lNb, int levelLength, Pattern ... patterns) {
		levelNb = lNb;
		length = levelLength;
		List<Pattern> tmp = new ArrayList<>();
		int i = 0;
		
		for (Pattern p : patterns) {
			if (i == length) {
				i = 0;
				levelNS.add(new ArrayList<>(tmp));
				tmp.clear();
			}
			
			tmp.add(p);
			i++;
		}
		
		levelNS.add(new ArrayList<>(tmp));
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
			
			levelEW.add(new ArrayList<>(tmp));
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
		levelNS.forEach(List::clear);
		levelNS.clear();
		
		if (levelEW != null) {
			levelEW.forEach(List::clear);
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
						(tmp.getBlock().getType() != Material.CAVE_AIR &&
								tmp.getBlock().getType() != Material.VOID_AIR))) {
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
						(tmp.getBlock().getType() != Material.CAVE_AIR &&
								tmp.getBlock().getType() != Material.VOID_AIR))) {
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
	public int getLevelNb() {
		return (levelNb);
	}
	
	/**
	 * Gets Level Lists
	 * 
	 * @return levelNS list that contains level pattern
	 */
	public List<Material> getLevel() {
		List<Material> compose = new ArrayList<>();
		
		levelNS.forEach(rows -> rows.forEach(item -> compose.add(item.getMaterial())));
		
		return Collections.unmodifiableList(compose);
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
