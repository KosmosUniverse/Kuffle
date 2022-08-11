package main.fr.kosmosuniverse.kuffle.core;

import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class Age {
	public String name;
	public int number;
	public ChatColor color;
	public Material box;
	
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
}
