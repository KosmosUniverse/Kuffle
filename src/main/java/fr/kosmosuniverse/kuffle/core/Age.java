package fr.kosmosuniverse.kuffle.core;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;

/**
 * 
 * @author KosmosUniverse
 *
 */
@Getter
public class Age {
	private final String name;
	private final int number;
	private final ChatColor color;
	private final Material box;
	
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
