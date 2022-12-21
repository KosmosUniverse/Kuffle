package main.fr.kosmosuniverse.kuffle.core;

import java.security.SecureRandom;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.type.KuffleType;
import main.fr.kosmosuniverse.kuffle.utils.Pair;

public class GameLoop {
	private BukkitTask runnable;
	private boolean finished = false;
	private int bestRank;
	private int worstRank;

	public void startRunnable() {
		final SecureRandom random = new SecureRandom();
		
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				if (KuffleMain.getInstance().isPaused()) {
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
						if (Config.getPrintTabAll()) {
							GameManager.printGameEnd();
						}
						
						runnable.cancel();
					}
				}
			}
		}.runTaskTimer(KuffleMain.getInstance(), 0, 20);
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
	
	private void runLoop(SecureRandom random) {
		GameManager.applyToPlayers(game -> {
			if (game.isLose()) {
				if (!game.isFinished()) {
					game.finish(worstRank);
					worstRank = GameManager.getWorstRank();
					GameManager.applyToPlayers(playerGame ->
						playerGame.getPlayer().sendMessage(LangManager.getMsgLang("GAME_ABANDONED", playerGame.getConfigLang()).replace("<#>", ChatColor.GOLD + "" + ChatColor.BOLD + game.getPlayer().getName() + ChatColor.BLUE))
					);
				}
			} else if (game.isFinished()) {
				game.playerRandomBarColor();
			} else {
				if (game.getCurrentTarget() == null) {
					checkTargetStatus(game);
				} else {
					resetOrDisplayTarget(game, random);
				}

				printTimerTarget(game);
			}
		});
	}
 	
	private void checkTargetStatus(Game game) {
		if (game.getAge() == (Config.getLastAge().getNumber() + 1)) {
			game.finish(bestRank);
			bestRank = GameManager.getBestRank();
			LogManager.getInstanceGame().logSystemMsg(game.getPlayer().getName() + " complete its game !");
			GameManager.applyToPlayers(playerGame ->
				playerGame.getPlayer().sendMessage(LangManager.getMsgLang("GAME_COMPLETE", playerGame.getConfigLang()).replace("<#>", ChatColor.GOLD + "" + ChatColor.BOLD + game.getPlayer().getName() + ChatColor.BLUE))
			);
		} else if (!Config.getTeam() && game.getTargetCount() >= (Config.getTargetPerAge() + 1)) {
			GameManager.nextPlayerAge(game.getPlayer().getName());
		} else if (Config.getTeam() && game.getTargetCount() >= (Config.getTargetPerAge() + 1)) {
			if (checkTeamMates(game)) {
				GameManager.nextPlayerAge(game.getPlayer().getName());
			}
		} else {
			newItem(game);
		}
	}
	
	private void resetOrDisplayTarget(Game game, SecureRandom random) {
		if (System.currentTimeMillis() - game.getTimeShuffle() > (game.getTime() * 60000)) {
			game.getPlayer().sendMessage(ChatColor.RED + LangManager.getMsgLang("TARGET_NOT_FOUND", game.getConfigLang()));
			LogManager.getInstanceGame().logSystemMsg("Player : " + game.getPlayer().getName() + " did not found target : " + game.getCurrentTarget());
			newItem(game);
		} else if (Config.getDouble() && !game.getCurrentTarget().contains("/")) {
			String currentTmp = TargetManager.newTarget(game.getAlreadyGot(), AgeManager.getAgeByNumber(game.getAge()).getName());

			game.addAlreadyGot(currentTmp);
			game.setCurrentTarget(game.getCurrentTarget() + "/" + currentTmp);
		} else if (!Config.getDouble() && game.getCurrentTarget().contains("/")) {
			String[] array = game.getCurrentTarget().split("/");

			game.setCurrentTarget(array[random.nextInt(2)]);
			String tmp = game.getCurrentTarget().equals(array[0]) ? array[1] : array[0];
			game.removeAlreadyGot(tmp);
		}
		
		if (KuffleMain.getInstance().getType().getType() == KuffleType.Type.BLOCKS && checkBlock(game)) {
			GameManager.playerFoundTarget(game.getPlayer().getName());
		}
	}
	
	private boolean checkBlock(Game game) {
		Location pPosition = game.getPlayer().getLocation().clone().add(0, -1, 0);
		double pY = pPosition.getY();
		
		for (double y = pY; y < (pY + 3); y++) {
			pPosition.setY(y);
			
			if (Config.getDouble()) {
				String[] targets = game.getCurrentTarget().split("/");
				
				if (targets[0].equals(pPosition.getBlock().getType().name().toLowerCase()) ||
						targets[1].equals(pPosition.getBlock().getType().name().toLowerCase())) {
					
					return true;
				}
			} else if (game.getCurrentTarget().equals(pPosition.getBlock().getType().name().toLowerCase())) {
				return true;
			}
		}
		
		return false;
	}

	private void printTimerTarget(Game tmpGame) {
		if (Config.getTeam() && tmpGame.getTargetCount() >= (Config.getTargetPerAge() + 1)) {
			ActionBar.sendMessage(ChatColor.LIGHT_PURPLE + LangManager.getMsgLang("TEAM_WAIT", tmpGame.getConfigLang()), tmpGame.getPlayer());
			return ;
		}

		long count = tmpGame.getTime() * 60000;
		String dispCuritem;

		count -= (System.currentTimeMillis() - tmpGame.getTimeShuffle());
		count /= 1000;

		if (tmpGame.getCurrentTarget() == null) {
			dispCuritem = LangManager.getMsgLang("SOMETHING_NEW", tmpGame.getConfigLang());
		} else {
			if (tmpGame.getTargetDisplay().contains("/")) {
				dispCuritem = LangManager.getMsgLang("TARGET_DOUBLE", tmpGame.getConfigLang()).replace("[#]", tmpGame.getTargetDisplay().split("/")[0]).replace("[##]", tmpGame.getTargetDisplay().split("/")[1]);
			} else {
				dispCuritem = tmpGame.getTargetDisplay();
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

		ActionBar.sendMessage(color + LangManager.getMsgLang("COUNTDOWN", tmpGame.getConfigLang()).replace("%i", "" + count).replace("%s", dispCuritem), tmpGame.getPlayer());
	}

	private boolean checkTeamMates(Game tmpGame) {
		boolean ret = true;
		Team team = TeamManager.getInstance().findTeamByPlayer(tmpGame.getPlayer().getName());
		
		for (Player player : team.getPlayers()) {
			Game game = GameManager.getGames().get(player.getName());
			
			if (game.getAge() <= tmpGame.getAge() &&
					game.getTargetCount() < (Config.getTargetPerAge() + 1)) {
				ret = false;
				break;
			}
		}

		return ret;
	}

	private void newItem(Game tmpGame) {
		if (Config.getDouble()) {
			String currentTarget = newItemSingle(tmpGame);
			tmpGame.addAlreadyGot(currentTarget);

			String currentTarget2 = newItemSingle(tmpGame);
			tmpGame.addAlreadyGot(currentTarget2);

			tmpGame.setCurrentTarget(currentTarget + "/" + currentTarget2);
		} else {
			tmpGame.setCurrentTarget(newItemSingle(tmpGame));
		}
		
		GameManager.updatePlayerDisplayTarget(tmpGame);
	}

	private String newItemSingle(Game tmpGame) {
		if (tmpGame.getAlreadyGot().size() >= TargetManager.getAgeTargets(AgeManager.getAgeByNumber(tmpGame.getAge()).getName()).size()) {
		tmpGame.resetAlreadyGot();
		}

		String ret;

		if (Config.getSame()) {
			Pair tmpPair = TargetManager.nextTarget(tmpGame.getAlreadyGot(), AgeManager.getAgeByNumber(tmpGame.getAge()).getName(), tmpGame.getSameIdx());

			tmpGame.setSameIdx((int) tmpPair.getKey());
			ret = (String) tmpPair.getValue();
		} else {
			ret = TargetManager.newTarget(tmpGame.getAlreadyGot(), AgeManager.getAgeByNumber(tmpGame.getAge()).getName());
		}

		return ret;
	}

	public void kill() {
		if (runnable != null) {
			runnable.cancel();
		}
	}
}
