package fr.kosmosuniverse.kuffle.exceptions;

/**
 * 
 * @author KosmosUniverse
 *
 */
public class KuffleEventNotUsableException extends Exception {
	/**
	 * Exception ID
	 */
	private static final long serialVersionUID = -5807518558597834711L;

	/**
	 * Constructor
	 * 
	 * @param errorMsg	The error message
	 */
	public KuffleEventNotUsableException(String errorMsg) {
		super(errorMsg);
	}
}
