package restresource;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import restresource.exceptions.ClientException;
import restresource.exceptions.ForbiddenAccessException;
import restresource.exceptions.ResourceInvalidException;
import restresource.exceptions.ResourceNotFoundException;
import restresource.exceptions.RestResourceException;
import restresource.exceptions.ServerException;
import restresource.exceptions.UnauthorizedAccessException;

import com.google.gson.Gson;

public class RestResource {
	public static final int NOT_FOUND = 404;
	public static final int UNAUTHORIZED_ACCESS = 401;
	public static final int FORBIDDEN_ACCESS = 403;
	public static final int RESOURCE_INVALID = 422;
	private static String format = "json";

	public static <T> T find(Object id, Class<T> klass) throws RestResourceException {
		StringBuilder sb = new StringBuilder(site(klass)).
				append(collectionName(klass)).
				append("/").
				append(id).
				append(".").
				append(format);

		String body = makeTheCall("GET", sb.toString());

		Gson gson = new Gson();
		@SuppressWarnings("unchecked")
		Map<String, Object> r = gson.fromJson("{" + body + "}", Map.class);
		return gson.fromJson(gson.toJson(r.get("person")), klass);
	}

	public static <T> List<T> all(Class<T> klass, String... params) throws RestResourceException {
		StringBuilder sb = new StringBuilder(site(klass)).
				append(collectionName(klass)).
				append(".").
				append(format);

		String body = makeTheCall("GET", sb.toString(), params);

		Gson gson = new Gson();
		@SuppressWarnings("unchecked")
		List<T> l = gson.fromJson(body, List.class);
		for (int i = 0; i < l.size(); i++)
			l.set(i, gson.fromJson(gson.toJson(l.get(i)), klass));
		return l;
	}

	protected static String makeTheCall(String method, String path,
			String... params) throws RestResourceException {
		HttpURLConnection connection = openConnection("GET", path, params);
		try {
			handleResponseCode(connection);
			return extractResponseBody(connection);
		} finally {
			connection.disconnect();
		}
	}

	protected static void handleResponseCode(HttpURLConnection connection)
			throws RestResourceException {
		try {
			int status = connection.getResponseCode();
			if (status == UNAUTHORIZED_ACCESS)
				throw new UnauthorizedAccessException(status);
			else if (status == FORBIDDEN_ACCESS)
				throw new ForbiddenAccessException(status);
			else if (status == RESOURCE_INVALID)
				throw new ResourceInvalidException(status);
			else if (status == NOT_FOUND)
				throw new ResourceNotFoundException(status);
			else if (status >= 400 && status < 500)
				throw new ClientException(status);
			else if (status >= 500 && status < 600)
				throw new ServerException(status);
		} catch (IOException e) {
			throw new RestResourceException("Error while reading the response"
					+ " code", e);
		}
	}

	protected static String extractResponseBody(HttpURLConnection connection)
			throws RestResourceException{
		Scanner scan = null;
		try {
			scan = new Scanner(connection.getInputStream()).useDelimiter("\\A");
		} catch (IOException e) {
			throw new RestResourceException(new StringBuilder("Error while ").
					append("reading server's response (from ").
					append(connection.getRequestMethod()).append(" ").
					append(connection.getURL()).append(").").toString(), e);
		}
		String body = scan.next();
		return body;
	}

	protected static HttpURLConnection openConnection(String method, String path,
			String... params) throws RestResourceException {
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
		} catch (MalformedURLException e) {
			throw new RestResourceException("Error while composing URL.", e);
		}
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			throw new RestResourceException(
					new StringBuilder("Error while trying to open the ").
					append("connection to ").
					append(url.toString()).toString(), e);
		}
		connection.setDoOutput(true); 
		connection.setInstanceFollowRedirects(false); 
		try {
			connection.setRequestMethod(method);
		} catch (ProtocolException e) {
			throw new RestResourceException("Protocol error.", e);
		} 
		connection.setRequestProperty("Content-Type", "application/" + format);
		return connection;
	}

	public static String site(Class<?> klass) {
		try {
			return invokeClassMethod(klass, "getSite");
		} catch (Exception e) {
			throw new RestResourceException("Error while trying to access " +
					klass.getName() + ".getSite().", e);
		}
	}

	public static String collectionName(Class<?> klass) {
		try {
			return invokeClassMethod(klass, "collectionName");
		} catch (Exception e) {
			return klass.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").
					toLowerCase() + "s";
		}
	}

	protected static String invokeClassMethod(Class<?> klass, String method,
			Object... args) throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		Method m = klass.getMethod(method);
		m.setAccessible(true);
		return (String) m.invoke(klass, args);
	}
}
