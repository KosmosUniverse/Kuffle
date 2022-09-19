package main.fr.kosmosuniverse.kuffle.core;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.utils.Pair;

public class GameLoop {
	private BukkitTask runnable;
	private boolean finished = false;
	private int bestRank;
	private int worstRank;

	public void startRunnable() {
		final ThreadLocalRandom random = ThreadLocalRandom.current();
		
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				if (KuffleMain.paused) {
					return ;
				}

				bestRank = GameManager.getBestRank();
				worstRank = GameManager.getWorstRank();

				finished = checkFinished();
				
				if (!finished) {
					runLoop(random);
				} else {
					boolean succeed = true;
					
					if (Config.getEndOne()) {
						succeed = GameManager.finishLast(worstRank);
					}
					
					if (succeed) {
						runnable.cancel();
					}
				}
			}
		}.runTaskTimer(KuffleMain.current, 0, 20);
	}
	
	private boolean checkFinished() {
		int nb = GameManager.getNbPlayerStillPlaying();
		boolean ret;
		
		if (nb == 0 || (nb == 1 && Config.getEndOne())) {
			ret = true;
		} else {
			ret = false;
		}
		
		return ret;
	}
	
	private void runLoop(ThreadLocalRandom random) {
		GameManager.applyToPlayers((game) -> {
			if (game.lose) {
				if (!game.finished) {
					GameManager.finish(game, worstRank);
					worstRank = GameManager.getWorstRank();
				}
			} else if (game.finished) {
				GameManager.playerRandomBarColor(game);
			} else {
				if (game.currentTarget == null) {
					checkTargetStatus(game);
				} else {
					resetOrDisplayTarget(game, random);
				}

				printTimerTarget(game);
			}
		});
	}
 	
	private void checkTargetStatus(Game game) {
		//game.player.sendMessage("Current : " + game.targetCount + ", goal : " + Config.getTargetPerAge());
		
		if (game.age == (Config.getLastAge().number + 1)) {
			GameManager.finish(game, bestRank);
			bestRank = GameManager.getBestRank();
			LogManager.getInstanceGame().logSystemMsg(game.player.getName() + " complete its game !");
			GameManager.applyToPlayers((playerGame) -> {
				playerGame.player.sendMessage(LangManager.getMsgLang("GAME_COMPLETE", playerGame.configLang).replace("<#>", ChatColor.GOLD + "" + ChatColor.BOLD + game.player.getName() + ChatColor.BLUE));
			});
		} else if ((!Config.getTeam() && game.targetCount >= Config.getTargetPerAge()) || (Config.getTeam() && checkTeamMates(game))) {
			GameManager.nextPlayerAge(game);
		} else {
			newItem(game);		
		}
	}
	
	private void resetOrDisplayTarget(Game game, ThreadLocalRandom random) {
		if (System.currentTimeMillis() - game.timeShuffle > (game.time * 60000)) {
			game.player.sendMessage(ChatColor.RED + LangManager.getMsgLang("ITEM_NOT_FOUND", game.configLang));
			LogManager.getInstanceGame().logSystemMsg("Player : " + game.player.getName() + " did not found target : " + game.currentTarget);
			newItem(game);
		} else if (Config.getDouble() && !game.currentTarget.contains("/")) {
			String currentTmp = TargetManager.newTarget(GameManager.getPlayerAlreadyGot(game), AgeManager.getAgeByNumber(game.age).name);

			GameManager.addToAlreadyGot(game, currentTmp);
			game.currentTarget = game.currentTarget + "/" + currentTmp;
		} else if (Config.getDouble() && game.currentTarget.contains("/")) {
			String[] array = game.currentTarget.split("/");

			game.currentTarget = array[random.nextInt(2)];
			String tmp = game.currentTarget.equals(array[0]) ? array[1] : array[0];
			GameManager.removeFromList(game, tmp);
		}
	}

	private void printTimerTarget(Game tmpGame) {
		if (Config.getTeam() && tmpGame.targetCount >= Config.getTargetPerAge()) {
			ActionBar.sendMessage(ChatColor.LIGHT_PURPLE + LangManager.getMsgLang("TEAM_WAIT", tmpGame.configLang), tmpGame.player);
			return ;
		}

		long count = tmpGame.time * 60000;
		String dispCuritem;

		count -= (System.currentTimeMillis() - tmpGame.timeShuffle);
		count /= 1000;

		if (tmpGame.currentTarget == null) {
			dispCuritem = LangManager.getMsgLang("SOMETHING_NEW", tmpGame.configLang);
		} else {
			if (tmpGame.targetDisplay.contains("/")) {
				dispCuritem = LangManager.getMsgLang("ITEM_DOUBLE", tmpGame.configLang).replace("[#]", tmpGame.targetDisplay.split("/")[0]).replace("[##]", tmpGame.targetDisplay.split("/")[1]);
			} else {
				dispCuritem = tmpGame.targetDisplay;
			}
		}

		ChatColor color = null;

		if (count < 30) {
			color = ChatColor.RED;
		} else if (count < 60) {
			color = ChatColor.YELLOW;
		} else {
			color = ChatColor.GREEN;
		}

		ActionBar.sendMessage(color + LangManager.getMsgLang("COUNTDOWN", tmpGame.configLang).replace("%i", "" + count).replace("%s", dispCuritem), tmpGame.player);
	}

	private boolean checkTeamMates(Game tmpGame) {
		boolean ret = true;
		Team team = TeamManager.findTeamByPlayer(tmpGame.player.getName());
		
		for (Player player : team.players) {
			Game game = GameManager.getGames().get(player.getName());
			
			if (game.age <= tmpGame.age &&
					game.targetCount < Config.getTargetPerAge()) {
				ret = false;
				break;
			}
		}

		return ret;
	}

	private void newItem(Game tmpGame) {
		if (Config.getDouble()) {
			String currentTarget = newItemSingle(tmpGame);
			GameManager.addToAlreadyGot(tmpGame, currentTarget);

			String currentTarget2 = newItemSingle(tmpGame);
			GameManager.addToAlreadyGot(tmpGame, currentTarget2);

			tmpGame.currentTarget = currentTarget + "/" + currentTarget2;
		} else {
			tmpGame.currentTarget = newItemSingle(tmpGame);
		}
		
		updatePlayerDisplayTarget(tmpGame);
	}

	private String newItemSingle(Game tmpGame) {
		if (GameManager.getPlayerAlreadyGot(tmpGame).size() >= TargetManager.getAgeTargets(AgeManager.getAgeByNumber(tmpGame.age).name).size()) {
			GameManager.resetPlayerList(tmpGame);
		}

		String ret;

		if (Config.getSame()) {
			Pair tmpPair = TargetManager.nextTarget(GameManager.getPlayerAlreadyGot(tmpGame), AgeManager.getAgeByNumber(tmpGame.age).name, tmpGame.sameIdx);

			tmpGame.sameIdx = (int) tmpPair.getKey();
			ret = (String) tmpPair.getValue();
		} else {
			ret = TargetManager.newTarget(tmpGame.alreadyGot, AgeManager.getAgeByNumber(tmpGame.age).name);
		}

		return ret;
	}
	
	private static void updatePlayerDisplayTarget(Game tmpGame) {
		if (tmpGame.currentTarget == null) {
			return ;
		}

		if (Config.getDouble()) {
			tmpGame.timeShuffle = System.currentTimeMillis();

			tmpGame.targetDisplay = LangManager.getTargetLang(tmpGame.currentTarget.split("/")[0], tmpGame.configLang) + "/" + LangManager.getTargetLang(tmpGame.currentTarget.split("/")[1], tmpGame.configLang);
		} else {
			if (!tmpGame.alreadyGot.contains(tmpGame.currentTarget)) {
				tmpGame.alreadyGot.add(tmpGame.currentTarget);
			}

			tmpGame.timeShuffle = System.currentTimeMillis();
			tmpGame.targetDisplay = LangManager.getTargetLang(tmpGame.currentTarget, tmpGame.configLang);
		}
		
		GameManager.updatePlayersHeadData(tmpGame.player.getName(), tmpGame.targetDisplay);
	}

	public void kill() {
		if (runnable != null) {
			runnable.cancel();
		}
	}
}
