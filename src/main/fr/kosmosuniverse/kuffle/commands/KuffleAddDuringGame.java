package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Game;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

public class KuffleAddDuringGame implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;

		Player player = (Player) sender;

		KuffleMain.systemLogs.logMsg(player.getName(), Utils.getLangString(player.getName(), "CMD_PERF").replace("<#>", "<ki-add-during-game>"));

		if (!player.hasPermission("ki-add-during-game")) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NOT_ALLOWED"));
			return false;
		}

		if (!KuffleMain.gameStarted) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "GAME_NOT_LAUNCHED"));			
			return true;
		}
		
		if (args.length == 0 || args.length > 2) {
			return false;
		}

		Player retPlayer;

		if ((retPlayer = KuffleList.searchPlayerByName(args[0])) == null) {
			return true;
		}

		if (KuffleMain.config.getTeam() && args.length == 2) {
			if (!KuffleMain.teams.hasTeam(args[1])) {
				KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "TEAM_NOT_EXISTS").replace("<#>", "<" + args[1] + ">"));
				return true;
			} else if (KuffleMain.teams.getTeam(args[1]).players.size() == KuffleMain.config.getTeamSize()) {
				KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "TEAM_FULL"));
				return true;
			}

			startPlayer(player, retPlayer, args[1]);
		} else if (args.length == 1 && !KuffleMain.config.getTeam()) {
			startPlayer(player, retPlayer, null);
		} else {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "TEAM_PREVENT_ADD"));			
			return false;
		}

		return true;
	}

	private void startPlayer(Player sender, Player player, String team) {
		KuffleMain.paused = true;

		KuffleMain.games.put(player.getName(), new Game(player));
		KuffleMain.systemLogs.writeMsg(sender, Utils.getLangString(sender.getName(), "ADDED_ONE_LIST"));

		if (team != null) {
			KuffleMain.teams.affectPlayer(team, player);
			KuffleMain.systemLogs.writeMsg(sender, Utils.getLangString(sender.getName(), "TEAM_ADD_PLAYER").replace("<#>", "<" + team + ">").replace("<##>", "<" + player.getName() + ">"));
			KuffleMain.games.get(player.getName()).setTeamName(KuffleMain.teams.findTeamByPlayer(player.getName()));
			KuffleMain.games.get(player.getName()).setSpawnLoc(KuffleMain.games.get(KuffleMain.teams.getTeam(team).getPlayersName().get(0)).getSpawnLoc());
			KuffleMain.playerRank.put(KuffleMain.games.get(player.getName()).getTeamName(), 0);

			player.setBedSpawnLocation(KuffleMain.games.get(KuffleMain.teams.getTeam(team).getPlayersName().get(0)).getSpawnLoc(), true);
			player.teleport(KuffleMain.games.get(KuffleMain.teams.getTeam(team).getPlayersName().get(0)).getPlayer());
		} else {
			KuffleMain.games.get(player.getName()).setSpawnLoc(player.getLocation());
			KuffleMain.games.get(player.getName()).getSpawnLoc().add(0, -1, 0).getBlock().setType(Material.BEDROCK);
			KuffleMain.playerRank.put(player.getName(), 0);

			player.setBedSpawnLocation(player.getLocation(), true);
		}

		player.sendMessage(Utils.getLangString(sender.getName(), "GAME_STARTED"));

		KuffleMain.games.get(player.getName()).setup();
		KuffleMain.scores.setupPlayerScores(KuffleMain.games.get(player.getName()));
		KuffleMain.updatePlayersHead();

		KuffleMain.paused = false;

		player.getInventory().addItem(KuffleStart.getStartBox());
		KuffleMain.games.get(player.getName()).updatePlayerListName();

		if (KuffleMain.config.getSaturation()) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 10, false, false, false));
		}
	}
}
