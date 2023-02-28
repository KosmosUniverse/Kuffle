package main.fr.kosmosuniverse.kuffle.multiblock;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;

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
	public Location createLocation(Player player) {
		player.sendMessage(LangManager.getMsgLang("ACTIVATED", GameManager.getPlayerLang(player.getName())).replace("%s", name));
		player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 50, false, false, false));
		
		return new Location(Bukkit.getWorld(world.getName()), player.getLocation().getX() - 1000, 80.0, player.getLocation().getZ() - 1000);
	}
}
