package test.support;

import com.google.gson.annotations.Expose;

public class Person implements restresource.RestModel {
	@Expose
	String name;

	@Override
	public String getRestSite() {
		return "http://localhost:4567/";
	}

	@Override
	public String getFormat() {
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public static String collectionName() {
		return "people";
	}
	
	public static String getSite() {
		return "http://localhost:4567/";
	}
}