package main.fr.kosmosuniverse.kuffle.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.multiblock.AMultiblock;
import main.fr.kosmosuniverse.kuffle.multiblock.ActivationType;
import main.fr.kosmosuniverse.kuffle.multiblock.MultiblockManager;
import main.fr.kosmosuniverse.kuffle.type.KuffleType;

public class PlayerMove implements Listener {
	private static final String END_TELEPORTER = "EndTeleporter";
	private static final String OVER_TELEPORTER = "OverWorldTeleporter";
	
	private Map<String, Long> sendMessage = new HashMap<>();
	
	@EventHandler
	public void onActivateMultiBlockEvent(PlayerMoveEvent event) {
		if (!KuffleMain.getInstance().isStarted() || KuffleMain.getInstance().getType().getType() != KuffleType.Type.BLOCKS) {
			return ;
		}
		
		Player player = event.getPlayer();
		AMultiblock multiblock;
		
		if (sendMessage.containsKey(player.getName()) && ((sendMessage.get(player.getName()) - System.currentTimeMillis()) / 1000) > 3) {
			sendMessage.remove(player.getName());
		}
		
		multiblock = MultiblockManager.searchMultiBlockByCore(player.getLocation().add(0, -1, 0).getBlock().getType());
		
		if (multiblock == null) {
			return ;
		}
		
		if (!multiblock.getMultiblock().checkMultiBlock(player.getLocation().add(0, -1, 0), player)) {
			return ;
		}
		
		if (multiblock.getName().equals(OVER_TELEPORTER)) {
			activateOverworldTeleporter(player, multiblock);
		} else if (multiblock.getName().equals(END_TELEPORTER)) {
			activateEndTeleporter(player, multiblock);
		} else if (!multiblock.getName().equals(OVER_TELEPORTER) && !multiblock.getName().equals(END_TELEPORTER)) {
			multiblock.onActivate(player, ActivationType.ACTIVATE);
		}
	}
	
	private void activateOverworldTeleporter(Player player, AMultiblock multiblock) {
		int xpAmount = KuffleMain.getInstance().getType().getXpActivable(OVER_TELEPORTER);
		
		if (player.getLevel() >= xpAmount) {
			player.setLevel(player.getLevel() - xpAmount);
			xpAmount = (xpAmount - 2) < 2 ? 2 : (xpAmount - 2);
			KuffleMain.getInstance().getType().setXpActivable(OVER_TELEPORTER, xpAmount);
			multiblock.onActivate(player, ActivationType.ACTIVATE);	
		} else {
			if (!sendMessage.containsKey(player.getName()) || ((sendMessage.get(player.getName()) - System.currentTimeMillis()) / 1000) > 3) {
				LogManager.getInstanceGame().writeMsg(player, LangManager.getMsgLang("XP_NEEDED", GameManager.getPlayerLang(player.getName())).replace("<#>", "" + xpAmount));
				sendMessage.put(player.getName(), System.currentTimeMillis());
			}
		}
	}
	
	private void activateEndTeleporter(Player player, AMultiblock multiblock) {
		int xpAmount = KuffleMain.getInstance().getType().getXpActivable(END_TELEPORTER);
		
		if (player.getLevel() >= xpAmount) {
			player.setLevel(player.getLevel() - xpAmount);
			xpAmount = (xpAmount - 1) < 1 ? 1 : (xpAmount - 1);
			KuffleMain.getInstance().getType().setXpActivable(END_TELEPORTER, xpAmount);
			multiblock.onActivate(player, ActivationType.ACTIVATE);	
		} else {
			if (!sendMessage.containsKey(player.getName()) || ((System.currentTimeMillis() - sendMessage.get(player.getName())) / 1000) > 3) {
				LogManager.getInstanceGame().writeMsg(player, LangManager.getMsgLang("XP_NEEDED", GameManager.getPlayerLang(player.getName())).replace("<#>", "" + xpAmount));
				sendMessage.put(player.getName(), System.currentTimeMillis());
			}
		}
	}
	
	@EventHandler
	public void onCorePlacedEvent(BlockPlaceEvent event) {
		if (!KuffleMain.getInstance().isStarted() || KuffleMain.getInstance().getType().getType() != KuffleType.Type.BLOCKS) {
			return ;
		}
		
		Player player = event.getPlayer();
		Block block = event.getBlockPlaced();
		AMultiblock multiblock;
		
		multiblock = MultiblockManager.searchMultiBlockByCore(block.getType());
		
		if (multiblock != null && multiblock.getMultiblock().checkMultiBlock(block.getLocation(), player)) {
			multiblock.onActivate(player, ActivationType.ASSEMBLE);
		}
	}
}
