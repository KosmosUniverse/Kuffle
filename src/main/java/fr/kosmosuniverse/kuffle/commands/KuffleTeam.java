package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.KuffleMain;
import fr.kosmosuniverse.kuffle.core.Config;
import fr.kosmosuniverse.kuffle.core.LangManager;
import fr.kosmosuniverse.kuffle.core.LogManager;
import fr.kosmosuniverse.kuffle.core.TeamManager;
import fr.kosmosuniverse.kuffle.utils.Utils;

import java.io.File;
import java.io.IOException;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleTeam extends AKuffleCommand {
	public KuffleTeam() {
		super("k-team", null, false, 1, 1, true);
	}

	@Override
	public boolean runCommand() {
		if ("show".equals(args[0])) {
			LogManager.getInstanceSystem().writeMsg(player, TeamManager.getInstance().printTeams());
			return true;
		}

		if (!"load".equals(args[0]) && !"save".equals(args[0])) {
			return false;
		}

		if (!player.hasPermission("k-op")) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_ALLOWED", Config.getLang()));
			return true;
		}

		if ("load".equals(args[0])) {
			if (!TeamManager.getInstance().getTeams().isEmpty()) {
				LogManager.getInstanceSystem().writeMsg(player, "Teams are already created, please clean them before loading.");
				return true;
			}

			try {
				TeamManager.getInstance().loadTeamsConfig(player, KuffleMain.getInstance().getDataFolder() + File.separator + "teamconfig.json");
			} catch (IOException e) {
				Utils.logException(e);
			}
		} else if ("save".equals(args[0])) {
			if (TeamManager.getInstance().getTeams().isEmpty()) {
				LogManager.getInstanceSystem().writeMsg(player, "No teams to save, create some teams to save them.");
				return true;
			}

			try {
				TeamManager.getInstance().saveTeamsConfig(KuffleMain.getInstance().getDataFolder() + File.separator + "teamconfig.json");
			} catch (IOException e) {
				Utils.logException(e);
			}
		}

		return true;
	}
}
