package main.fr.kosmosuniverse.kuffle.exceptions;

public class KuffleFileLoadException extends Exception {
	public KuffleFileLoadException(String errorMsg) {
		super(errorMsg);
	}
	
	public KuffleFileLoadException(String errorMsg, Throwable error) {
		super(errorMsg, error);
	}
}
