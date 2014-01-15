package restresource.test.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import restresource.exceptions.RestResourceException;
import restresource.test.support.Person;
import restresource.test.support.StatusCode;
import restresource.utils.RestUtils;

public class RestUtilsTest {

	@Test
	public void testCollectionName() {
		assertEquals("people", RestUtils.collectionName(Person.class));
		assertEquals("string_builders", RestUtils.collectionName(StringBuilder.class));
	}

	@Test
	public void testElementName() {
		assertEquals("person", RestUtils.elementName(Person.class));
		assertEquals("string_builder", RestUtils.elementName(StringBuilder.class));
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
