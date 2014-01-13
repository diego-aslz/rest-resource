package test.support;

import com.sun.net.httpserver.HttpServer;

public class FakeServerConfig {
	private HttpServer server;

	public void setServer(HttpServer server) {
		this.server = server;
	}

	public void get(String path, String headers, String result, int status) {
//		server.createContext(arg0, arg1)
	}
}
