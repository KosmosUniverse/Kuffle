package test.fr.kosmosuniverse.utils.itemutils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import main.fr.kosmosuniverse.kuffle.utils.ItemUtils;

class ItemUtilsTest {

	@BeforeAll
	static void setUpBeforeClass() {
	}

	@AfterAll
	static void tearDownAfterClass() {
	}

	@Ignore
	@Test
	void testItemWithName() {
		ItemStack item = ItemUtils.itemMakerName(Material.ACACIA_PLANKS, 12, "test");
		
		assertTrue(item.getType() == Material.ACACIA_PLANKS, "Item does not have the good type");
		assertEquals(item.getAmount(), 12, "Invalid item amount");
		assertTrue(item.hasItemMeta(), "Invalid item meta");
		assertTrue(item.getItemMeta().hasDisplayName(), "Invalid item does not has display name");
		assertEquals(item.getItemMeta().getDisplayName(), "test", "Invalid item does not have the good name");
	}

}
