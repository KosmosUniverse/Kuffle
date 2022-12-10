package test.fr.kosmosuniverse.kuffle.utils.filesconformity.unittest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.utils.FilesConformity;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

/**
 * 
 * @author KosmosUniverse
 *
 */
class LevelTest {
	/**
	 * Contains path to files used in these tests
	 */
	private static final String DATA_PATH = "E:\\Java\\workspace\\Kuffle\\src\\test\\fr\\kosmosuniverse\\utils\\fileconformity\\resources\\levels\\";
	
	/**
	 * Default Constructor
	 */
	LevelTest() {
	}
	
	/**
	 * Setup system log file
	 */
	@BeforeAll
	static void setUpBeforeClass() {
		LogManager.setupInstanceSystem("C:\\Temp\\Kuffle\\unittest\\KuffleSystemlogs.txt");
	}
	
	/**
	 * Test correct levels.json file
	 */
	@Test
	void testCorrect() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "levels_correct.json"));
			String content = Utils.readFileContent(stream);
			
			assertTrue(FilesConformity.levelsConformity(content),
					"levels_correct.json should be conform.");
		} catch (IOException e) {
			Utils.logException(e);
			fail("Exception raised: " + e.getMessage());
		}
	}
	
	/**
	 * Test levels file with "Number" level parameter missing
	 */
	@Test
	void testMissingNumber() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "levels_missing_number.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.levelsConformity(content),
					"Level conformity must reject when number is missing !!!");
		} catch (IOException e) {
			Utils.logException(e);
			fail("Exception raised: " + e.getMessage());
		}
	}
	
	/**
	 * Test levels file with "Seconds" level parameter missing
	 */
	@Test
	void testMissingSeconds() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "levels_missing_seconds.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.levelsConformity(content),
					"Level conformity must reject when seconds is missing !!!");
		} catch (IOException e) {
			Utils.logException(e);
			fail("Exception raised: " + e.getMessage());
		}
	}
	
	/**
	 * Test levels file with "Lose" level parameter missing
	 */
	@Test
	void testMissingLose() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "levels_missing_lose.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.levelsConformity(content),
					"Level conformity must reject when lose is missing !!!");
		} catch (IOException e) {
			Utils.logException(e);
			fail("Exception raised: " + e.getMessage());
		}
	}
	
	/**
	 * Test levels file with "Number" level parameter invalid
	 */
	@Test
	void testInvalidNumber() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "levels_invalid_number.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.levelsConformity(content),
					"Level conformity must reject when number is invalid !!!");
		} catch (IOException e) {
			Utils.logException(e);
			fail("Exception raised: " + e.getMessage());
		}
	}
	
	/**
	 * Test levels file with "Seconds" level parameter invalid
	 */
	@Test
	void testInvalidSeconds() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "levels_invalid_seconds.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.levelsConformity(content),
					"Level conformity must reject when seconds is invalid !!!");
		} catch (IOException e) {
			Utils.logException(e);
			fail("Exception raised: " + e.getMessage());
		}
	}
	
	/**
	 * Test levels file with "Lose" level parameter invalid
	 */
	@Test
	void testInvalidLose() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "levels_invalid_lose.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.levelsConformity(content),
					"Level conformity must reject when lose is invalid !!!");
		} catch (IOException e) {
			Utils.logException(e);
			fail("Exception raised: " + e.getMessage());
		}
	}
}
