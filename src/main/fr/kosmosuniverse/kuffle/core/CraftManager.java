package main.fr.kosmosuniverse.kuffle.core;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import main.fr.kosmosuniverse.kuffle.crafts.ACrafts;
import main.fr.kosmosuniverse.kuffle.crafts.Bell;
import main.fr.kosmosuniverse.kuffle.crafts.activables.CoralCompass;
import main.fr.kosmosuniverse.kuffle.crafts.activables.EndPortalFrame;
import main.fr.kosmosuniverse.kuffle.crafts.activables.EndTeleporter;
import main.fr.kosmosuniverse.kuffle.crafts.activables.OverworldTeleporter;
import main.fr.kosmosuniverse.kuffle.crafts.armors.ChainmailBoots;
import main.fr.kosmosuniverse.kuffle.crafts.armors.ChainmailChestplate;
import main.fr.kosmosuniverse.kuffle.crafts.armors.ChainmailHelmet;
import main.fr.kosmosuniverse.kuffle.crafts.armors.ChainmailLeggings;
import main.fr.kosmosuniverse.kuffle.crafts.armors.DiamondHorseArmor;
import main.fr.kosmosuniverse.kuffle.crafts.armors.GoldHorseArmor;
import main.fr.kosmosuniverse.kuffle.crafts.armors.IronHorseArmor;
import main.fr.kosmosuniverse.kuffle.crafts.armors.Saddle;
import main.fr.kosmosuniverse.kuffle.crafts.naturals.BuddingAmethyst;
import main.fr.kosmosuniverse.kuffle.crafts.naturals.MossBlock;
import main.fr.kosmosuniverse.kuffle.crafts.naturals.MossyCobblestone;
import main.fr.kosmosuniverse.kuffle.crafts.naturals.MossyStoneBrick;
import main.fr.kosmosuniverse.kuffle.crafts.naturals.Mycelium;
import main.fr.kosmosuniverse.kuffle.crafts.naturals.PointedDripstone;
import main.fr.kosmosuniverse.kuffle.crafts.naturals.PowderSnowBucket;
import main.fr.kosmosuniverse.kuffle.crafts.naturals.RedNetherBrick;
import main.fr.kosmosuniverse.kuffle.crafts.naturals.RedSand;
import main.fr.kosmosuniverse.kuffle.crafts.naturals.SmallDripleaf;
import main.fr.kosmosuniverse.kuffle.crafts.ores.CoalOre;
import main.fr.kosmosuniverse.kuffle.crafts.ores.CoalOreDeepslate;
import main.fr.kosmosuniverse.kuffle.crafts.ores.CopperOre;
import main.fr.kosmosuniverse.kuffle.crafts.ores.CopperOreDeepslate;
import main.fr.kosmosuniverse.kuffle.crafts.ores.DiamondOre;
import main.fr.kosmosuniverse.kuffle.crafts.ores.DiamondOreDeepslate;
import main.fr.kosmosuniverse.kuffle.crafts.ores.EmeraldOre;
import main.fr.kosmosuniverse.kuffle.crafts.ores.EmeraldOreDeepslate;
import main.fr.kosmosuniverse.kuffle.crafts.ores.ExposedCopper;
import main.fr.kosmosuniverse.kuffle.crafts.ores.GoldOre;
import main.fr.kosmosuniverse.kuffle.crafts.ores.GoldOreDeepslate;
import main.fr.kosmosuniverse.kuffle.crafts.ores.IronOre;
import main.fr.kosmosuniverse.kuffle.crafts.ores.IronOreDeepslate;
import main.fr.kosmosuniverse.kuffle.crafts.ores.LapisOre;
import main.fr.kosmosuniverse.kuffle.crafts.ores.LapisOreDeepslate;
import main.fr.kosmosuniverse.kuffle.crafts.ores.OxidizedCopper;
import main.fr.kosmosuniverse.kuffle.crafts.ores.QuartzOre;
import main.fr.kosmosuniverse.kuffle.crafts.ores.RedstoneOre;
import main.fr.kosmosuniverse.kuffle.crafts.ores.RedstoneOreDeepslate;
import main.fr.kosmosuniverse.kuffle.crafts.ores.WeatheredCopper;
import main.fr.kosmosuniverse.kuffle.crafts.resources.BrainCoralBlock;
import main.fr.kosmosuniverse.kuffle.crafts.resources.BubbleCoralBlock;
import main.fr.kosmosuniverse.kuffle.crafts.resources.Coal;
import main.fr.kosmosuniverse.kuffle.crafts.resources.Diamond;
import main.fr.kosmosuniverse.kuffle.crafts.resources.Emerald;
import main.fr.kosmosuniverse.kuffle.crafts.resources.FireCoralBlock;
import main.fr.kosmosuniverse.kuffle.crafts.resources.HornCoralBlock;
import main.fr.kosmosuniverse.kuffle.crafts.resources.Lapis;
import main.fr.kosmosuniverse.kuffle.crafts.resources.Quartz;
import main.fr.kosmosuniverse.kuffle.crafts.resources.RawCopper;
import main.fr.kosmosuniverse.kuffle.crafts.resources.RawGold;
import main.fr.kosmosuniverse.kuffle.crafts.resources.RawIron;
import main.fr.kosmosuniverse.kuffle.crafts.resources.Redstone;
import main.fr.kosmosuniverse.kuffle.crafts.resources.TubeCoralBlock;
import main.fr.kosmosuniverse.kuffle.utils.ItemUtils;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

public class CraftManager {
	private List<ACrafts> recipes = new ArrayList<>();
	
	public CraftManager() {
		recipes.add(new EndPortalFrame());
		recipes.add(new EndTeleporter());
		recipes.add(new OverworldTeleporter());
		recipes.add(new CoralCompass());
		
		if (VersionManager.findVersionNumber(VersionManager.getVersion()) >= VersionManager.findVersionNumber("1.16")) {
			recipes.add(new ChainmailHelmet());
			recipes.add(new ChainmailChestplate());
			recipes.add(new ChainmailLeggings());
			recipes.add(new ChainmailBoots());
		}
		
		if (VersionManager.findVersionNumber(VersionManager.getVersion()) >= VersionManager.findVersionNumber("1.17")) {
			recipes.add(new MossBlock());
			recipes.add(new SmallDripleaf());
			recipes.add(new PowderSnowBucket());
			recipes.add(new BuddingAmethyst());
		}
		
		if (!Config.getCrafts()) {
			return;
		}
		
		recipes.add(new RedSand());
		recipes.add(new Mycelium());
		recipes.add(new MossyCobblestone());
		recipes.add(new MossyStoneBrick());
		
		recipes.add(new TubeCoralBlock());
		recipes.add(new BubbleCoralBlock());
		recipes.add(new HornCoralBlock());
		recipes.add(new FireCoralBlock());
		recipes.add(new BrainCoralBlock());
		
		recipes.add(new Coal());
		recipes.add(new Lapis());
		recipes.add(new Redstone());
		recipes.add(new Diamond());
		recipes.add(new Emerald());
		recipes.add(new Quartz());
		
		recipes.add(new CoalOre());
		recipes.add(new LapisOre());
		recipes.add(new RedstoneOre());
		recipes.add(new DiamondOre());
		recipes.add(new EmeraldOre());
		recipes.add(new QuartzOre());
		
		recipes.add(new RedNetherBrick());
		
		recipes.add(new Bell());
		
		recipes.add(new Saddle());
		recipes.add(new IronHorseArmor());
		recipes.add(new GoldHorseArmor());
		recipes.add(new DiamondHorseArmor());
		
		if (VersionManager.findVersionNumber(VersionManager.getVersion()) >= VersionManager.findVersionNumber("1.17")) {
			recipes.add(new CoalOreDeepslate());
			recipes.add(new CopperOreDeepslate());
			recipes.add(new DiamondOreDeepslate());
			recipes.add(new EmeraldOreDeepslate());
			recipes.add(new GoldOreDeepslate());
			recipes.add(new IronOreDeepslate());
			recipes.add(new LapisOreDeepslate());
			recipes.add(new RedstoneOreDeepslate());
			recipes.add(new CopperOre());
			recipes.add(new GoldOre());
			recipes.add(new IronOre());
			recipes.add(new RawCopper());
			recipes.add(new RawGold());
			recipes.add(new RawIron());
			recipes.add(new PointedDripstone());
			recipes.add(new ExposedCopper());
			recipes.add(new WeatheredCopper());
			recipes.add(new OxidizedCopper());
		}
	}
	
	public void clear() {
		if (recipes != null) {
			recipes.clear();
		}
	}
	
	public void addCraft(ACrafts craft) {
		recipes.add(craft);
	}
	
	public void removeCraft(String name) {
		ACrafts craft = null;
		
		for (ACrafts tmp : recipes) {
			if (tmp.getName().equals(name)) {
				craft = tmp;
			}
		}
		
		if (craft != null) {
			recipes.remove(craft);
		}
	}
	
	public List<ACrafts> getRecipeList() {
		return (recipes);
	}
	
	public Inventory getAllCraftsInventory() {
		Inventory inv = Bukkit.createInventory(null, Utils.getNbInventoryRows(recipes.size()), "§8AllCustomCrafts");
		int i = 0;
		
		for (ACrafts item : recipes) {
			inv.setItem(i, item.getItem());
			i++;
		}
		
		return (inv);
	}
	
	public ACrafts findCraftInventoryByItem(ItemStack item) {
		for (ACrafts craft : recipes) {
			if (ItemUtils.itemComparison(craft.getItem(), item, item.hasItemMeta(),
					item.hasItemMeta() ? item.getItemMeta().hasDisplayName() : false,
					item.hasItemMeta() ? item.getItemMeta().hasLore() : false)) {
				return (craft);
			}
		}
		
		return null;
	}
	
	public ACrafts findCraftByInventoryName(String invName) {
		for (ACrafts craft : recipes) {
			String name = "§8" + craft.getName();
			
			if (invName.contains(name)) {
				return (craft);
			}
		}
		
		return null;
	}
	
	public ItemStack findItemByName(String itemName) {
		for (ACrafts craft : recipes) {
			if (itemName.equals(craft.getName())) {
				return (craft.getItem());
			}
		}
		
		return null;
	}
}
