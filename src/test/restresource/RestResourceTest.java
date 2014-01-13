package test.restresource;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import restresource.RestResource;
import test.support.Person;

public class RestResourceTest {
	@Test
	public void testFind() {
		Person p = RestResource.find(1, Person.class);
		assertEquals("John", p.getName());
	}

	@Test
	public void testFindNotFound() {
		assertEquals(null, RestResource.find(25, Person.class));
	}

	@Test
	public void testAll() {
		List<Person> l = RestResource.all(Person.class);
		assertEquals(2, l.size());
		assertEquals("John", l.get(0).getName());
		assertEquals("Mary", l.get(1).getName());
	}

	@Test
	public void testAllWithQueryParameter() {
		List<Person> l = RestResource.all(Person.class, "name=John", "test=true");
		assertEquals(1, l.size());
		assertEquals("John", l.get(0).getName());
	}
	
	@Test
	public void testCollectionName() {
		assertEquals("people", RestResource.collectionName(Person.class));
		assertEquals("string_builders", RestResource.collectionName(StringBuilder.class));
	}
}
