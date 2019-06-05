package T145.metaltransport.errors;

public class EmptyItemStackException extends Exception {

	public EmptyItemStackException(String message) {
		super(message);
	}

	public EmptyItemStackException(Throwable cause) {
		super(cause);
	}

	public EmptyItemStackException(String message, Throwable cause) {
		super(message, cause);
	}

	public EmptyItemStackException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
