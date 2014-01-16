package restresource.support;

public class Request {
	private String method;
	private String path;
	private String[] params;

	public Request(String method, String path, String... params) {
		this.method = method;
		this.path = path;
		this.params = params;
	}

	public String method() {
		return method;
	}

	public String path() {
		return path;
	}

	public String[] params() {
		return params;
	}
}
