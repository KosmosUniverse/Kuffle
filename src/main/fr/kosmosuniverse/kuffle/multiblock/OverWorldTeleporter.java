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
public class OverWorldTeleporter extends AMultiblock {
	/**
	 * Constructor
	 */
	public OverWorldTeleporter() {
		name = "OverWorldTeleporter";
		
		squareSize = 3;
		
		item = new ItemStack(Material.END_PORTAL_FRAME);
		ItemMeta itM = item.getItemMeta();
		itM.setDisplayName(name);
		item.setItemMeta(itM);
		
		multiblock = new MultiBlock(Material.END_PORTAL_FRAME);
		
		multiblock.addLevel(new Level(0, 3,
				new Pattern(Material.END_STONE_BRICKS, -1, 0, -1),
				new Pattern(Material.QUARTZ_PILLAR, 0, 0, -1),
				new Pattern(Material.END_STONE_BRICKS, 1, 0, -1),
				new Pattern(Material.PURPUR_STAIRS, -1, 0, 0),
				new Pattern(Material.END_PORTAL_FRAME, 0, 0, 0),
				new Pattern(Material.PURPUR_STAIRS, 1, 0, 0),
				new Pattern(Material.PURPUR_STAIRS, -1, 0, 1),
				new Pattern(Material.PURPUR_STAIRS, 0, 0, 1),
				new Pattern(Material.PURPUR_STAIRS, 1, 0, 1)));
		
		multiblock.addLevel(new Level(1, 3,
				new Pattern(Material.PURPUR_PILLAR, -1, 1, -1),
				new Pattern(Material.CHISELED_QUARTZ_BLOCK, 0, 1, -1),
				new Pattern(Material.PURPUR_PILLAR, 1, 1, -1),
				new Pattern(Material.END_ROD, -1, 1, 0),
				new Pattern(Material.AIR, 0, 1, 0),
				new Pattern(Material.END_ROD, 1, 1, 0),
				new Pattern(Material.AIR, -1, 1, 1),
				new Pattern(Material.AIR, 0, 1, 1),
				new Pattern(Material.AIR, 1, 1, 1)));
		
		multiblock.addLevel(new Level(2, 3,
				new Pattern(Material.AIR, -1, 2, -1),
				new Pattern(Material.PURPUR_PILLAR, 0, 2, -1),
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
			
			Location tmp = new Location(Bukkit.getWorld(world.getName()), player.getLocation().getX() - 1000, 80.0, player.getLocation().getZ() - 1000);
			
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
			} else if (i == 3 || i == 5) {
				inv.setItem(i, new ItemStack(Material.END_STONE_BRICKS));
			} else if (i == 4) {
				inv.setItem(i, new ItemStack(Material.QUARTZ_PILLAR));
			} else if (i == 12 || i == 14 || i == 21 || i == 22 || i == 23) {
				inv.setItem(i, new ItemStack(Material.PURPUR_STAIRS));
			} else if (i == 13) {
				inv.setItem(i, new ItemStack(Material.END_PORTAL_FRAME));
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
			} else if (i == 3 || i == 5) {
				inv.setItem(i, new ItemStack(Material.PURPUR_PILLAR));
			} else if (i == 4) {
				inv.setItem(i, new ItemStack(Material.CHISELED_QUARTZ_BLOCK));
			} else if (i == 12 || i == 14) {
				inv.setItem(i, new ItemStack(Material.END_ROD));
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
				inv.setItem(i, new ItemStack(Material.PURPUR_PILLAR));
			} else if (i == 3 || i == 5 || i == 12 || i == 13 || i == 14|| i == 21 || i == 22 || i == 23) {
				inv.setItem(i, new ItemStack(grayPane));
			} else {
				inv.setItem(i, new ItemStack(limePane));
			}
		}
		
		return inv;
	}
}
