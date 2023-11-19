package main.fr.kosmosuniverse.kuffle.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.ActionBar;
import main.fr.kosmosuniverse.kuffle.core.Config;
import main.fr.kosmosuniverse.kuffle.core.CraftManager;
import main.fr.kosmosuniverse.kuffle.core.GameLoop;
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
public class KuffleStart extends AKuffleCommand {
	private static final String GAME_STARTED = "GAME_STARTED";
	
	public KuffleStart() {
		super("k-start", true, false, 0, 0, false);
	}

	@Override
	public boolean runCommand() {
		if (GameManager.getGames().size() == 0) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NO_PLAYERS", Config.getLang()));

			return false;
		}

		if (Config.getTeam() && !GameManager.checkTeams()) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("PLAYER_NOT_IN_TEAM", Config.getLang()));
			
			return true;
		} else if (!Config.getTeam()) {
			TeamManager.getInstance().clear();
		}
		
		GameManager.applyToPlayers(game -> {
			game.setConfigLang(Config.getLang());
			
			if (Config.getSaturation()) {
				game.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 10, false, false, false));
			}
			
			game.getPlayer().sendMessage(LangManager.getMsgLang(GAME_STARTED, game.getConfigLang()));
		});
		
		GameManager.applyToSpectators(player -> player.sendMessage(LangManager.getMsgLang(GAME_STARTED, Config.getLang())));

		KuffleMain.getInstance().getType().setXpActivable("EndTeleporter", Config.getXpEnd());
		KuffleMain.getInstance().getType().setXpActivable("OverworldTeleporter", Config.getXpOverworld());
		KuffleMain.getInstance().getType().setXpActivable("CoralCompass", Config.getXpCoral());
		
		LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(GAME_STARTED, Config.getLang()));

		int spread = spreadAndSpawn(player);

		if (Config.getTeam() && Config.getTeamInv()) {
			TeamManager.getInstance().setupTeamsInv();
		}
		
		TargetManager.shuffleTargets();
		GameManager.updatePlayersHeads();
		GameManager.setupPlayersRanks();

		KuffleMain.getInstance().setPaused(true);

		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
			GameManager.applyToPlayers(game ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.RED + "5" + ChatColor.RESET, game.getPlayer()));

			if (Config.getSBTT()) {
				KuffleMain.getInstance().getType().setupSbtt();
			}
		}, 20 + spread);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () ->
			GameManager.applyToPlayers(game ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.GOLD + "4" + ChatColor.RESET, game.getPlayer()))
		, 40 + spread);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
			GameManager.applyToPlayers(game ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.YELLOW + "3" + ChatColor.RESET, game.getPlayer()));
			
			CraftManager.enableCrafts();
		}, 60 + spread);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () ->
			GameManager.applyToPlayers(game ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.GREEN + "2" + ChatColor.RESET, game.getPlayer()))
		, 80 + spread);

		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
			GameManager.applyToPlayers(game -> {
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.BLUE + "1" + ChatColor.RESET, game.getPlayer());
				game.setupPlayer();
			});

			ScoreManager.setupPlayersScores();
		}, 100 + spread);

		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
			GameManager.applyToPlayers(game -> {
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + "GO!" + ChatColor.RESET, game.getPlayer());
				ItemStack box = getStartBox(game.getPlayer().getName());
				game.getPlayer().getInventory().addItem(box);
				game.sendTips();
			});

			if (KuffleMain.getInstance().getGameLoop() == null) {
				KuffleMain.getInstance().setGameLoop(new GameLoop());
			}
			
			KuffleMain.getInstance().getGameLoop().startRunnable();
			KuffleMain.getInstance().setStarted(true);
			KuffleMain.getInstance().setPaused(false);
			
			GameManager.applyToSpectators(player -> {
				player.setGameMode(GameMode.SPECTATOR);
				player.setScoreboard(ScoreManager.getScoreboard());
			});
		}, 120 + spread);

		return true;
	}

	/**
	 * Sets players spawn location after spreading if on in config
	 * 
	 * @param sender The player that made start command
	 * 
	 * @return 20 is players have been spread, 0 instead
	 */
	private int spreadAndSpawn(Player sender) {
		if (Config.getSpread()) {
			SpreadPlayer.spreadPlayers(sender, Config.getSpreadDistance(), Config.getSpreadRadius(),
					GameManager.getPlayerList());

			GameManager.applyToPlayers(game -> {
				if (Config.getTeam()) {
					game.setTeamName(TeamManager.getInstance().findTeamByPlayer(game.getPlayer().getName()).getName());
				}

				game.getPlayer().setBedSpawnLocation(game.getPlayer().getLocation(), true);
				game.setSpawnLoc(game.getPlayer().getLocation());
				game.getSpawnLoc().add(0, -1, 0).getBlock().setType(Material.BEDROCK);
			});

			return 20;
		} else {
			Location spawn = new Location(sender.getLocation().getWorld(), -1, -1, -1);

			GameManager.applyToPlayers(spawn, (game, spawnLoc) -> {
				if (Config.getTeam()) {
					game.setTeamName(TeamManager.getInstance().findTeamByPlayer(game.getPlayer().getName()).getName());
				}

				if (((Location) spawnLoc).getY() < 0) {
					Location tmp = game.getPlayer().getLocation().getWorld().getSpawnLocation();

					((Location) spawnLoc).setWorld(tmp.getWorld());
					((Location) spawnLoc).setX(tmp.getX());
					((Location) spawnLoc).setY(tmp.getY());
					((Location) spawnLoc).setZ(tmp.getZ());

					((Location) spawnLoc).subtract(0, 1, 0).getBlock().setType(Material.BEDROCK);
				}

				game.setSpawnLoc(((Location) spawnLoc));
			});

			return 0;
		}
	}

	/**
	 * Makes the start shulker box
	 * 
	 * @return the shulker box ItemStack
	 */
	static ItemStack getStartBox(String player) {
		return ItemUtils.itemMaker(Material.WHITE_SHULKER_BOX, 1, "Start Box", "Owner:" + player);
	}
}
