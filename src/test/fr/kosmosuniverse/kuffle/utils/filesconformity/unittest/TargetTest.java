package test.fr.kosmosuniverse.kuffle.utils.filesconformity.unittest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import main.fr.kosmosuniverse.kuffle.core.AgeManager;
import main.fr.kosmosuniverse.kuffle.core.LogManager;
import main.fr.kosmosuniverse.kuffle.exceptions.KuffleFileLoadException;
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
	 * 
	 * @throws KuffleFileLoadException if file load failed
	 */
	@BeforeAll
	static void setUpBeforeClass() throws KuffleFileLoadException {
		LogManager.setupInstanceSystem("C:\\Temp\\Kuffle\\unittest\\KuffleSystemlogs.txt");
		
		try {
			AgeManager.setupAges(FilesConformity.getContent("ages.json", true));
		} catch (IllegalArgumentException | ParseException e) {
			Utils.logException(e);
			AgeManager.clear();
			
			throw new KuffleFileLoadException("Ages load failed !");
		}	}
	
	/**
	 * TearDown to clear ages list
	 */
	@AfterAll
	static void tearDownAfterClass() {
		AgeManager.clear();
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
			Utils.logException(e);
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
			Utils.logException(e);
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
			Utils.logException(e);
			fail("Exception raised: " + e.getMessage());
		}
	}
}
