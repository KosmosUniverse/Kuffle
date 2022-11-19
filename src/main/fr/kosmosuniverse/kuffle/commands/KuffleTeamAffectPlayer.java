package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.TeamManager;
import main.fr.kosmosuniverse.kuffle.type.KuffleType;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleTeamAffectPlayer implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		LogManager.getInstanceSystem().logMsg(player.getName(), LangManager.getMsgLang("CMD_PERF", Config.getLang()).replace("<#>", "<k-team-affect-player>"));
		
		if (!player.hasPermission("k-team-affect-player")) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_ALLOWED", Config.getLang()));
			return false;
		}
		
		if (KuffleMain.type.getType() == KuffleType.Type.NO_TYPE) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("KUFFLE_TYPE_NOT_CONFIG", Config.getLang()));
			return true;
		}
		
		if (KuffleMain.gameStarted && GameManager.getGames().size() > 0) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_LAUNCHED", Config.getLang()));
			return true;
		}
		
		if (args.length != 2) {
			return false;
		}
		
		if (!TeamManager.getInstance().hasTeam(args[0])) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_NOT_EXISTS", Config.getLang()).replace("<#>", "<" + args[0] + ">"));
			return true;
		}
		
		if (TeamManager.getInstance().getTeam(args[0]).players.size() == Config.getTeamSize()) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_FULL", Config.getLang()));
			return true;
		}
		
		if (!GameManager.hasPlayer(args[1])) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("PLAYER_NOT_IN_GAME", Config.getLang()));
			return true;
		}
		
		TeamManager.getInstance().affectPlayer(args[0], GameManager.getPlayer(args[1]));
		LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_ADD_PLAYER", Config.getLang()).replace("<#>", "<" + args[1] + ">").replace("<##>", "<" + args[0] + ">"));
		
		return true;
	}

}
