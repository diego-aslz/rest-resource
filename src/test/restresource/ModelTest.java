package test.restresource;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import restresource.RestResource;
import test.support.Person;

public class ModelTest {
	@Test
	public void testFind() {
		Person p = (Person) RestResource.find(1, Person.class);
		assertEquals("John", p.getName());
	}

	@Test
	public void testFindNotFound() {
		assertEquals(null, RestResource.find(25, Person.class));
	}

	@Test
	public void testAll() {
		List<?> l = RestResource.all(Person.class);
		assertEquals(2, l.size());
		assertEquals("John", ((Person)l.get(0)).getName());
		assertEquals("Mary", ((Person)l.get(1)).getName());
	}

	@Test
	public void testAllWithQueryParameter() {
		List<?> l = RestResource.all(Person.class, "name=John", "test=true");
		assertEquals(1, l.size());
		assertEquals("John", ((Person)l.get(0)).getName());
	}
}
