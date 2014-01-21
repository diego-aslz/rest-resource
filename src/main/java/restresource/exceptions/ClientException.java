package restresource.exceptions;

public class ClientException extends StatusException {
	private static final long serialVersionUID = -8605834289264100317L;

	public ClientException(int status, String method, String url) {
		super(status, method, url);
	}
}
