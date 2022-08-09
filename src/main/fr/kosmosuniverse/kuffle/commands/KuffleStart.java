package main.fr.kosmosuniverse.kuffle.commands;

import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.ActionBar;
import main.fr.kosmosuniverse.kuffle.core.SpreadPlayer;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

public class KuffleStart implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;

		Player player = (Player) sender;

		KuffleMain.systemLogs.logMsg(player.getName(), Utils.getLangString(player.getName(), "CMD_PERF").replace("<#>", "<ki-start>"));

		if (!player.hasPermission("ki-start")) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NOT_ALLOWED"));
			return false;
		}

		if (KuffleMain.games.size() == 0) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NO_PLAYERS"));

			return false;
		}

		if (KuffleMain.gameStarted) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "GAME_LAUNCHED"));
			return false;
		}

		if (KuffleMain.config.getTeam() && !checkTeams()) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "PLAYER_NOT_IN_TEAM"));
			return true;
		} else if (!KuffleMain.config.getTeam()) {
			KuffleMain.teams.resetAll();
		}
		
		if (KuffleMain.config.getSaturation()) {
			KuffleMain.games.forEach((playerName, game) ->
				game.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 10, false, false, false))
			);
		}

		for (String key : KuffleMain.allItems.keySet()) {
			Collections.shuffle(KuffleMain.allItems.get(key));
		}

		KuffleMain.games.forEach((playerName, game) ->
			game.getPlayer().sendMessage(Utils.getLangString(player.getName(), "GAME_STARTED"))
		);

		KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(player.getName(), "GAME_STARTED"));

		int spread = spreadAndSpawn(player);

		KuffleMain.playersHeads = Bukkit.createInventory(null, Utils.getNbInventoryRows(KuffleMain.games.size()), "§8Players");

		KuffleMain.games.forEach((playerName, game) -> {
			KuffleMain.playersHeads.addItem(Utils.getHead(game.getPlayer()));

			if (KuffleMain.config.getTeam() && !KuffleMain.playerRank.containsKey(game.getTeamName())) {
				KuffleMain.playerRank.put(game.getTeamName(), 0);
			} else {
				KuffleMain.playerRank.put(playerName, 0);
			}
		});

		KuffleMain.paused = true;

		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () -> {
			KuffleMain.games.forEach((playerName, game) ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.RED + "5" + ChatColor.RESET, game.getPlayer())
			);

			if (KuffleMain.config.getSBTT()) {
				Utils.setupTemplates();
			}
		}, 20 + spread);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () ->
			KuffleMain.games.forEach((playerName, game) ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.GOLD + "4" + ChatColor.RESET, game.getPlayer())
			)
		, 40 + spread);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () ->
			KuffleMain.games.forEach((playerName, game) ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.YELLOW + "3" + ChatColor.RESET, game.getPlayer())
			)
		, 60 + spread);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () ->
			KuffleMain.games.forEach((playerName, game) ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.GREEN + "2" + ChatColor.RESET, game.getPlayer())
			)
		, 80 + spread);

		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () -> {
			KuffleMain.games.forEach((playerName, game) -> {
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.BLUE + "1" + ChatColor.RESET, game.getPlayer());
				game.setup();
			});

			KuffleMain.scores.setupPlayerScores();
		}, 100 + spread);

		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () -> {
			ItemStack box = getStartBox();

			KuffleMain.games.forEach((playerName, game) -> {
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + "GO!" + ChatColor.RESET, game.getPlayer());
				game.getPlayer().getInventory().addItem(box);
			});

			KuffleMain.loop.startRunnable();
			KuffleMain.gameStarted = true;
			KuffleMain.paused = false;
		}, 120 + spread);

		return true;
	}
	
	private int spreadAndSpawn(Player sender) {
		if (KuffleMain.config.getSpread()) {
			SpreadPlayer.spreadPlayers(sender, KuffleMain.config.getSpreadDistance(), KuffleMain.config.getSpreadRadius(), Utils.getPlayerList());

			KuffleMain.games.forEach((playerName, game) -> {
				if (KuffleMain.config.getTeam()) {
					game.setTeamName(KuffleMain.teams.findTeamByPlayer(playerName));
				}

				game.getPlayer().setBedSpawnLocation(game.getPlayer().getLocation(), true);
				game.setSpawnLoc(game.getPlayer().getLocation());
				game.getSpawnLoc().add(0, -1, 0).getBlock().setType(Material.BEDROCK);
			});

			return 20;
		} else {
			Location spawn = null;

			for (String playerName : KuffleMain.games.keySet()) {
				if (KuffleMain.config.getTeam()) {
					KuffleMain.games.get(playerName).setTeamName(KuffleMain.teams.findTeamByPlayer(playerName));
				}

				if (spawn == null) {
					spawn = KuffleMain.games.get(playerName).getPlayer().getLocation().getWorld().getSpawnLocation();
				}

				KuffleMain.games.get(playerName).setSpawnLoc(spawn);
			}

			if (spawn != null) {
				spawn.subtract(0, 1, 0).getBlock().setType(Material.BEDROCK);
			}
			return 0;
		}
	}

	static ItemStack getStartBox() {
		ItemStack item = new ItemStack(Material.WHITE_SHULKER_BOX);
		ItemMeta itM = item.getItemMeta();

		itM.setDisplayName("Start Box");
		item.setItemMeta(itM);

		return item;
	}

	public boolean checkTeams() {
		for (String playerName : KuffleMain.games.keySet()) {
			if (!KuffleMain.teams.isInTeam(playerName)) {
				return false;
			}
		}

		return true;
	}
}
