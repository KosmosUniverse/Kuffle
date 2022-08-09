package test.fr.kosmosuniverse.utils.fileconformity.unittest;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import main.fr.kosmosuniverse.kuffle.KuffleMain;
import main.fr.kosmosuniverse.kuffle.core.Age;
import main.fr.kosmosuniverse.kuffle.core.Logs;
import main.fr.kosmosuniverse.kuffle.utils.FilesConformity;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

/**
 * 
 * @author KosmosUniverse
 *
 */
class RewardTest {
	/**
	 * Contains path to files used in these tests
	 */
	private static final String DATA_PATH = "E:\\Java\\workspace\\Kuffle\\src\\test\\fr\\kosmosuniverse\\utils\\fileconformity\\resources\\rewards\\";
	
	/**
	 * Default Constructor
	 */
	RewardTest() {
	}
	
	/**
	 * SetUp system log file and ages list
	 */
	@BeforeAll
	static void setUpBeforeClass() {
		Logs.getInstanceSystem("C:\\Temp\\Kuffle\\unittest\\KuffleSystemlogs.txt");
		KuffleMain.ages = new ArrayList<>();
		KuffleMain.ages.add(new Age("Archaic_Age", 0, "RED", "RED"));
		KuffleMain.ages.add(new Age("Classic_Age", 1, "RED", "RED"));
		KuffleMain.ages.add(new Age("Mineric_Age", 2, "RED", "RED"));
	}
	
	/**
	 * TearDown to clear ages list
	 */
	@AfterAll
	static void tearDownAfterClass() {
		KuffleMain.ages.clear();
	}

	/**
	 * Test correct rewards.json file
	 */
	@Test
	void testCorrect() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "rewards_correct.json"));
			String content = Utils.readFileContent(stream);
			
			assertTrue(FilesConformity.rewardsConformity(content),
					"rewards_correct.json should be conform.");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception raised: " + e.getMessage());
		}
	}

	/**
	 * Test rewards file with wrong age name
	 */
	@Test
	void testInvalidAge() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "rewards_invalid_age.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.rewardsConformity(content),
					"Rewards conformity must reject bad age names !!!");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception raised: " + e.getMessage());
		}
	}
	
	/**
	 * Test rewards file with "Amount" reward parameter invalid
	 */
	@Test
	void testInvalidAmount() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "rewards_invalid_amount.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.rewardsConformity(content),
					"rewards conformity must reject when amount is invalid !!!");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception raised: " + e.getMessage());
		}
	}
	
	/**
	 * Test rewards file with "Effect" reward parameter invalid
	 */
	@Test
	void testInvalidEffect() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "rewards_invalid_effect.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.rewardsConformity(content),
					"rewards conformity must reject when effect is invalid !!!");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception raised: " + e.getMessage());
		}
	}
	
	/**
	 * Test rewards file with "Effects" reward parameter invalid
	 */
	@Test
	void testInvalidEffects() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "rewards_invalid_effects.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.rewardsConformity(content),
					"rewards conformity must reject when effects is invalid !!!");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception raised: " + e.getMessage());
		}
	}
	
	/**
	 * Test rewards file with "Enchant" reward parameter invalid
	 */
	@Test
	void testInvalidEnchant() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "rewards_invalid_enchant.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.rewardsConformity(content),
					"rewards conformity must reject when enchant is invalid !!!");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception raised: " + e.getMessage());
		}
	}
	
	/**
	 * Test rewards file with "Enchants" reward parameter invalid
	 */
	@Test
	void testInvalidEnchants() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "rewards_invalid_enchants.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.rewardsConformity(content),
					"rewards conformity must reject when enchants is invalid !!!");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception raised: " + e.getMessage());
		}
	}
	
	/**
	 * Test rewards file with "Level" reward parameter invalid
	 */
	@Test
	void testInvalidLevel() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "rewards_invalid_level.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.rewardsConformity(content),
					"rewards conformity must reject when level is invalid !!!");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception raised: " + e.getMessage());
		}
	}
	
	/**
	 * Test rewards file with target is invalid
	 */
	@Test
	void testInvalidTarget() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "rewards_invalid_target.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.rewardsConformity(content),
					"rewards conformity must reject unknown Minecraft target !!!");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception raised: " + e.getMessage());
		}
	}
	
	/**
	 * Test rewards file with "Amount" reward parameter is missing
	 */
	@Test
	void testMissingAmount() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "rewards_missing_amount.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.rewardsConformity(content),
					"rewards conformity must reject when amount is missing !!!");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception raised: " + e.getMessage());
		}
	}
	
	/**
	 * Test rewards file with "Effect" reward parameter is missing
	 */
	@Test
	void testMissingEffect() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "rewards_missing_effect.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.rewardsConformity(content),
					"rewards conformity must reject when effect is missing !!!");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception raised: " + e.getMessage());
		}
	}
	
	/**
	 * Test rewards file with "Enchant" reward parameter is missing
	 */
	@Test
	void testMissingEnchant() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "rewards_missing_enchant.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.rewardsConformity(content),
					"rewards conformity must reject when enchant is missing !!!");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception raised: " + e.getMessage());
		}
	}

	/**
	 * Test rewards file with "Level" reward parameter is missing
	 */
	@Test
	void testMissingLevel() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "rewards_missing_level.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.rewardsConformity(content),
					"rewards conformity must reject when level is missing !!!");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception raised: " + e.getMessage());
		}
	}
}
