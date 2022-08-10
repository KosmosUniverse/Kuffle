package main.fr.kosmosuniverse.kuffle.type;

import org.bukkit.plugin.java.JavaPlugin;

import main.fr.kosmosuniverse.kuffle.core.TargetManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.RewardManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
import main.fr.kosmosuniverse.kuffle.utils.FilesConformity;

public class KuffleBlocks extends KuffleType {
	public KuffleBlocks(JavaPlugin plugin) throws KuffleFileLoadException {
		super(plugin);
		setupTypeResources(plugin);
	}
	
	public void clear() {
		super.clear();
	}

	@Override
	protected void setupTypeResources(JavaPlugin plugin) throws KuffleFileLoadException  {
		if ((allMsgLangs = LangManager.getAllItemsLang(FilesConformity.getContent("blocks_msg_langs.json"), plugin.getDataFolder())) == null) {
			throw new KuffleFileLoadException("KO");
		}
		
		if ((allTargetLangs = LangManager.getAllItemsLang(FilesConformity.getContent("blocks_lang.json"), plugin.getDataFolder())) == null) {
			throw new KuffleFileLoadException("KO");
		}
		
		langs = LangManager.findAllLangs(allTargetLangs);
		
		if ((allTargets = TargetManager.getAllItems(ages, FilesConformity.getContent("blocks_%v.json"), plugin.getDataFolder())) == null) {
			throw new KuffleFileLoadException("KO");
		}
		
		allTargetInvs = TargetManager.getItemsInvs(allTargets);
				
		if ((allRewards = RewardManager.getAllRewards(ages, FilesConformity.getContent("blocks_rewards_%v.json"), plugin.getDataFolder())) == null) {
			throw new KuffleFileLoadException("KO");
		}
	}
}
