package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import main.fr.kosmosuniverse.kuffle.type.KuffleType;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

/**
 *
 * @author KosmosUniverse
 *
 */
public abstract class AKuffleCommand implements CommandExecutor  {
	protected String name = null;
	protected boolean checkType = false;
	protected boolean checkStarted = false;
	protected boolean checkArgs = false;
	protected boolean checkTeamEnable = false;
	protected boolean isTyped = false;
	protected boolean isStarted = false;
	protected int argsMin = -1;
	protected int argsMax = -1;

	protected Player player = null;
	protected String[] args = null;
	
	protected abstract boolean runCommand() throws KuffleCommandFalseException;
	
	protected AKuffleCommand(String cmdName, Boolean typed,
			Boolean started, Integer aMin,
			Integer aMax, boolean team) {
		name = cmdName;
		checkType = typed != null;
		checkStarted = started != null;
		checkArgs = aMin != null && aMax != null;
		checkTeamEnable = team;
		
		if (checkType) {
			isTyped = typed;
		}
		
		if (checkStarted) {
			isStarted = started;
		}
		
		if (checkArgs) {
			argsMin = aMin;
			argsMax = aMax;
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		
		player = (Player) sender;
		this.args = args;

		LogManager.getInstanceSystem().logMsg(player.getName(), LangManager.getMsgLang("CMD_PERF", Config.getLang()).replace("<#>", "<" + name + ">"));

		if (!player.hasPermission(name)) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_ALLOWED", Config.getLang()));
			return true;
		}
		
		if (checkType && KuffleMain.getInstance().getType().getType() == KuffleType.Type.NO_TYPE) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("KUFFLE_TYPE_NOT_CONFIG", Config.getLang()));
			return true;
		}
		
		if (checkStarted && KuffleMain.getInstance().isStarted() != isStarted) {
			if (KuffleMain.getInstance().isStarted())
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_LAUNCHED", Config.getLang()));
			else
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_NOT_LAUNCHED", Config.getLang()));
			
			return true;
		}
		
		if (checkArgs && (args.length < argsMin || args.length > argsMax)) {
			return false;
		}
		
		if (checkTeamEnable && !Config.getTeam()) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_ENABLE", Config.getLang()));
			return true;
		}
		
		boolean ret = true;
		
		try {
			ret = runCommand();
		} catch (KuffleCommandFalseException e) {
			Utils.logException(e);
		}
		
		player = null;
		this.args = null;
		
		return ret;
	}

}
