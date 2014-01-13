package test.support;

import java.io.IOException;
import java.net.InetSocketAddress;

import restresource.utils.Proc;

import com.sun.net.httpserver.HttpServer;

public class FakeServer {
	private static HttpServer server;
	private static FakeServerConfig config;
	
	protected static HttpServer getServer() throws IOException {
		if (server == null)
			server = HttpServer.create();
		return server;
	}

	protected static FakeServerConfig getConfig() {
		if (config == null)
			config = new FakeServerConfig();
		return config;
	}

	public static void fake(int port, Proc block) {
		if (server != null)
			server.stop(server.getAddress().getPort());
		try {
			getServer().bind(new InetSocketAddress(port), 0);
			block.run(getConfig());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
