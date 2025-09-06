package fr.kosmosuniverse.kuffle.exceptions;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleCommandFalseException extends Exception {

	/**
	 * Exception ID
	 */
	private static final long serialVersionUID = -8771555067156570227L;

	/**
	 * Constructor
	 */
	public KuffleCommandFalseException() {
		super("false");
	}
	
	/**
	 * Constructor
	 * 
	 * @param errorMsg	The error message
	 */
	public KuffleCommandFalseException(String errorMsg) {
		super(errorMsg);
	}
	
	/**
	 * Constructor
	 * 
	 * @param errorMsg	The error message
	 * @param error		Another Exception to link
	 */
	public KuffleCommandFalseException(String errorMsg, Throwable error) {
		super(errorMsg, error);
	}
}
