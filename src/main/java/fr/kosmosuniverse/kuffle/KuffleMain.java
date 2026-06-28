package fr.kosmosuniverse.kuffle;

import fr.kosmosuniverse.kuffle.core.*;
import fr.kosmosuniverse.kuffle.datamanagers.LangManager;
import lombok.Getter;
import org.bukkit.Bukkit;
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

		loaded = PartyTmp.createParty(this);

		if (loaded) {
			LogManager.getInstanceSystem().logMsg(this.getName(), LangManager.getMsgLang("ON", Config.getLang()));
		} else {
			Bukkit.getPluginManager().disablePlugin(this);
		}
	}

	@Override
	public void onDisable() {
		if (loaded) {
			LogManager.getInstanceSystem().logMsg(this.getName(), LangManager.getMsgLang("OFF", Config.getLang()));
			
			PartyTmp.getInstance().clear();
		}
	}

}
