package main.fr.kosmosuniverse.kuffle.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RewardManager {
	private RewardManager() {
		throw new IllegalStateException("Utility class");
	}
	
	public static synchronized Map<String, Map<String, RewardElem>> getAllRewards(List<Age> ages, String rewardsContent, File dataFolder) {
		Map<String, Map<String, RewardElem>> finalMap = new HashMap<>();
		
		int max = AgeManager.getAgeMaxNumber(ages);
		
		for (int ageCnt = 0; ageCnt <= max; ageCnt++) {
			finalMap.put(AgeManager.getAgeByNumber(ages, ageCnt).name, getAgeRewards(AgeManager.getAgeByNumber(ages, ageCnt).name, rewardsContent, dataFolder));
		}
		
		return finalMap;
	}
	
	public static synchronized Map<String, RewardElem> getAgeRewards(String age, String rewardsContent, File dataFolder) {
		Map<String, RewardElem> ageRewards = new HashMap<>();
		JSONObject rewards;
		JSONParser jsonParser = new JSONParser();
		
		try (FileWriter writer = new FileWriter(dataFolder.getPath() + File.separator + "logs.txt", true)) {
			rewards = (JSONObject) jsonParser.parse(rewardsContent);
		
			JSONObject ageObject = new JSONObject();
			
			ageObject = (JSONObject) rewards.get(age);
			
			for (Iterator<?> it = ageObject.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				JSONObject tmp = (JSONObject) ageObject.get(key);
				ageRewards.put(key, new RewardElem(key, Integer.parseInt(((Long) tmp.get("Amount")).toString()), (String) tmp.get("Enchant"), Integer.parseInt(((Long) tmp.get("Level")).toString()), (String) tmp.get("Effect")));
			}
		
			StringBuilder sb = new StringBuilder();

			for (String key : ageRewards.keySet()) {
				sb.append(ageRewards.get(key).toString()).append("\n");
			}
			
			writer.append(sb.toString());
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		
		return ageRewards;
	}
	
	public static synchronized void givePlayerRewardEffect(Map<String, RewardElem> ageReward, Player player) {
		for (String k : ageReward.keySet()) {
			if (k.contains("potion")) {				
				player.addPotionEffect(new PotionEffect(findEffect(ageReward.get(k).getEffect()), 999999, ageReward.get(k).getAmount()));
			}
		}
	}
	
	public static synchronized void givePlayerReward(Map<String, RewardElem> ageReward, Player player, List<Age> ages, int age) {
		List<ItemStack> items = new ArrayList<>();
		
		ItemStack container = new ItemStack(AgeManager.getAgeByNumber(ages, age).box);
		
		BlockStateMeta containerMeta = (BlockStateMeta) container.getItemMeta();
		ShulkerBox box = (ShulkerBox) containerMeta.getBlockState();
		Inventory inv = box.getInventory();
		
		for (String k : ageReward.keySet()) {
			ItemStack it;
			
			if (ageReward.get(k).enchant()) {
				items.add(giveEnchantedItem(k, ageReward.get(k)));
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
		
		ItemMeta itM = container.getItemMeta();
		itM.setDisplayName(AgeManager.getAgeByNumber(ages, age).name.replace("_", " "));
		container.setItemMeta(itM);
		
		Map<Integer, ItemStack> ret = player.getInventory().addItem(container);
		
		if (ret != null) {
			for (Integer i : ret.keySet()) {
				player.getWorld().dropItem(player.getLocation(), ret.get(i));
			}
		}
	}
	
	private static ItemStack giveEnchantedItem(String key, RewardElem elem) {
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
	
	public static void managePreviousEffects(Map<String, RewardElem> ageReward, Player player) {
		for (String key : ageReward.keySet()) {
			if (key.contains("potion")) {
				player.removePotionEffect(findEffect(ageReward.get(key).getEffect()));
			}
		}
	}
	
	public static Enchantment getEnchantment(String enchant) {
		for (Enchantment e : Enchantment.values()) {
			if (e.getKey().toString().split(":")[1].equals(enchant)) {
				return e;
			}
		}
		return null;
	}

	public static PotionEffectType findEffect(String effect) {
		for (PotionEffectType potion : PotionEffectType.values()) {
			if (potion.getName().equalsIgnoreCase(effect)) {
				return potion;
			}
		}
		
		return null;
	}
}
