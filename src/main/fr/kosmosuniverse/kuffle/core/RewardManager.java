package main.fr.kosmosuniverse.kuffle.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import main.fr.kosmosuniverse.kuffle.utils.ItemUtils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class RewardManager {
	private static Map<String, Map<String, RewardElem>> rewards = null;
	
	/**
	 * Private RewardManager constructor
	 * 
	 * @throws IllegalStateException
	 */
	private RewardManager() {
		throw new IllegalStateException("Utility class");
	}
	
	/**
	 * Clears the rewards map
	 */
	public static void clear() {
		if (rewards != null) {
			rewards.forEach((k, v) -> {
				if (v != null) {
					v.clear();
				}
			});
			
			rewards.clear();
		}
	}
	
	/**
	 * Setup all rewards
	 * 
	 * @param rewardsContent	reward file content
	 * 
	 * @throws ParseException if JSONParser.parse fails
	 */
	public static void setupRewards(String rewardsContent) throws ParseException {
		JSONParser jsonParser = new JSONParser();
		JSONObject allObj = (JSONObject) jsonParser.parse(rewardsContent);
		
		rewards = new HashMap<>();
		
		setupVersion(allObj);
	}
	
	private static void setupVersion(JSONObject allObj) {
		for (Object version : allObj.keySet()) {
			if (VersionManager.isVersionValid(version.toString(), null)) {
				JSONObject versionObj = (JSONObject) allObj.get(version);
				
				setupAges(versionObj);
			}
		}
	}
	
	private static void setupAges(JSONObject versionObj) {
		for (Object age : versionObj.keySet()) {
			JSONObject ageObj = (JSONObject) versionObj.get(age);
			
			if (!rewards.containsKey(age.toString())) {
				rewards.put(age.toString(), new HashMap<>());
			}

			setupReward(age.toString(), ageObj);
		}
	}
	
	private static void setupReward(String age, JSONObject ageObj) {
		for (Object reward : ageObj.keySet()) {
			JSONObject rewardObj = (JSONObject) ageObj.get(reward);
			
			String amount = rewardObj.get("Amount").toString();
			String enchant = rewardObj.containsKey("Enchant") ? rewardObj.get("Enchant").toString() : null;
			String level = rewardObj.containsKey("Level") ? rewardObj.get("Level").toString() : null;
			String effect = rewardObj.containsKey("Effect") ? rewardObj.get("Effect").toString() : null;
			
			rewards.get(age).put(reward.toString(), new RewardElem(reward.toString(), Integer.parseInt(amount.toString()), enchant, level == null ? -1 : Integer.parseInt(level.toString()), effect));
		}
	}
	
	/**
	 * Gives an age rewards to a specific player
	 * 
	 * @param ageName	The age name to take rewards from
	 * @param player	The player that will receive these rewards
	 */
	public static void givePlayerReward(String ageName, Player player) {
		List<ItemStack> items = new ArrayList<>();
		Map<String, RewardElem> ageReward = rewards.get(ageName);
		
		ItemStack container = ItemUtils.itemMaker(AgeManager.getAgeByName(ageName).box, 1, ageName.replace("_Age", "") + " Reward", "Owner:" + player.getName());
		
		BlockStateMeta containerMeta = (BlockStateMeta) container.getItemMeta();
		ShulkerBox box = (ShulkerBox) containerMeta.getBlockState();
		Inventory inv = box.getInventory();
		
		for (String k : ageReward.keySet()) {
			ItemStack it;
			
			if (ageReward.get(k).enchant()) {
				items.add(setupEnchantedItem(k, ageReward.get(k)));
			} else if (k.contains("potion")) {
				givePotionEffect(ageReward.get(k), player);
			} else {
				it = new ItemStack(Material.matchMaterial(k), ageReward.get(k).getAmount());
				items.add(new ItemStack(it));
			}
		}

		for (ItemStack it : items) {
			inv.addItem(it);
		}

		box.update();
		containerMeta.setBlockState(box);
		container.setItemMeta(containerMeta);
		
		Map<Integer, ItemStack> ret = player.getInventory().addItem(container);
		
		if (ret != null) {
			for (Integer i : ret.keySet()) {
				player.getWorld().dropItem(player.getLocation(), ret.get(i));
			}
		}
	}
	
	/**
	 * Setup an enchanted item
	 * 
	 * @param key	Item type
	 * @param elem	RewardElemn object
	 * 
	 * @return the ItemStack corresponding to the enchanted item
	 */
	private static ItemStack setupEnchantedItem(String key, RewardElem elem) {
		ItemStack it = new ItemStack(Material.matchMaterial(key), elem.getAmount());
		
		if (elem.getEnchant().contains(",")) {
			String[] tmp = elem.getEnchant().split(",");
			
			for (String enchant : tmp) {
				if (getEnchantment(enchant) != null) {
					it.addUnsafeEnchantment(getEnchantment(enchant), elem.getLevel());
				}
			}
		} else {
			if (getEnchantment(elem.getEnchant()) != null) {
				it.addUnsafeEnchantment(getEnchantment(elem.getEnchant()), elem.getLevel());		
			}
		}
		
		return it;
	}
	
	/**
	 * Gives potion effects to a player
	 * 
	 * @param elem		The potion effect to give as a RewardElem
	 * @param player	The player that will receive the effects
	 */
	private static void givePotionEffect(RewardElem elem, Player player) {
		if (elem.getEffect().contains(",")) {
			String[] tmp = elem.getEffect().split(",");
			
			for (String effect : tmp) {
				if (getEnchantment(effect) != null) {
					player.addPotionEffect(new PotionEffect(findEffect(elem.getEffect()), 999999, elem.getAmount()));
				}
			}
		} else {
			player.addPotionEffect(new PotionEffect(findEffect(elem.getEffect()), 999999, elem.getAmount()));
		}
	}
	
	/**
	 * Gets an enchantment from its name
	 * 
	 * @param enchant	the enchantment to get
	 * 
	 * @return the Enchantment Object if enchant exists, null instead
	 */
	public static Enchantment getEnchantment(String enchant) {
		for (Enchantment e : Enchantment.values()) {
			if (e.getKey().toString().split(":")[1].equals(enchant)) {
				return e;
			}
		}
		return null;
	}

	/**
	 * Gets a specific effect 
	 * 
	 * @param effect	the effect name
	 * 
	 * @return The PotionEffectType if effect exists, null instead
	 */
	public static PotionEffectType findEffect(String effect) {
		for (PotionEffectType potion : PotionEffectType.values()) {
			if (potion.getName().equalsIgnoreCase(effect)) {
				return potion;
			}
		}
		
		return null;
	}
	
	/**
	 * Gives to a specific player the effects of a specific Age
	 * 
	 * @param player	The player that will receive the effect
	 * @param age		The Age the effect will be taken from
	 */
	public static void givePlayerRewardEffect(Player player, String age) {
		Map<String, RewardElem> ageReward = rewards.get(age);
		
		for (String k : ageReward.keySet()) {
			if (k.contains("potion")) {				
				player.addPotionEffect(new PotionEffect(findEffect(ageReward.get(k).getEffect()), 999999, ageReward.get(k).getAmount()));
			}
		}
	}
	
	/**
	 * Removes previous reward effects
	 * 
	 * @param ageName	The age name of the rewards it may remove
	 * @param player	The player concerned about this removal
	 */
	public static void removePreviousRewardEffects(String ageName, Player player) {
		Map<String, RewardElem> ageReward = rewards.get(ageName);
		
		for (String key : ageReward.keySet()) {
			if (key.contains("potion")) {
				player.removePotionEffect(findEffect(ageReward.get(key).getEffect()));
			}
		}
	}
}
