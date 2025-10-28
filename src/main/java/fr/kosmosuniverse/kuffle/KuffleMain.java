package fr.kosmosuniverse.kuffle;

import fr.kosmosuniverse.kuffle.core.Config;
import fr.kosmosuniverse.kuffle.core.LangManager;
import fr.kosmosuniverse.kuffle.core.LogManager;
import fr.kosmosuniverse.kuffle.core.Party;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleMain extends JavaPlugin {
	@Getter
    private static KuffleMain instance = null;
	@Getter
    private String version = null;
	private boolean loaded = false;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		reloadConfig();
		
		instance = this;
		version = this.getDescription().getVersion();
		loaded = true;

		Party.getInstance();

		LogManager.getInstanceSystem().logMsg(this.getName(), LangManager.getMsgLang("ON", Config.getLang()));
	}

	@Override
	public void onDisable() {
		if (loaded) {
			LogManager.getInstanceSystem().logMsg(this.getName(), LangManager.getMsgLang("OFF", Config.getLang()));
			
			Party.getInstance().getType().clear();
		}
	}

}
