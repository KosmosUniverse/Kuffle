package fr.kosmosuniverse.kuffle.exceptions;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleConfigException extends Exception {

	/**
	 * Exception ID
	 */
	private static final long serialVersionUID = 9116578580097831503L;

	/**
	 * Constructor
	 * 
	 * @param errorMsg	The error message
	 */
	public KuffleConfigException(String errorMsg) {
		super(errorMsg);
	}
}
