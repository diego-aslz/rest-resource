package restresource;

public interface RequestListener {
	public void requestMade(String method, String path, String... params);
}
