package fr.kosmosuniverse.kuffle.utils;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * 
 * @author KosmosUniverse
 *
 */
public final class SerializeUtils {
	/**
	 * Private SerializeUtils constructor
	 * 
	 * @throws IllegalStateException Utility Class Constructor Exception
	 */
	private SerializeUtils() {
		throw new IllegalStateException("Utility class");
	}
	
	public static String readString(ObjectInputStream ois) throws IOException {
		String s = ois.readUTF();
		
		if (s.equals("$null$")) {
			s = null;
		}
		
		return s;
	}
}
