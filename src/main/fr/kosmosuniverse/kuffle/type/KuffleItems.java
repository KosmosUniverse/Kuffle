package main.fr.kosmosuniverse.kuffle.type;

import java.util.List;
import java.util.Map;

import org.bukkit.plugin.java.JavaPlugin;

import main.fr.kosmosuniverse.kuffle.core.ItemManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.RewardManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
import main.fr.kosmosuniverse.kuffle.utils.FilesConformity;

public class KuffleItems extends KuffleType {
	public static Map<String, List<String>> allSbtts = null;
	
	public KuffleItems(JavaPlugin plugin) throws KuffleFileLoadException {
		super(plugin);
		setupTypeResources(plugin);
	}
	
	public void clear() {
		super.clear();
	}

	@Override
	protected void setupTypeResources(JavaPlugin plugin) throws KuffleFileLoadException  {
		if ((allMsgLangs = LangManager.getAllItemsLang(FilesConformity.getContent("items_msg_langs.json"), plugin.getDataFolder())) == null) {
			throw new KuffleFileLoadException("KO");
		}
		
		if ((allTargetLangs = LangManager.getAllItemsLang(FilesConformity.getContent("items_lang.json"), plugin.getDataFolder())) == null) {
			throw new KuffleFileLoadException("KO");
		}
		
		langs = LangManager.findAllLangs(allTargetLangs);
		
		if ((allTargets = ItemManager.getAllItems(ages, FilesConformity.getContent("items_%v.json"), plugin.getDataFolder())) == null) {
			throw new KuffleFileLoadException("KO");
		}
		
		allTargetInvs = ItemManager.getItemsInvs(allTargets);
		
		if ((allSbtts = ItemManager.getAllItems(ages, FilesConformity.getContent("sbtt_%v.json"), plugin.getDataFolder())) == null) {
			throw new KuffleFileLoadException("KO");
		}
		
		if ((allRewards = RewardManager.getAllRewards(ages, FilesConformity.getContent("items_rewards_%v.json"), plugin.getDataFolder())) == null) {
			throw new KuffleFileLoadException("KO");
		}
	}
	
	public final Map<String, List<String>> getSbtts() {
		return allSbtts;
	}
}
