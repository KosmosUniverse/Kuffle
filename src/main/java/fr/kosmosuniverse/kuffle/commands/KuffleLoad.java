package fr.kosmosuniverse.kuffle.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import fr.kosmosuniverse.kuffle.KuffleMain;
import fr.kosmosuniverse.kuffle.core.*;
import fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;
import fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
import fr.kosmosuniverse.kuffle.type.KuffleType;
import fr.kosmosuniverse.kuffle.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleLoad extends AKuffleCommand {
	private final File dataFolder;
	private static final String GAME_FILE = "Game.k";
	
	/**
	 * Constructor
	 * 
	 * @param folder	The Kuffle plugin folder
	 */
	public KuffleLoad(File folder) {
		super("k-load", null, false, 0, 0, false);
		dataFolder = folder;
	}
	
	@Override
	public boolean runCommand() throws KuffleCommandFalseException {
		if (Party.getInstance().getPlayers() != null &&
				!Party.getInstance().getPlayers().getList().isEmpty()) {
			LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("LIST_NOT_EMPTY", Config.getLang()) + ".");
			throw new KuffleCommandFalseException();
		}
		
		if (Utils.fileExists(dataFolder.getPath(), GAME_FILE) && !loadGameFile(player)) {
			throw new KuffleCommandFalseException();
		}
		
		try {
			Party.getInstance().getGames().loadPlayers(dataFolder.getPath());
		} catch (RuntimeException e) {
			LogManager.getInstanceSystem().writeMsg(player, "Cannot load game, please contact an administrator.");
			Utils.logException(e);
			throw new KuffleCommandFalseException();
		}
		
		Party.getInstance().getPlayers().updatePlayersHeads(Party.getInstance().getGames().getGames().entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getCurrentTarget())));
		
		if (Config.getTeam()) {
			try {
				TeamManager.getInstance().loadTeams(dataFolder.getPath());
			} catch (ClassNotFoundException | IOException e) {
				Utils.logException(e);
			}
		}
		
		finalSetupAndCountdown();
		
		return true;
	}
	
	private boolean loadGameFile(Player player) {
		try (FileInputStream fis = new FileInputStream(dataFolder.getPath() + File.separator + GAME_FILE)) {
			ObjectInputStream ois = new ObjectInputStream(fis);
			GameHolder holder = (GameHolder) ois.readObject();
			ois.close();
			
			KuffleType.Type type = KuffleType.Type.valueOf(holder.getKuffleType());

			if (Party.getInstance().getType().getType() != type &&
					Party.getInstance().getType().getType() != KuffleType.Type.NO_TYPE) {
				LogManager.getInstanceSystem().writeMsg(player, LangManager.getMsgLang("WRONG_TYPE", Config.getLang()));
				return false;
			} else if (Party.getInstance().getType().getType() == KuffleType.Type.NO_TYPE) {
				Party.getInstance().setType(player, type);
			}
			
			Config.loadConfig(holder.getConfig());
			Party.getInstance().getRanks().loadRanks(holder.getPlayerRanks(), holder.getPlayerRanks(), holder.getNextRanks());
			Party.getInstance().getType().loadXpMax(holder.getXpMap());
			
			holder.clear();
		} catch (IOException | ClassNotFoundException | KuffleFileLoadException e) {
			Utils.logException(e);
		}
		
		return true;
	}
	
	private void finalSetupAndCountdown() {
		Party.getInstance().getGames().getGames().forEach((playerName, playerData) -> {
			Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
				ActionBar.sendRawTitle(ChatColor.BOLD + String.valueOf(ChatColor.RED) + "5" + ChatColor.RESET, Objects.requireNonNull(Bukkit.getPlayer(playerName)));

				if (Config.getSBTT()) {
					Party.getInstance().getType().setupSbtt();
				}
			}, 20);

			Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
				ActionBar.sendRawTitle(ChatColor.BOLD + String.valueOf(ChatColor.GOLD) + "4" + ChatColor.RESET, Objects.requireNonNull(Bukkit.getPlayer(playerName)));

				if (Config.getSaturation()) {
					Objects.requireNonNull(Bukkit.getPlayer(playerName)).addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 10, false, false, false));
				}
			} , 40);

			Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> ActionBar.sendRawTitle(ChatColor.BOLD + String.valueOf(ChatColor.YELLOW) + "3" + ChatColor.RESET, Objects.requireNonNull(Bukkit.getPlayer(playerName))), 60);

			Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> ActionBar.sendRawTitle(ChatColor.BOLD + String.valueOf(ChatColor.GREEN) + "2" + ChatColor.RESET, Objects.requireNonNull(Bukkit.getPlayer(playerName))), 80);

			Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
				ScoreManager.setupPlayersScores();
				ActionBar.sendRawTitle(ChatColor.BOLD + String.valueOf(ChatColor.BLUE) + "1" + ChatColor.RESET, Objects.requireNonNull(Bukkit.getPlayer(playerName)));
			}, 100);

			Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
				ActionBar.sendRawTitle(ChatColor.BOLD + String.valueOf(ChatColor.DARK_PURPLE) + "GO!" + ChatColor.RESET, Objects.requireNonNull(Bukkit.getPlayer(playerName)));

				if (Party.getInstance().getGames().getGameLoop() == null) {
					Party.getInstance().getGames().init();
				}

				Party.getInstance().launch();
			}, 120);
		});


		

		

		

		

		

	}
}
