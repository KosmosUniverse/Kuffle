package main.fr.kosmosuniverse.kuffle.core;

import java.io.Serializable;

/**
 * 
 * @author KosmosUniverse
 *
 */
class ConfigHolder implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public boolean saturation;
	public boolean spread;
	public boolean rewards;
	public boolean skip;
	public boolean crafts;
	public boolean team;
	public boolean same;
	public boolean duoMode;
	public boolean sbttMode;
	public boolean printTab;
	public boolean printTabAll;
	public boolean endOne;
	public boolean passiveAll;
	public boolean passiveTeam;
	public int sbttAmount;
	public int teamSize;
	public int spreadDistance;
	public int spreadRadius;
	public int targetPerAge;
	public int skipAge;
	public int lastAge;
	public int startTime;
	public int addedTime;
	public int level;
	public int xpEnd;
	public int xpOverworld;
	public int xpCoral;
	public String lang;
	
	/**
	 * Default constructor
	 */
	public ConfigHolder() {
	}
	
	/**
	 * Copy constructor that fill all config values fon other @config
	 * 
	 * @param config	the source config
	 */
	public ConfigHolder(ConfigHolder config) {
		  saturation = config.saturation;
		  spread = config.spread;
		  rewards = config.rewards;
		  skip = config.skip;
		  crafts = config.crafts;
		  team = config.team;
		  same = config.same;
		  duoMode = config.duoMode;
		  sbttMode = config.sbttMode;
		  printTab = config.printTab;
		  printTabAll = config.printTabAll;
		  endOne = config.endOne;
		  passiveAll = config.passiveAll;
		  passiveTeam = config.passiveTeam;
		  sbttAmount = config.sbttAmount;
		  teamSize = config.teamSize;
		  spreadDistance = config.spreadDistance;
		  spreadRadius = config.spreadRadius;
		  targetPerAge = config.targetPerAge;
		  skipAge = config.skipAge;
		  lastAge = config.lastAge;
		  startTime = config.startTime;
		  addedTime = config.addedTime;
		  level = config.level;
		  xpEnd = config.xpEnd;
		  xpOverworld = config.xpOverworld;
		  xpCoral = config.xpCoral;
		  lang = config.lang;
	}
}
