package fr.kosmosuniverse.kuffle.commands;

import fr.kosmosuniverse.kuffle.core.AgeManager;
import fr.kosmosuniverse.kuffle.core.CraftManager;
import fr.kosmosuniverse.kuffle.core.RewardManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KuffleGive extends AKuffleCommand {
	public KuffleGive() {
		super("k-give", true, null, 3, 4, false);
	}

	@Override
	protected boolean runCommand() {
		Player target = Bukkit.getPlayer(args[0]);
		
		if (target == null) {
			player.sendMessage("Player [" + args[0] + "] is not connected.");
			
			return false;
		}
		
		if (args.length == 3 && "reward".equals(args[1])) {
			String age = args[2];
			
			if (!AgeManager.ageExists(age)) {
				player.sendMessage("Age [" + age + "] does not exist.");
				
				return false;
			}
			
			RewardManager.givePlayerReward(age, target);
		} else if ("item".equals(args[1])) {
			ItemStack item;
			int amount = 1;
			
			try {
				if (args.length == 4) {
					amount = Integer.parseInt(args[3]);
				}
			} catch (NumberFormatException e) {
				player.sendMessage("Amount [" + args[3] + "] is invalid.");
				
				return false;				
			} 
			
			item = getKuffleItem(args[2], amount);
			
			if (item == null) {
				player.sendMessage("Item [" + args[2] + "] does not exists.");
				
				return false;
			}
			
			target.getInventory().addItem(item);
		}
		
		return true;
	}

	/**
	 * Search if item is a Kuffle made Item
	 * 
	 * @param item		The Item to search
	 * @param amount	The amount to give
	 * 
	 * @return the item if it is a Kuffle one, null instead
	 */
	private ItemStack getKuffleItem(String item, int amount) {
		if (item.endsWith("_Age") && AgeManager.ageExists(item)) {
			return RewardManager.getAgeRewardBox(item);
		}
		
		ItemStack result = CraftManager.findItemByName(item);
		
		if (result != null) {
			result.setAmount(amount);
		}
		
		return result;
	}
}