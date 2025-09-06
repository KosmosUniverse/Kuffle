package fr.kosmosuniverse.kuffle.multiblock;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.kosmosuniverse.kuffle.core.LangManager;
import fr.kosmosuniverse.kuffle.core.Party;
import fr.kosmosuniverse.kuffle.utils.ItemsUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import net.md_5.bungee.api.ChatColor;

/**
 * 
 * @author KosmosUniverse
 *
 */
@Getter
public abstract class AMultiblock {
	protected String name;
	protected int squareSize;
	protected MultiBlock multiblock;
	protected List<Inventory> invs = new ArrayList<>();
	protected ItemStack item;
	protected World world = null;
	
	/**
	 * Creates all multiblock inventories
	 */
	public void createInventories() {
		multiblock.getLevels().forEach(level -> invs.add(setupLayer(level, multiblock.getLevels().size())));
	}
	
	/**
	 * Setups an inventory layer based on level patterns
	 * 
	 * @param level		The level
	 * @param maxLevels	The max number of level in the multiblock
	 * 
	 * @return the inventory created for the layer
	 */
	private Inventory setupLayer(Level level, int maxLevels) {
		Inventory inv = Bukkit.createInventory(null, 27, ChatColor.BLACK + name + " Layer " + (level.getLevelNb() + 1));
		List<Material> compose = level.getLevel();
		int composeCnt = 0;
		
		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, level.getLevelNb() == 0 ? ItemsUtils.getBackPane() : ItemsUtils.getPreviousPane());
			} else {
				inv.setItem(i, findInvElem(i, level.getLevelNb(), maxLevels, compose, composeCnt));
			}
			
			if ((i >= 3 && i <= 5) || (i >= 12 && i <= 14) || (i >= 21 && i <= 23)) {
				composeCnt++;
			}
		}
		
		return inv;
	}
	
	/**
	 * Finds the appropriate item to put in the layer inventory depending on its position in the inventory
	 * 
	 * @param invCnt		the item position in the inventory
	 * @param levelNb		The level number
	 * @param maxLevels		The maximum of level for that multiblock
	 * @param compose		The level material list
	 * @param composeCnt	The compose counter
	 * 
	 * @return the appropriate item for this position in the inventory
	 */
	private ItemStack findInvElem(int invCnt, int levelNb, int maxLevels, List<Material> compose, int composeCnt) {
		ItemStack it;
		
		if (invCnt == 8) {
			it = levelNb == (maxLevels - 1) ? ItemsUtils.getLimitPane() : ItemsUtils.getNextPane();
		} else if ((invCnt >= 3 && invCnt <= 5) ||
				(invCnt >= 12 && invCnt <= 14) ||
				(invCnt >= 21 && invCnt <= 23)) {
			if (compose.get(composeCnt) == Material.AIR) {
				it = ItemsUtils.getEmptyPane();
			} else {
				it = new ItemStack(compose.get(composeCnt));
			}
		} else {
			it = ItemsUtils.getLimitPane();
		}
		
		return it;
	}
	
	/**
	 * create the location to teleport the player
	 * 
	 * @param player	The player to teleport
	 * 
	 * @return the location
	 */
	public abstract Location createLocation(Player player);
	
	/**
	 * Clears this object
	 */
	public void clear() {
		multiblock.clear();
		invs.clear();
	}
	
	/**
	 * Called when multiblock is assembled or activated
	 * 
	 * @param player	The player that triggered it
	 * @param type		The Activation type (Assemble or Activate)
	 */
	public void onActivate(Player player, ActivationType type) {
		if (type == ActivationType.ASSEMBLE) {
			player.sendMessage(LangManager.getMsgLang("CONSTRUCTED", Party.getInstance().getGames().getGames().get(player.getName()).getConfigLang()).replace("%s", name));
		} else if (type == ActivationType.ACTIVATE && world != null) {
			Location tmp = createLocation(player);
			
			if (tmp != null) {
				tmp.setY(Objects.requireNonNull(tmp.getWorld()).getHighestBlockAt(tmp).getY() + 2.0);
				
				player.teleport(tmp);
				player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);	
			}
		}
	}
	
	/**
	 * Get Inventory from current and clicked item
	 * 
	 * @param current	The current inventory
	 * @param item		The clicked item in the current inventory
	 * @param master	The multiblock master inventory
	 * @param first		True if @current is the first inventory
	 * 
	 * @return The inventory to display to player
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
			if (Objects.requireNonNull(item.getItemMeta()).getDisplayName().equals("<- Back")) {
				return (master);
			} else if (item.getItemMeta().getDisplayName().equals("<- Previous") && idx > 0) {
				idx -= 1;
				return (invs.get(idx));
			}
		}
		
		return null;
	}
	
	/**
	 * Sets @world to overlord
	 */
	public void findNormalWorld() {
		for (World w : Bukkit.getWorlds()) {
			if (!w.getName().contains("nether") && !w.getName().contains("the_end")) {
				world = w;
			}
		}
	}

	/**
	 * Gets multi-block's item type
	 *
	 * @return the item type
	 */
	public Material getType() {
		return item.getType();
	}

	/**
	 * Gets multi-block's core type
	 *
	 * @return the core type
	 */
	public Material getCore() {
		return multiblock.getCore();
	}
}
