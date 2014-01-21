package restresource.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import restresource.Id;
import restresource.exceptions.RestResourceException;

public final class RestUtils {

	/**
	 * Tries to extract the site of a resource using the class method
	 * 'getSite()' of it.
	 * @param klass Class of the resource.
	 * @return The site address.
	 * @throws RestResourceException When the method is not found.
	 */
	public static String site(Class<?> klass) throws RestResourceException {
		try {
			return invokeMethod(klass, "getSite");
		} catch (Exception e) {
			throw new RestResourceException("Error while trying to access " +
					klass.getName() + ".getSite().", e);
		}
	}

	/**
	 * Determines the collection name to be used in the URL when making a call
	 * to the web service. If the 'klass' parameter implements the method
	 * 'collectionName', then it will be used. Otherwise, the collection name will
	 * be the class' name lower cased, with underscores instead of camel case and
	 * an 's' at the end.
	 * @param klass
	 * @return
	 */
	public static String collectionName(Class<?> klass) {
		try {
			return invokeMethod(klass, "collectionName");
		} catch (Exception e) {
			return elementName(klass) + "s";
		}
	}

	public static String collectionPath(Class<?> klass, String... paths) {
		return collectionPath(klass, null, paths);
	}

	public static String collectionPath(Class<?> klass,
			Map<String, String> params, String... paths) {
		StringBuilder sb = new StringBuilder(collectionName(klass));
		for (String path : paths)
			sb.append("/").append(path);
		String extension = extension(klass);
		if (extension != null && !extension.isEmpty())
			sb.append(".").append(extension);
		if (params != null) {
			String sep = "?";
			for (String key : params.keySet()) {
				sb.append(sep).append(key).append("=").append(params.get(key));
				sep = "&";
			}
		}
		return sb.toString();
	}

	public static String collectionUrl(Class<?> klass, String... paths) {
		return collectionUrl(klass, null, paths);
	}

	public static String collectionUrl(Class<?> klass,
			Map<String, String> params, String... paths) {
		return new StringBuilder(site(klass)).
				append(collectionPath(klass, params, paths)).toString();
	}

	/**
	 * Determines the element name to be used when generating a Json object.
	 * If the 'klass' parameter implements the method
	 * 'elementName', then it will be used. Otherwise, the element name will
	 * be the class' name lower cased, with underscores instead of camel case.
	 * @param klass
	 * @return
	 */
	public static String elementName(Class<?> klass) {
		try {
			return invokeMethod(klass, "elementName");
		} catch (Exception e) {
			return klass.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").
					toLowerCase();
		}
	}

	public static String elementPath(Object element, String... paths) {
		return elementPath(element, null, paths);
	}

	public static String elementPath(Object element, Map<String, String> params,
			String... paths) {
		String[] path = new String[paths.length + 1];
		path[0] = id(element).toString();
		System.arraycopy(paths, 0, path, 1, paths.length);
		return collectionPath(element.getClass(), params, path);
	}

	public static String elementUrl(Object element, String... paths) {
		return elementUrl(element, null, paths);
	}

	public static String elementUrl(Object element, Map<String, String> params,
			String... paths) {
		return new StringBuilder(site(element.getClass())).
				append(elementPath(element, params, paths)).toString();
	}

	/**
	 * Finds the id of the an object. If the object has some field with the
	 * {@link Id} annotation, this field will be used. Otherwise, a field with
	 * the name 'id' will be used. In the later case, if there's no 'id' field,
	 * an exception will be raised.
	 * @param element
	 * @return
	 * @throws RestResourceException
	 */
	public static Object id(Object element) throws RestResourceException {
		Field idField = null;
		Field annotatedField = null;
		for (Field f : element.getClass().getDeclaredFields()) {
			if (f.isAnnotationPresent(Id.class)) {
				annotatedField = f;
				break;
			}
			if (f.getName().equals("id"))
				idField = f;
		}
		if (annotatedField != null)
			idField = annotatedField;
		if (idField == null)
			throw new RestResourceException("Element " + element + " should "
					+ "have a Field annotated with @restresource.Id or with the"
					+ " name 'id'.");
		try {
			idField.setAccessible(true);
			return idField.get(element);
		} catch (Exception e) {
			throw new RestResourceException("Error while trying to access id "
					+ "field (" + idField.getName() + ") for element " +
					element, e);
		}
	}

	public static String urlFor(Object collectionOrElement,
			Map<String, String> params, String... path) {
		if (collectionOrElement instanceof Class)
			return collectionUrl((Class<?>) collectionOrElement, params, path);
		else
			return elementUrl(collectionOrElement, params, path);
	}

	public static String format(Object collectionOrElement) {
		return "json";
	}

	protected static String extension(Class<?> klass) {
		try {
			return invokeMethod(klass, "extension");
		} catch (Exception e) {
			return format(klass);
		}
	}

	protected static String invokeMethod(Class<?> klass, String method,
			Object... args) throws NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {
		Method m = klass.getMethod(method);
		m.setAccessible(true);
		return (String) m.invoke(klass, args);
	}
}
