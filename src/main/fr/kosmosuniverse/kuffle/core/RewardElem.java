package main.fr.kosmosuniverse.kuffle.core;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class RewardElem {
	private String name;
	private Integer amount;
	private boolean enchantOn = false;
	private String enchant = null;
	private Integer level;
	private boolean effectOn = false;
	private String effect = null;
	
	/**
	 * Constructor
	 * 
	 * @param rewardName	Reward name
	 * @param rewardAmount	Reward amount
	 * @param rewardEnchant	Reward enchant, "" if not used
	 * @param rewardLevel	Reward level for enchant or effect
	 * @param rewardEffect	Reward effect, "" if not used
	 */
	public RewardElem(String rewardName, Integer rewardAmount, String rewardEnchant, Integer rewardLevel, String rewardEffect) {
		name = rewardName;
		amount = rewardAmount;
		if (rewardEnchant != null && !rewardEnchant.equals("")) {
			enchantOn = true;
			enchant = rewardEnchant;
			level = rewardLevel;
		}
		
		if (rewardEffect != null && !rewardEffect.equals("")) {
			effectOn = true;
			effect = rewardEffect;
		}
	}
	
	/**
	 * Gets the reward name
	 * 
	 * @return the name
	 */
	public String getName() {
		return (name);
	}
	
	/**
	 * Gets the reward amount 
	 * 
	 * @return the amount
	 */
	public Integer getAmount() {
		return (amount);
	}
	
	/**
	 * Gets the reward enchant status
	 * 
	 * @return True if enchant is used, False instead
	 */
	public Boolean enchant() {
		return (enchantOn);
	}
	
	/**
	 * Gets the reward effect status
	 * 
	 * @return True if effect is used, False instead
	 */
	public Boolean effect() {
		return (effectOn);
	}
	
	/**
	 * Gets the reward level
	 * 
	 * @return the level
	 */
	public Integer getLevel() {
		return (level);
	}
	
	/**
	 * Gets the reward enchant
	 * 
	 * @return the enchant
	 */
	public String getEnchant() {
		return (enchant);
	}
	
	/**
	 * Gets the reward effect
	 * 
	 * @return the effect
	 */
	public String getEffect() {
		return (effect);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Name: [" + name + "]").append("\n");
		sb.append("Amount: [" + amount + "]").append("\n");
		if (enchantOn) {
			sb.append("Enchant: [" + enchant + "]").append("\n");
			sb.append("Level: [" + level + "]").append("\n");
		}
		
		if (effectOn) {
			sb.append("Effect: [" + effect + "]").append("\n");
		}
	
		return (sb.toString());
	}
}