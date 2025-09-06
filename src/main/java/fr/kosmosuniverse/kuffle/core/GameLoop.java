package fr.kosmosuniverse.kuffle.core;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import fr.kosmosuniverse.kuffle.KuffleMain;
import fr.kosmosuniverse.kuffle.type.KuffleType;
import fr.kosmosuniverse.kuffle.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class GameLoop {
	private BukkitTask runnable;

	/**
	 * Starts the runnable
	 */
	public void startRunnable() {
		final SecureRandom random = new SecureRandom();
		
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				if (Party.getInstance().getStatus() == GameStatus.PAUSED) {
					return ;
				}

				if (!checkFinished()) {
					runLoop(random);
				} else {
					if (Config.getEndOne()) {
						Party.getInstance().getGames().finishLast();
					}

					if (Config.getPrintTabAll()) {
						Party.getInstance().getGames().printGameEnd();
					}

					runnable.cancel();
				}
			}
		}.runTaskTimer(KuffleMain.getInstance(), 0, 20);
	}
	
	/**
	 * Check if the game is finished
	 * 
	 * @return True if finished, False instead
	 */
	private boolean checkFinished() {
		int nb = Party.getInstance().getGames().getNbPlayerStillPlaying();

		return nb == 0 || (nb == 1 && Config.getEndOne());
	}
	
	/**
	 * Run the game
	 * 
	 * @param random	The random generator
	 */
	private void runLoop(SecureRandom random) {
		Party.getInstance().getGames().getGames().forEach((playerName, playerData) -> {
			if (playerData.isLose() && !playerData.isFinished()) {
				Party.getInstance().getGames().playerLose(playerName);
				Party.getInstance().getGames().getGames().forEach((receiverName, receiverData) -> Objects.requireNonNull(Bukkit.getPlayer(receiverName)).sendMessage(LangManager.getMsgLang("GAME_ABANDONED", receiverData.getConfigLang()).replace("<#>", ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + playerName + ChatColor.BLUE)));
				Party.getInstance().getSpectators().getList().forEach(specName -> Objects.requireNonNull(Bukkit.getPlayer(specName)).sendMessage(LangManager.getMsgLang("GAME_ABANDONED", Config.getLang()).replace("<#>", ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + playerName + ChatColor.BLUE)));
			} else if (playerData.isFinished()) {
				Party.getInstance().getGames().playerRandomBarColor(playerName);
			} else {
				if (playerData.getCurrentTarget() == null) {
					checkTargetStatus(playerName, playerData);
				} else {
					resetOrDisplayTarget(playerName, playerData, random);
				}

				printTimerTarget(playerName, playerData);
			}
		});
	}
 	
	/**
	 * Check target for a specific player
	 *
	 * @param playerName	The player name
	 * @param playerData	The player data
	 */
	private void checkTargetStatus(String playerName, PlayerData playerData) {
		if (playerData.getAge() == (Config.getLastAge().getNumber() + 1)) {
			Party.getInstance().getGames().playerFinish(playerName);
			LogManager.getInstanceGame().logSystemMsg(playerName + " complete its game !");
			Party.getInstance().getGames().getGames().forEach((receiverName, receiverData) -> Objects.requireNonNull(Bukkit.getPlayer(receiverName)).sendMessage(LangManager.getMsgLang("GAME_COMPLETE", receiverData.getConfigLang()).replace("<#>", ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + playerName + ChatColor.BLUE)));
			Party.getInstance().getSpectators().getList().forEach(specName -> Objects.requireNonNull(Bukkit.getPlayer(specName)).sendMessage(LangManager.getMsgLang("GAME_COMPLETE", Config.getLang()).replace("<#>", ChatColor.GOLD + String.valueOf(ChatColor.BOLD) + playerName + ChatColor.BLUE)));
		} else if (!Config.getTeam() && playerData.getTargetCount() >= (Config.getTargetPerAge() + 1)) {
			Party.getInstance().getGames().nextPlayerAge(playerName);
		} else if (Config.getTeam() && playerData.getTargetCount() >= (Config.getTargetPerAge() + 1)) {
			if (checkTeamMates(playerName, playerData)) {
				Party.getInstance().getGames().nextPlayerAge(playerName);
			}
		} else {
			newItem(playerData);
		}
	}
	
	/**
	 * Update target display
	 *
	 * @param playerName	The player name
	 * @param playerData	The player data to update
	 * @param random		The random generator
	 */
	private void resetOrDisplayTarget(String playerName, PlayerData playerData, SecureRandom random) {
		if (System.currentTimeMillis() - playerData.getTimeTarget() > (getTime(playerData) * 60000L)) {
			Objects.requireNonNull(Bukkit.getPlayer(playerName)).sendMessage(ChatColor.RED + LangManager.getMsgLang("TARGET_NOT_FOUND", playerData.getConfigLang()));
			LogManager.getInstanceGame().logSystemMsg("Player : " + playerName + " did not found target : " + playerData.getCurrentTarget());
			newItem(playerData);
		} else if (Config.getDouble() && !playerData.getCurrentTarget().contains("/")) {
			String currentTmp = TargetManager.newTarget(playerData.getAlreadyGot(), AgeManager.getAgeByNumber(playerData.getAge()).getName());

			playerData.addAlreadyGot(currentTmp);
			playerData.setCurrentTarget(playerData.getCurrentTarget() + "/" + currentTmp);
			updatePlayerDisplayTarget(playerData);
		} else if (!Config.getDouble() && playerData.getCurrentTarget().contains("/")) {
			String[] array = playerData.getCurrentTarget().split("/");

			playerData.setCurrentTarget(array[random.nextInt(2)]);
			String tmp = playerData.getCurrentTarget().equals(array[0]) ? array[1] : array[0];
			playerData.removeAlreadyGot(tmp);
			updatePlayerDisplayTarget(playerData);
		}
		
		if (Party.getInstance().getType().getType() == KuffleType.Type.BLOCKS && checkBlock(playerName, playerData)) {
			Party.getInstance().getGames().playerFoundTarget(playerName);
		}
	}

	private int getTime(PlayerData playerData) {
		return Config.getStartTime() + (Config.getAddedTime() * playerData.getAge());
	}
	
	/**
	 * Check Block for kuffle Block type
	 *
	 * @param playerName	The player name
	 * @param playerData	The player data to check
	 * 
	 * @return True if the block is valid, False instead
	 */
	private boolean checkBlock(String playerName, PlayerData playerData) {
		if (Bukkit.getOnlinePlayers().stream().noneMatch(p -> p.getName().equals(playerName))) {
			return false;
		}

		Location pPosition = Objects.requireNonNull(Bukkit.getPlayer(playerName)).getLocation().clone().add(0, -1, 0);
		double pY = pPosition.getY();
		
		for (double y = pY; y < (pY + 3); y++) {
			pPosition.setY(y);
			
			if (Config.getDouble()) {
				String[] targets = playerData.getCurrentTarget().split("/");
				
				if (targets[0].equals(pPosition.getBlock().getType().name().toLowerCase()) ||
						targets[1].equals(pPosition.getBlock().getType().name().toLowerCase())) {
					
					return true;
				}
			} else if (playerData.getCurrentTarget().equals(pPosition.getBlock().getType().name().toLowerCase())) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Display target
	 *
	 * @param playerName	The player name
	 * @param playerData	The player data to check
	 */
	private void printTimerTarget(String playerName, PlayerData playerData) {
		if (Config.getTeam() && playerData.getTargetCount() >= (Config.getTargetPerAge() + 1)) {
			ActionBar.sendMessage(ChatColor.LIGHT_PURPLE + LangManager.getMsgLang("TEAM_WAIT", playerData.getConfigLang()), Objects.requireNonNull(Bukkit.getPlayer(playerName)));
			return ;
		}

		long count = getTime(playerData) * 60000L;
		String dspCurItem;

		count -= (System.currentTimeMillis() - playerData.getTimeTarget());
		count /= 1000;

		if (playerData.getCurrentTarget() == null) {
			dspCurItem = LangManager.getMsgLang("SOMETHING_NEW", playerData.getConfigLang());
		} else {
			if (playerData.getCurrentTargetDisplay().contains("/")) {
				dspCurItem = LangManager.getMsgLang("TARGET_DOUBLE", playerData.getConfigLang()).replace("[#]", playerData.getCurrentTargetDisplay().split("/")[0]).replace("[##]", playerData.getCurrentTargetDisplay().split("/")[1]);
			} else {
				dspCurItem = playerData.getCurrentTargetDisplay();
			}
		}

		ChatColor color;

		if (count < 30) {
			color = ChatColor.RED;
		} else if (count < 60) {
			color = ChatColor.YELLOW;
		} else {
			color = ChatColor.GREEN;
		}

		ActionBar.sendMessage(color + LangManager.getMsgLang("COUNTDOWN", playerData.getConfigLang()).replace("%i", String.valueOf(count)).replace("%s", dspCurItem), Objects.requireNonNull(Bukkit.getPlayer(playerName)));
	}

	/**
	 * Check if teammates have finished their age
	 * 
	 * @param playerName	The player name
	 * @param playerData	The player data
	 * 
	 * @return True if all team player have finished their age, False instead
	 */
	private boolean checkTeamMates(String playerName, PlayerData playerData) {
		boolean ret = true;
		Team team = TeamManager.getInstance().findTeamByPlayer(playerName);
		
		for (String name : team.getPlayers()) {
			PlayerData searchData = Party.getInstance().getGames().getGames().get(name);
			
			if (searchData.getAge() <= playerData.getAge() &&
					searchData.getTargetCount() < (Config.getTargetPerAge() + 1)) {
				ret = false;
				break;
			}
		}

		return ret;
	}

	/**
	 * Generates a new target for a player by checking activated modes
	 * 
	 * @param playerData	The player data
	 */
	private void newItem(PlayerData playerData) {
		if (Config.getDouble()) {
			String currentTarget = newItemSingle(playerData);
			playerData.addAlreadyGot(currentTarget);

			String currentTarget2 = newItemSingle(playerData);
			playerData.addAlreadyGot(currentTarget2);

			playerData.setCurrentTarget(currentTarget + "/" + currentTarget2);
		} else {
			playerData.setCurrentTarget(newItemSingle(playerData));
		}
		
		updatePlayerDisplayTarget(playerData);
	}

	/**
	 * Generates a new target without checking modes
	 * 
	 * @param playerData	The player game
	 * 
	 * @return The target
	 */
	private String newItemSingle(PlayerData playerData) {
		if (playerData.getAlreadyGot().size() >= TargetManager.getAgeTargets(AgeManager.getAgeByNumber(playerData.getAge()).getName()).size()) {
			playerData.clearAlreadyGot();
		}

		String ret;

		if (Config.getSame()) {
			Pair tmpPair = TargetManager.nextTarget(playerData.getAlreadyGot(), AgeManager.getAgeByNumber(playerData.getAge()).getName(), playerData.getSameIdx());

			playerData.setSameIdx((int) tmpPair.getKey());
			ret = (String) tmpPair.getValue();
		} else {
			ret = TargetManager.newTarget(playerData.getAlreadyGot(), AgeManager.getAgeByNumber(playerData.getAge()).getName());
		}

		return ret;
	}

	public static void updatePlayerDisplayTarget(PlayerData playerData) {
		if (playerData.getCurrentTarget() == null) {
			return ;
		}

		playerData.setTimeTarget(System.currentTimeMillis() - playerData.getInterval() + 5);

		if (Config.getDouble()) {
			playerData.setCurrentTargetDisplay(LangManager.getTargetLang(playerData.getCurrentTarget().split("/")[0], playerData.getConfigLang()) + "/" + LangManager.getTargetLang(playerData.getCurrentTarget().split("/")[1], playerData.getConfigLang()));
		} else {
			playerData.addAlreadyGot(playerData.getCurrentTarget());

			playerData.setCurrentTargetDisplay(LangManager.getTargetLang(playerData.getCurrentTarget(), playerData.getConfigLang()));
		}

		if (Party.getInstance().getGames().getGames().entrySet().stream().noneMatch(e -> e.getValue().getCurrentTarget() == null)) {
			Party.getInstance().getPlayers().updatePlayersHeads(Party.getInstance().getGames().getGames().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getCurrentTarget())));
		}
	}

	/**
	 * Stops the runnable
	 */
	public void kill() {
		if (runnable != null) {
			runnable.cancel();
		}
	}
}
