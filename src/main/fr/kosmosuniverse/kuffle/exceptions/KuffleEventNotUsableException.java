package main.fr.kosmosuniverse.kuffle.exceptions;

public class KuffleEventNotUsableException extends Exception {
	public KuffleEventNotUsableException(String errorMsg) {
		super(errorMsg);
	}
	
	public KuffleEventNotUsableException(String errorMsg, Throwable error) {
		super(errorMsg, error);
	}
}
