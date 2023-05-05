package main.fr.kosmosuniverse.kuffle.commands;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import main.fr.kosmosuniverse.kuffle.core.AgeManager;
import main.fr.kosmosuniverse.kuffle.core.CraftManager;
import main.fr.kosmosuniverse.kuffle.core.RewardManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleCommandFalseException;

public class KuffleGive extends AKuffleCommand {
	public KuffleGive() {
		super("k-give", true, null, 3, 4, false);
	}

	@Override
	protected boolean runCommand() throws KuffleCommandFalseException {
		Player target;
		Optional<? extends Player> targetTmp = Bukkit.getServer().getOnlinePlayers().stream()
		.filter(player -> player.getName().equalsIgnoreCase(args[0]))
		.findFirst();
		
		if (!targetTmp.isPresent()) {
			player.sendMessage("Player [" + args[0] + "] is not connected.");
			
			return false;
		} else {
			target = targetTmp.get();
		}
		
		if (args.length == 3 && "reward".equals(args[1])) {
			String age = args[2];
			
			if (!AgeManager.ageExists(age)) {
				player.sendMessage("Age [" + age + "] does not exist.");
				
				return false;
			}
			
			RewardManager.givePlayerReward(age, target);
		} else if ("item".equals(args[1])) {
			ItemStack item = null;
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
		if (item.endsWith("_Age")) {
			if (AgeManager.ageExists(item)) {
				return RewardManager.getAgeRewardBox(item);
			}
		}
		
		ItemStack result = CraftManager.findItemByName(item);
		
		if (result != null) {
			result.setAmount(amount);
		}
		
		return result;
	}
}