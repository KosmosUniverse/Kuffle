	package main.fr.kosmosuniverse.kuffle.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.utils.SerializeUtils;
import main.fr.kosmosuniverse.kuffle.utils.Utils;
import net.md_5.bungee.api.ChatColor;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class Game implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final String GAME_DONE = "GAME_DONE";
	private static final String FINISH_TOTAL = "FINISH_TOTAL";
	private static final String INTERVAL_TIME = "interval";
	private static final String NULL = "$null$";
	
	private List<String> alreadyGot = null;
	private Map<String, Long> times = null;

	private boolean finished;
	private boolean lose;
	private boolean dead;
	private boolean discovered;
	private boolean tips;

	private int time;
	private int targetCount = 1;
	private int age = 0;
	private int gameRank = -1;
	private int sameIdx = 0;

	private int deathCount = 0;
	private int skipCount = 0;
	private int sbttCount = 0;
	
	private long totalTime = 0;
	private long timeShuffle = -1;
	private long interval = -1;
	private long timeBase;

	private String currentTarget = null;
	private String targetDisplay = null;
	private String configLang = null;
	private String teamName = null;

	private Location spawnLoc = null;
	private Location deathLoc = null;

	private List<ItemStack> deathInv = null;
	
	private Player player = null;
	private Score score = null;
	private BossBar ageDisplay = null;

	/**
	 * Constructor
	 * 
	 * @param gamePlayer	The player that own this Game instance as Player object
	 */
	public Game(Player gamePlayer) {
		player = gamePlayer;
		
		alreadyGot = new ArrayList<>();
		times = new HashMap<>();
		
		finished = false;
		lose = false;
		dead = false;
		discovered = false;
		configLang = Config.getLang();
		tips = Config.hasTips();
	}
	
	/**
	 * Clears alreadyGot list and times map
	 */
	public void clear() {
		alreadyGot.clear();
		times.clear();
	}
	
	/**
	 * Setup Player after Game created by load
	 * 
	 * @param p	The player to link to this Game
	 */
	public void setupPostLoad(Player p) {
		player = p;
		ageDisplay = Bukkit.createBossBar(LangManager.getMsgLang("START", Config.getLang()), BarColor.PURPLE, BarStyle.SOLID);
		ageDisplay.addPlayer(player);
	}
	
	/**
	 * Setup basics variables for a player
	 */
	public void setupPlayer() {
		time = Config.getStartTime();
		timeBase = System.currentTimeMillis();
		ageDisplay = Bukkit.createBossBar(LangManager.getMsgLang("START", configLang), BarColor.PURPLE, BarStyle.SOLID);
		ageDisplay.addPlayer(player);
		
		updatePlayerBar();
	}
	
	/**
	 * Setup scores for a specific player
	 * 
	 * @param scoreboard	The scoreboard to apply to the player
	 * @param score			The score to apply
	 */
	public void setupPlayerScores(Scoreboard scoreboard, Score score) {
		this.score = score;
		this.score.setScore(1);
		player.setScoreboard(scoreboard);
	}
	
	/**
	 * Reloads the score based on targetCount
	 */
	public void reloadScore() {
		this.score.setScore(targetCount);
	}

	/**
	 * Set player BossBar color randomly
	 */
	public void playerRandomBarColor() {
		BarColor[] colors = BarColor.values();
		SecureRandom random = new SecureRandom();
		
		ageDisplay.setColor(colors[random.nextInt(colors.length)]);
	}
	
	/**
	 * Updates the player boss bar
	 */
	public void updatePlayerBar() {
		if (lose) {
			ageDisplay.setProgress(0.0);
			ageDisplay.setTitle(LangManager.getMsgLang(GAME_DONE, configLang).replace("%i", "" + gameRank));

			return ;
		}

		if (finished || age == (Config.getLastAge().getNumber() + 1)) {
			ageDisplay.setProgress(1.0);
			ageDisplay.setTitle(LangManager.getMsgLang(GAME_DONE, configLang).replace("%i", "" + gameRank));

			return ;
		}

		double calc = ((double) targetCount) / Config.getTargetPerAge();
		calc = calc > 1.0 ? 1.0 : calc;
		ageDisplay.setProgress(calc);
		ageDisplay.setTitle(AgeManager.getAgeByNumber(age).getName().replace("_", " ") + ": " + targetCount);
	}
	
	/**
	 * Updates the player name display in tab
	 */
	public void updatePlayerListName() {
		if (Config.getTeam()) {
			player.setPlayerListName("[" + TeamManager.getInstance().getTeam(teamName).getColor() + teamName + ChatColor.RESET + "] - " + AgeManager.getAgeByNumber(age).getColor() + player.getName());
		} else {
			player.setPlayerListName(AgeManager.getAgeByNumber(age).getColor() + player.getName());
		}
	}
	
	/**
	 * Pause the player's game by saving the difference between actual time and player's timer
	 */
	public void pausePlayer() {
		interval = System.currentTimeMillis() - timeShuffle;
	}

	/**
	 * Resume the player's game by loading the difference between actual time and pause time
	 */
	public void resumePlayer() {
		timeShuffle = System.currentTimeMillis() - interval;
		interval = -1;
	}
	
	/**
	 * Checks if a specific target is the target of this player
	 * 
	 * @param target	The target to check
	 * 
	 * @return True if @target is the @player's target
	 */
	public boolean checkPlayerTarget(ItemStack target) {
		boolean ret = false;
		
		if (currentTarget == null) {
			return ret;
		}
		
		if (Config.getDouble()) {
			String[] targets = currentTarget.split("/");

			ret = targets[0].equals(target.getType().name().toLowerCase()) ||
					targets[1].equals(target.getType().name().toLowerCase()) ||
					(targets[0].startsWith("*") && target.getType().name().toLowerCase().contains(targets[0].replace("*", ""))) ||
					(targets[1].startsWith("*") && target.getType().name().toLowerCase().contains(targets[1].replace("*", "")));
		} else {
			ret = currentTarget.equals(target.getType().name().toLowerCase()) ||
					(currentTarget.startsWith("*") && target.getType().name().toLowerCase().contains(currentTarget.replace("*", "")));
		}
		
		return ret;
	}
	
	/**
	 * Player found its target
	 * 
	 * @param isSbtt	True if sbtt was used instead of target
	 */
	public void playerFoundTarget(boolean isSbtt) {
		currentTarget = null;
		targetCount++;
		player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 1f, 1f);
		score.setScore(targetCount);
		updatePlayerBar();
		
		if (isSbtt) {
			sbttCount++;
		} else {
			LogManager.getInstanceGame().logMsg(player.getName(), LangManager.getMsgLang("TARGET_FOUND", configLang).replace("[#]", "[" + currentTarget + "]"));
		}
	}
	
	/**
	 * Skip target for a specific player
	 * 
	 * @param malus		If True a malus of 1 target is applied
	 * 
	 * @return True if target skipped, False instead
	 */
	public boolean skipPlayerTarget(boolean malus) {
		if (malus) {
			skipCount++;
			
			if ((age + 1) < Config.getSkipAge().getNumber()) {
				LogManager.getInstanceGame().writeMsg(player, LangManager.getMsgLang("CANT_SKIP_AGE", configLang));

				return false;
			}

			if (targetCount == 1) {
				LogManager.getInstanceGame().writeMsg(player, LangManager.getMsgLang("CANT_SKIP_FIRST", configLang));

				return false;
			}

			targetCount--;

			if (currentTarget.contains("/")) {
				LogManager.getInstanceGame().writeMsg(player, LangManager.getMsgLang("ITEMS_SKIP", configLang).replace("[#]", "[" + currentTarget.split("/")[0] + "]").replace("[##]", "[" + currentTarget.split("/")[1] + "]"));
			} else {
				LogManager.getInstanceGame().writeMsg(player, LangManager.getMsgLang("ITEM_SKIP", configLang).replace("[#]", "[" + currentTarget + "]"));
			}

			score.setScore(targetCount);
			currentTarget = null;
			
			updatePlayerBar();
		} else {
			score.setScore(targetCount);
			currentTarget = null;

			updatePlayerBar();
		}

		return true;
	}
	
	/**
	 * Player goes to the next Age
	 */
	public void nextPlayerAge() {
		if (Config.getRewards()) {
			if (age > 0) {
				RewardManager.removePreviousRewardEffects(AgeManager.getAgeByNumber(age - 1).getName(), player);
			}
			
			if (age < Config.getLastAge().getNumber()) {
				RewardManager.givePlayerReward(AgeManager.getAgeByNumber(age).getName(), player);
			}
		}
		
		times.put(AgeManager.getAgeByNumber(age).getName(), System.currentTimeMillis() - timeBase);
		totalTime += times.get(AgeManager.getAgeByNumber(age).getName()) / 1000;

		player.sendMessage(LangManager.getMsgLang("TIME_AGE", configLang).replace("%t", Utils.getTimeFromSec(totalTime)));
		
		timeBase = System.currentTimeMillis();
		alreadyGot.clear();
		currentTarget = null;
		targetCount = 1;
		sameIdx = 0;
		age++;
		time = time + Config.getAddedTime();
		player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1f, 1f);
		score.setScore(targetCount);
		
		if (age == (Config.getLastAge().getNumber() + 1)) {
			return ;
		}
		
		updatePlayerListName();
		updatePlayerBar();
	}
	
	/**
	 * Send Tips to the player
	 */
	public void sendTips() {
		if (tips && age <= Config.getLastAge().getNumber()) {
			String ageName = AgeManager.getAgeByNumber(age).getName().replace("_Age", "").toUpperCase();
			
			player.sendMessage(ChatColor.BLUE + LangManager.getMsgLang("TIPS_" + ageName, configLang) + ChatColor.RESET);
			
			if (age == 2) {
				player.sendMessage(ChatColor.BLUE + LangManager.getMsgLang("TIPS_END_TELEPORTER", configLang) + ChatColor.RESET);
			} else if (age == 3) {
				player.sendMessage(ChatColor.BLUE + LangManager.getMsgLang("TIPS_OVERWORLD_TELEPORTER", configLang) + ChatColor.RESET);
			}
		}
	}
	
	/**
	 * Finish an Age for a player
	 * 
	 * @param player	The player
	 */
	public void finishAge(String player) {
		targetCount = Config.getTargetPerAge() + 1;
		currentTarget = null;
		score.setScore(targetCount);
		updatePlayerBar();
	}
	
	/**
	 * Makes a Player finish with specific rank
	 * 
	 * @param rank	The player rank
	 */
	public void finish(int rank) {
		finished = true;
		gameRank = rank;
		ageDisplay.setTitle(LangManager.getMsgLang(GAME_DONE, configLang).replace("%i", "" + gameRank));

		if (lose) {
			ageDisplay.setProgress(0.0f);
		} else {
			ageDisplay.setProgress(1.0f);
		}

		for (PotionEffect pe : player.getActivePotionEffects()) {
			player.removePotionEffect(pe.getType());
		}

		if (lose) {
			times.put(AgeManager.getAgeByNumber(age).getName(), (System.currentTimeMillis() - timeBase) * -1);
			age++;
			
			for (int cnt = age; cnt < (Config.getLastAge().getNumber() + 1); cnt++) {
				times.put(AgeManager.getAgeByNumber(cnt).getName(), (long) -1);
			}
		} else {
			times.put(AgeManager.getAgeByNumber(age).getName(), System.currentTimeMillis() - timeBase);
		}

		age = -1;

		updatePlayerListName();

		if (Config.getPrintTab()) {
			player.sendMessage(playerString(configLang));
		}
		
		LogManager.getInstanceGame().logSystemMsg(logString());
	}
	
	/**
	 * Prints the game end result tab from a specific player
	 * 
	 * @param receiverLang	The lang to use for strings
	 * 
	 * @return the player string
	 */
	public String playerString(String receiverLang) {
		long total = 0;
		StringBuilder sb = new StringBuilder();
		
		sb.append(ChatColor.GOLD + "" + ChatColor.BOLD + player.getName() + ChatColor.RESET + ":").append("\n");
		sb.append(ChatColor.BLUE + LangManager.getMsgLang("DEATH_COUNT", receiverLang).replace("%i", "" + ChatColor.RESET + deathCount)).append("\n");
		sb.append(ChatColor.BLUE + LangManager.getMsgLang("SKIP_COUNT", receiverLang).replace("%i", "" + ChatColor.RESET + skipCount)).append("\n");
		sb.append(ChatColor.BLUE + LangManager.getMsgLang("TEMPLATE_COUNT", receiverLang).replace("%i", "" + ChatColor.RESET + sbttCount)).append("\n");
		sb.append(ChatColor.BLUE + LangManager.getMsgLang("TIME_TAB", receiverLang)).append("\n");

		boolean abandon = false;
		
		for (int i = 0; i < (Config.getLastAge().getNumber() + 1); i++) {
			Age tmpAge = AgeManager.getAgeByNumber(i);

			if (times.get(tmpAge.getName()) == -1) {
				sb.append(LangManager.getMsgLang("FINISH_ABANDON", receiverLang).replace("%s", tmpAge.getColor() + tmpAge.getName().replace("_Age", "") + ChatColor.BLUE)).append("\n");
				abandon = true;
			} else if (times.get(tmpAge.getName()) < 0) {
				sb.append(LangManager.getMsgLang("ABANDON_AFTER", receiverLang).replace("%s", tmpAge.getColor() + tmpAge.getName().replace("_Age", "") + ChatColor.BLUE).replace("%t", ChatColor.RESET + Utils.getTimeFromSec((times.get(tmpAge.getName()) * -1) / 1000))).append("\n");
				total += (times.get(tmpAge.getName()) * -1) / 1000;
				abandon = true;
			} else {
				sb.append(LangManager.getMsgLang("FINISH_TIME", receiverLang).replace("%s", tmpAge.getColor() + tmpAge.getName().replace("_Age", "") + ChatColor.BLUE).replace("%t", ChatColor.RESET + Utils.getTimeFromSec(times.get(tmpAge.getName()) / 1000))).append("\n");
				total += times.get(tmpAge.getName()) / 1000;
			}
		}
		
		if (abandon) {
			sb.append(ChatColor.BLUE + LangManager.getMsgLang(FINISH_TOTAL, receiverLang).replace("%t", ChatColor.RESET + LangManager.getMsgLang("ABANDONED", receiverLang)));
			sb.append(" (").append(Utils.getTimeFromSec(total)).append(")");
		} else {
			sb.append(ChatColor.BLUE + LangManager.getMsgLang(FINISH_TOTAL, receiverLang).replace("%t", ChatColor.RESET + Utils.getTimeFromSec(total)));
		}
		
		return sb.toString();
	}
	
	/**
	 * Logs the game end result tab from a specific player
	 * 
	 * @return the log String
	 */
	public String logString() {
		long total = 0;
		String lang = Config.getLang();
		StringBuilder sb = new StringBuilder();
		
		sb.append(player.getName() + ":").append("\n");
		sb.append(LangManager.getMsgLang("DEATH_COUNT", lang).replace("%i", "" + deathCount)).append("\n");
		sb.append(LangManager.getMsgLang("SKIP_COUNT", lang).replace("%i", "" + skipCount)).append("\n");
		sb.append(LangManager.getMsgLang("TEMPLATE_COUNT", lang).replace("%i", "" + sbttCount)).append("\n");
		sb.append(LangManager.getMsgLang("TIME_TAB", lang)).append("\n");

		boolean abandon = false;
		
		for (int i = 0; i < (Config.getLastAge().getNumber() + 1); i++) {
			Age tmpAge = AgeManager.getAgeByNumber(i);

			if (times.get(tmpAge.getName()) == -1) {
				sb.append(LangManager.getMsgLang("FINISH_ABANDON", lang).replace("%s", tmpAge.getColor() + tmpAge.getName().replace("_Age", "") + ChatColor.BLUE)).append("\n");
				abandon = true;
			} else if (times.get(tmpAge.getName()) < 0) {
				sb.append(LangManager.getMsgLang("ABANDON_AFTER", lang).replace("%s", tmpAge.getColor() + tmpAge.getName().replace("_Age", "") + ChatColor.BLUE).replace("%t", ChatColor.RESET + Utils.getTimeFromSec((times.get(tmpAge.getName()) * -1) / 1000))).append("\n");
				total += (times.get(tmpAge.getName()) * -1) / 1000;
				abandon = true;
			} else {
				sb.append(LangManager.getMsgLang("FINISH_TIME", lang).replace("%s", tmpAge.getColor() + tmpAge.getName().replace("_Age", "") + ChatColor.BLUE).replace("%t", ChatColor.RESET + Utils.getTimeFromSec(times.get(tmpAge.getName()) / 1000))).append("\n");
				total += times.get(tmpAge.getName()) / 1000;
			}
		}

		if (abandon) {
			sb.append(LangManager.getMsgLang(FINISH_TOTAL, lang).replace("%t", LangManager.getMsgLang("ABANDONED", lang)));
			sb.append(" (").append(Utils.getTimeFromSec(total)).append(")");
		} else {
			sb.append(LangManager.getMsgLang(FINISH_TOTAL, lang).replace("%t", Utils.getTimeFromSec(total)));
		}

		return sb.toString();
	}
	
	/**
	 * Gives effects to a player depending on his current Age
	 */
	public void reloadPlayerEffects() {
		if (Config.getRewards()) {
			if (Config.getSaturation()) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 10, false, false, false));
			}

			int tmp = age - 1;

			if (tmp < 0)
				return;

			RewardManager.givePlayerRewardEffect(player, AgeManager.getAgeByNumber(tmp).getName());
		}
	}
	
	/**
	 * Give to a players one or more Potion Effects
	 * 
	 * @param effects	The effect(s) he will receive
	 */
	public void giveEffectsToPlayer(PotionEffect... effects) {
		for (PotionEffect effect : effects) {
			player.addPotionEffect(effect);
		}
	}
	
	/**
	 * Store actual player inventory into another one
	 */
	public void storePlayerInv() {
		if (deathInv != null) {
			deathInv.clear();
		}
		
		deathInv = new ArrayList<>();

		for (ItemStack item : player.getInventory().getContents()) {
			if (item != null) {
				deathInv.add(item);
			}
		}
	}
	
	/**
	 * Give to a player the content of its saved inventory
	 */
	public void restorePlayerInv() {
		for (ItemStack item : deathInv) {
			HashMap<Integer, ItemStack> ret = player.getInventory().addItem(item);
			
			if (!ret.isEmpty()) {
				for (Integer cnt : ret.keySet()) {
					player.getWorld().dropItem(player.getLocation(), ret.get(cnt));
				}
			}
			
			ret.clear();
		}

		deathLoc = null;
		dead = false;
	}
	
	/**
	 * Player died
	 * 
	 * @param deathLoc	The death Location
	 */
	public void playerDied(Location deathLoc) {
		deathCount++;
		
		if (Config.getLevel().isLosable()) {
			lose = true;
		} else {
			storePlayerInv();
			player.getInventory().clear();
		}
		
		this.deathLoc = deathLoc;
		dead = true;
	}
	
	/**
	 * Sets the player lang

	 * @param lang		The lang to set
	 */
	public void setPlayerLang(String lang) {
		if (lang.equals(configLang)) {
			return ;
		}

		configLang = lang;

		if (currentTarget != null) {
			if (Config.getDouble()) {
				targetDisplay = LangManager.getTargetLang(currentTarget.split("/")[0], configLang) + "/" + LangManager.getTargetLang(currentTarget.split("/")[1], configLang);
			} else {
				targetDisplay = LangManager.getTargetLang(currentTarget, configLang);
			}
		}
	}
	
	/**
	 * Teleport a specific player to his death location
	 */
	public void teleportAutoBack() {
		player.sendMessage(LangManager.getMsgLang("TP_BACK", configLang).replace("%i", "" + Config.getLevel().getSeconds()));
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.getInstance(), () -> {
			Location loc = deathLoc;
			
			if (loc == null) {
				return;
			}
			
			if (loc.getWorld().getName().contains("the_end") && loc.getY() < 0) {
				Utils.changeLocForEnd(loc);
			}
			
			Utils.createSafeBox(loc, player.getName());
			
			player.teleport(loc);
			
			restorePlayerInv();

			for (PotionEffect p : player.getActivePotionEffects()) {
				player.removePotionEffect(p.getType());
			}
			
			reloadPlayerEffects();
			
			for (Entity e : player.getNearbyEntities(3.0, 3.0, 3.0)) {
				if (e.getType() != EntityType.DROPPED_ITEM &&
						e.getType() != EntityType.PLAYER) {
					e.remove();
				}
			}
		}, (Config.getLevel().getSeconds() * 20));
	}
	
	/**
	 * Defines what will be stored in the player save file
	 * 
	 * @param oStream	The ObjectOoutputStream
	 * 
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream oStream) throws IOException {
		oStream.writeBoolean(finished);
		oStream.writeBoolean(lose);
		oStream.writeBoolean(dead);
		
		oStream.writeInt(time);
		oStream.writeInt(targetCount);
		oStream.writeInt(age);
		oStream.writeInt(gameRank);
		oStream.writeInt(sameIdx);

		oStream.writeInt(deathCount);
		oStream.writeInt(skipCount);
		oStream.writeInt(sbttCount);
		
		oStream.writeLong(totalTime);
		interval = System.currentTimeMillis() - timeShuffle;
		oStream.writeLong(interval);
		
		oStream.writeUTF(currentTarget == null ? NULL : currentTarget);
		oStream.writeUTF(teamName == null ? NULL : teamName);
		oStream.writeUTF(configLang == null ? NULL : configLang);
		
		oStream.writeObject(spawnLoc.serialize());
		
		if (dead) {
			oStream.writeObject(deathLoc.serialize());
			oStream.writeInt(deathInv.size());
			
			for (ItemStack item : deathInv) {
				oStream.writeObject(item.serialize());
			}
		}
		
		if (!finished) {
			times.put(INTERVAL_TIME, System.currentTimeMillis() - timeBase);
		}
		
		oStream.writeObject(times);
		oStream.writeObject(alreadyGot);
	}
	
	/**
	 * Read Game info from input stream
	 * 
	 * @param iStream	The stream that contains all Game infos
	 * 
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream iStream) throws ClassNotFoundException, IOException  {
		timeBase = System.currentTimeMillis();
		
		finished = iStream.readBoolean();
		lose = iStream.readBoolean();
		dead = iStream.readBoolean();
		
		time = iStream.readInt();
		targetCount = iStream.readInt();
		age = iStream.readInt();
		gameRank = iStream.readInt();
		sameIdx = iStream.readInt();
		
		deathCount = iStream.readInt();
		skipCount = iStream.readInt();
		sbttCount = iStream.readInt();
		
		totalTime = iStream.readLong();
		interval = iStream.readLong();
		
		currentTarget = SerializeUtils.readString(iStream);
		teamName = SerializeUtils.readString(iStream);
		configLang = SerializeUtils.readString(iStream);
		
		spawnLoc = Location.deserialize((Map<String, Object>) iStream.readObject());
		
		if (dead) {
			deathLoc = Location.deserialize((Map<String, Object>) iStream.readObject());
			int size = iStream.readInt();
			deathInv = new ArrayList<>();
			
			for (int i = 0; i < size; i++) {
				deathInv.add(ItemStack.deserialize((Map<String, Object>) iStream.readObject()));
			}
		}
		
		times = (Map<String, Long>) iStream.readObject();
		
		if (!finished) {
			timeBase = System.currentTimeMillis() - times.get(INTERVAL_TIME);
			times.remove(INTERVAL_TIME);
		}
		
		alreadyGot = (List<String>) iStream.readObject();
	}

	/**
	 * @return the alreadyGot list as an unmodifiable list
	 */
	public List<String> getAlreadyGot() {
		return Collections.unmodifiableList(alreadyGot);
	}

	/**
	 * @return the times
	 */
	public Map<String, Long> getTimes() {
		return times;
	}

	/**
	 * @return the finished
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * @return the lose
	 */
	public boolean isLose() {
		return lose;
	}

	/**
	 * @return the dead
	 */
	public boolean isDead() {
		return dead;
	}
	
	/**
	 * @return the discovery state
	 */
	public boolean hasDiscovered() {
		return discovered;
	}
	
	/**
	 * @return the tips state
	 */
	public boolean hasTips() {
		return tips;
	}

	/**
	 * @return the time
	 */
	public int getTime() {
		return time;
	}

	/**
	 * @return the targetCount
	 */
	public int getTargetCount() {
		return targetCount;
	}

	/**
	 * @return the age
	 */
	public int getAge() {
		return age;
	}

	/**
	 * @return the gameRank
	 */
	public int getGameRank() {
		return gameRank;
	}

	/**
	 * @return the sameIdx
	 */
	public int getSameIdx() {
		return sameIdx;
	}

	/**
	 * @return the deathCount
	 */
	public int getDeathCount() {
		return deathCount;
	}

	/**
	 * @return the skipCount
	 */
	public int getSkipCount() {
		return skipCount;
	}

	/**
	 * @return the sbttCount
	 */
	public int getSbttCount() {
		return sbttCount;
	}

	/**
	 * @return the totalTime
	 */
	public long getTotalTime() {
		return totalTime;
	}

	/**
	 * @return the timeShuffle
	 */
	public long getTimeShuffle() {
		return timeShuffle;
	}

	/**
	 * @return the interval
	 */
	public long getInterval() {
		return interval;
	}

	/**
	 * @return the timeBase
	 */
	public long getTimeBase() {
		return timeBase;
	}

	/**
	 * @return the currentTarget
	 */
	public String getCurrentTarget() {
		return currentTarget;
	}

	/**
	 * @return the targetDisplay
	 */
	public String getTargetDisplay() {
		return targetDisplay;
	}

	/**
	 * @return the configLang
	 */
	public String getConfigLang() {
		return configLang;
	}

	/**
	 * @return the teamName
	 */
	public String getTeamName() {
		return teamName;
	}

	/**
	 * @return the spawnLoc
	 */
	public Location getSpawnLoc() {
		return spawnLoc;
	}

	/**
	 * @return the deathLoc
	 */
	public Location getDeathLoc() {
		return deathLoc;
	}

	/**
	 * @return the deathInv
	 */
	public List<ItemStack> getDeathInv() {
		return deathInv;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return the score
	 */
	public Score getScore() {
		return score;
	}

	/**
	 * @return the ageDisplay
	 */
	public BossBar getAgeDisplay() {
		return ageDisplay;
	}

	/**
	 * @param target	The target to add
	 */
	public void addAlreadyGot(String target) {
		if (!alreadyGot.contains(target)) {
			alreadyGot.add(target);
		}
	}
	
	/**
	 * @param target	The target to remove
	 */
	public void removeAlreadyGot(String target) {
		if (alreadyGot.contains(target)) {
			alreadyGot.remove(target);
		}
	}
	
	public void resetAlreadyGot() {
		if (!alreadyGot.isEmpty()) {
			alreadyGot.clear();
		}
	}

	/**
	 * @param times the times to set
	 */
	public void setTimes(Map<String, Long> times) {
		this.times = times;
	}

	/**
	 * @param finished the finished to set
	 */
	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	/**
	 * @param lose the lose to set
	 */
	public void setLose(boolean lose) {
		this.lose = lose;
	}

	/**
	 * @param dead the dead to set
	 */
	public void setDead(boolean dead) {
		this.dead = dead;
	}
	
	/**
	 * @param discovered	the discovery state to set
	 */
	public void setDiscovered(boolean discovered) {
		this.discovered = discovered;
	}
	
	/**
	 * @param tips	the tips state to set
	 */
	public void setTips(boolean tips) {
		this.tips = tips;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(int time) {
		this.time = time;
	}

	/**
	 * @param targetCount the targetCount to set
	 */
	public void setTargetCount(int targetCount) {
		this.targetCount = targetCount;
	}

	/**
	 * @param age the age to set
	 */
	public void setAge(int age) {
		this.age = age;
	}

	/**
	 * @param gameRank the gameRank to set
	 */
	public void setGameRank(int gameRank) {
		this.gameRank = gameRank;
	}

	/**
	 * @param sameIdx the sameIdx to set
	 */
	public void setSameIdx(int sameIdx) {
		this.sameIdx = sameIdx;
	}

	/**
	 * @param deathCount the deathCount to set
	 */
	public void setDeathCount(int deathCount) {
		this.deathCount = deathCount;
	}

	/**
	 * @param skipCount the skipCount to set
	 */
	public void setSkipCount(int skipCount) {
		this.skipCount = skipCount;
	}

	/**
	 * @param sbttCount the sbttCount to set
	 */
	public void setSbttCount(int sbttCount) {
		this.sbttCount = sbttCount;
	}

	/**
	 * @param totalTime the totalTime to set
	 */
	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}

	/**
	 * @param timeShuffle the timeShuffle to set
	 */
	public void setTimeShuffle(long timeShuffle) {
		this.timeShuffle = timeShuffle;
	}

	/**
	 * @param interval the interval to set
	 */
	public void setInterval(long interval) {
		this.interval = interval;
	}

	/**
	 * @param timeBase the timeBase to set
	 */
	public void setTimeBase(long timeBase) {
		this.timeBase = timeBase;
	}

	/**
	 * @param currentTarget the currentTarget to set
	 */
	public void setCurrentTarget(String currentTarget) {
		this.currentTarget = currentTarget;
	}

	/**
	 * @param targetDisplay the targetDisplay to set
	 */
	public void setTargetDisplay(String targetDisplay) {
		this.targetDisplay = targetDisplay;
	}

	/**
	 * @param configLang the configLang to set
	 */
	public void setConfigLang(String configLang) {
		this.configLang = configLang;
	}

	/**
	 * @param teamName the teamName to set
	 */
	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	/**
	 * @param spawnLoc the spawnLoc to set
	 */
	public void setSpawnLoc(Location spawnLoc) {
		this.spawnLoc = spawnLoc;
	}

	/**
	 * @param deathLoc the deathLoc to set
	 */
	public void setDeathLoc(Location deathLoc) {
		this.deathLoc = deathLoc;
	}

	/**
	 * @param deathInv the deathInv to set
	 */
	public void setDeathInv(List<ItemStack> deathInv) {
		this.deathInv = deathInv;
	}

	/**
	 * @param player the player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(Score score) {
		this.score = score;
	}

	/**
	 * @param ageDisplay the ageDisplay to set
	 */
	public void setAgeDisplay(BossBar ageDisplay) {
		this.ageDisplay = ageDisplay;
	}
}
