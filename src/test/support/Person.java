package test.support;

import com.google.gson.annotations.Expose;

public class Person {
	@Expose
	String name;

	public static String getSite() {
		return "http://localhost:4567/";
	}

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