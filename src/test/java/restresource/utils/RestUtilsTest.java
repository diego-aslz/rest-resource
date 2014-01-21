package restresource.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import restresource.exceptions.RestResourceException;
import restresource.support.Person;
import restresource.support.StatusCode;

public class RestUtilsTest {

	@Test
	public void testCollectionName() {
		assertEquals("people", RestUtils.collectionName(Person.class));
		assertEquals("string_builders", RestUtils.collectionName(StringBuilder.class));
	}

	@Test
	public void testCollectionPath() {
		assertEquals("people.json", RestUtils.collectionPath(Person.class));
		assertEquals("people/born/here.json", RestUtils.collectionPath(Person.class,
				"born", "here"));
	}

	@Test
	public void testCollectionUrl() {
		assertEquals("http://localhost:4567/people.json", RestUtils.collectionUrl(Person.class));
		assertEquals("http://localhost:4567/people/born/here.json",
				RestUtils.collectionUrl(Person.class, "born", "here"));
		Map<String, String> params = new HashMap<String, String>();
		params.put("param", "true");
		assertEquals("http://localhost:4567/people/born/here.json?param=true",
				RestUtils.collectionUrl(Person.class, params, "born", "here"));
	}

	@Test
	public void testElementName() {
		assertEquals("person", RestUtils.elementName(Person.class));
		assertEquals("string_builder", RestUtils.elementName(StringBuilder.class));
	}

	@Test
	public void testElementPath() {
		assertEquals("people/1.json", RestUtils.elementPath(new Person(1)));
		assertEquals("people/1/promote/test.json", RestUtils.elementPath(new Person(1),
				"promote", "test"));
		Map<String, String> params = new HashMap<String, String>();
		params.put("param", "true");
		assertEquals("people/1/born/here.json?param=true",
				RestUtils.elementPath(new Person(1), params, "born", "here"));
	}

	@Test
	public void testElementUrl() {
		assertEquals("http://localhost:4567/people/1.json", RestUtils.elementUrl(new Person(1)));
		assertEquals("http://localhost:4567/people/1/born/here.json",
				RestUtils.elementUrl(new Person(1), "born", "here"));
		Map<String, String> params = new HashMap<String, String>();
		params.put("param", "true");
		assertEquals("http://localhost:4567/people/1/born/here.json?param=true",
				RestUtils.elementUrl(new Person(1), params, "born", "here"));
}

	@Test
	public void testId() {
		Person p = new Person();
		p.setId(25);
		assertEquals(25, RestUtils.id(p));

		RestResourceException exc = null;
		try {
			RestUtils.id("");
		} catch(RestResourceException e) {
			exc = e;
		}
		assertNotNull(exc);

		StatusCode sc = new StatusCode(200);
		sc.setCustomId(200);
		assertEquals(200, RestUtils.id(sc));
	}
}
