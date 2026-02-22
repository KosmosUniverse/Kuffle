package fr.kosmosuniverse.kuffle.core;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 
 * @author KosmosUniverse
 *
 */
@Getter
@Setter
public class ConfigHolder implements Serializable {
	private static final long serialVersionUID = 1L;

	// System config
	private boolean logResults;
	private String startType;

	// Game config
	private boolean tips;
	private boolean saturation;
	private boolean spread;
	private boolean rewards;
	private boolean skip;
	private boolean crafts;
	private boolean team;
	private boolean teamInv;
	private boolean coop;
	private boolean coopSkip;
	private boolean same;
	private boolean duoMode;
	private boolean sbttMode;
	private boolean printTab;
	private boolean endOne;
	private boolean passiveAll;
	private boolean passiveTeam;
	private int sbttAmount;
	private int teamSize;
	private int teamInvSize;
	private int coopBase;
	private int coopUpdate;
	private int spreadDistance;
	private int spreadRadius;
	private int targetPerAge;
	private int skipAge;
	private int lastAge;
	private int startTime;
	private int addedTime;
	private int level;
	private int xpEnd;
	private int xpOverworld;
	private int xpCoral;
	private String lang;
	
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
		logResults = config.logResults;
		startType = config.startType;

		tips = config.tips;
		saturation = config.saturation;
		spread = config.spread;
		rewards = config.rewards;
		skip = config.skip;
		crafts = config.crafts;
		team = config.team;
		teamInv = config.teamInv;
		coop = config.coop;
		coopSkip = config.coopSkip;
		same = config.same;
		duoMode = config.duoMode;
		sbttMode = config.sbttMode;
		printTab = config.printTab;
		endOne = config.endOne;
		passiveAll = config.passiveAll;
		passiveTeam = config.passiveTeam;
		sbttAmount = config.sbttAmount;
		teamSize = config.teamSize;
		teamInvSize = config.teamInvSize;
		coopBase = config.coopBase;
		coopUpdate = config.coopUpdate;
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
