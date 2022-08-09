package main.fr.kosmosuniverse.kuffle.core;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class ActionBar {
	private ActionBar() {
		throw new IllegalStateException("Utility class");
    }
	
	public static void sendMessage(String msg, Player player) {		
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(msg).create());
	}
	
	public static void sendRawTitle(String msg, Player player) {
		player.sendTitle(msg, null, 5, 10, 5);
	}
}