package restresource;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;

public class RestResource {
	public static final int NOT_FOUND = 404;
	private static String format = "json";

	public static Object find(Object id, Class<?> klass) {
		StringBuilder sb = new StringBuilder(site(klass)).
				append(collectionName(klass)).
				append("/").
				append(id).
				append(".").
				append(format);
		HttpURLConnection connection = openConnection("GET",  sb.toString());

		String body = null;
		try {
			try {
				if (connection.getResponseCode() == NOT_FOUND)
					return null;
			} catch (IOException e) {
				e.printStackTrace();
			}

			body = extractResponseBody(connection);
		} finally {
			connection.disconnect();
		}

		Gson gson = new Gson();
		@SuppressWarnings("unchecked")
		Map<String, Object> r = gson.fromJson("{" + body + "}", Map.class);
		return gson.fromJson(gson.toJson(r.get("person")), klass);
	}

	protected static String extractResponseBody(HttpURLConnection connection) {
		Scanner scan = null;
		try {
			scan = new Scanner(connection.getInputStream()).useDelimiter("\\A");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String body = scan.next();
		return body;
	}

	protected static HttpURLConnection openConnection(String method, String path,
			String... params) {
		URL url = null;
		try {
			StringBuilder sb = new StringBuilder(path);
			String separator = "?";
			for (String param : params) {
				sb.append(separator);
				sb.append(param);
				separator = "&";
			}
			url = new URL(sb.toString());
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		connection.setDoOutput(true); 
		connection.setInstanceFollowRedirects(false); 
		try {
			connection.setRequestMethod(method);
		} catch (ProtocolException e1) {
			e1.printStackTrace();
		} 
		connection.setRequestProperty("Content-Type", "application/" + format);
		return connection;
	}

	public static List<Object> all(Class<?> klass, String... params) {
		StringBuilder sb = new StringBuilder(site(klass)).
				append(collectionName(klass)).
				append(".").
				append(format);
		HttpURLConnection connection = openConnection("GET", sb.toString(),
				params);
		String body = null;
		try {
			body = extractResponseBody(connection);
		} finally {
			connection.disconnect();
		}
		Gson gson = new Gson();
		@SuppressWarnings("unchecked")
		List<Object> l = gson.fromJson(body, List.class);
		for (int i = 0; i < l.size(); i++)
			l.set(i, gson.fromJson(gson.toJson(l.get(i)), klass));
		return l;
	}

	protected static String site(Class<?> klass) {
		return invokeClassMethod(klass, "getSite", null);
	}

	protected static String collectionName(Class<?> klass) {
		return invokeClassMethod(klass, "collectionName",
				klass.getSimpleName().toLowerCase() + "s");
	}

	protected static String invokeClassMethod(Class<?> klass, String method,
			String whenFail, Object... args) {
		try {
			Method m = klass.getMethod(method);
			return (String) m.invoke(klass, args);
		} catch (Exception e) {
		}
		return whenFail;
	}
}
