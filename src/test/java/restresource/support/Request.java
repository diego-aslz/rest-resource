package restresource.support;

public class Request {
	private String method;
	private String path;
	private String body;

	public Request(String method, String path, String body) {
		this.method = method;
		this.path   = path;
		this.body   = body;
	}

	public String method() {
		return method;
	}

	public String path() {
		return path;
	}

	public String body() {
		return body;
	}
}
