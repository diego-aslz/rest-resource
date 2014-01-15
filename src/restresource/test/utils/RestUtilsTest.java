package restresource.test.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import restresource.test.support.Person;
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
}
