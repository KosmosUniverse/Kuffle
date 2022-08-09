package main.fr.kosmosuniverse.kuffle.core;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public class Age {
	public String name;
	public int number;
	public ChatColor color;
	public Material box;
	
	public Age(String ageName, int ageNumber, ChatColor ageColor, Material ageBox) {
		name = ageName;
		number = ageNumber;
		color = ageColor;
		box = ageBox;
	}
	
	public Age(String ageName, int ageNumber, String ageColor, String ageBox) {
		name = ageName;
		number = ageNumber;
		color = ChatColor.valueOf(ageColor);
		box = Material.matchMaterial(ageBox);
	}
}
