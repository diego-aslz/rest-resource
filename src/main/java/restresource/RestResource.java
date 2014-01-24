package restresource;

import static restresource.utils.RestUtils.elementName;
import static restresource.utils.RestUtils.format;
import static restresource.utils.RestUtils.id;
import static restresource.utils.RestUtils.urlFor;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.apache.commons.codec.binary.Base64;

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

	private static List<RequestListener> requestListeners;

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
		return loadElement(klass, get(klass, id.toString()), new Gson());
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
	public static <T> List<T> all(Class<T> klass, Map<String, String> params)
			throws RestResourceException {
		return loadCollection(klass, get(klass, params), new Gson());
	}
	public static <T> List<T> all(Class<T> klass) {
		return all(klass, null);
	}

	/**
	 * Sends the object to the web service via POST request. It's the CREATE
	 * action.
	 * @param o Object to be saved
	 * @return The saved object as it returned from the server.
	 * @throws RestResourceException If a known error occurs during the saving
	 * process.
	 */
	public static <T extends Element> T save(T o) throws RestResourceException {
		@SuppressWarnings("unchecked")
		Class<T> klass = (Class<T>) o.getClass();

		Gson gson = new Gson();
		String body = null;
		Object id = id(o);
		if (id == null || (id instanceof Integer && (Integer)id <= 0)) {
			body = post(klass, o);
		} else {
			body = put(klass, o, id.toString());
		}

		T el = loadElement(klass, body, gson);
		if (el == null)
			return o;
		return el;
	}

	public static void destroy(Object o) throws RestResourceException {
		delete(o);
	}

	public static String get(Object collectionOrElement, String... path)
			throws RestResourceException {
		return get(collectionOrElement, null, path);
	}

	public static String get(Object collectionOrElement,
			Map<String, String> params, String... path)
					throws RestResourceException {
		return makeTheCall("GET", urlFor(collectionOrElement, params, path),
				format(collectionOrElement));
	}

	public static String post(Object collectionOrElement,
			Map<String, String> params, Element element, String... path)
					throws RestResourceException {
		return makeTheCall("POST", urlFor(collectionOrElement, params, path),
				parseElement(element), format(collectionOrElement));
	}
	public static String post(Object collectionOrElement,
			Element element, String... path) throws RestResourceException {
		return post(collectionOrElement, null, element, path);
	}
	public static String post(Object collectionOrElement,
			Map<String, String> params, String... path)
					throws RestResourceException {
		return post(collectionOrElement, params, null, path);
	}
	public static String post(Object collectionOrElement, String... path)
			throws RestResourceException {
		return post(collectionOrElement, (Element)null, path);
	}

	public static String put(Object collectionOrElement,
			Map<String, String> params, Element element, String... path)
					throws RestResourceException {
		return makeTheCall("PUT", urlFor(collectionOrElement, params, path),
				parseElement(element), format(collectionOrElement));
	}
	public static String put(Object collectionOrElement,
			Element element, String... path) throws RestResourceException {
		return put(collectionOrElement, null, element, path);
	}
	public static String put(Object collectionOrElement,
			Map<String, String> params, String... path)
					throws RestResourceException {
		return put(collectionOrElement, params, null, path);
	}
	public static String put(Object collectionOrElement, String... path)
			throws RestResourceException {
		return put(collectionOrElement, null, null, path);
	}

	public static String delete(Object collectionOrElement,
			Map<String, String> params, String... path)
					throws RestResourceException {
		return makeTheCall("DELETE", urlFor(collectionOrElement, params, path),
				format(collectionOrElement));
	}
	public static String delete(Object collectionOrElement, String... path)
			throws RestResourceException {
		return delete(collectionOrElement, null, path);
	}

	public static <T> T loadElement(Class<T> klass, String body, Gson gson)
			throws RestResourceException {
		try {
			if (body == null || body.isEmpty())
				return null;
			@SuppressWarnings("unchecked")
			Map<String, Object> r = gson.fromJson(body, Map.class);
			return gson.fromJson(gson.toJson(r.get(elementName(klass))), klass);
		} catch(JsonSyntaxException e) {
			throw new RestResourceException("Unable to parse response body to "
					+ "JSON: " + body, e);
		}
	}
	public static <T> T loadElement(Class<T> klass, String body)
			throws RestResourceException {
		return loadElement(klass, body, new Gson());
	}

	public static <T> List<T> loadCollection(Class<T> klass, String body,
			Gson gson) throws RestResourceException {
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
	public static <T> List<T> loadCollection(Class<T> klass, String body)
			throws RestResourceException {
		return loadCollection(klass, body, new Gson());
	}

	public static <T> String parseElement(T o, Gson gson) {
		if (o == null)
			return "";
		return new StringBuilder("{\"").append(elementName(o.getClass())).
				append("\"").append(": ").append(gson.toJson(o)).append("}").
				toString();
	}
	public static <T> String parseElement(T o) {
		return parseElement(o, new Gson());
	}

	protected static String makeTheCall(String method, String path,
			String body, String format) throws RestResourceException {
		HttpURLConnection connection = openConnection(method, path, body, format);
		try {
			if (requestListeners != null)
				for (RequestListener l : requestListeners)
					l.requestMade(method, path, body);
			if (handleResponseCode(connection) == NO_CONTENT)
				return null;
			return extractResponseBody(connection);
		} finally {
			connection.disconnect();
		}
	}
	protected static String makeTheCall(String method, String path,
			String format) throws RestResourceException {
		return makeTheCall(method, path, null, format);
	}

	protected static int handleResponseCode(HttpURLConnection connection)
			throws RestResourceException {
		try {
			int status = connection.getResponseCode();
			String url = connection.getURL().toString();
			String method = connection.getRequestMethod();
			if (status == UNAUTHORIZED_ACCESS)
				throw new UnauthorizedAccessException(status, method, url);
			else if (status == FORBIDDEN_ACCESS)
				throw new ForbiddenAccessException(status, method, url);
			else if (status == RESOURCE_INVALID)
				throw new ResourceInvalidException(status, method, url);
			else if (status == NOT_FOUND)
				throw new ResourceNotFoundException(status, method, url);
			else if (status >= 400 && status < 500)
				throw new ClientException(status, method, url);
			else if (status >= 500 && status < 600)
				throw new ServerException(status, method, url);
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
			String body, String format) throws RestResourceException {
		URL url = null;
		try {
			url = new URL(path);
		} catch (MalformedURLException e) {
			throw new RestResourceException("Error while composing URL (method="
					+ method + ", path=" + path + ")", e);
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
		String basicAuth = url.getUserInfo();
		if (basicAuth != null && !basicAuth.isEmpty())
			connection.addRequestProperty("Authorization", "Basic " + 
					new String(Base64.encodeBase64(basicAuth.getBytes())));
		connection.setRequestProperty("Accept", "application/" + format);
		connection.setRequestProperty("Content-Type", "application/" + format);
		if (body != null) {
			try {
				DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
				dos.writeBytes(body);
				dos.flush();
				dos.close();
			} catch (IOException e) {
				throw new RestResourceException("Error while writing "
						+ method + " with body (" + body + ")", e);
			}
		}
		return connection;
	}
	protected static HttpURLConnection openConnection(String method, String path,
			String format) throws RestResourceException {
		return openConnection(method, path, null, format);
	}

	public static void addRequestListener(RequestListener requestListener) {
		if (requestListeners == null)
			requestListeners = new ArrayList<RequestListener>();
		requestListeners.add(requestListener);
	}
}
