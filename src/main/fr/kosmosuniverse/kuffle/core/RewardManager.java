package main.fr.kosmosuniverse.kuffle.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

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
	private static final String POTION = "potion";
	private static Map<String, Map<String, RewardElem>> rewards = null;
	private static Map<String, ItemStack> boxes = null;
	private static Map<String, List<PotionEffect>> effects = null;
	
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
		if (boxes != null) {
			boxes.clear();
		}
		
		if (effects != null) {
			effects.forEach((k, v) ->  {
				if (v != null) {
					v.clear();
				}
			});
			
			effects.clear();
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
		boxes = new HashMap<>();
		effects = new HashMap<>();
		
		setupVersion(allObj);
		
		createBoxesAndEffects();

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
	 * Setup rewards per version
	 * 
	 * @param allObj	JSON object to parse
	 */
	private static void setupVersion(JSONObject allObj) {
		for (Object version : allObj.keySet()) {
			if (VersionManager.isVersionValid(version.toString(), null)) {
				JSONObject versionObj = (JSONObject) allObj.get(version);
				
				setupAges(versionObj);
			}
		}
	}
	
	/**
	 * Setup rewards per Age
	 * 
	 * @param versionObj JSON object to parse
	 */
	private static void setupAges(JSONObject versionObj) {
		for (Object age : versionObj.keySet()) {
			JSONObject ageObj = (JSONObject) versionObj.get(age);
			
			if (!rewards.containsKey(age.toString())) {
				rewards.put(age.toString(), new HashMap<>());
			}

			setupReward(age.toString(), ageObj);
		}
	}
	
	/**
	 * Setup rewards per item
	 * 
	 * @param age		reward item age
	 * @param ageObj	JSON object to parse
	 */
	private static void setupReward(String age, JSONObject ageObj) {
		for (Object reward : ageObj.keySet()) {
			JSONObject rewardObj = (JSONObject) ageObj.get(reward);
			
			String amount = rewardObj.get("Amount").toString();
			String enchant = rewardObj.containsKey("Enchant") ? rewardObj.get("Enchant").toString() : null;
			String level = rewardObj.containsKey("Level") ? rewardObj.get("Level").toString() : null;
			String effect = rewardObj.containsKey("Effect") ? rewardObj.get("Effect").toString() : null;
			
			rewards.get(age).put(reward.toString(), new RewardElem(reward.toString(), Integer.parseInt(amount), enchant, level == null ? -1 : Integer.parseInt(level), effect));
		}
	}
	
	/**
	 * Create Boxes and effects map
	 */
	private static void createBoxesAndEffects() {
		rewards.entrySet().stream()
		.forEach(entry -> createBoxesAndEffectsPerAge(entry.getKey(), entry.getValue()));
	}
	
	/**
	 * Fills boxes and effects map per age
	 * 
	 * @param ageName	The reward's age 
	 * @param rewards	The reward to split in items and effects
	 */
	private static void createBoxesAndEffectsPerAge(String ageName, Map<String, RewardElem> rewards) {
		ItemStack item = createBoxPerAge(ageName, rewards);
		
		if (item != null) {
			boxes.put(ageName, item);
		}
		
		createEffectPerAge(ageName, rewards);		
	}
	
	/**
	 * Create Shulker reward box and its content
	 * 
	 * @param ageName	The age reward box
	 * @param rewards	The rewards to put inside
	 * 
	 * @return the shulker box, or null if rewards is empty or has not items
	 */
	private static ItemStack createBoxPerAge(String ageName, Map<String, RewardElem> rewards) {
		List<ItemStack> items = new ArrayList<>();
		
		rewards.entrySet().stream()
		.filter(entry -> !entry.getKey().contains(POTION))
		.forEach(entry -> {
			if (entry.getValue().enchant()) {
				items.add(setupEnchantedItem(entry.getKey(), entry.getValue()));
			} else {
				items.add(new ItemStack(Material.matchMaterial(entry.getKey()), entry.getValue().getAmount()));
			}
		});
		
		if (items.isEmpty()) {
			return null;
		}
		
		ItemStack container = ItemUtils.itemMaker(AgeManager.getAgeByName(ageName).getBox(), 1, ageName.replace("_Age", "") + " Reward");
		BlockStateMeta containerMeta = (BlockStateMeta) container.getItemMeta();
		ShulkerBox box = (ShulkerBox) containerMeta.getBlockState();
		Inventory inv = box.getInventory();
		
		items.forEach(item -> inv.addItem(item));
		box.update();
		containerMeta.setBlockState(box);
		container.setItemMeta(containerMeta);
		
		items.clear();
		
		return container;
	}
	
	/**
	 * Creates effect list for an Age
	 * 
	 * @param ageName	The age of reward
	 * @param rewards	The reward to search
	 * 
	 * @return the list of potion effect this age reward contains, null if none
	 */
	private static void createEffectPerAge(String ageName, Map<String, RewardElem> ageRewards) {
		effects.put(ageName, new ArrayList<>());

		ageRewards.entrySet().stream()
		.filter(entry -> entry.getKey().contains(POTION))
		.forEach(entry -> {
			if (entry.getValue().getEffect().contains(",")) {
				String[] rawEffects = entry.getValue().getEffect().split(",");
				
				for (String e : rawEffects) {
					PotionEffectType p = findEffect(e);

					if (p != null) {
						effects.get(ageName).add(new PotionEffect(p, 999999, entry.getValue().getAmount()));
					}
				}
			} else {
				PotionEffectType p = findEffect(entry.getValue().getEffect());

				if (p != null) {
					effects.get(ageName).add(new PotionEffect(p, 999999, entry.getValue().getAmount()));
				}
			}
		});
	}
	
	/**
	 * Gives an age rewards to a specific player
	 * 
	 * @param ageName	The age name to take rewards from
	 * @param player	The player that will receive these rewards
	 */
	public static void givePlayerReward(String ageName, Player player) {
		if (boxes.containsKey(ageName)) {
			Map<Integer, ItemStack> ret = player.getInventory().addItem(boxes.get(ageName));
			
			if (ret != null) {
				for (Integer i : ret.keySet()) {
					player.getWorld().dropItem(player.getLocation(), ret.get(i));
				}
			}
		}
		
		if (effects.containsKey(ageName)) {
			effects.get(ageName).forEach(effect -> player.addPotionEffect(effect));
		}
	}
	
	/**
	 * Gets shulker reward box
	 * 
	 * @param ageName	The age
	 * 
	 * @return the shulker box as an ItemStack
	 */
	public static ItemStack getAgeRewardBox(String ageName) {
		Optional<Entry<String, ItemStack>> item = boxes.entrySet().stream()
		.filter(entry -> entry.getKey().equalsIgnoreCase(ageName))
		.findAny();
		
		return item.isPresent() ? item.get().getValue() : null;
	}
	
	/**
	 * Gets the ages reward box
	 * 
	 * @return ages box ItemStack list
	 */
	public static List<ItemStack> getAgeRewardBoxes() {
		return boxes.entrySet().stream().map(entry -> entry.getValue()).collect(Collectors.toList());
	}
	
	/**
	 * Gives the reward effect to a specific player for a specific age
	 * 
	 * @param ageName	The age
	 * @param player	The recipient
	 */
	public static void givePlayerAgeRewardEffect(String ageName, Player player) {
		effects.entrySet().stream()
		.filter(entry -> entry.getKey().equalsIgnoreCase(ageName))
		.forEach(entry -> entry.getValue().forEach(e -> player.addPotionEffect(e)));
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
		if (!effects.containsKey(age)) {
			return ;
		}
		
		effects.get(age).forEach(effect -> player.addPotionEffect(effect));
	}
	
	/**
	 * Removes previous reward effects
	 * 
	 * @param ageName	The age name of the rewards it may remove
	 * @param player	The player concerned about this removal
	 */
	public static void removePreviousRewardEffects(String ageName, Player player) {
		if (!effects.containsKey(ageName)) {
			return ;
		}
		
		effects.get(ageName).forEach(effect -> player.removePotionEffect(effect.getType()));
	}
}
