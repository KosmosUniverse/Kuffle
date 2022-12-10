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
	private static KuffleMain instance = null;
	
	private KuffleType type = null;
	private GameLoop loop;
	
	private String version = null;
	
	private boolean paused = false;
	private boolean loaded = false;
	private boolean gameStarted = false;
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		reloadConfig();
		
		instance = this;
		version = this.getDescription().getVersion();
		
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
		return instance;
	}
	
	public KuffleType getType() {
		return type;
	}
	
	public GameLoop getGameLoop() {
		return loop;
	}
	
	public String getVersion() {
		return version;
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	public boolean isLoaded() {
		return loaded;
	}
	
	public boolean isStarted() {
		return gameStarted;
	}
	
	public void setType(KuffleType kuffleType) {
		type = kuffleType;
	}
	
	public void setGameLoop(GameLoop gameLoop) {
		loop = gameLoop;
	}
	
	public void setPaused(boolean gamePaused) {
		paused = gamePaused;
	}
	
	public void setStarted(boolean started) {
		gameStarted = started;
	}
}
