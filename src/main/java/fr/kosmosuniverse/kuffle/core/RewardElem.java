package fr.kosmosuniverse.kuffle.core;

import lombok.Getter;

/**
 * 
 * @author KosmosUniverse
 *
 */
@Getter
public class RewardElem {
	private final String name;
	private final Integer amount;
	private final boolean enchantOn;
	private final String enchant;
	private final Integer level;
	private final boolean effectOn;
	private final String effect;
	
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

		if (rewardEnchant != null && !rewardEnchant.isEmpty()) {
			enchantOn = true;
			enchant = rewardEnchant;
			level = rewardLevel;
		} else {
			enchantOn = false;
			enchant = null;
			level = 0;
		}
		
		if (rewardEffect != null && !rewardEffect.isEmpty()) {
			effectOn = true;
			effect = rewardEffect;
		} else {
			effectOn = false;
			effect = null;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Name: [").append(name).append("]").append("\n");
		sb.append("Amount: [").append(amount).append("]").append("\n");
		if (enchantOn) {
			sb.append("Enchant: [").append(enchant).append("]").append("\n");
			sb.append("Level: [").append(level).append("]").append("\n");
		}
		
		if (effectOn) {
			sb.append("Effect: [").append(effect).append("]").append("\n");
		}
	
		return (sb.toString());
	}
}