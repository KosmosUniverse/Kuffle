package main.fr.kosmosuniverse.kuffle.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Score;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class Game {
	private ArrayList<String> alreadyGot;
	private HashMap<String, Long> times;
	private long totalTime = 0;

	private boolean finished;
	private boolean lose;
	private boolean dead;

	private int time;
	private int itemCount = 1;
	private int age = 0;
	private int gameRank = -1;
	private int sameIdx = 0;

	private int deathCount = 0;
	private int skipCount = 0;
	private int sbttCount = 0;

	private long timeShuffle = -1;
	private long interval = -1;
	private long timeBase;

	private String currentItem;
	private String itemDisplay;
	private String configLang;
	private String teamName;

	private Location spawnLoc;
	private Location deathLoc;

	private Player player;
	private Inventory deathInv = null;
	private Score itemScore;
	private BossBar ageDisplay;

	public Game(Player gamePlayer) {
		player = gamePlayer;
		finished = false;
		lose = false;
		dead = false;
	}

	public void setup() {
		time = KuffleMain.config.getStartTime();
		timeBase = System.currentTimeMillis();
		times = new HashMap<>();
		alreadyGot = new ArrayList<>();
		configLang = KuffleMain.config.getLang();
		ageDisplay = Bukkit.createBossBar(Utils.getLangString(player.getName(), "START"), BarColor.PURPLE, BarStyle.SOLID);
		ageDisplay.addPlayer(player);
		deathLoc = null;
		updateBar();
	}

	public void stop() {
		for (PotionEffect pe : player.getActivePotionEffects()) {
			player.removePotionEffect(pe.getType());
		}

		resetBar();
		alreadyGot.clear();
	}

	@SuppressWarnings("unchecked")
	public String save() {
		JSONObject jsonSpawn = new JSONObject();

		jsonSpawn.put("World", spawnLoc.getWorld().getName());
		jsonSpawn.put("X", spawnLoc.getX());
		jsonSpawn.put("Y", spawnLoc.getY());
		jsonSpawn.put("Z", spawnLoc.getZ());

		JSONObject jsonDeath = new JSONObject();
		if (deathLoc == null) {
			jsonDeath = null;
		} else {
			jsonDeath.put("World", deathLoc.getWorld().getName());
			jsonDeath.put("X", deathLoc.getX());
			jsonDeath.put("Y", deathLoc.getY());
			jsonDeath.put("Z", deathLoc.getZ());
		}

		JSONObject global = new JSONObject();

		if (deathInv != null) {
			try {
				saveInventory();
			} catch (IOException e) {
				KuffleMain.systemLogs.logSystemMsg(e.getMessage());
			}
		}

		global.put("age", age);
		global.put("maxAge", KuffleMain.config.getMaxAges());
		global.put("current", currentItem);
		global.put("interval", System.currentTimeMillis() - timeShuffle);
		global.put("time", time);
		global.put("isDead", dead);
		global.put("itemCount", itemCount);
		global.put("spawn", jsonSpawn);
		global.put("death", jsonDeath);
		global.put("teamName", teamName);
		global.put("sameIdx", sameIdx);
		global.put("deathCount", deathCount);
		global.put("skipCount", skipCount);
		global.put("finished", finished);
		global.put("lose", lose);

		JSONArray got = new JSONArray();

		for (String item : alreadyGot) {
			got.add(item);
		}

		global.put("alreadyGot", got);

		JSONObject saveTimes = new JSONObject();

		times.forEach(saveTimes::put);

		saveTimes.put("interval", System.currentTimeMillis() - timeBase);

		global.put("times", saveTimes);

		return (global.toString());
	}

	public void saveInventory() throws IOException {
        File f = new File(KuffleMain.current.getDataFolder().getPath(), player.getName() + ".yml");
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        c.set("inventory.content", deathInv.getContents());
        c.save(f);
    }

	@SuppressWarnings("unchecked")
	public void loadInventory() throws IOException {
        File f = new File(KuffleMain.current.getDataFolder().getPath(), player.getName() + ".yml");
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);

        deathInv = Bukkit.createInventory(null, 54);

        ItemStack[] content = ((List<ItemStack>) c.get("inventory.content")).toArray(new ItemStack[0]);
        deathInv.setContents(content);
    }

	public void load() {
		if (finished) {
			gameRank = KuffleMain.playerRank.get(player.getName());
		}

		updateBar();
		reloadEffects();
		updatePlayerListName();
		itemScore.setScore(itemCount);
	}

	public void pause() {
		interval = System.currentTimeMillis() - timeShuffle;
	}

	public void resume() {
		timeShuffle = System.currentTimeMillis() - interval;
		interval = -1;
	}

	private void updateBar() {
		if (lose) {
			ageDisplay.setProgress(0.0);
			ageDisplay.setTitle(Utils.getLangString(player.getName(), "GAME_DONE").replace("%i", "" + gameRank));

			return ;
		}

		if (finished) {
			ageDisplay.setProgress(1.0);
			ageDisplay.setTitle(Utils.getLangString(player.getName(), "GAME_DONE").replace("%i", "" + gameRank));

			return ;
		}

		double calc = ((double) itemCount) / KuffleMain.config.getItemPerAge();
		calc = calc > 1.0 ? 1.0 : calc;
		ageDisplay.setProgress(calc);
		ageDisplay.setTitle(AgeManager.getAgeByNumber(KuffleMain.ages, age).name.replace("_", " ") + ": " + itemCount);
	}

	public void resetBar() {
		if (ageDisplay != null && ageDisplay.getPlayers().size() != 0) {
			ageDisplay.removeAll();
			ageDisplay = null;
		}
	}

	public void foundSBTT() {
		sbttCount++;

		found();
	}

	public void found() {
		currentItem = null;
		itemCount++;
		player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 1f, 1f);
		itemScore.setScore(itemCount);
		updateBar();
	}

	public void nextAge() {
		if (KuffleMain.config.getRewards()) {
			if (age > 0) {
				RewardManager.managePreviousEffects(KuffleMain.allRewards.get(AgeManager.getAgeByNumber(KuffleMain.ages, age - 1).name), player);
			}

			RewardManager.givePlayerReward(KuffleMain.allRewards.get(AgeManager.getAgeByNumber(KuffleMain.ages, age).name), player, KuffleMain.ages,  AgeManager.getAgeByNumber(KuffleMain.ages, age).number);
		}

		times.put(AgeManager.getAgeByNumber(KuffleMain.ages, age).name, System.currentTimeMillis() - timeBase);
		totalTime += times.get(AgeManager.getAgeByNumber(KuffleMain.ages, age).name) / 1000;

		timeBase = System.currentTimeMillis();
		alreadyGot.clear();
		currentItem = null;
		itemCount = 1;
		sameIdx = 0;
		age++;
		time = time + KuffleMain.config.getAddedTime();
		player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1f, 1f);
		updatePlayerListName();
		itemScore.setScore(itemCount);
		updateBar();

		Age tmpAge = AgeManager.getAgeByNumber(KuffleMain.ages, age);

		for (String playerName : KuffleMain.games.keySet()) {
			KuffleMain.games.get(playerName).getPlayer().sendMessage(Utils.getLangString(playerName, "AGE_MOVED").replace("<#>", ChatColor.BLUE + "<§6§l" + player.getName() + ChatColor.BLUE + ">").replace("<##>", "<" + tmpAge.color + tmpAge.name.replace("_Age", "") + ChatColor.BLUE + ">"));
		}

		player.sendMessage(Utils.getLangString(player.getName(), "TIME_AGE").replace("%t", Utils.getTimeFromSec(totalTime)));
	}

	public void finish(int rank) {
		finished = true;

		if (KuffleMain.config.getTeam()) {
			int tmpRank;

			if ((tmpRank = checkTeamMateRank()) != -1) {
				rank = tmpRank;
			}
		}

		gameRank = rank;
		ageDisplay.setTitle(Utils.getLangString(player.getName(), "GAME_DONE").replace("%i", "" + gameRank));

		if (lose) {
			ageDisplay.setProgress(0.0f);
		} else {
			ageDisplay.setProgress(1.0f);
		}

		KuffleMain.playerRank.put(player.getName(), gameRank);
		KuffleMain.updatePlayersHeadData(player.getName(), null);

		for (PotionEffect pe : player.getActivePotionEffects()) {
			player.removePotionEffect(pe.getType());
		}

		if (lose) {
			for (int cnt = age; cnt < KuffleMain.config.getMaxAges(); cnt++) {
				times.put(AgeManager.getAgeByNumber(KuffleMain.ages, cnt).name, (long) -1);
			}
		} else {
			times.put(AgeManager.getAgeByNumber(KuffleMain.ages, age).name, System.currentTimeMillis() - timeBase);
		}

		age = -1;

		updatePlayerListName();

		Utils.printPlayer(player.getName(), player.getName());
		Utils.logPlayer(player.getName());
	}

	private int checkTeamMateRank() {
		int tmp = -1;

		for (String playerName : KuffleMain.games.keySet()) {
			if (KuffleMain.games.get(playerName).getTeamName().equals(teamName) &&
					KuffleMain.games.get(playerName).getRank() != -1) {
				tmp = KuffleMain.games.get(playerName).getRank();
			}
		}

		return tmp;
	}

	public void randomBarColor() {
		BarColor[] colors = BarColor.values();
		
		ageDisplay.setColor(colors[ThreadLocalRandom.current().nextInt(colors.length)]);
	}

	public boolean skip(boolean malus) {
		skipCount++;

		if (malus) {
			if ((age + 1) < KuffleMain.config.getSkipAge()) {
				KuffleMain.gameLogs.writeMsg(player, Utils.getLangString(player.getName(), "CANT_SKIP_AGE"));

				return false;
			}

			if (itemCount == 1) {
				KuffleMain.gameLogs.writeMsg(player, Utils.getLangString(player.getName(), "CANT_SKIP_FIRST"));

				return false;
			}

			itemCount--;

			if (currentItem.contains("/")) {
				KuffleMain.gameLogs.writeMsg(player, Utils.getLangString(player.getName(), "ITEMS_SKIP").replace("[#]", "[" + currentItem.split("/")[0] + "]").replace("[##]", "[" + currentItem.split("/")[1] + "]"));
			} else {
				KuffleMain.gameLogs.writeMsg(player, Utils.getLangString(player.getName(), "ITEM_SKIP").replace("[#]", "[" + currentItem + "]"));
			}

			itemScore.setScore(itemCount);
			updateBar();
			currentItem = null;
		} else {
			itemScore.setScore(itemCount);
			updateBar();
			currentItem = null;
		}

		return true;
	}

	public void reloadEffects() {
		if (KuffleMain.config.getRewards()) {
			if (KuffleMain.config.getSaturation()) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 10, false, false, false));
			}

			int tmp = age - 1;

			if (tmp < 0)
				return;

			RewardManager.givePlayerRewardEffect(KuffleMain.allRewards.get(AgeManager.getAgeByNumber(KuffleMain.ages, tmp).name), player);
		}
	}

	public void savePlayerInv() {
		deathInv = Bukkit.createInventory(null, 54);

		for (ItemStack item : player.getInventory().getContents()) {
			if (item != null) {
				deathInv.addItem(item);
			}
		}
	}

	public void restorePlayerInv() {
		for (ItemStack item : deathInv.getContents()) {
			if (item != null) {
				HashMap<Integer, ItemStack> ret = player.getInventory().addItem(item);
				if (!ret.isEmpty()) {
					for (Integer cnt : ret.keySet()) {
						player.getWorld().dropItem(player.getLocation(), ret.get(cnt));
					}
				}
			}
		}

		deathInv.clear();
		deathInv = null;
		deathLoc = null;
		dead = false;
	}

	public void updatePlayerListName() {
		if (KuffleMain.config.getTeam()) {
			player.setPlayerListName("[" + KuffleMain.teams.getTeam(teamName).color + teamName + ChatColor.RESET + "] - " + AgeManager.getAgeByNumber(KuffleMain.ages, age).color + player.getName());
		} else {
			player.setPlayerListName(AgeManager.getAgeByNumber(KuffleMain.ages, age).color + player.getName());
		}
	}

	public List<String> getAlreadyGot() {
		return alreadyGot;
	}

	public Player getPlayer() {
		return player;
	}

	public boolean getFinished() {
		return finished;
	}

	public boolean getLose() {
		return lose;
	}

	public boolean getDead() {
		return dead;
	}

	public int getTime() {
		return time;
	}

	public int getItemCount() {
		return itemCount;
	}

	public int getAge() {
		return age;
	}

	public int getRank() {
		return gameRank;
	}

	public int getSameIdx() {
		return sameIdx;
	}

	public int getDeathCount() {
		return deathCount;
	}

	public int getSkipCount() {
		return skipCount;
	}

	public int getSbttCount() {
		return sbttCount;
	}

	public long getTimeShuffle() {
		return timeShuffle;
	}

	public long getAgeTime(String age) {
		return times.get(age);
	}

	public String getCurrentItem() {
		return currentItem;
	}

	public String getItemDisplay() {
		return itemDisplay;
	}

	public String getTeamName() {
		return teamName;
	}

	public String getLang() {
		return configLang;
	}

	public Score getItemScore() {
		return itemScore;
	}

	public Location getSpawnLoc() {
		return spawnLoc;
	}

	public Location getDeathLoc() {
		return deathLoc;
	}

	public void setAlreadyGot(JSONArray got) {
		for (Object element : got) {
			alreadyGot.add((String) element);
		}
	}

	public void setFinished(boolean gameFinished) {
		finished = gameFinished;
	}

	public void setLose(boolean gameLose) {
		lose = gameLose;
	}

	public void setDead(boolean death) {
		dead = death;

		if (dead) {
			deathCount++;
		}
	}

	public void setTime(int gameTime) {
		time = gameTime;
	}

	public void setItemCount(int itemNb) {
		itemCount = itemNb;

		if (itemScore != null) {
			itemScore.setScore(itemCount);
		}

		updateBar();
	}

	public void setAge(int gameAge) {
		if (age == gameAge) {
			return;
		}

		age = gameAge;
		alreadyGot.clear();
	}

	public void setSameIdx(int idx) {
		sameIdx = idx;
	}

	public void setDeathCount(int death) {
		deathCount = death;
	}

	public void setSkipCount(int skip) {
		skipCount = skip;
	}

	public void setTimeShuffle(long shuffle) {
		timeShuffle = shuffle;
	}

	public void setCurrentItem(String current) {
		currentItem = current;

		if (currentItem == null) {
			return ;
		}

		if (KuffleMain.config.getDouble()) {
			timeShuffle = System.currentTimeMillis();

			itemDisplay = LangManager.findDisplay(KuffleMain.allItemsLangs, currentItem.split("/")[0], configLang) + "/" + LangManager.findDisplay(KuffleMain.allItemsLangs, currentItem.split("/")[1], configLang);
			KuffleMain.updatePlayersHeadData(player.getName(), itemDisplay);
		} else {
			if (!alreadyGot.contains(currentItem)) {
				alreadyGot.add(currentItem);
			}

			timeShuffle = System.currentTimeMillis();
			itemDisplay = LangManager.findDisplay(KuffleMain.allItemsLangs, currentItem, configLang);
			KuffleMain.updatePlayersHeadData(player.getName(), itemDisplay);
		}
	}

	public void setTeamName(String team) {
		teamName = team;
	}

	public void setLang(String lang) {
		if (lang.equals(configLang)) {
			return ;
		}

		configLang = lang;

		if (currentItem != null) {
			if (KuffleMain.config.getDouble()) {
				itemDisplay = LangManager.findDisplay(KuffleMain.allItemsLangs, currentItem.split("/")[0], configLang) + "/" + LangManager.findDisplay(KuffleMain.allItemsLangs, currentItem.split("/")[1], configLang);
			} else {
				itemDisplay = LangManager.findDisplay(KuffleMain.allItemsLangs, currentItem, configLang);
			}
		}
	}

	public void setItemScore(Score gameScore) {
		itemScore = gameScore;
	}

	public void setDeathInv(Inventory death) {
		deathInv = death;
	}

	public void setSpawnLoc(Location spawn) {
		spawnLoc = spawn;
	}

	public void setSpawnLoc(JSONObject spawn) {
		spawnLoc = new Location(Bukkit.getWorld((String) spawn.get("World")),
				(double) spawn.get("X"),
				(double) spawn.get("Y"),
				(double) spawn.get("Z"));
	}

	public void setDeathLoc(Location spawn) {
		deathLoc = spawn;
	}

	public void setDeathLoc(JSONObject spawn) {
		if (spawn == null) {
			deathLoc = null;
		} else {
			deathLoc = new Location(Bukkit.getWorld((String) spawn.get("World")),
					(double) spawn.get("X"),
					(double) spawn.get("Y"),
					(double) spawn.get("Z"));
		}
	}

	public void setTimes(JSONObject gameTimes) {
		timeBase = System.currentTimeMillis() - (Long) gameTimes.get("interval");

		for (int i = 0; i < KuffleMain.config.getMaxAges(); i++) {
			Age ageTime = AgeManager.getAgeByNumber(KuffleMain.ages, i);

			if (gameTimes.containsKey(ageTime.name)) {
				times.put(ageTime.name, (Long) gameTimes.get(ageTime.name));
			}
		}
	}

	public void addToAlreadyGot(String item) {
		alreadyGot.add(item);
	}

	public void removeFromList(String[] array) {
		alreadyGot.remove(currentItem.equals(array[0]) ? array[1] : array[0]);
	}

	public void resetList() {
		alreadyGot.clear();
	}
}
