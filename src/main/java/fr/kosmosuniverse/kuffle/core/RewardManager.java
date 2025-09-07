package fr.kosmosuniverse.kuffle.core;

import java.util.*;
import java.util.Map.Entry;

import fr.kosmosuniverse.kuffle.utils.FileUtils;
import fr.kosmosuniverse.kuffle.utils.ItemMaker;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.JSONObject;

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
	 * @throws IllegalStateException Utility Class Constructor Exception
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
	 */
	public static void setupRewards(String rewardsContent) {
		JSONObject allObj = FileUtils.readJSONObjectFromContent(rewardsContent);
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
		for (String version : allObj.keySet()) {
			if (VersionManager.isVersionValid(version, null)) {
				JSONObject versionObj = allObj.getJSONObject(version);
				
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
		for (String age : versionObj.keySet()) {
			JSONObject ageObj = versionObj.getJSONObject(age);
			
			if (!rewards.containsKey(age)) {
				rewards.put(age, new HashMap<>());
			}

			setupReward(age, ageObj);
		}
	}
	
	/**
	 * Setup rewards per item
	 * 
	 * @param age		reward item age
	 * @param ageObj	JSON object to parse
	 */
	private static void setupReward(String age, JSONObject ageObj) {
		for (String reward : ageObj.keySet()) {
			JSONObject rewardObj = ageObj.getJSONObject(reward);
			
			int amount = rewardObj.getInt("Amount");
			String enchant = rewardObj.has("Enchant") ? rewardObj.getString("Enchant") : null;
			Integer level = rewardObj.has("Level") ? rewardObj.getInt("Level") : null;
			String effect = rewardObj.has("Effect") ? rewardObj.getString("Effect") : null;
			
			rewards.get(age).put(reward, new RewardElem(reward, amount, enchant, level == null ? -1 : level, effect));
		}
	}
	
	/**
	 * Create Boxes and effects map
	 */
	private static void createBoxesAndEffects() {
		rewards.forEach(RewardManager::createBoxesAndEffectsPerAge);
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
			if (entry.getValue().isEnchantOn()) {
				items.add(setupEnchantedItem(entry.getKey(), entry.getValue()));
			} else {
				items.add(new ItemStack(Objects.requireNonNull(Material.matchMaterial(entry.getKey())), entry.getValue().getAmount()));
			}
		});
		
		if (items.isEmpty()) {
			return null;
		}
		
		ItemStack container = ItemMaker.newItem(AgeManager.getAgeByName(ageName).getBox()).addName(ageName.replace("_Age", "") + " Reward").getItem();
		BlockStateMeta containerMeta = (BlockStateMeta) container.getItemMeta();
		ShulkerBox box = (ShulkerBox) Objects.requireNonNull(containerMeta).getBlockState();
		Inventory inv = box.getInventory();
		
		items.forEach(inv::addItem);
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

			for (Integer i : ret.keySet()) {
				player.getWorld().dropItem(player.getLocation(), ret.get(i));
			}
		}
		
		if (effects.containsKey(ageName)) {
			effects.get(ageName).forEach(player::addPotionEffect);
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
		
		return item.map(Entry::getValue).orElse(null);
	}

	/**
	 * Set up an enchanted item
	 * 
	 * @param key	Item type
	 * @param elem	RewardElem object
	 * 
	 * @return the ItemStack corresponding to the enchanted item
	 */
	private static ItemStack setupEnchantedItem(String key, RewardElem elem) {
		ItemStack it = new ItemStack(Objects.requireNonNull(Material.matchMaterial(key)), elem.getAmount());
		
		if (elem.getEnchant().contains(",")) {
			String[] tmp = elem.getEnchant().split(",");
			
			for (String enchant : tmp) {
				if (getEnchantment(enchant) != null) {
					it.addUnsafeEnchantment(Objects.requireNonNull(getEnchantment(enchant)), elem.getLevel());
				}
			}
		} else {
			if (getEnchantment(elem.getEnchant()) != null) {
				it.addUnsafeEnchantment(Objects.requireNonNull(getEnchantment(elem.getEnchant())), elem.getLevel());
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
		
		effects.get(age).forEach(player::addPotionEffect);
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
