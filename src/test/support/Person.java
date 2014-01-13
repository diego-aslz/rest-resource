package test.support;

import com.google.gson.annotations.Expose;

public class Person {
	@Expose
	String name;

	/**
	 * Determines the address where the RESTful Web Service is.
	 */
	public static String getSite() {
		return "http://localhost:4567/";
	}

	/**
	 * Determines the name of the collection to be used in the URL for the
	 * calls. This method is optional. If it's not present, the name of class,
	 * lower cased, plus "s" will be used.
	 */
	public static String collectionName() {
		return "people";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}