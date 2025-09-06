package fr.kosmosuniverse.kuffle.multiblock;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

/**
 * 
 * @author KosmosUniverse
 *
 */
@Getter
@Setter
public class Pattern {
	private Material material;
	private int x;
	private int y;
	private int z;
	
	/**
	 * Constructor
	 * 
	 * @param patternMaterial	The material
	 * @param patternX			The X relative position
	 * @param patternY			The Y relative position
	 * @param patternZ			The Z relative position 
	 */
	public Pattern(Material patternMaterial, int patternX, int patternY, int patternZ) {
		material = patternMaterial;
		x = patternX;
		y = patternY;
		z = patternZ;
	}
}
