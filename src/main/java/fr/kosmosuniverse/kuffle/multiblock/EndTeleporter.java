package fr.kosmosuniverse.kuffle.multiblock;

import fr.kosmosuniverse.kuffle.core.LangManager;
import fr.kosmosuniverse.kuffle.core.Party;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

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
		Objects.requireNonNull(itM).setDisplayName(name);
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

	public Location createLocation(Player player) {
		player.sendMessage(LangManager.getMsgLang("ACTIVATED", Party.getInstance().getGames().getGames().get(player.getName()).getConfigLang()).replace("%s", name));
		player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 50, false, false, false));
		Location tmp = new Location(Bukkit.getWorld(world.getName() + "_the_end"), player.getLocation().getX() + 1000, 60.0, player.getLocation().getZ() + 1000);
		
		while (tmp.getBlock().getType() != Material.END_STONE) {
			tmp.add(10, 0, 10);
		}
		
		return tmp;
	}
}
