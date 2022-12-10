package main.fr.kosmosuniverse.kuffle.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Score;

import main.fr.kosmosuniverse.kuffle.utils.SerializeUtils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class Game implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public List<String> alreadyGot = null;
	public Map<String, Long> times = null;

	public boolean finished;
	public boolean lose;
	public boolean dead;

	public int time;
	public int targetCount = 1;
	public int age = 0;
	public int gameRank = -1;
	public int sameIdx = 0;

	public int deathCount = 0;
	public int skipCount = 0;
	public int sbttCount = 0;
	
	public long totalTime = 0;
	public long timeShuffle = -1;
	public long interval = -1;
	public long timeBase;

	public String currentTarget = null;
	public String targetDisplay = null;
	public String configLang = null;
	public String teamName = null;

	public Location spawnLoc = null;
	public Location deathLoc = null;

	public List<ItemStack> deathInv = null;
	
	public Player player = null;
	public Score score = null;
	public BossBar ageDisplay = null;

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
		
		oStream.writeUTF(currentTarget == null ? "$null$" : currentTarget);
		oStream.writeUTF(teamName == null ? "$null$" : teamName);
		oStream.writeUTF(configLang == null ? "$null$" : configLang);
		
		oStream.writeObject(spawnLoc.serialize());
		
		if (dead) {
			oStream.writeObject(deathLoc.serialize());
			oStream.writeInt(deathInv.size());
			
			for (ItemStack item : deathInv) {
				oStream.writeObject(item.serialize());
			}
		}
		
		if (!finished) {
			times.put("interval", System.currentTimeMillis() - timeBase);
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
		time = Config.getStartTime();
		timeBase = System.currentTimeMillis();
		
		finished = iStream.readBoolean();
		lose = iStream.readBoolean();
		dead = iStream.readBoolean();
		
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
			timeBase = System.currentTimeMillis() - times.get("interval");
			times.remove("interval");
		}
		
		alreadyGot = (List<String>) iStream.readObject();
	}
}
