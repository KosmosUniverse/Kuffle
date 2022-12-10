package main.fr.kosmosuniverse.kuffle.core;

import java.io.Serializable;

/**
 * 
 * @author KosmosUniverse
 *
 */
class ConfigHolder implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private boolean saturation;
	private boolean spread;
	private boolean rewards;
	private boolean skip;
	private boolean crafts;
	private boolean team;
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

	/**
	 * @return the saturation
	 */
	public boolean isSaturation() {
		return saturation;
	}

	/**
	 * @return the spread
	 */
	public boolean isSpread() {
		return spread;
	}

	/**
	 * @return the rewards
	 */
	public boolean isRewards() {
		return rewards;
	}

	/**
	 * @return the skip
	 */
	public boolean isSkip() {
		return skip;
	}

	/**
	 * @return the crafts
	 */
	public boolean isCrafts() {
		return crafts;
	}

	/**
	 * @return the team
	 */
	public boolean isTeam() {
		return team;
	}

	/**
	 * @return the same
	 */
	public boolean isSame() {
		return same;
	}

	/**
	 * @return the duoMode
	 */
	public boolean isDuoMode() {
		return duoMode;
	}

	/**
	 * @return the sbttMode
	 */
	public boolean isSbttMode() {
		return sbttMode;
	}

	/**
	 * @return the printTab
	 */
	public boolean isPrintTab() {
		return printTab;
	}

	/**
	 * @return the printTabAll
	 */
	public boolean isPrintTabAll() {
		return printTabAll;
	}

	/**
	 * @return the endOne
	 */
	public boolean isEndOne() {
		return endOne;
	}

	/**
	 * @return the passiveAll
	 */
	public boolean isPassiveAll() {
		return passiveAll;
	}

	/**
	 * @return the passiveTeam
	 */
	public boolean isPassiveTeam() {
		return passiveTeam;
	}

	/**
	 * @return the sbttAmount
	 */
	public int getSbttAmount() {
		return sbttAmount;
	}

	/**
	 * @return the teamSize
	 */
	public int getTeamSize() {
		return teamSize;
	}

	/**
	 * @return the spreadDistance
	 */
	public int getSpreadDistance() {
		return spreadDistance;
	}

	/**
	 * @return the spreadRadius
	 */
	public int getSpreadRadius() {
		return spreadRadius;
	}

	/**
	 * @return the targetPerAge
	 */
	public int getTargetPerAge() {
		return targetPerAge;
	}

	/**
	 * @return the skipAge
	 */
	public int getSkipAge() {
		return skipAge;
	}

	/**
	 * @return the lastAge
	 */
	public int getLastAge() {
		return lastAge;
	}

	/**
	 * @return the startTime
	 */
	public int getStartTime() {
		return startTime;
	}

	/**
	 * @return the addedTime
	 */
	public int getAddedTime() {
		return addedTime;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @return the xpEnd
	 */
	public int getXpEnd() {
		return xpEnd;
	}

	/**
	 * @return the xpOverworld
	 */
	public int getXpOverworld() {
		return xpOverworld;
	}

	/**
	 * @return the xpCoral
	 */
	public int getXpCoral() {
		return xpCoral;
	}

	/**
	 * @return the lang
	 */
	public String getLang() {
		return lang;
	}

	/**
	 * @param saturation the saturation to set
	 */
	public void setSaturation(boolean saturation) {
		this.saturation = saturation;
	}

	/**
	 * @param spread the spread to set
	 */
	public void setSpread(boolean spread) {
		this.spread = spread;
	}

	/**
	 * @param rewards the rewards to set
	 */
	public void setRewards(boolean rewards) {
		this.rewards = rewards;
	}

	/**
	 * @param skip the skip to set
	 */
	public void setSkip(boolean skip) {
		this.skip = skip;
	}

	/**
	 * @param crafts the crafts to set
	 */
	public void setCrafts(boolean crafts) {
		this.crafts = crafts;
	}

	/**
	 * @param team the team to set
	 */
	public void setTeam(boolean team) {
		this.team = team;
	}

	/**
	 * @param same the same to set
	 */
	public void setSame(boolean same) {
		this.same = same;
	}

	/**
	 * @param duoMode the duoMode to set
	 */
	public void setDuoMode(boolean duoMode) {
		this.duoMode = duoMode;
	}

	/**
	 * @param sbttMode the sbttMode to set
	 */
	public void setSbttMode(boolean sbttMode) {
		this.sbttMode = sbttMode;
	}

	/**
	 * @param printTab the printTab to set
	 */
	public void setPrintTab(boolean printTab) {
		this.printTab = printTab;
	}

	/**
	 * @param printTabAll the printTabAll to set
	 */
	public void setPrintTabAll(boolean printTabAll) {
		this.printTabAll = printTabAll;
	}

	/**
	 * @param endOne the endOne to set
	 */
	public void setEndOne(boolean endOne) {
		this.endOne = endOne;
	}

	/**
	 * @param passiveAll the passiveAll to set
	 */
	public void setPassiveAll(boolean passiveAll) {
		this.passiveAll = passiveAll;
	}

	/**
	 * @param passiveTeam the passiveTeam to set
	 */
	public void setPassiveTeam(boolean passiveTeam) {
		this.passiveTeam = passiveTeam;
	}

	/**
	 * @param sbttAmount the sbttAmount to set
	 */
	public void setSbttAmount(int sbttAmount) {
		this.sbttAmount = sbttAmount;
	}

	/**
	 * @param teamSize the teamSize to set
	 */
	public void setTeamSize(int teamSize) {
		this.teamSize = teamSize;
	}

	/**
	 * @param spreadDistance the spreadDistance to set
	 */
	public void setSpreadDistance(int spreadDistance) {
		this.spreadDistance = spreadDistance;
	}

	/**
	 * @param spreadRadius the spreadRadius to set
	 */
	public void setSpreadRadius(int spreadRadius) {
		this.spreadRadius = spreadRadius;
	}

	/**
	 * @param targetPerAge the targetPerAge to set
	 */
	public void setTargetPerAge(int targetPerAge) {
		this.targetPerAge = targetPerAge;
	}

	/**
	 * @param skipAge the skipAge to set
	 */
	public void setSkipAge(int skipAge) {
		this.skipAge = skipAge;
	}

	/**
	 * @param lastAge the lastAge to set
	 */
	public void setLastAge(int lastAge) {
		this.lastAge = lastAge;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	/**
	 * @param addedTime the addedTime to set
	 */
	public void setAddedTime(int addedTime) {
		this.addedTime = addedTime;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * @param xpEnd the xpEnd to set
	 */
	public void setXpEnd(int xpEnd) {
		this.xpEnd = xpEnd;
	}

	/**
	 * @param xpOverworld the xpOverworld to set
	 */
	public void setXpOverworld(int xpOverworld) {
		this.xpOverworld = xpOverworld;
	}

	/**
	 * @param xpCoral the xpCoral to set
	 */
	public void setXpCoral(int xpCoral) {
		this.xpCoral = xpCoral;
	}

	/**
	 * @param lang the lang to set
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}
}
