package fr.kosmosuniverse.kuffle.listeners;

import java.util.HashMap;
import java.util.Map;

import fr.kosmosuniverse.kuffle.core.PartyTmp;
import fr.kosmosuniverse.kuffle.datamanagers.LangManager;
import fr.kosmosuniverse.kuffle.core.LogManager;
import fr.kosmosuniverse.kuffle.mode.Mode;
import fr.kosmosuniverse.kuffle.multiblock.AMultiblock;
import fr.kosmosuniverse.kuffle.multiblock.ActivationType;
import fr.kosmosuniverse.kuffle.multiblock.MultiblockManager;
import fr.kosmosuniverse.kuffle.states.States;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMove implements Listener {
	private static final String END_TELEPORTER = "EndTeleporter";
	private static final String OVER_TELEPORTER = "OverWorldTeleporter";
	
	private final Map<String, Long> sendMessage = new HashMap<>();
	
	@EventHandler
	public void onActivateMultiBlockEvent(PlayerMoveEvent event) {
		if (PartyTmp.getInstance().getGameState().getState() != States.RUNNING ||
				PartyTmp.getInstance().getGameMode().getMode() != Mode.BLOCKS) {
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
		
		if (!multiblock.getMultiblock().checkMultiBlock(player.getLocation().add(0, -1, 0))) {
			return ;
		}
		
		if (multiblock.getName().equals(OVER_TELEPORTER)) {
			activateOverworldTeleporter(player, multiblock);
		} else if (multiblock.getName().equals(END_TELEPORTER)) {
			activateEndTeleporter(player, multiblock);
		} else {
			multiblock.onActivate(player, ActivationType.ACTIVATE);
		}
	}
	
	private void activateOverworldTeleporter(Player player, AMultiblock multiblock) {
		int xpAmount = PartyTmp.getInstance().getGameManager().getXpActivables().get(OVER_TELEPORTER);
		
		if (player.getLevel() >= xpAmount) {
			player.setLevel(player.getLevel() - xpAmount);
			xpAmount = Math.max((xpAmount - 2), 2);
			PartyTmp.getInstance().getGameManager().getXpActivables().put(OVER_TELEPORTER, xpAmount);
			multiblock.onActivate(player, ActivationType.ACTIVATE);	
		} else {
			if (!sendMessage.containsKey(player.getName()) ||
					((sendMessage.get(player.getName()) - System.currentTimeMillis()) / 1000) > 3) {
				LogManager.getInstanceGame().writeMsg(player, LangManager.getMsgLang("XP_NEEDED", PartyTmp.getInstance().getGameManager().getPlayerLang(player.getName())).replace("<#>", String.valueOf(xpAmount)));
				sendMessage.put(player.getName(), System.currentTimeMillis());
			}
		}
	}
	
	private void activateEndTeleporter(Player player, AMultiblock multiblock) {
		int xpAmount = PartyTmp.getInstance().getGameManager().getXpActivables().get(END_TELEPORTER);
		
		if (player.getLevel() >= xpAmount) {
			player.setLevel(player.getLevel() - xpAmount);
			xpAmount = Math.max((xpAmount - 1), 1);
			PartyTmp.getInstance().getGameManager().getXpActivables().put(END_TELEPORTER, xpAmount);
			multiblock.onActivate(player, ActivationType.ACTIVATE);	
		} else {
			if (!sendMessage.containsKey(player.getName()) || ((System.currentTimeMillis() - sendMessage.get(player.getName())) / 1000) > 3) {
				LogManager.getInstanceGame().writeMsg(player, LangManager.getMsgLang("XP_NEEDED", PartyTmp.getInstance().getGameManager().getPlayerLang(player.getName())).replace("<#>", String.valueOf(xpAmount)));
				sendMessage.put(player.getName(), System.currentTimeMillis());
			}
		}
	}
	
	@EventHandler
	public void onCorePlacedEvent(BlockPlaceEvent event) {
		if (PartyTmp.getInstance().getGameState().getState() != States.RUNNING ||
				PartyTmp.getInstance().getGameMode().getMode() != Mode.BLOCKS) {
			return ;
		}
		
		Player player = event.getPlayer();
		Block block = event.getBlockPlaced();
		AMultiblock multiblock;
		
		multiblock = MultiblockManager.searchMultiBlockByCore(block.getType());
		
		if (multiblock != null && multiblock.getMultiblock().checkMultiBlock(block.getLocation())) {
			multiblock.onActivate(player, ActivationType.ASSEMBLE);
		}
	}
}
