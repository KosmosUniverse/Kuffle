package main.fr.kosmosuniverse.kuffle.listeners;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.CraftManager;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.Team;
import main.fr.kosmosuniverse.kuffle.core.TeamManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleEventNotUsableException;
import main.fr.kosmosuniverse.kuffle.type.KuffleType;
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
	 * @throws ClassNotFoundException 
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	@EventHandler
	public void onLeftClick(PlayerInteractEvent event) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		if (KuffleMain.type.getType() != KuffleType.Type.ITEMS) {
			return ;
		}
		
		try {
			if (onLeftClickGeneric(event)) {
				return ;
			}
		} catch (KuffleEventNotUsableException e) {
			return ;
		}
		
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		EquipmentSlot hand = event.getHand();
		
		if (hand != EquipmentSlot.HAND) {
			return ;
		}
		
		if (ItemUtils.itemComparison(item, CraftManager.findItemByName("EndTeleporter")) &&
				checkXp(player, KuffleMain.type.getXpActivable("EndTeleporter"))) {
			consumeItem(player, event.getHand());
			
			endTeleporter(player);
			
			return ;
		}
		
		if (ItemUtils.itemComparison(item, CraftManager.findItemByName("OverworldTeleporter")) &&
				checkXp(player, KuffleMain.type.getXpActivable("OverworldTeleporter"))) {
			consumeItem(player, event.getHand());
			
			overworldTeleporter(player);
			
			return ;
		}
				
		if (CraftManager.isTemplate(item)) {
			event.setCancelled(true);
			consumeItem(player, event.getHand());
			GameManager.playerFoundSBTT(player.getName());
			CraftManager.reloadTemplate(item.getItemMeta().getDisplayName());
			LogManager.getInstanceGame().logMsg(player.getName(), "just used " + item.getItemMeta().getDisplayName() + " !");
			
			return ;
		}
		
		if (GameManager.checkPlayerTarget(player.getName(), item)) {
			GameManager.playerFoundTarget(player.getName());
		}
	}
	
	/**
	 * Checks if player has enough xp levels
	 * 
	 * @param player	The player to check
	 * @param xpMin		The Xp minimum amount the player must have
	 * 
	 * @return True if the player has enough xp levels, False instead
	 */
	private boolean checkXp(Player player, int xpMin) {
		boolean ret = false;
		
		if (player.getLevel() < xpMin) {
			ret = false;
			player.sendMessage(LangManager.getMsgLang("XP_NEEDED", GameManager.getPlayerLang(player.getName())).replace("<#>", "" + xpMin));
		} else {
			ret = true;
			player.setLevel(player.getLevel() - xpMin);
		}
		
		return ret;
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
		
		teleport(tmp, player, LangManager.getMsgLang("TP_END", GameManager.getPlayerLang(player.getName())));
		
		int xpAmount = KuffleMain.type.getXpActivable("EndTeleporter");
		xpAmount = (xpAmount - 1) < 1 ? 1 : (xpAmount - 1);
		KuffleMain.type.setXpActivable("EndTeleporter", xpAmount);
		
	}

	/**
	 * Manages the EndTeleporter player teleportation
	 * 
	 * @param player	The player to teleport to Overworld
	 */
	private void overworldTeleporter(Player player) {
		Location tmp = new Location(Bukkit.getWorld(Utils.findNormalWorld().getName()), player.getLocation().getX() - 1000, 80.0, player.getLocation().getZ() - 1000);
		
		teleport(tmp, player, LangManager.getMsgLang("TP_OVERWORLD", GameManager.getPlayerLang(player.getName())));
		
		int xpAmount = KuffleMain.type.getXpActivable("OverworldTeleporter");
		xpAmount = (xpAmount - 2) < 2 ? 2 : (xpAmount - 2);
		KuffleMain.type.setXpActivable("OverworldTeleporter", xpAmount);
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
			Team team = TeamManager.getInstance().findTeamByPlayer(player.getName());
			
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