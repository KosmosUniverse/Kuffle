package test.fr.kosmosuniverse.kuffle.crafts.unittest;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

import main.fr.kosmosuniverse.kuffle.crafts.ACraft;
import main.fr.kosmosuniverse.kuffle.crafts.CraftImpl;
import main.fr.kosmosuniverse.kuffle.utils.Utils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class CraftImplTest {
	/**
	 * Contains path to files used in these tests
	 */
	private static final String DATA_PATH = ".\\src\\test\\fr\\kosmosuniverse\\kuffle\\crafts\\resources\\";
	
	/**
	 * Test craft
	 */
	@Test
	public void testWShaped() {
		JSONObject jsonCraft = null;
		
		try (InputStream stream = new FileInputStream(new File(DATA_PATH + "WShapedClassic.json"))) {
			String content = Utils.readFileContent(stream);
			JSONParser parser = new JSONParser();
			jsonCraft = (JSONObject) parser.parse(content);
		} catch (IOException | ParseException e) {
			fail("Exception raised: " + e.getMessage());
		}
		
		if (jsonCraft == null) {
			fail("json craft is null");
		}
		
		for (Object key : jsonCraft.keySet()) {
			@SuppressWarnings("unused")
			ACraft craft = new CraftImpl((JSONObject) jsonCraft.get(key));
		}
	}
}
