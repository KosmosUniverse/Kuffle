package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleConfigException;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleConfig extends AKuffleCommand {
	private static final String CONFIG_SET = "CONFIG_SET";
	private static final String CONFIG_NOT_SET = "CONFIG_NOT_SET";
	
	/**
	 * 
	 * @author KosmosUniverse
	 *
	 */
	private enum ResultType {
		GOOD,
		BAD,
		INVALID
	}
	
	public KuffleConfig() {
		super("k-config", null, null, null, null, false);
	}
	
	@Override
	public boolean runCommand() {
		if (args.length % 2 == 1) {
			return false;
		}
		
		LogManager.getInstanceSystem().logMsg(player.getName(), LangManager.getMsgLang("CMD_PERF", Config.getLang()).replace("<#>", "<k-config>"));
		
		if (args.length == 0) {
			player.sendMessage(Config.displayConfig());
			return true;
		}
		
		if (!player.hasPermission("k-op")) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_ALLOWED", Config.getLang()));
			return true;
		}
		
		String before = "";
		
		for (int i = 0; i < args.length; i++) {
			if (i % 2 == 0) {
				before = args[i].toUpperCase();
			} else {
				ResultType ret = invokeMethod(player, before, args[i]);
				
				if (ret == ResultType.GOOD) {
					LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang(CONFIG_SET, Config.getLang()).replace("[#]", before).replace("[##]", "[" + args[i] + "]"));	
				} else if (ret == ResultType.BAD) {
					LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang(CONFIG_NOT_SET, Config.getLang()).replace("[#]", before).replace("[##]", "[" + args[i] + "]"));
				}
			}
		}
		
		return true;
	}
	
	/**
	 * If key exists in config elements, execute setElem to set config value
	 * 
	 * @param player	player that call this config change
	 * @param before	Key that might exist in config keys
	 * @param current	Value to set for config {before} element
	 * 
	 * @return a ResultType
	 */
	private ResultType invokeMethod(Player player, String before, String current) {
		ResultType ret = ResultType.GOOD;
		
		if (!Config.hasKey(before)) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("KEY_NOT_REC", Config.getLang()).replace("[#]", "[" + before + "]"));
			ret = ResultType.INVALID;
		} else {
			try {
				Config.setElem(before, current);
			} catch (KuffleConfigException e) {
				player.sendMessage(e.getMessage());
				ret = ResultType.BAD;
			}
		}
		
		return ret;
	}
}
