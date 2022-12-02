package main.fr.kosmosuniverse.kuffle.multiblock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import main.fr.kosmosuniverse.kuffle.core.Age;
import main.fr.kosmosuniverse.kuffle.core.AgeManager;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.TargetManager;
import main.fr.kosmosuniverse.kuffle.utils.ItemUtils;
import main.fr.kosmosuniverse.kuffle.utils.Utils;
import net.md_5.bungee.api.ChatColor;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class MultiblockManager {
	private static List<AMultiblock> multiblocks = null;
	
	/**
	 * Constructor
	 */
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
	 * Gets multiblocks cores
	 * 
	 * @return a Map of Cores and the name of their multiblocks
	 */
	public static Map<Material, String> getCores() {
		Map<Material, String> cores = multiblocks.stream().collect(Collectors.toMap(AMultiblock::getType, AMultiblock::getName));
		
		return cores;
	}
	
	/**
	 * Checks if @name multiblock exists
	 * 
	 * @param name	The name to check
	 * 
	 * @return True if a multiblock exists with this name, False instead
	 */
	public static boolean hasMultiblock(String name) {
		return multiblocks.stream().anyMatch(m -> name.equals(m.getName()));
	}
	
	/**
	 * Clears @multiblocks list
	 */
	public static void clear() {
		multiblocks.forEach(mb -> {
			mb.multiblock.clear();
			mb.invs.clear();
		});
		
		multiblocks.clear();
	}
	
	/**
	 * Creates templates
	 */
	public static void createTemplates() {
		List<String> done = new ArrayList<>();
		List<Material> tmp = new ArrayList<>();
		
		for (int ageCnt = 0; ageCnt < Config.getLastAge().number; ageCnt++) {
			Age age = AgeManager.getAgeByNumber(ageCnt);
			
			for (int i = 0; i < Config.getSBTTAmount(); i++) {
				done.add(TargetManager.newSbtt(done, age.name));
			}
			
			for (String block : done) {
				tmp.add(Material.matchMaterial(block));
			}
			
			multiblocks.add(new Template(age.name, tmp));

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
		for (int ageCnt = 0; ageCnt < Config.getLastAge().number; ageCnt++) {
			Age age = AgeManager.getAgeByNumber(ageCnt);
			
			removeTemplate(age.name);
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
		Optional<AMultiblock> tmp = multiblocks.stream().filter(m -> core == m.getCore()).findFirst();
		
		return tmp.isPresent() ? tmp.get() : null;
	}
	
	/**
	 * Searches a multiblock by its name
	 * 
	 * @param name	The name to search for
	 * 
	 * @return The Multiblock
	 */
	public static AMultiblock searchMultiBlockByName(String name) {
		Optional<AMultiblock> tmp = multiblocks.stream().filter(m -> name.contains(m.getName())).findFirst();
		
		return tmp.isPresent() ? tmp.get() : null;
	}
	
	/**
	 * Searches a multiblock by its inventory name
	 * 
	 * @param invName	The inventory name to search for
	 * 
	 * @return The Multiblock
	 */
	public static AMultiblock searchMultiBlockByInventoryName(String invName) {
		Optional<AMultiblock> tmp = multiblocks.stream().filter(m -> invName.contains(m.getName())).findFirst();
		
		return tmp.isPresent() ? tmp.get() : null;
	}
	
	/**
	 * Searches a multiblock by its item type
	 * 
	 * @param item	The item type to search for
	 * 
	 * @return The Multiblock
	 */
	public static AMultiblock searchMultiBlockByItemType(Material item) {
		Optional<AMultiblock> tmp = multiblocks.stream().filter(m -> item == m.getType()).findFirst();
		
		return tmp.isPresent() ? tmp.get() : null;
	}
	
	/**
	 * Searches a multiblock by its item
	 * 
	 * @param item	The item to search for
	 * 
	 * @return The Multiblock
	 */
	public static AMultiblock searchMultiBlockByItem(ItemStack item) {
		Optional<AMultiblock> tmp = multiblocks.stream().filter(m -> ItemUtils.itemComparison(item, m.getItem())).findFirst();
		
		return tmp.isPresent() ? tmp.get() : null;
	}
}
