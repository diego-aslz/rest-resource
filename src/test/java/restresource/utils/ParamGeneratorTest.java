package restresource.utils;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

public class ParamGeneratorTest {
	@Test
	public void testGeneratesParameter() {
		Map<String, String> map = new ParamGenerator("key", "value").map();
		assertEquals(1, map.size());
		assertEquals("value", map.get("key"));
	}

	@Test
	public void testAppendsParameter() {
		Map<String, String> map = new ParamGenerator("a", "1").
				append("b", "2").map();
		assertEquals(2, map.size());
		assertEquals("1", map.get("a"));
		assertEquals("2", map.get("b"));
	}
}
