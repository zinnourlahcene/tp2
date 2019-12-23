package ca.uqtr.tp2.Persistence.exception;

public class SQLSyntaxException extends PersitenceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SQLSyntaxException() {
		
	}

	public SQLSyntaxException(String message) {
		super(message);
		
	}

	public SQLSyntaxException(Throwable cause) {
		super(cause);
	}

	public SQLSyntaxException(String message, Throwable cause) {
		super(message, cause);
	}

	public SQLSyntaxException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
