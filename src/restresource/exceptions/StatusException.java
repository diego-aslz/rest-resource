package restresource.exceptions;

public class StatusException extends RestResourceException {
	private static final long serialVersionUID = 939096920529430351L;
	
	protected int status;

	public StatusException(int status) {
		super();
		this.status = status;
	}

	public int getStatus() {
		return status;
	}
}
