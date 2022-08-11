package test.fr.kosmosuniverse.utils.fileconformity.unittest;

import static org.junit.jupiter.api.Assertions.*;

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
class LangTest {
	/**
	 * Contains path to files used in these tests
	 */
	private static final String DATA_PATH = "E:\\Java\\workspace\\Kuffle\\src\\test\\fr\\kosmosuniverse\\utils\\fileconformity\\resources\\langs\\";
	
	/**
	 * Default Constructor
	 */
	LangTest() {
	}
	
	/**
	 * Setup system log file
	 */
	@BeforeAll
	static void setUpBeforeClass() {
		LogManager.getInstanceSystem("C:\\Temp\\Kuffle\\unittest\\KuffleSystemlogs.txt");
	}

	/**
	 * Test correct langs.json file
	 */
	@Test
	void testLangsCorrect() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "langs_correct.json"));
			String content = Utils.readFileContent(stream);
			
			assertTrue(FilesConformity.msgLangConformity(content),
					"langs_correct.json is not conform.");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception raised: " + e.getMessage());
		}
	}
	
	/**
	 * Test correct targets_langs.json file
	 */
	@Test
	void testTargetsLangsCorrect() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "targets_langs_correct.json"));
			String content = Utils.readFileContent(stream);
			
			assertTrue(FilesConformity.itemLangConformity(content),
					"targets_langs_correct.json should be conform.");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception raised: " + e.getMessage());
		}
	}

	/**
	 * Test langs file with invalid lang
	 */
	@Test
	void testLangsInvalidLang() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "langs_invalid_lang.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.msgLangConformity(content),
					"All langs used have to be in the first target langs !!!");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception raised: " + e.getMessage());
		}
	}
	
	/**
	 * Test targets_langs file with invalid lang
	 */
	@Test
	void testTargetsLangsInvalidLang() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "targets_langs_invalid_lang.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.itemLangConformity(content),
					"All langs used have to be in the first target langs !!!");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception raised: " + e.getMessage());
		}
	}
	
	/**
	 * Test targets_langs file with invalid target
	 */
	@Test
	void testTargetsLangsNotExists() {
		try {
			InputStream stream = new FileInputStream(new File(DATA_PATH + "targets_langs_not_exists.json"));
			String content = Utils.readFileContent(stream);
			
			assertFalse(FilesConformity.itemLangConformity(content),
					"Langs are defines for an unknown Minecraft target !!!");
		} catch (IOException e) {
			e.printStackTrace();
			fail("Exception raised: " + e.getMessage());
		}
	}
}
