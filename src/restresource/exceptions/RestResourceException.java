package restresource.exceptions;

public class RestResourceException extends RuntimeException {
	private static final long serialVersionUID = -3276535990581226335L;

	public RestResourceException() {
		super();
	}

	public RestResourceException(String msg) {
		super(msg);
	}

	public RestResourceException(String msg, Throwable e) {
		super(msg, e);
	}
}
