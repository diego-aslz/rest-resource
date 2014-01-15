package restresource;

import static restresource.utils.RestUtils.collectionName;
import static restresource.utils.RestUtils.elementName;
import static restresource.utils.RestUtils.id;
import static restresource.utils.RestUtils.site;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import restresource.exceptions.ClientException;
import restresource.exceptions.ForbiddenAccessException;
import restresource.exceptions.ResourceInvalidException;
import restresource.exceptions.ResourceNotFoundException;
import restresource.exceptions.RestResourceException;
import restresource.exceptions.ServerException;
import restresource.exceptions.UnauthorizedAccessException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class RestResource {
	public static final int NO_CONTENT = 204;
	public static final int NOT_FOUND = 404;
	public static final int UNAUTHORIZED_ACCESS = 401;
	public static final int FORBIDDEN_ACCESS = 403;
	public static final int RESOURCE_INVALID = 422;
	private static String format = "json";

	/**
	 * Loads a single object from the RESTful web service. It's the SHOW action.
	 * @param id Id of the desired object.
	 * @param klass Class of the desired object. Will be used to determine
	 * the collection name to compose the URL.
	 * @return The loaded object.
	 * @throws RestResourceException When something goes wrong while
	 * communicating with the server, handling the status of the response or
	 * parsing its body.
	 */
	public static <T> T find(Object id, Class<T> klass) throws RestResourceException {
		StringBuilder sb = new StringBuilder(site(klass)).
				append(collectionName(klass)).
				append("/").
				append(id).
				append(".").
				append(format);

		String body = makeTheCall("GET", sb.toString());

		return loadElement(klass, body, new Gson());
	}

	/**
	 * Loads a list of objects from the RESTful web service. It's the INDEX action.
	 * @param klass Class of the desired object. Will be used to determine
	 * the collection name to compose the URL.
	 * @param params The query parameters.
	 * @return The loaded list.
	 * @throws RestResourceException When something goes wrong while
	 * communicating with the server, handling the status of the response or
	 * parsing its body.
	 */
	public static <T> List<T> all(Class<T> klass, String... params)
			throws RestResourceException {
		StringBuilder sb = new StringBuilder(site(klass)).
				append(collectionName(klass)).
				append(".").
				append(format);

		String body = makeTheCall("GET", sb.toString(), params);

		return loadCollection(klass, body, new Gson());
	}

	/**
	 * Sends the object to the web service via POST request. It's the CREATE
	 * action.
	 * @param o Object to be saved
	 * @return The saved object as it returned from the server.
	 * @throws RestResourceException If a known error occurs during the saving
	 * process.
	 */
	public static <T> T save(T o) throws RestResourceException {
		@SuppressWarnings("unchecked")
		Class<T> klass = (Class<T>) o.getClass();

		Gson gson = new Gson();
		String entity = new StringBuilder("{\"").append(elementName(klass)).
				append("\"").append(": ").append(gson.toJson(o)).append("}").
				toString();
		String body = null;
		Object id = id(o);
		if (id == null || (id instanceof Integer && (Integer)id <= 0)) {
			String path = new StringBuilder(site(klass)).
					append(collectionName(klass)).
					append(".").
					append(format).toString();
			body = makeTheCall("POST", path, entity);
		} else {
			String path = new StringBuilder(site(klass)).
					append(collectionName(klass)).
					append("/").
					append(id).
					append(".").
					append(format).toString();
			body = makeTheCall("PUT", path, entity);
		}

		if (body == null)
			return o;
		return loadElement(klass, body, gson);
	}

	public static void delete(Object o) throws RestResourceException {
		Class<?> klass = o.getClass();
		StringBuilder sb = new StringBuilder(site(klass)).
				append(collectionName(klass)).
				append("/").
				append(id(o)).
				append(".").
				append(format);

		makeTheCall("DELETE", sb.toString());
	}

	protected static <T> T loadElement(Class<T> klass, String body, Gson gson)
			throws RestResourceException {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> r = gson.fromJson(body, Map.class);
			return gson.fromJson(gson.toJson(r.get(elementName(klass))),
					klass);
		} catch(JsonSyntaxException e) {
			throw new RestResourceException("Unable to parse response body to "
					+ "JSON: " + body, e);
		}
	}

	protected static <T> List<T> loadCollection(Class<T> klass, String body,
			Gson gson) {
		try {
			@SuppressWarnings("unchecked")
			List<T> l = gson.fromJson(body, List.class);
			for (int i = 0; i < l.size(); i++)
				l.set(i, gson.fromJson(gson.toJson(l.get(i)), klass));
			return l;
		} catch(JsonSyntaxException e) {
			throw new RestResourceException("Unable to parse response body to "
					+ "JSON: " + body, e);
		}
	}

	protected static String makeTheCall(String method, String path,
			String... params) throws RestResourceException {
		HttpURLConnection connection = openConnection(method, path, params);
		try {
			if (handleResponseCode(connection) == NO_CONTENT)
				return null;;
			return extractResponseBody(connection);
		} finally {
			connection.disconnect();
		}
	}

	protected static int handleResponseCode(HttpURLConnection connection)
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
			return status;
		} catch (IOException e) {
			throw new RestResourceException("Error while reading the response"
					+ " code", e);
		}
	}

	protected static String extractResponseBody(HttpURLConnection connection)
			throws RestResourceException {
		Scanner scan = null;
		try {
			scan = new Scanner(connection.getInputStream()).useDelimiter("\\A");
		} catch (IOException e) {
			throw new RestResourceException(new StringBuilder("Error while ").
					append("reading server's response (from ").
					append(connection.getRequestMethod()).append(" ").
					append(connection.getURL()).append(").").toString(), e);
		}
		try {
			return scan.next();
		} catch(NoSuchElementException e) {
			throw new RestResourceException(new StringBuilder("Error while ").
					append("reading server's response (from ").
					append(connection.getRequestMethod()).append(" ").
					append(connection.getURL()).append(").").toString(), e);
		}
	}

	protected static HttpURLConnection openConnection(String method, String path,
			String... params) throws RestResourceException {
		URL url = null;
		try {
			StringBuilder sb = new StringBuilder(path);
			if (method.equals("GET")) {
				String separator = "?";
				for (String param : params) {
					sb.append(separator);
					sb.append(param);
					separator = "&";
				}
			}
			url = new URL(sb.toString());
		} catch (MalformedURLException e) {
			throw new RestResourceException("Error while composing URL (method="
					+ method + ", path=" + path + ", params=" + params + ")", e);
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
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setInstanceFollowRedirects(false);
		try {
			connection.setRequestMethod(method);
		} catch (ProtocolException e) {
			throw new RestResourceException("Protocol error.", e);
		}
		if (!method.equals("GET")) {
			connection.setRequestProperty("Accept", "application/" + format);
			connection.setRequestProperty("Content-Type", "application/" + format);
			if (params.length > 0)
				try {
					DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
					for (String param : params)
						dos.writeBytes(param);
					dos.flush();
					dos.close();
				} catch (IOException e) {
					throw new RestResourceException("Error while writing POST "
							+ "parameters (" + params + ")", e);
				}
		}
		return connection;
	}
}
