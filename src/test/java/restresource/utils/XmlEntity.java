package restresource.utils;

import restresource.Element;

public class XmlEntity implements Element {
	private int id;

	public XmlEntity(int id) {
		super();
		this.id = id;
	}

	public static String extension() {
		return "xml";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
