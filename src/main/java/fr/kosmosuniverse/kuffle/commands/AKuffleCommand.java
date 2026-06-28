package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.core.*;
import fr.kosmosuniverse.kuffle.datamanagers.LangManager;
import fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import fr.kosmosuniverse.kuffle.mode.Mode;
import fr.kosmosuniverse.kuffle.states.States;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author KosmosUniverse
 *
 */
public abstract class AKuffleCommand implements CommandExecutor  {
	protected final String name;
	protected final boolean checkMode;
	protected final boolean checkStarted;
	protected final boolean checkArgs;
	protected final boolean checkTeamEnable;
	protected boolean hasMode = false;
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
		checkMode = typed != null;
		checkStarted = started != null;
		checkArgs = aMin != null && aMax != null;
		checkTeamEnable = team;
		
		if (checkMode) {
			hasMode = typed;
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
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String msg, String[] args) {
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
		
		if (checkMode && hasMode && PartyTmp.getInstance().getGameMode().getMode() == Mode.NO_MODE) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("KUFFLE_TYPE_NOT_CONFIG", Config.getLang()));
			return true;
		}
		
		if (checkStarted &&
				((isStarted && PartyTmp.getInstance().getGameState().getState() == States.NOT_RUNNING) ||
						(!isStarted && PartyTmp.getInstance().getGameState().getState() != States.NOT_RUNNING))) {
			if (!isStarted)
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
		} catch (KuffleCommandFalseException ignored) {
		}
		
		player = null;
		this.args = null;
		
		return ret;
	}

}
