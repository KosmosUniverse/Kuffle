package main.fr.kosmosuniverse.kuffle.core;

import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class Age {
	private String name;
	private int number;
	private ChatColor color;
	private Material box;
	
	/**
	 * Constructor
	 * 
	 * @param ageName		The Age name
	 * @param ageNumber		The Age number
	 * @param ageColor		The Age color as ChatColor object
	 * @param ageBox		The Age shulker box as Material object
	 */
	public Age(String ageName, int ageNumber, ChatColor ageColor, Material ageBox) {
		name = ageName;
		number = ageNumber;
		color = ageColor;
		box = ageBox;
	}
	
	/**
	 * Constructor
	 * 
	 * @param ageName		The Age name
	 * @param ageNumber		The Age number
	 * @param ageColor		The Age color as String
	 * @param ageBox		The Age shulker box as String
	 */
	public Age(String ageName, int ageNumber, String ageColor, String ageBox) {
		name = ageName;
		number = ageNumber;
		color = ChatColor.valueOf(ageColor);
		box = Material.matchMaterial(ageBox);
	}
	
	/**
	 * Gets Age name
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets Age number
	 * 
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}
	
	/**
	 * Gets Age color
	 * 
	 * @return the color
	 */
	public ChatColor getColor() {
		return color;
	}
	
	/**
	 * Gets Age box
	 * 
	 * @return the box
	 */
	public Material getBox() {
		return box;
	}
}
