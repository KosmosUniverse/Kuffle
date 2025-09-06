package fr.kosmosuniverse.kuffle.utils;

import fr.kosmosuniverse.kuffle.KuffleMain;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class FileUtils {
	/**
	 * Private FileUtils constructor
	 * 
	 * @throws IllegalStateException to be unable to instance this utility class
	 */
	private FileUtils() {
		throw new IllegalStateException("Utility class");
	}
	
	/**
	 * Reads InputStream in content
	 * 
	 * @param in Stream to read
	 * @return file content
	 * @throws IOException if read fails
	 */
	public static String readFileContent(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line;
        
        while ((line = br.readLine()) != null) {
            sb.append(line).append(System.lineSeparator());
        }
 
        return sb.toString();
	}
	
	/**
	 * Parse JSON file with Object content
	 *
	 * @param file		File to parse
	 * 
	 * @return JSON object or null
	 */
	public static JSONObject readJSONFileObject(String file) throws IOException {
		String rawValues = readFileContent(KuffleMain.getInstance().getResource(file));
		JSONTokener tokenizer = new JSONTokener(rawValues);

		return new JSONObject(tokenizer);
	}

	public static JSONObject readJSONObjectFromContent(String content) {
		JSONTokener tokenizer = new JSONTokener(content);

		return new JSONObject(tokenizer);
	}
}
