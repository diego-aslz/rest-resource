package restresource.support;

import restresource.Element;

public class User implements Element {
	private static String site;

	public static String getSite() {
		return site;
	}

	public static void setSite(String site) {
		User.site = site;
	}
}
