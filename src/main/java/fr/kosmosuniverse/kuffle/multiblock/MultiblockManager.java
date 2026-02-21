package fr.kosmosuniverse.kuffle.multiblock;

import java.util.*;

import fr.kosmosuniverse.kuffle.core.Age;
import fr.kosmosuniverse.kuffle.core.AgeManager;
import fr.kosmosuniverse.kuffle.core.Config;
import fr.kosmosuniverse.kuffle.core.TargetManager;
import fr.kosmosuniverse.kuffle.utils.ItemMaker;
import fr.kosmosuniverse.kuffle.utils.ItemsUtils;
import fr.kosmosuniverse.kuffle.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class MultiblockManager {
	private static List<AMultiblock> multiblocks = null;
	private static final String MAIN_INV = "AllMultiBlocks";
	private static final Map<String, Inventory> invs = new HashMap<>();
	
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

		createInventories();
	}

	public static void clear() {
		multiblocks.clear();
		invs.clear();
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

		multiblocks.stream()
				.filter(multiblock -> multiblock instanceof Template)
				.forEach(template -> {
					invs.putAll(template.createInventories(MAIN_INV));
					invs.get(MAIN_INV).addItem(ItemMaker.newItem(template.getCore())
							.addName(template.getName())
							.addTag("invname", template.getName() + " Layer 1")
							.getItem());
				});
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
			invs.get(MAIN_INV).remove(tmp.get().getCore());
			invs.entrySet().removeIf(e -> e.getKey().contains(tmp.get().getName()));
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

		multiblocks.stream()
				.filter(multiblock -> multiblock instanceof Template)
				.filter(template -> template.getName().equals(age))
				.forEach(template -> {
					invs.putAll(template.createInventories(MAIN_INV));
					invs.get(MAIN_INV).addItem(ItemMaker.newItem(template.getCore())
							.addName(template.getName())
							.addTag("invname", template.getName() + " Layer 1")
							.getItem());
				});

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

	public static Inventory getMainInv() {
		return getInv(MAIN_INV);
	}

	public static Inventory getInv(String invName) {
		return invs.get(invName);
	}

	public static boolean hasInv(String invName) {
		return getInv(invName) != null;
	}

	private static void createInventories() {
		createMainInventory();

		multiblocks.forEach(multiblock -> invs.putAll(multiblock.createInventories(MAIN_INV)));
	}

	private static void createMainInventory() {
		Inventory inv = Bukkit.createInventory(null, Utils.getNbInventoryRows(multiblocks.size()) + 9, MAIN_INV);

		setupFirstRow(inv);

		multiblocks.forEach(multiblock -> inv.addItem(ItemMaker.newItem(multiblock.getCore())
				.addName(multiblock.getName())
				.addTag("invname", multiblock.getName() + " Layer 1")
				.getItem()));

		invs.put("AllMultiBlocks", inv);
	}

	private static void setupFirstRow(Inventory inv) {
		for (int i = 0; i < 9; i++) {
			if (i == 0) {
				inv.setItem(i, ItemMaker.newItem(ItemsUtils.getQuitPane()).getItem());
			} else {
				inv.setItem(i, ItemMaker.newItem(ItemsUtils.getLimitPane()).getItem());
			}
		}
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
}
