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
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.utils.FilesConformity;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

/**
 * 
 * @author KosmosUniverse
 *
 */
class TargetTest {
	/**
	 * Contains path to files used in these tests
	 */
	private static final String DATA_PATH = "E:\\Java\\workspace\\Kuffle\\src\\test\\fr\\kosmosuniverse\\utils\\fileconformity\\resources\\targets\\";
	
	/**
	 * Default Constructor
	 */
	TargetTest() {
	}
	
	/**
	 * SetUp system log file and ages list
	 */
	@BeforeAll
	static void setUpBeforeClass() {
		LogManager.getInstanceSystem("C:\\Temp\\Kuffle\\unittest\\KuffleSystemlogs.txt");
		KuffleMain.ages = new ArrayList<>();
		KuffleMain.ages.add(new Age("Archaic_Age", 0, "RED", "RED"));
	}
	
	/**
	 * TearDown to clear ages list
	 */
	@AfterAll
	static void tearDownAfterClass() {
		KuffleMain.ages.clear();
	}

	/**
	 * Test correct targets.json file
	 */
	@Test
	void testCorrect() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "targets_correct.json"));
			String content = Utils.readFileContent(stream);
			
			assertTrue(FilesConformity.itemsConformity(content),
					"targets_correct.json should be conform.");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception raised: " + e.getMessage());
		}
	}

	/**
	 * Test targets file with wrong age name
	 */
	@Test
	void testInvalidAge() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "targets_invalid_age.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.itemsConformity(content),
					"Targets conformity must reject bad age names !!!");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception raised: " + e.getMessage());
		}
	}
	
	/**
	 * Test targets file with wrong target
	 */
	@Test
	void testInvalidTarget() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "targets_invalid_target.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.itemsConformity(content),
					"Targets conformity must reject unknown Minecraft target !!!");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception raised: " + e.getMessage());
		}
	}
}
