package fr.kosmosuniverse.kuffle.core;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class ActionBar {
	
	/**
	 * Private ActionBar constructor
	 * 
	 * @throws IllegalStateException Utility Class Constructor Exception
	 */
	private ActionBar() {
		throw new IllegalStateException("Utility class");
    }
	
	/**
	 * Sends an ActionBar message to a player 
	 * 
	 * @param msg		The message to send
	 * @param player	The player that will receive the message
	 */
	public static void sendMessage(String msg, Player player) {		
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(msg).create());
	}
	
	/**
	 * Sends a Title message to a player 
	 * 
	 * @param msg		The message to send
	 * @param player	The player that will receive the message
	 */
	public static void sendRawTitle(String msg, Player player) {
		player.sendTitle(msg, null, 5, 10, 5);
	}
}