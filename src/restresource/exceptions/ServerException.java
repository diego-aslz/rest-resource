package restresource.exceptions;

public class ServerException extends StatusException {
	private static final long serialVersionUID = 6998627939580154617L;

	public ServerException(int status) {
		super(status);
	}
}
