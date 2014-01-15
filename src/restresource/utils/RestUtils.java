package restresource.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
			return invokeClassMethod(klass, "getSite");
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
			return invokeClassMethod(klass, "collectionName");
		} catch (Exception e) {
			return elementName(klass) + "s";
		}
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
			return invokeClassMethod(klass, "elementName");
		} catch (Exception e) {
			return klass.getSimpleName().replaceAll("([a-z])([A-Z])", "$1_$2").
					toLowerCase();
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
