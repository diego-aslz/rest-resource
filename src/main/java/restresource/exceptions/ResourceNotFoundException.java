package restresource.exceptions;

public class ResourceNotFoundException extends ClientException {
	private static final long serialVersionUID = 7665776579015193598L;

	public ResourceNotFoundException(int status) {
		super(status);
	}
}
