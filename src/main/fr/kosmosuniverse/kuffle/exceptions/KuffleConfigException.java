package main.fr.kosmosuniverse.kuffle.exceptions;

public class KuffleConfigException extends Exception {
	public KuffleConfigException(String errorMsg) {
		super(errorMsg);
	}
	
	public KuffleConfigException(String errorMsg, Throwable error) {
		super(errorMsg, error);
	}
}
