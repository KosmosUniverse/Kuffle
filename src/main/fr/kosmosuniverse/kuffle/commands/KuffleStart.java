package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.ActionBar;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.CraftManager;
import main.fr.kosmosuniverse.kuffle.core.GameManager;
import main.fr.kosmosuniverse.kuffle.core.LangManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.core.ScoreManager;
import main.fr.kosmosuniverse.kuffle.core.SpreadPlayer;
import main.fr.kosmosuniverse.kuffle.core.TargetManager;
import main.fr.kosmosuniverse.kuffle.core.TeamManager;
import main.fr.kosmosuniverse.kuffle.utils.ItemUtils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleStart implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;

		Player player = (Player) sender;

		LogManager.getInstanceSystem().logMsg(player.getName(), LangManager.getMsgLang("CMD_PERF", Config.getLang()).replace("<#>", "<ki-start>"));

		if (!player.hasPermission("ki-start")) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NOT_ALLOWED", Config.getLang()));
			return false;
		}

		if (GameManager.getGames().size() == 0) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NO_PLAYERS", Config.getLang()));

			return false;
		}

		if (KuffleMain.gameStarted) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("GAME_LAUNCHED", Config.getLang()));
			
			return false;
		}

		if (Config.getTeam() && !GameManager.checkTeams()) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("PLAYER_NOT_IN_TEAM", Config.getLang()));
			
			return true;
		} else if (!Config.getTeam()) {
			TeamManager.clear();
		}
		
		GameManager.applyToPlayers((game) -> {
			if (Config.getSaturation()) {
				game.player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 10, false, false, false));
			}

			game.player.sendMessage(LangManager.getMsgLang("GAME_STARTED", game.configLang));
		});

		LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang("GAME_STARTED", Config.getLang()));

		int spread = spreadAndSpawn(player);

		TargetManager.shuffleTargets();
		GameManager.updatePlayersHeads();
		GameManager.setupPlayersRanks();

		KuffleMain.paused = true;

		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () -> {
			GameManager.applyToPlayers((game) ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.RED + "5" + ChatColor.RESET, game.player)
			);

			if (Config.getSBTT()) {
				CraftManager.reloadTemplates();
			}
		}, 20 + spread);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () ->
			GameManager.applyToPlayers((game) ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.GOLD + "4" + ChatColor.RESET, game.player)
			)
		, 40 + spread);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () ->
			GameManager.applyToPlayers((game) ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.YELLOW + "3" + ChatColor.RESET, game.player)
			)
		, 60 + spread);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () ->
			GameManager.applyToPlayers((game) ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.GREEN + "2" + ChatColor.RESET, game.player)
			)
		, 80 + spread);

		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () -> {
			GameManager.applyToPlayers((game) -> {
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.BLUE + "1" + ChatColor.RESET, game.player);
				GameManager.setupPlayer(game);
			});

			ScoreManager.setupPlayerScores();
		}, 100 + spread);

		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () -> {
			ItemStack box = getStartBox();

			GameManager.applyToPlayers((game) -> {
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + "GO!" + ChatColor.RESET, game.player);
				game.player.getInventory().addItem(box);
			});

			KuffleMain.loop.startRunnable();
			KuffleMain.gameStarted = true;
			KuffleMain.paused = false;
		}, 120 + spread);

		return true;
	}
	
	/**
	 * Sets players spawn location after spreading if on in config
	 * 
	 * @param sender	The player thatmade start command
	 * 
	 * @return 20 is players have been spread, 0 instead
	 */
	private int spreadAndSpawn(Player sender) {
		if (Config.getSpread()) {
			SpreadPlayer.spreadPlayers(sender, Config.getSpreadDistance(), Config.getSpreadRadius(), GameManager.getPlayerList());

			GameManager.applyToPlayers((game) -> {
				if (Config.getTeam()) {
					game.teamName = TeamManager.findTeamByPlayer(game.player.getName()).name;
				}

				game.player.setBedSpawnLocation(game.player.getLocation(), true);
				game.spawnLoc = game.player.getLocation();
				game.spawnLoc.add(0, -1, 0).getBlock().setType(Material.BEDROCK);
			});

			return 20;
		} else {
			Location spawn = new Location(sender.getLocation().getWorld(), -1, -1, -1);

			GameManager.applyToPlayers(spawn, (game, spawnLoc) -> {
				if (Config.getTeam()) {
					game.teamName = TeamManager.findTeamByPlayer(game.player.getName()).name;
				}

				if (((Location) spawnLoc).getY() < 0) {
					Location tmp = game.player.getLocation().getWorld().getSpawnLocation();
					
					((Location) spawnLoc).setWorld(tmp.getWorld());
					((Location) spawnLoc).setX(tmp.getX());
					((Location) spawnLoc).setY(tmp.getY());
					((Location) spawnLoc).setZ(tmp.getZ());
					
					((Location) spawnLoc).subtract(0, 1, 0).getBlock().setType(Material.BEDROCK);
				}

				game.spawnLoc = ((Location) spawnLoc);
			});
			
			return 0;
		}
	}

	/**
	 * Makes the start shulker box
	 * 
	 * @return the shulker box ItemStack
	 */
	static ItemStack getStartBox() {
		return ItemUtils.itemMakerName(Material.WHITE_SHULKER_BOX, 1, "Start Box");
	}
}
