package restresource.exceptions;

public class UnauthorizedAccessException extends ClientException {
	private static final long serialVersionUID = -6308494934001150589L;

	public UnauthorizedAccessException(int status, String method, String url) {
		super(status, method, url);
	}
}
