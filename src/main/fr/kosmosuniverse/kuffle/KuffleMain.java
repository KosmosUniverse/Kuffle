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
	public static KuffleMain current;
	public static KuffleType type = null;
	
	public static GameLoop loop;
	
	public static boolean paused = false;
	public static boolean loaded = false;
	public static boolean gameStarted = false;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		reloadConfig();
		
		current = this;
		
		try {
			type = new KuffleNoType(this);
		} catch (KuffleFileLoadException e) {
			Utils.logException(e);
			this.getPluginLoader().disablePlugin(this);
		}
		
		loaded = true;
		current = this;

		LogManager.getInstanceSystem().logMsg(this.getName(), LangManager.getMsgLang("ON", Config.getLang()));
	}

	@Override
	public void onDisable() {
		if (loaded) {
			LogManager.getInstanceSystem().logMsg(this.getName(), LangManager.getMsgLang("OFF", Config.getLang()));
			
			type.clear();
		}
	}
}
