package main.fr.kosmosuniverse.kuffle.core;

public class RewardElem {
	private String name;
	private Integer amount;
	private boolean enchantOn = false;
	private String enchant = null;
	private Integer level;
	private boolean effectOn = false;
	private String effect = null;
	
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
	
	public String getName() {
		return (name);
	}
	
	public Integer getAmount() {
		return (amount);
	}
	
	public Boolean enchant() {
		return (enchantOn);
	}
	
	public Boolean effect() {
		return (effectOn);
	}
	
	public Integer getLevel() {
		return (level);
	}
	
	public String getEnchant() {
		return (enchant);
	}
	
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