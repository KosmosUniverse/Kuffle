package fr.kosmosuniverse.kuffle.multiblock;

import java.util.*;

import fr.kosmosuniverse.kuffle.core.LangManager;
import fr.kosmosuniverse.kuffle.core.Party;
import fr.kosmosuniverse.kuffle.utils.ItemMaker;
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
	protected ItemStack item;
	protected World world = null;

	public Map<String, Inventory> createInventories(String mainInv) {
		Map<String, Inventory> invs = new HashMap<>();

		multiblock.getLevels().forEach(level -> invs.put(name + " Layer " + (level.getLevelNb() + 1), createLayerInventory(mainInv, level, multiblock.getLevels().size())));

		return invs;
	}

    protected void setupFirstRow(Inventory inv, String mainInv, String prevInv, String nextInv) {
		for (int i = 0; i < 9; i++) {
			if (i == 0) {
				inv.setItem(i, prevInv != null ? ItemMaker.newItem(ItemsUtils.getPreviousPane())
						.addTag("invname", prevInv)
						.getItem() : ItemMaker.newItem(ItemsUtils.getBackPane())
						.addTag("invname", mainInv)
						.getItem());
			} else if (i == 4) {
				inv.setItem(i, mainInv != null ? ItemMaker.newItem(Material.MAGENTA_STAINED_GLASS_PANE)
						.addName("Main Menu")
						.addTag("invname", mainInv)
						.getItem() : ItemsUtils.getLimitPane());
			} else if (i == 8) {
				inv.setItem(i, nextInv != null ? ItemMaker.newItem(ItemsUtils.getNextPane())
						.addTag("invname", nextInv)
						.getItem() : ItemsUtils.getLimitPane());
			} else {
				inv.setItem(i, ItemMaker.newItem(ItemsUtils.getLimitPane()).getItem());
			}
		}
	}

	private Inventory createLayerInventory(String mainInv, Level level, int maxLevels) {
		Inventory inv = Bukkit.createInventory(null, 36, name + " Layer " + (level.getLevelNb() + 1));
		List<Material> compose = level.getLevel();
		int composeCnt = 0;

		setupFirstRow(inv, mainInv, level.getLevelNb() != 0 ? (name + " Layer " + level.getLevelNb()) : null, level.getLevelNb() == (maxLevels - 1) ? null : (name + " Layer " + (level.getLevelNb() + 2)));

		for (int i = 9; i < 36; i++) {
			if ((i >= 12 && i <= 14) ||
					(i >= 21 && i <= 23) ||
					(i >= 30 && i <= 32)) {
				inv.setItem(i, compose.get(composeCnt) == Material.AIR ? ItemsUtils.getEmptyPane() : ItemMaker.newItem(compose.get(composeCnt)).getItem());
				composeCnt++;
			} else {
				inv.setItem(i, ItemMaker.newItem(ItemsUtils.getLimitPane()).getItem());
			}
		}

		return inv;
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
	 * Gets multi-block's core type
	 *
	 * @return the core type
	 */
	public Material getCore() {
		return multiblock.getCore();
	}
}
