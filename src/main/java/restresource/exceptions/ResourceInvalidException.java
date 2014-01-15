package restresource.exceptions;

public class ResourceInvalidException extends ClientException {
	private static final long serialVersionUID = -2439544847882464632L;

	public ResourceInvalidException(int status) {
		super(status);
	}
}
