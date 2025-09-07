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

}
