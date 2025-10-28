package fr.kosmosuniverse.kuffle.multiblock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import fr.kosmosuniverse.kuffle.core.Age;
import fr.kosmosuniverse.kuffle.core.AgeManager;
import fr.kosmosuniverse.kuffle.core.Config;
import fr.kosmosuniverse.kuffle.core.TargetManager;
import fr.kosmosuniverse.kuffle.utils.ItemsUtils;
import fr.kosmosuniverse.kuffle.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class MultiblockManager {
	private static List<AMultiblock> multiblocks = null;
	
	/**
	 * Private default constructor
	 */
	private MultiblockManager() {
		throw new IllegalStateException("");
	}
	
	public static void setup() {
		multiblocks = new ArrayList<>();
		
		multiblocks.add(new EndTeleporter());
		multiblocks.add(new OverWorldTeleporter());
	}
	
	/**
	 * Gets the multiblocks List
	 * 
	 * @return the @multiblocks List
	 */
	public static List<AMultiblock> getMultiblocks() {
		return Collections.unmodifiableList(multiblocks);
	}

    /**
	 * Creates templates
	 */
	public static void createTemplates() {
		List<String> done = new ArrayList<>();
		List<Material> tmp = new ArrayList<>();
		
		for (int ageCnt = 0; ageCnt < Config.getLastAge().getNumber(); ageCnt++) {
			Age age = AgeManager.getAgeByNumber(ageCnt);
			
			for (int i = 0; i < Config.getSBTTAmount(); i++) {
				done.add(TargetManager.newSbtt(done, age.getName()));
			}
			
			for (String block : done) {
				tmp.add(Material.matchMaterial(block));
			}
			
			multiblocks.add(new Template(age.getName(), tmp));

			tmp.clear();
			done.clear();
		}
	}
	
	/**
	 * Remove a Template based on its name
	 * 
	 * @param name	The name of template to remove
	 */
	public static void removeTemplate(String name) {
		if (!Config.getSBTT()) {
			return ;
		}
		
		Optional<AMultiblock> tmp = multiblocks.stream().filter(m -> name.equals(m.getName())).findFirst();
		
		if (tmp.isPresent()) {
			tmp.get().clear();
			multiblocks.remove(tmp.get());
		}
	}
	
	/**
	 * Reloads template for a specific @age
	 * 
	 * @param age	The age to reload
	 */
	public static void reloadTemplate(String age) {
		List<Material> compose = new ArrayList<>();
		List<String> done = new ArrayList<>();
		
		removeTemplate(age);
		
		for (int i = 0; i < Config.getSBTTAmount(); i++) {
			compose.add(Material.matchMaterial(TargetManager.newSbtt(done, age)));
		}
		
		multiblocks.add(new Template(age, compose));

		done.clear();
		compose.clear();
	}
	
	/**
	 * Removes all templates
	 */
	public static void removeTemplates() {
		if (!Config.getSBTT()) {
			return ;
		}
		for (int ageCnt = 0; ageCnt < Config.getLastAge().getNumber(); ageCnt++) {
			Age age = AgeManager.getAgeByNumber(ageCnt);
			
			removeTemplate(age.getName());
		}
	}
	
	/**
	 * Gets an inventory containing all multiblocks items
	 * 
	 * @return the inventory
	 */
	public static Inventory getMultiblocksInventories() {
		Inventory inv = Bukkit.createInventory(null, Utils.getNbInventoryRows(multiblocks.size()), ChatColor.BLACK + "AllMultiBlocks");
		int i = 0;
		
		for (AMultiblock mb : multiblocks) {
			inv.setItem(i, mb.getItem());
			i++;
		}
		
		return (inv);
	}
	
	/**
	 * Searches a multiblock by its core
	 * 
	 * @param core	The core to search for
	 * 
	 * @return The Multiblock
	 */
	public static AMultiblock searchMultiBlockByCore(Material core) {
		return multiblocks.stream().filter(m -> core == m.getCore()).findFirst().orElse(null);
	}
	
	/**
	 * Searches a multiblock by its name
	 * 
	 * @param name	The name to search for
	 * 
	 * @return The Multiblock
	 */
	public static AMultiblock searchMultiBlockByName(String name) {
		return multiblocks.stream().filter(m -> name.contains(m.getName())).findFirst().orElse(null);
	}
	
	/**
	 * Searches a multiblock by its inventory name
	 * 
	 * @param invName	The inventory name to search for
	 * 
	 * @return The Multiblock
	 */
	public static AMultiblock searchMultiBlockByInventoryName(String invName) {
		return multiblocks.stream().filter(m -> invName.contains(m.getName())).findFirst().orElse(null);
	}

	/**
	 * Searches a multiblock by its item
	 * 
	 * @param item	The item to search for
	 * 
	 * @return The Multiblock
	 */
	public static AMultiblock searchMultiBlockByItem(ItemStack item) {
		return multiblocks.stream().filter(m -> ItemsUtils.itemComparison(item, m.getItem())).findFirst().orElse(null);
	}
}
