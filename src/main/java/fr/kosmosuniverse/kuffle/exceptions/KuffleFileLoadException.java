package fr.kosmosuniverse.kuffle.exceptions;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleFileLoadException extends Exception {

	/**
	 * Exception ID
	 */
	private static final long serialVersionUID = 5606420571648343497L;

	/**
	 * Constructor
	 * 
	 * @param errorMsg	The error message
	 */
	public KuffleFileLoadException(String errorMsg) {
		super(errorMsg);
	}
	
	/**
	 * Constructor
	 * 
	 * @param errorMsg	The error message
	 * @param error		Another Exception to link
	 */
	public KuffleFileLoadException(String errorMsg, Throwable error) {
		super(errorMsg, error);
	}
}
