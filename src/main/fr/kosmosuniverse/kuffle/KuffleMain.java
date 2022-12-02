package main.fr.kosmosuniverse.kuffle;

import org.bukkit.plugin.java.JavaPlugin;

import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameLoop;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
import main.fr.kosmosuniverse.kuffle.type.KuffleType;
import main.fr.kosmosuniverse.kuffle.type.KuffleNoType;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleMain extends JavaPlugin {
	public static KuffleType type = null;
	private static KuffleMain current = null;
	public static String version = null;
	
	public static GameLoop loop;
	
	public static boolean paused = false;
	public static boolean loaded = false;
	public static boolean gameStarted = false;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		reloadConfig();
		
		version = this.getDescription().getVersion();
		current = this;
		
		try {
			type = new KuffleNoType(this);
		} catch (KuffleFileLoadException e) {
			Utils.logException(e);
			this.getPluginLoader().disablePlugin(this);
		}
		
		loaded = true;

		LogManager.getInstanceSystem().logMsg(this.getName(), LangManager.getMsgLang("ON", Config.getLang()));
	}

	@Override
	public void onDisable() {
		if (loaded) {
			LogManager.getInstanceSystem().logMsg(this.getName(), LangManager.getMsgLang("OFF", Config.getLang()));
			
			type.clear();
		}
	}
	
	public static KuffleMain getInstance() {
		return current;
	}
	
	public static String getVersion() {
		return version;
	}
}
