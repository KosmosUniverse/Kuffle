package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.ScoreManager;
import main.fr.kosmosuniverse.kuffle.core.TeamManager;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleAddDuringGame implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;

		Player player = (Player) sender;

		LogManager.getInstanceSystem().logMsg(player.getName(), LangManager.getMsgLang("CMD_PERF", Config.getLang()).replace("<#>", "<k-add-during-game>"));

		if (!player.hasPermission("k-add-during-game")) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_ALLOWED", Config.getLang()));
			return false;
		}

		if (!KuffleMain.gameStarted) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_NOT_LAUNCHED", Config.getLang()));			
			return true;
		}
		
		if (args.length == 0 || args.length > 2) {
			return false;
		}

		Player retPlayer;

		if ((retPlayer = Utils.searchPlayerByName(args[0])) == null) {
			return true;
		}

		if (Config.getTeam() && args.length == 2) {
			if (!TeamManager.getInstance().hasTeam(args[1])) {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_NOT_EXISTS", Config.getLang()).replace("<#>", "<" + args[1] + ">"));
				return true;
			} else if (TeamManager.getInstance().getTeam(args[1]).players.size() == Config.getTeamSize()) {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_FULL", Config.getLang()));
				return true;
			}

			startPlayer(player, retPlayer, args[1]);
		} else if (args.length == 1 && !Config.getTeam()) {
			startPlayer(player, retPlayer, null);
		} else {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("TEAM_PREVENT_ADD", Config.getLang()));			
			return false;
		}

		return true;
	}

	/**
	 * Starts a player
	 * 
	 * @param sender	The player that want to start @player
	 * @param player	The player for whom the game will start
	 * @param team		The player's team if needed
	 */
	private void startPlayer(Player sender, Player player, String team) {
		KuffleMain.paused = true;

		GameManager.addPlayer(player);
		LogManager.getInstanceSystem().writeMsg(sender, LangManager.getMsgLang("ADDED_ONE_LIST", Config.getLang()));

		if (team != null) {
			TeamManager.getInstance().affectPlayer(team, player);
			LogManager.getInstanceSystem().writeMsg(sender, LangManager.getMsgLang("TEAM_ADD_PLAYER", Config.getLang()).replace("<#>", "<" + team + ">").replace("<##>", "<" + player.getName() + ">"));

			GameManager.applyToPlayer(player.getName(), (game) -> {
				game.teamName = team;
				game.spawnLoc = GameManager.getPlayerSpawnLoc(TeamManager.getInstance().getTeam(team).getPlayersName().get(0));
			});

			player.setBedSpawnLocation(GameManager.getPlayerSpawnLoc(player.getName()), true);
			player.teleport(GameManager.getPlayer(TeamManager.getInstance().getTeam(team).getPlayersName().get(0)).getPlayer());
		} else {
			GameManager.applyToPlayer(player.getName(), (game) -> {
				game.spawnLoc = player.getLocation();
				game.spawnLoc.add(0, -1, 0).getBlock().setType(Material.BEDROCK);
			});
			
			player.setBedSpawnLocation(player.getLocation(), true);
		}

		GameManager.addToPlayersRanks(player.getName());
		player.sendMessage(LangManager.getMsgLang("GAME_STARTED", Config.getLang()));

		GameManager.setupPlayer(player.getName());
		
		ScoreManager.setupPlayerScore(player.getName());
		GameManager.updatePlayersHeads();

		KuffleMain.paused = false;

		player.getInventory().addItem(KuffleStart.getStartBox(player.getName()));

		if (Config.getSaturation()) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 10, false, false, false));
		}
	}
}
