package restresource;

import static org.junit.Assert.assertEquals;
import static restresource.support.TestUtils.assertRaises;
import static restresource.support.TestUtils.assertRaisesStatusException;

import java.util.List;

import org.junit.Test;

import restresource.exceptions.ResourceNotFoundException;
import restresource.support.Person;
import restresource.support.StatusCode;
import restresource.utils.Proc;

public class RestResourceIntegrationTest {
	@Test
	public void testFind() {
		Person p = RestResource.find(1, Person.class);
		assertEquals("John", p.getName());
	}

	@Test
	public void testFindRaisesCorrectException() {
		assertRaisesStatusException(new Proc() {
			@Override
			public void call(Object... args) {
				RestResource.find(args[0], StatusCode.class);
			}
		});
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
	public void testAllRaisesCorrectException() {
		assertRaisesStatusException(new Proc() {
			@Override
			public void call(Object... args) {
				RestResource.all(StatusCode.class, "status=" + args[0]);
			}
		});
	}

	@Test
	public void testCreate() {
		Person p = new Person();
		p.setName("Test");
		p = RestResource.save(p);
		assertEquals(1, p.getId());
		assertEquals("Test", p.getName());
	}

	@Test
	public void testCreateRaisesCorrectException() {
		assertRaisesStatusException(new Proc() {
			@Override
			public void call(Object... args) {
				RestResource.save(new StatusCode((Integer) args[0]));
			}
		});
	}

	@Test
	public void testUpdate() {
		Person p = new Person();
		p.setId(1);
		p.setName("Test");
		p = RestResource.save(p);
		assertEquals(1, p.getId());
		assertEquals("Test", p.getName());
	}

	@Test
	public void testDelete() {
		final StatusCode sc = new StatusCode();
		sc.setCustomId(1);
		assertRaises(ResourceNotFoundException.class, new Proc() {
			@Override
			public void call(Object... args) {
				RestResource.delete(sc);
			}
		});

		Person p = new Person();
		p.setId(1);
		RestResource.delete(p);
	}
}
