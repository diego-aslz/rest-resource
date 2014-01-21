package restresource.utils;

import java.util.HashMap;
import java.util.Map;

public class ParamGenerator {
	private Map<String, String> params;
	
	public ParamGenerator() {
		params = new HashMap<String, String>();
	}

	public ParamGenerator(String key, Object value) {
		this();
		append(key, value);
	}
	
	public Map<String, String> map() {
		return params;
	}

	public ParamGenerator append(String key, Object value) {
		params.put(key, value.toString());
		return this;
	}
}
