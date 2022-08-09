package test.fr.kosmosuniverse.utils.fileconformity.unittest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import main.fr.kosmosuniverse.kuffle.core.Logs;
import main.fr.kosmosuniverse.kuffle.utils.FilesConformity;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

/**
 * 
 * @author KosmosUniverse
 *
 */
class AgeTest {
	/**
	 * Contains path to files used in these tests
	 */
	private static final String DATA_PATH = "E:\\Java\\workspace\\Kuffle\\src\\test\\fr\\kosmosuniverse\\utils\\fileconformity\\resources\\ages\\";
	
	/**
	 * Default Constructor
	 */
	AgeTest() {
	}
	
	/**
	 * Setup system log file
	 */
	@BeforeAll
	static void setUpBeforeClass() {
		Logs.getInstanceSystem("C:\\Temp\\Kuffle\\unittest\\KuffleSystemlogs.txt");
	}
	
	/**
	 * Test correct ages.json file
	 */
	@Test
	void testCorrect() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "ages_correct.json"));
			String content = Utils.readFileContent(stream);
			
			assertTrue(FilesConformity.ageConformity(content),
					"ages_correct.json should be conform.");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception raised: " + e.getMessage());
		}
	}

	/**
	 * Test ages file with wrong age name format
	 */
	@Test
	void testBadName() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "ages_bad_name.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.ageConformity(content),
					"Age conformity must reject bad names !!!");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception raised: " + e.getMessage());
		}
	}
	
	/**
	 * Test ages file with "Number" age parameter missing
	 */
	@Test
	void testMissingNumber() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "ages_missing_number.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.ageConformity(content),
					"Age conformity must reject when number is missing !!!");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception raised: " + e.getMessage());
		}
	}
	
	/**
	 * Test ages file with "TextColor" age parameter missing
	 */
	@Test
	void testMissingTextColor() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "ages_missing_text_color.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.ageConformity(content),
					"Age conformity must reject when text color is missing !!!");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception raised: " + e.getMessage());
		}
	}
	
	/**
	 * Test ages file with "BoxColor" age parameter missing
	 */
	@Test
	void testMissingBoxColor() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "ages_missing_box_color.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.ageConformity(content),
					"Age conformity must reject when box color is missing !!!");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception raised: " + e.getMessage());
		}
	}
	
	/**
	 * Test ages file with "Number" age parameter invalid
	 */
	@Test
	void testInvalidNumber() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "ages_invalid_number.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.ageConformity(content),
					"Age conformity must reject when number is invalid !!!");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception raised: " + e.getMessage());
		}
	}
	
	/**
	 * Test ages file with "TextColor" age parameter invalid
	 */
	@Test
	void testInvalidTextColor() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "ages_invalid_text_color.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.ageConformity(content),
					"Age conformity must reject when text color is invalid !!!");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception raised: " + e.getMessage());
		}
	}
	
	/**
	 * Test ages file with "BoxColor" age parameter invalid
	 */
	@Test
	void testInvalidBoxColor() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "ages_invalid_box_color.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.ageConformity(content),
					"Age conformity must reject when box color is invalid !!!");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception raised: " + e.getMessage());
		}
	}
}
