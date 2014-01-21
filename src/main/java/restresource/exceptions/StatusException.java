package restresource.exceptions;

public class StatusException extends RestResourceException {
	private static final long serialVersionUID = 939096920529430351L;
	
	protected int status;

	public StatusException(int status, String method, String url) {
		super(new StringBuilder("Unexpected status received (").append(status).
				append(") for ").append(method).append(" ").append(url).
				toString());
		this.status = status;
	}

	public int getStatus() {
		return status;
	}
}
