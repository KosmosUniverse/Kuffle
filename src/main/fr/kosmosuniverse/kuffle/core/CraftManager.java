package main.fr.kosmosuniverse.kuffle.core;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.crafts.ACrafts;
import main.fr.kosmosuniverse.kuffle.crafts.Bell;
import main.fr.kosmosuniverse.kuffle.crafts.activables.CoralCompass;
import main.fr.kosmosuniverse.kuffle.crafts.activables.EndPortalFrame;
import main.fr.kosmosuniverse.kuffle.crafts.activables.EndTeleporter;
import main.fr.kosmosuniverse.kuffle.crafts.activables.OverworldTeleporter;
import main.fr.kosmosuniverse.kuffle.crafts.activables.Template;
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
import main.fr.kosmosuniverse.kuffle.type.KuffleType;
import main.fr.kosmosuniverse.kuffle.utils.ItemUtils;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class CraftManager {
	private static List<ACrafts> recipes = new ArrayList<>();
	
	/**
	 * Setups the crafts (Mandatory and Optional if Crafts option is true)
	 * 
	 * @param gameType	The Game type to know which crafts to load
	 */
	public static void setupCrafts(KuffleType.Type gameType) {
		recipes.add(new EndPortalFrame());
		
		if (gameType == KuffleType.Type.ITEMS) {
			loadItemsCrafts();
		} else if (gameType == KuffleType.Type.BLOCKS) {
			loadBlocksCrafts();
		}
		
		if (VersionManager.getVersionByValue(VersionManager.getVersion()) >= VersionManager.getVersionByValue("1.17")) {
			recipes.add(new MossBlock());
			recipes.add(new SmallDripleaf());
			recipes.add(new BuddingAmethyst());
		}
		
		if (!Config.getCrafts()) {
			return;
		}
		
		// Rares
		recipes.add(new RedSand());
		recipes.add(new Mycelium());
		recipes.add(new MossyCobblestone());
		recipes.add(new MossyStoneBrick());
		
		// Corals
		recipes.add(new TubeCoralBlock());
		recipes.add(new BubbleCoralBlock());
		recipes.add(new HornCoralBlock());
		recipes.add(new FireCoralBlock());
		recipes.add(new BrainCoralBlock());
		
		// Resources
		recipes.add(new Coal());
		recipes.add(new Lapis());
		recipes.add(new Redstone());
		recipes.add(new Diamond());
		recipes.add(new Emerald());
		recipes.add(new Quartz());
		
		// Ores
		recipes.add(new CoalOre());
		recipes.add(new LapisOre());
		recipes.add(new RedstoneOre());
		recipes.add(new DiamondOre());
		recipes.add(new EmeraldOre());
		recipes.add(new QuartzOre());
		
		// Specifics
		recipes.add(new RedNetherBrick());
		recipes.add(new Bell());
		recipes.add(new Saddle());
		
		if (VersionManager.getVersionByValue(VersionManager.getVersion()) >= VersionManager.getVersionByValue("1.17")) {
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
	
	/**
	 * Loads only the Items game type custom crafts
	 */
	private static void loadItemsCrafts() {
		recipes.add(new EndTeleporter());
		recipes.add(new OverworldTeleporter());
		recipes.add(new CoralCompass());
		
		if (VersionManager.getVersionByValue(VersionManager.getVersion()) >= VersionManager.getVersionByValue("1.16")) {
			recipes.add(new ChainmailHelmet());
			recipes.add(new ChainmailChestplate());
			recipes.add(new ChainmailLeggings());
			recipes.add(new ChainmailBoots());
		}
		
		if (VersionManager.getVersionByValue(VersionManager.getVersion()) >= VersionManager.getVersionByValue("1.17")) {
			recipes.add(new MossBlock());
			recipes.add(new SmallDripleaf());
			recipes.add(new BuddingAmethyst());
		}
		
		if (Config.getCrafts()) {
			recipes.add(new IronHorseArmor());
			recipes.add(new GoldHorseArmor());
			recipes.add(new DiamondHorseArmor());
		}
	}
	
	/**
	 * Loads only the Blocks game type custom crafts
	 */
	private static void loadBlocksCrafts() {
		//Nothing for the moment
	}
	
	/**
	 * Clears the recipes list
	 */
	public static void clear() {
		if (recipes != null) {
			recipes.clear();
		}
	}
	
	/**
	 * Add a craft to the recipes list
	 * 
	 * @param craft	The ACraft object to add
	 */
	public static void addCraft(ACrafts craft) {
		recipes.add(craft);
		KuffleMain.current.getServer().addRecipe(craft.getRecipe());
	}
	
	/**
	 * Removes a craft from the recipes list by name
	 * 
	 * @param name	The ACraft object name
	 */
	public static void removeCraft(String name) {
		ACrafts craft = null;
		
		for (ACrafts tmp : recipes) {
			if (tmp.getName().equals(name)) {
				craft = tmp;
			}
		}
		
		if (craft != null) {
			recipes.remove(craft);
			
			NamespacedKey n = new NamespacedKey(KuffleMain.current, name);
			KuffleMain.current.getServer().removeRecipe(n);
		}
	}
	
	/**
	 * Gets the recipes list
	 * 
	 * @return the recipes list as ACraft list
	 */
	public static List<ACrafts> getRecipeList() {
		return (recipes);
	}
	
	/**
	 * Get an Inventory containing all custom crafts
	 * 
	 * @return the inventory of all crafts
	 */
	public static Inventory getAllCraftsInventory() {
		Inventory inv = Bukkit.createInventory(null, Utils.getNbInventoryRows(recipes.size()), "§8AllCustomCrafts");
		int i = 0;
		
		for (ACrafts item : recipes) {
			inv.setItem(i, item.getItem());
			i++;
		}
		
		return (inv);
	}
	
	/**
	 * Gets ACraft object by ItemStack
	 * 
	 * @param item	the item as a key to find ACraft
	 * 
	 * @return the found ACraft object, null instead
	 */
	public static ACrafts getCraftByItem(ItemStack item) {
		for (ACrafts craft : recipes) {
			if (ItemUtils.itemComparison(craft.getItem(), item, item.hasItemMeta(),
					item.hasItemMeta() ? item.getItemMeta().hasDisplayName() : false,
					item.hasItemMeta() ? item.getItemMeta().hasLore() : false)) {
				return (craft);
			}
		}
		
		return null;
	}
	
	/**
	 * Gets ACraft object by Inventory
	 * 
	 * @param invName	the inventory name as a key to find craft
	 * 
	 * @return the found ACraft object, null instead
	 */
	public static ACrafts getCraftByInventoryName(String invName) {
		for (ACrafts craft : recipes) {
			String name = "§8" + craft.getName();
			
			if (invName.contains(name)) {
				return (craft);
			}
		}
		
		return null;
	}
	
	/**
	 * Gets craft result by item name
	 * 
	 * @param itemName	the name to search in recipe results
	 * 
	 * @return the ItemStack object found by name, null instead
	 */
	public static ItemStack findItemByName(String itemName) {
		for (ACrafts craft : recipes) {
			if (itemName.equals(craft.getName())) {
				return (craft.getItem());
			}
		}
		
		return null;
	}
	
	/**
	 * Setups the template items
	 */
	public static void setupCraftTemplates() {
		List<Template> templates = new ArrayList<>();

		for (int i = 0; i < Config.getLastAge().number; i++)  {
			String name = AgeManager.getAgeByNumber(i).name;

			name = name.replace("_Age", "");
			templates.add(new Template(name, getMaterials(AgeManager.getAgeByNumber(i).name)));
		}

		for (Template t : templates) {
			addCraft(t);
		}
	}

	/**
	 * Removes the template items
	 */
	public static void removeCraftTemplates() {
		for (int i = 0; i < Config.getLastAge().number; i++)  {
			String name = AgeManager.getAgeByNumber(i).name;
			
			name = name.replace("_Age", "");
			name = name + "Template";

			removeCraft(name);
		}
	}
	
	/**
	 * Reloads the templates
	 * 
	 * @param name	The old template name
	 * @param age	The Age for the new template
	 */
	public static void reloadTemplate(String name, String age) {
		removeCraft(name);

		String tmp = age;

		tmp = tmp.replace("_Age", "");

		Template t = new Template(tmp, getMaterials(age));

		addCraft(t);

		GameManager.getGames().forEach((playerName, game) ->
			game.player.discoverRecipe(new NamespacedKey(KuffleMain.current, t.getName()))
		);
	}
	
	/**
	 * Get Material list for templates in a specific Age
	 * 
	 * @param age	The specific Age
	 * 
	 * @return the material list
	 */
	private static List<Material> getMaterials(String age) {
		List<Material> compose = new ArrayList<>();
		List<String> done = new ArrayList<>();

		for (int cnt = 0; cnt < Config.getSBTTAmount(); cnt++) {
			done.add(TargetManager.newTarget(done, age));
		}

		for (String item : done) {
			compose.add(Material.matchMaterial(item));
		}

		done.clear();
		return compose;
	}
	
	/**
	 * Checks if a specific item is a template
	 * 
	 * @param item	The item to check
	 * 
	 * @return True if item is a template, False instead
	 */
	public static boolean isTemplate(ItemStack item) {
		boolean ret = false;
		
		for (ACrafts recipe : recipes) {
			if (recipe.getName().toLowerCase().contains("template")) {
				if (ItemUtils.itemComparison(item, recipe.getItem(), true, true, true)) {
					ret = true;
					break;
				}
			}
		}
		
		return ret;
	}
}
