package main.fr.kosmosuniverse.kuffle.multiblock;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * 
 * @author KosmosUniverse
 *
 */
public abstract class AMultiblock {
	protected String name;
	protected int squareSize;
	protected MultiBlock multiblock;
	protected List<Inventory> invs = new ArrayList<>();
	protected ItemStack item;
	protected World world = null;
	
	/**
	 * Called when multiblock is assemble or activated
	 * 
	 * @param player	The player that triggered it
	 * @param type		The Activation type (Assemble or Activate)
	 */
	public abstract void onActivate(Player player, ActivationType type);
	
	/**
	 * Creates multiblock's inventories
	 */
	public abstract void createInventories();
	
	/**
	 * CLears this object
	 */
	public void clear() {
		multiblock.clear();
		invs.clear();
	}
	
	/**
	 * Get Inventory from current and clicked item
	 * 
	 * @param current	The current inventory
	 * @param item		The clicked item in the current inventory
	 * @param master	The multiblock master inventory
	 * @param first		True if <current> is the first inventory
	 * 
	 * @return The inventory to diplay to player
	 */
	public Inventory getInventory(Inventory current, ItemStack item, Inventory master, boolean first) {
		int idx = -1;

		if (first) {
			return (invs.get(0));
		}
		
		for (Inventory inv : invs) {
			if (inv.equals(current)) {
				idx = invs.indexOf(inv);
				break;
			}
		}
		
		if (idx == -1) {
			return null;
		}
		
		if (item.getType() == Material.BLUE_STAINED_GLASS_PANE) {
			if (idx == invs.size() - 1) {
				return null;
			}
			idx += 1;
			return (invs.get(idx));
		} else if (item.getType() == Material.RED_STAINED_GLASS_PANE) {
			if (item.getItemMeta().getDisplayName().equals("<- Back")) {
				return (master);
			} else if (item.getItemMeta().getDisplayName().equals("<- Previous")) {
				if (idx > 0) {
					idx -= 1;
					return (invs.get(idx));
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Sets <world> to overworld
	 */
	public void findNormalWorld() {
		for (World w : Bukkit.getWorlds()) {
			if (!w.getName().contains("nether") && !w.getName().contains("the_end")) {
				world = w;
			}
		}
	}
	
	/**
	 * Gets multiblock's name
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets multiblock's item
	 * 
	 * @return the item
	 */
	public ItemStack getItem() {
		return item;
	}
	
	/**
	 * Gets multiblock's item type
	 * 
	 * @return the item type
	 */
	public Material getType() {
		return item.getType();
	}

	/**
	 * Gets multiblock's core type
	 * 
	 * @return the core type
	 */
	public Material getCore() {
		return multiblock.getCore();
	}
	
	/**
	 * Gets multiblock
	 * 
	 * @return the multiblock
	 */
	public MultiBlock getMultiblock() {
		return multiblock;
	}
}
