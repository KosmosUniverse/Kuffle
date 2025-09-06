package fr.kosmosuniverse.kuffle.utils;

import lombok.Getter;

/**
 * 
 * @author KosmosUniverse
 *
 */
@Getter
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
}
