package restresource.exceptions;

public class ForbiddenAccessException extends ClientException {
	private static final long serialVersionUID = -4927157760962315800L;

	public ForbiddenAccessException(int status, String method, String url) {
		super(status, method, url);
	}
}
