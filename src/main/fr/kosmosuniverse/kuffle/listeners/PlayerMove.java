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
	private Map<String, Long> sendMessage = new HashMap<>();
	
	@EventHandler
	public void onActivateMultiBlockEvent(PlayerMoveEvent event) {
		if (!KuffleMain.gameStarted || KuffleMain.type.getType() != KuffleType.Type.BLOCKS) {
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
		
		if (multiblock.getMultiblock().checkMultiBlock(player.getLocation().add(0, -1, 0), player)) {
			if (multiblock.getName().equals("OverWorldTeleporter")) {
				int xpAmount = KuffleMain.type.getXpActivable("OverworldTeleporter");
				
				if (player.getLevel() >= xpAmount) {
					player.setLevel(player.getLevel() - xpAmount);
					xpAmount = (xpAmount - 2) < 2 ? 2 : (xpAmount - 2);
					KuffleMain.type.setXpActivable("OverworldTeleporter", xpAmount);
					multiblock.onActivate(player, ActivationType.ACTIVATE);	
				} else {
					if (!sendMessage.containsKey(player.getName()) || ((sendMessage.get(player.getName()) - sendMessage.get(player.getName())) / 1000) > 3) {
						LogManager.getInstanceGame().writeMsg(player, LangManager.getMsgLang("XP_NEEDED", GameManager.getPlayerLang(player.getName())).replace("%i", "" + xpAmount));
						sendMessage.put(player.getName(), System.currentTimeMillis());
					}
				}
			} else if (multiblock.getName().equals("EndTeleporter")) {
				int xpAmount = KuffleMain.type.getXpActivable("EndTeleporter");
				
				if (player.getLevel() >= xpAmount) {
					player.setLevel(player.getLevel() - xpAmount);
					xpAmount = (xpAmount - 1) < 1 ? 1 : (xpAmount - 1);
					KuffleMain.type.setXpActivable("EndTeleporter", xpAmount);
					multiblock.onActivate(player, ActivationType.ACTIVATE);	
				} else {
					if (!sendMessage.containsKey(player.getName()) || ((System.currentTimeMillis() - sendMessage.get(player.getName())) / 1000) > 3) {
						LogManager.getInstanceGame().writeMsg(player, LangManager.getMsgLang("XP_NEEDED", GameManager.getPlayerLang(player.getName())).replace("%i", "5"));
						sendMessage.put(player.getName(), System.currentTimeMillis());
					}
				}
			} else if (!multiblock.getName().equals("OverWorldTeleporter") && !multiblock.getName().equals("EndTeleporter")) {
				multiblock.onActivate(player, ActivationType.ACTIVATE);
			}
		}
	}
	
	@EventHandler
	public void onCorePlacedEvent(BlockPlaceEvent event) {
		if (!KuffleMain.gameStarted || KuffleMain.type.getType() != KuffleType.Type.BLOCKS) {
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
