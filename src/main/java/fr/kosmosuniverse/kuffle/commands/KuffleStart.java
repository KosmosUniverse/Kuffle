package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.KuffleMain;
import fr.kosmosuniverse.kuffle.core.*;
import fr.kosmosuniverse.kuffle.utils.ItemMaker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

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
		if (Party.getInstance().getPlayers().getList().isEmpty()) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("NO_PLAYERS", Config.getLang()));

			return false;
		}

		if (Config.getTeam() && !TeamManager.getInstance().checkPlayerInTeams()) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("PLAYER_NOT_IN_TEAM", Config.getLang()));
			
			return true;
		} else if (!Config.getTeam()) {
			TeamManager.getInstance().clear();
		}

		Party.getInstance().setup();

		Party.getInstance().getPlayers().getList().forEach(playerName -> {
			Party.getInstance().getGames().getGames().get(playerName).setConfigLang(Config.getLang());
			
			if (Config.getSaturation()) {
				Objects.requireNonNull(Bukkit.getPlayer(playerName)).addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 10, false, false, false));
			}
			
			Objects.requireNonNull(Bukkit.getPlayer(playerName)).sendMessage(LangManager.getMsgLang(GAME_STARTED, Party.getInstance().getGames().getGames().get(playerName).getConfigLang()));
		});
		
		Party.getInstance().getSpectators().getList().forEach(playerName -> Objects.requireNonNull(Bukkit.getPlayer(playerName)).sendMessage(LangManager.getMsgLang(GAME_STARTED, Config.getLang())));

		Party.getInstance().getType().setXpActivable("EndTeleporter", Config.getXpEnd());
		Party.getInstance().getType().setXpActivable("OverworldTeleporter", Config.getXpOverworld());
		Party.getInstance().getType().setXpActivable("CoralCompass", Config.getXpCoral());
		
		LogManager.getInstanceSystem().logSystemMsg(LangManager.getMsgLang(GAME_STARTED, Config.getLang()));

		int spread = spreadAndSpawn(player);

		if (Config.getTeam() && Config.getTeamInv()) {
			TeamManager.getInstance().setupTeamsInv();
		}
		
		TargetManager.shuffleTargets();
		Party.getInstance().getPlayers().updatePlayersHeads();
		Party.getInstance().getSpectators().updatePlayersHeads();
		Party.getInstance().getRanks().init();

		Party.getInstance().getPlayers().getList().forEach(playerName -> {
			Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
				ActionBar.sendRawTitle(ChatColor.BOLD + String.valueOf(ChatColor.RED) + "5" + ChatColor.RESET, Objects.requireNonNull(Bukkit.getPlayer(playerName)));

				if (Config.getSBTT()) {
					Party.getInstance().getType().setupSbtt();
				}
			}, 20 + spread);

			Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> ActionBar.sendRawTitle(ChatColor.BOLD + String.valueOf(ChatColor.GOLD) + "4" + ChatColor.RESET, Objects.requireNonNull(Bukkit.getPlayer(playerName))), 40 + spread);

			Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
				ActionBar.sendRawTitle(ChatColor.BOLD + String.valueOf(ChatColor.YELLOW) + "3" + ChatColor.RESET, Objects.requireNonNull(Bukkit.getPlayer(playerName)));
				CraftManager.enableCrafts();
			}, 60 + spread);

			Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> ActionBar.sendRawTitle(ChatColor.BOLD + String.valueOf(ChatColor.GREEN) + "2" + ChatColor.RESET, Objects.requireNonNull(Bukkit.getPlayer(playerName))), 80 + spread);

			Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
				ActionBar.sendRawTitle(ChatColor.BOLD + String.valueOf(ChatColor.BLUE) + "1" + ChatColor.RESET, Objects.requireNonNull(Bukkit.getPlayer(playerName)));
				Party.getInstance().getGames().getGames().get(playerName).setup(Bukkit.getPlayer(playerName));
				Party.getInstance().getGames().updatePlayerBar(playerName);
				ScoreManager.setupPlayerScores(playerName);
			}, 100 + spread);

			Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
				ActionBar.sendRawTitle(ChatColor.BOLD + String.valueOf(ChatColor.DARK_PURPLE) + "GO" + ChatColor.RESET, Objects.requireNonNull(Bukkit.getPlayer(playerName)));

				ItemStack box = getStartBox(playerName);
				Objects.requireNonNull(Bukkit.getPlayer(playerName)).getInventory().addItem(box);
				Party.getInstance().getGames().sendTips(playerName);
			}, 120 + spread);
		});

		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> Party.getInstance().launch(), 120 + spread);

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
					Party.getInstance().getPlayers().getList());

			Party.getInstance().getPlayers().getList().forEach(playerName -> {
				Objects.requireNonNull(Bukkit.getPlayer(playerName)).setBedSpawnLocation(Objects.requireNonNull(Bukkit.getPlayer(playerName)).getLocation(), true);
				Party.getInstance().getGames().getGames().get(playerName).setSpawnLoc(Objects.requireNonNull(Bukkit.getPlayer(playerName)).getLocation());
				Party.getInstance().getGames().getGames().get(playerName).getSpawnLoc().add(0, -1, 0).getBlock().setType(Material.BEDROCK);
			});

			return 20;
		} else {
			Party.getInstance().getGames().getGames().forEach((playerName, playerData) -> {
				Location spawnLoc = Objects.requireNonNull(Objects.requireNonNull(Bukkit.getPlayer(playerName)).getLocation().getWorld()).getSpawnLocation();

				spawnLoc.getBlock().setType(Material.BEDROCK);
				playerData.setSpawnLoc(spawnLoc.clone());
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
		return ItemMaker.newItem(Material.WHITE_SHULKER_BOX).addName("Start Box").addLore("Owner:" + player).getItem();
	}
}
