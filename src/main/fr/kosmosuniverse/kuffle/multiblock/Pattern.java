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
	 * @param _m	The material
	 * @param _x	The X relative position
	 * @param _y	The Y relative position
	 * @param _z	The Z relative position 
	 */
	public Pattern(Material _m, int _x, int _y, int _z) {
		m = _m;
		x = _x;
		y = _y;
		z = _z;
	}
	
	/**
	 * Gets the material
	 * 
	 * @return the Material <m>
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
	 * @param _m	The material to set
	 */
	public void setMaterial(Material _m) {
		m = _m;
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
