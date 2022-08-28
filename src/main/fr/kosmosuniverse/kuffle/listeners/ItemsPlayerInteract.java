package main.fr.kosmosuniverse.kuffle.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.CraftManager;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.Team;
import main.fr.kosmosuniverse.kuffle.core.TeamManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleEventNotUsableException;
import main.fr.kosmosuniverse.kuffle.utils.ItemUtils;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class ItemsPlayerInteract extends PlayerInteract {
	
	/**
	 * Constructor
	 */
	public ItemsPlayerInteract() {
		super();
	}
	
	/**
	 * Manages the behavior of player left click specific for Items Kuffle type
	 * 
	 * @param event	The PlayerInteractEvent
	 */
	@EventHandler
	public void onLeftClick(PlayerInteractEvent event) {
		try {
			if (onLeftClickGeneric(event)) {
				return ;
			}
		} catch (KuffleEventNotUsableException e) {
			return ;
		}
		
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		
		if (ItemUtils.itemComparison(item, CraftManager.findItemByName("EndTeleporter"), true, true, true)) {
			consumeItem(player, event.getHand());
			
			endTeleporter(player);
			
			return ;
		}
		
		if (ItemUtils.itemComparison(item, CraftManager.findItemByName("OverworldTeleporter"), true, true, true)) {
			consumeItem(player, event.getHand());
			
			overworldTeleporter(player);
			
			return ;
		}
				
		if (CraftManager.isTemplate(item)) {
			event.setCancelled(true);
			consumeItem(player, event.getHand());
			GameManager.playerFoundSBTT(player.getName());
			LogManager.getInstanceGame().logMsg(player.getName(), "just used " + item.getItemMeta().getDisplayName() + " !");
			
			return ;
		}
				
		if (GameManager.checkPlayerTarget(player.getName(), item)) {
			GameManager.playerFoundTarget(player.getName());
		}
	}
	
	/**
	 * Manages the EndTeleporter player teleportation
	 * 
	 * @param player	The player to teleport to End
	 */
	private void endTeleporter(Player player) {
		Location tmp = new Location(Bukkit.getWorld(Utils.findNormalWorld().getName() + "_the_end"), player.getLocation().getX() + 1000, 60.0, player.getLocation().getZ() + 1000);
		
		while (tmp.getBlock().getType() != Material.END_STONE) {
			tmp.add(10, 0, 10);
		}
		
		teleport(tmp, player, "Teleported to the End.");
		
		if (xpActivables.get("EndTeleporter") > 1) {
			xpActivables.put("EndTeleporter", xpActivables.get("EndTeleporter") - 1);
		}
	}

	/**
	 * Manages the EndTeleporter player teleportation
	 * 
	 * @param player	The player to teleport to Overworld
	 */
	private void overworldTeleporter(Player player) {
		Location tmp = new Location(Bukkit.getWorld(Utils.findNormalWorld().getName()), player.getLocation().getX() - 1000, 80.0, player.getLocation().getZ() - 1000);
		
		teleport(tmp, player, "Teleported to the Overworld.");
		
		if (xpActivables.get("OverworldTeleporter") > 1) {
			int tmpXp = xpActivables.get("OverworldTeleporter") - 2;
			xpActivables.put("OverworldTeleporter", tmpXp < 1 ? 1 : tmpXp);
		}
	}
	
	/**
	 * Teleport a player at a specific location
	 * 
	 * @param loc		The location to teleport the player
	 * @param player	The player to teleport
	 * @param msg		The msg to log at player teleportation
	 */
	private void teleport(Location loc, Player player, String msg) {
		loc.setY((double) loc.getWorld().getHighestBlockAt(loc).getY());
		
		if (Config.getTeam()) {
			Team team = TeamManager.findTeamByPlayer(player.getName());
			
			team.getPlayers().forEach((p) -> {
				p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 50, false, false, false));
				p.teleport(loc);
				p.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
				
				LogManager.getInstanceGame().logMsg(p.getName(), msg);
			});
		} else {
			player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 50, false, false, false));
			player.teleport(loc);
			player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
			
			LogManager.getInstanceGame().logMsg(player.getName(), msg);
		}
	}
}