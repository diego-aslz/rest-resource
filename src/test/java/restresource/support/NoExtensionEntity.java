package restresource.support;

import restresource.Element;

public class NoExtensionEntity implements Element {
	private int id;

	public NoExtensionEntity(int id) {
		super();
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public static String extension() {
		return null;
	}
}
