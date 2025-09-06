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
	
	private boolean tips;
	private boolean saturation;
	private boolean spread;
	private boolean rewards;
	private boolean skip;
	private boolean crafts;
	private boolean team;
	private boolean teamInv;
	private boolean same;
	private boolean duoMode;
	private boolean sbttMode;
	private boolean printTab;
	private boolean printTabAll;
	private boolean endOne;
	private boolean passiveAll;
	private boolean passiveTeam;
	private int sbttAmount;
	private int teamSize;
	private int teamInvSize;
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
		tips = config.tips;
		saturation = config.saturation;
		spread = config.spread;
		rewards = config.rewards;
		skip = config.skip;
		crafts = config.crafts;
		team = config.team;
		teamInv = config.teamInv;
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
		teamInvSize = config.teamInvSize;
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
