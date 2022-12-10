package main.fr.kosmosuniverse.kuffle.multiblock;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import net.md_5.bungee.api.ChatColor;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class EndTeleporter extends AMultiblock {
	/**
	 * Constructor
	 */
	public EndTeleporter() {
		name = "EndTeleporter";
		
		squareSize = 3;
		
		item = new ItemStack(Material.OBSIDIAN);
		ItemMeta itM = item.getItemMeta();
		itM.setDisplayName(name);
		item.setItemMeta(itM);
		
		multiblock = new MultiBlock(Material.OBSIDIAN);
		
		multiblock.addLevel(new Level(0, 3,
				new Pattern(Material.COAL_BLOCK, -1, 0, -1),
				new Pattern(Material.COAL_BLOCK, 0, 0, -1),
				new Pattern(Material.COAL_BLOCK, 1, 0, -1),
				new Pattern(Material.NETHER_BRICK_STAIRS, -1, 0, 0),
				new Pattern(Material.OBSIDIAN, 0, 0, 0),
				new Pattern(Material.NETHER_BRICK_STAIRS, 1, 0, 0),
				new Pattern(Material.NETHER_BRICK_STAIRS, -1, 0, 1),
				new Pattern(Material.NETHER_BRICK_STAIRS, 0, 0, 1),
				new Pattern(Material.NETHER_BRICK_STAIRS, 1, 0, 1)));
		
		multiblock.addLevel(new Level(1, 3,
				new Pattern(Material.IRON_BLOCK, -1, 1, -1),
				new Pattern(Material.RED_NETHER_BRICK_WALL, 0, 1, -1),
				new Pattern(Material.GOLD_BLOCK, 1, 1, -1),
				new Pattern(Material.STONE_BRICK_WALL, -1, 1, 0),
				new Pattern(Material.AIR, 0, 1, 0),
				new Pattern(Material.STONE_BRICK_WALL, 1, 1, 0),
				new Pattern(Material.AIR, -1, 1, 1),
				new Pattern(Material.AIR, 0, 1, 1),
				new Pattern(Material.AIR, 1, 1, 1)));
		
		multiblock.addLevel(new Level(2, 3,
				new Pattern(Material.AIR, -1, 2, -1),
				new Pattern(Material.DIAMOND_BLOCK, 0, 2, -1),
				new Pattern(Material.AIR, 1, 2, -1),
				new Pattern(Material.AIR, -1, 2, 0),
				new Pattern(Material.AIR, 0, 2, 0),
				new Pattern(Material.AIR, 1, 2, 0),
				new Pattern(Material.AIR, -1, 2, 1),
				new Pattern(Material.AIR, 0, 2, 1),
				new Pattern(Material.AIR, 1, 2, 1)));
		
		createInventories();
		findNormalWorld();
	}

	@Override
	public void onActivate(Player player, ActivationType type) {
		if (type == ActivationType.ASSEMBLE) {
			player.sendMessage(LangManager.getMsgLang("CONSTRUCTED", GameManager.getPlayerLang(player.getName())).replace("%s", name));
		} else if (type == ActivationType.ACTIVATE && world != null) {
			player.sendMessage(LangManager.getMsgLang("ACTIVATED", GameManager.getPlayerLang(player.getName())).replace("%s", name));
			player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 50, false, false, false));
			Location tmp = new Location(Bukkit.getWorld(world.getName() + "_the_end"), player.getLocation().getX() + 1000, 60.0, player.getLocation().getZ() + 1000);
			
			while (tmp.getBlock().getType() != Material.END_STONE) {
				tmp.add(10, 0, 10);
			}
			
			tmp.setY(tmp.getWorld().getHighestBlockAt(tmp).getY() + 2.0);
			
			player.teleport(tmp);
			player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);	
		}
	}

	@Override
	public void createInventories() {
		invs.add(setupLayer1());
		invs.add(setupLayer2());
		invs.add(setupLayer3());
	}
	
	/**
	 * Creates multiblock layer 1
	 * 
	 * @return the first layer
	 */
	private Inventory setupLayer1() {
		Inventory inv = Bukkit.createInventory(null, 27, ChatColor.BLACK + name + " Layer 1");
		
		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, new ItemStack(backPane));
			} else if (i == 8) {
				inv.setItem(i, new ItemStack(bluePane));
			} else if (i == 3 || i == 4 || i == 5) {
				inv.setItem(i, new ItemStack(Material.COAL_BLOCK));
			} else if (i == 12 || i == 14 || i == 21 || i == 22 || i == 23) {
				inv.setItem(i, new ItemStack(Material.NETHER_BRICK_STAIRS));
			} else if (i == 13) {
				inv.setItem(i, new ItemStack(Material.OBSIDIAN));
			} else {
				inv.setItem(i, new ItemStack(limePane));
			}
		}
		
		return inv;
	}
	
	/**
	 * Creates multiblock layer 2
	 * 
	 * @return the second layer
	 */
	private Inventory setupLayer2() {
		Inventory inv = Bukkit.createInventory(null, 27, ChatColor.BLACK + name + " Layer 2");
		
		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, new ItemStack(previousPane));
			} else if (i == 8) {
				inv.setItem(i, new ItemStack(bluePane));
			} else if (i == 3) {
				inv.setItem(i, new ItemStack(Material.IRON_BLOCK));
			} else if (i == 4) {
				inv.setItem(i, new ItemStack(Material.RED_NETHER_BRICK_WALL));
			} else if (i == 5) {
				inv.setItem(i, new ItemStack(Material.GOLD_BLOCK));
			} else if (i == 12 || i == 14) {
				inv.setItem(i, new ItemStack(Material.STONE_BRICK_WALL));
			} else if (i == 13 || i == 21 || i == 22 || i == 23) {
				inv.setItem(i, new ItemStack(grayPane));
			} else {
				inv.setItem(i, new ItemStack(limePane));
			}
		}
		
		return inv;
	}
	
	/**
	 * Creates multiblock layer 3
	 * 
	 * @return the third layer
	 */
	private Inventory setupLayer3() {
		Inventory inv = Bukkit.createInventory(null, 27, ChatColor.BLACK + name + " Layer 3");
		
		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, new ItemStack(previousPane));
			} else if (i == 4) {
				inv.setItem(i, new ItemStack(Material.DIAMOND_BLOCK));
			} else if (i == 3 || i == 5 || i == 12 || i == 13 || i == 14|| i == 21 || i == 22 || i == 23) {
				inv.setItem(i, new ItemStack(grayPane));
			} else {
				inv.setItem(i, new ItemStack(limePane));
			}
		}
		
		return inv;
	}
}
