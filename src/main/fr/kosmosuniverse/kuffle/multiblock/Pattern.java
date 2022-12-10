package main.fr.kosmosuniverse.kuffle.multiblock;

import org.bukkit.Material;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class Pattern {
	private Material m;
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
		m = patternMaterial;
		x = patternX;
		y = patternY;
		z = patternZ;
	}
	
	/**
	 * Gets the material
	 * 
	 * @return the Material
	 */
	public Material getMaterial() {
		return this.m;
	}
	
	/**
	 * Gets the X relative position
	 * 
	 * @return the X position
	 */
	public int getX() {
		return this.x;
	}
	
	/**
	 * Gets the Y relative position
	 * 
	 * @return the Y position
	 */
	public int getY() {
		return this.y;
	}
	
	/**
	 * Gets the Z relative position
	 * 
	 * @return the Z position
	 */
	public int getZ() {
		return this.z;
	}
	
	/**
	 * Sets the Material
	 * 
	 * @param patternMaterial	The material to set
	 */
	public void setMaterial(Material patternMaterial) {
		m = patternMaterial;
	}
	
	/**
	 * Sets the X relative position
	 * 
	 * @param x	The X relative position
	 */
	public void setX(int x) {
		this.x = x;
	}
	
	/**
	 * Sets the Y relative position
	 * 
	 * @param y	The Y relative position
	 */
	public void setY(int y) {
		this.y = y;
	}
	
	/**
	 * Sets the Z relative position
	 * 
	 * @param z	The Z relative position
	 */
	public void setZ(int z) {
		this.z = z;
	}
}
