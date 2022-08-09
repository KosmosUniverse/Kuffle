package main.fr.kosmosuniverse.kuffle.utils;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class Pair {
	private Object key;
	private Object value;
	
	/**
	 * Constructor
	 * 
	 * @param pairKey	Key of the current pair
	 * @param pairValue	Value of the current Pair
	 */
	public Pair(Object pairKey, Object pairValue) {
		key = pairKey;
		value = pairValue;
	}
	
	/**
	 * Getter for the current pair key
	 * 
	 * @return the key
	 */
	public Object getKey() {
		return key;
	}
	
	/**
	 * Getter for the current pair value
	 * 
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}
}
