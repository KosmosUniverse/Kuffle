package fr.kosmosuniverse.kuffle.listeners;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

import fr.kosmosuniverse.kuffle.KuffleMain;
import fr.kosmosuniverse.kuffle.core.*;
import fr.kosmosuniverse.kuffle.datamanagers.crafts.CraftManager;
import fr.kosmosuniverse.kuffle.datamanagers.LangManager;
import fr.kosmosuniverse.kuffle.event.TargetFoundEvent;
import fr.kosmosuniverse.kuffle.exceptions.KuffleEventNotUsableException;
import fr.kosmosuniverse.kuffle.utils.ItemsUtils;
import fr.kosmosuniverse.kuffle.utils.Utils;
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

/**
 * 
 * @author KosmosUniverse
 *
 */
public class ItemsPlayerInteract extends PlayerInteract {
	private static final String END_TELEPORTER = "EndTeleporter";
	private static final String OVER_TELEPORTER = "OverworldTeleporter";
	
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
			if (onRightClickGeneric(event)) {
				return ;
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | ClassNotFoundException e) {
			Utils.logException(e);
			return ;
		} catch (KuffleEventNotUsableException e) {
			return;
		}
		
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		EquipmentSlot hand = event.getHand();
		
		if (hand != EquipmentSlot.HAND) {
			return ;
		}
		
		if (ItemsUtils.itemComparison(item, CraftManager.findItemByName(END_TELEPORTER)) &&
				checkXp(player, PartyTmp.getInstance().getGameManager().getXpActivables().get(END_TELEPORTER))) {
			endTeleporter(player);
			Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> consumeItem(player, item), 40);
			
			return ;
		}
		
		if (ItemsUtils.itemComparison(item, CraftManager.findItemByName(OVER_TELEPORTER)) &&
				checkXp(player, PartyTmp.getInstance().getGameManager().getXpActivables().get(OVER_TELEPORTER))) {
			overworldTeleporter(player);
			Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> consumeItem(player, item), 40);
			
			return ;
		}
				
		if (CraftManager.isTemplate(item)) {
			event.setCancelled(true);
			PartyTmp.getInstance().sbttFound(player);
			Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> consumeItem(player, item), 40);
			CraftManager.reloadTemplate(Objects.requireNonNull(Objects.requireNonNull(item).getItemMeta()).getDisplayName());
			LogManager.getInstanceGame().writeMsg(player, "just used " + item.getItemMeta().getDisplayName() + " !");
			
			return ;
		}

		if (item != null && PartyTmp.getInstance().checkPlayerTarget(player, item.getType().name())) {
			Bukkit.getPluginManager().callEvent(new TargetFoundEvent(player, item.getType().name()));
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
			player.sendMessage(LangManager.getMsgLang("XP_NEEDED", PartyTmp.getInstance().getGameManager().getPlayerLang(player.getName())).replace("<#>", String.valueOf(xpMin)));
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
		Location tmp = new Location(Bukkit.getWorld(Objects.requireNonNull(Utils.findNormalWorld()).getName() + "_the_end"), player.getLocation().getX() + 1000, 60.0, player.getLocation().getZ() + 1000);
		
		while (tmp.getBlock().getType() != Material.END_STONE) {
			tmp.add(10, 0, 10);
		}
		
		teleport(tmp, player, LangManager.getMsgLang("TP_END", PartyTmp.getInstance().getGameManager().getPlayerLang(player.getName())));
		
		int xpAmount = PartyTmp.getInstance().getGameManager().getXpActivables().get(END_TELEPORTER);
		xpAmount = Math.max((xpAmount - 1), 1);
		PartyTmp.getInstance().getGameManager().getXpActivables().put(END_TELEPORTER, xpAmount);
	}

	/**
	 * Manages the EndTeleporter player teleportation
	 * 
	 * @param player	The player to teleport to Overworld
	 */
	private void overworldTeleporter(Player player) {
		Location tmp = new Location(Bukkit.getWorld(Objects.requireNonNull(Utils.findNormalWorld()).getName()), player.getLocation().getX() - 1000, 80.0, player.getLocation().getZ() - 1000);
		
		teleport(tmp, player, LangManager.getMsgLang("TP_OVERWORLD", PartyTmp.getInstance().getGameManager().getPlayerLang(player.getName())));
		
		int xpAmount = PartyTmp.getInstance().getGameManager().getXpActivables().get(OVER_TELEPORTER);
		xpAmount = Math.max((xpAmount - 2), 2);
		PartyTmp.getInstance().getGameManager().getXpActivables().put(OVER_TELEPORTER, xpAmount);
	}
	
	/**
	 * Teleport a player at a specific location
	 * 
	 * @param loc		The location to teleport the player
	 * @param player	The player to teleport
	 * @param msg		The msg to log at player teleportation
	 */
	private void teleport(Location loc, Player player, String msg) {
		loc.setY(Objects.requireNonNull(loc.getWorld()).getHighestBlockAt(loc).getY());
		
		if (Config.getTeam()) {
			Team team = TeamManager.getInstance().findTeamByPlayer(player.getName());
			
			team.getPlayers().forEach(p -> {
				Objects.requireNonNull(Bukkit.getPlayer(p)).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 50, false, false, false));
				Objects.requireNonNull(Bukkit.getPlayer(p)).teleport(loc);
				Objects.requireNonNull(Bukkit.getPlayer(p)).removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
				
				LogManager.getInstanceGame().logMsg(p, msg);
			});
		} else {
			player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 50, false, false, false));
			player.teleport(loc);
			player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
			
			LogManager.getInstanceGame().logMsg(player.getName(), msg);
		}
	}
}