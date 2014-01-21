package restresource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static restresource.support.TestUtils.assertLastRequest;
import static restresource.support.TestUtils.assertRaises;
import static restresource.support.TestUtils.assertRaisesStatusException;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import restresource.exceptions.ResourceNotFoundException;
import restresource.exceptions.UnauthorizedAccessException;
import restresource.support.Person;
import restresource.support.StatusCode;
import restresource.support.TestUtils;
import restresource.support.User;
import restresource.utils.ParamGenerator;
import restresource.utils.Proc;

public class RestResourceIntegrationTest {
	@Before
	public void before() {
		TestUtils.recordRequests();
	}

	@Test
	public void testFind() {
		Person p = RestResource.find(1, Person.class);
		assertEquals("John", p.getName());
		assertLastRequest("GET", "http://localhost:4567/people/1.json");
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
		assertLastRequest("GET", "http://localhost:4567/people.json");
	}

	@Test
	public void testAllWithQueryParameter() {
		List<Person> l = RestResource.all(Person.class,
				new ParamGenerator("name", "John").append("test", "true").map());
		assertLastRequest("GET", "http://localhost:4567/people.json?"
				+ "test=true&name=John");
		assertEquals(1, l.size());
		assertEquals("John", l.get(0).getName());
	}

	@Test
	public void testAllRaisesCorrectException() {
		assertRaisesStatusException(new Proc() {
			@Override
			public void call(Object... args) {
				RestResource.all(StatusCode.class,
						new ParamGenerator("status", args[0]).map());
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
		assertLastRequest("POST", "http://localhost:4567/people.json",
				"{\"person\": {\"id\":0,\"name\":\"Test\"}}");
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
		assertLastRequest("PUT", "http://localhost:4567/people/1.json",
				"{\"person\": {\"id\":1,\"name\":\"Test\"}}");
	}

	@Test
	public void testDestroy() {
		final StatusCode sc = new StatusCode();
		sc.setCustomId(1);
		assertRaises(ResourceNotFoundException.class, new Proc() {
			@Override
			public void call(Object... args) {
				RestResource.destroy(sc);
			}
		});

		Person p = new Person();
		p.setId(1);
		RestResource.destroy(p);
		assertLastRequest("DELETE", "http://localhost:4567/people/1.json");
	}

	@Test
	public void testGet() {
		assertEquals("{\"person\":{\"name\":\"John\"}}",
				RestResource.get(Person.class, "1"));
		assertLastRequest("GET", "http://localhost:4567/people/1.json");

		assertEquals("[{\"name\":\"John\"},{\"name\":\"Mary\"}]",
				RestResource.get(Person.class));
		assertLastRequest("GET", "http://localhost:4567/people.json");

		assertEquals("[{\"name\":\"John\"}]",
				RestResource.get(Person.class, new ParamGenerator("name",
						"John").append("test", true).map()));
		assertLastRequest("GET",
				"http://localhost:4567/people.json?test=true&name=John");
	}

	@Test
	public void testPost() {
		assertEquals("{\"person\":{\"id\":1,\"name\":\"John\"}}",
				RestResource.post(Person.class,
						new Person(0, "John")));
		assertLastRequest("POST", "http://localhost:4567/people.json",
				"{\"person\": {\"id\":0,\"name\":\"John\"}}");
		try {
			RestResource.post(Person.class, new ParamGenerator("test", "true").
					map(), "create");
		} catch(ResourceNotFoundException e) {
			// ignore this
		}
		assertLastRequest("POST",
				"http://localhost:4567/people/create.json?test=true", "");
	}

	@Test
	public void testPut() {
		String body = "{\"person\": {\"id\":1,\"name\":\"John\"}}";
		assertNull(RestResource.put(new Person(1), new Person(1, "John")));
		assertLastRequest("PUT", "http://localhost:4567/people/1.json", body);

		try {
			RestResource.put(new Person(1), new ParamGenerator("test", "false").
					map(), "update");
		} catch(ResourceNotFoundException e) {
			// ignore this
		}
		assertLastRequest("PUT",
				"http://localhost:4567/people/1/update.json?test=false", "");

		try {
			RestResource.put(new Person(1), "promote");
		} catch(ResourceNotFoundException e) {
			// ignore this
		}
		assertLastRequest("PUT",
				"http://localhost:4567/people/1/promote.json", "");
	}

	@Test
	public void testDelete() {
		assertNull(RestResource.delete(new Person(1)));
		assertLastRequest("DELETE", "http://localhost:4567/people/1.json");

		try {
			assertNull(RestResource.delete(Person.class));
		} catch(ResourceNotFoundException e) {
			// ignore this
		}
		assertLastRequest("DELETE", "http://localhost:4567/people.json");

		try {
			RestResource.delete(new Person(1), new ParamGenerator("test",
					"delete").map(), "fire");
		} catch(ResourceNotFoundException e) {
			// ignore this
		}
		assertLastRequest("DELETE",
				"http://localhost:4567/people/1/fire.json?test=delete");

		try {
			RestResource.delete(new Person(1), "fire");
		} catch(ResourceNotFoundException e) {
			// ignore this
		}
		assertLastRequest("DELETE", "http://localhost:4567/people/1/fire.json");
	}

	@Test
	public void testAuth() {
		assertRaises(UnauthorizedAccessException.class, new Proc() {
			@Override
			public void call(Object... args) {
				User.setSite("http://localhost:4567/");
				RestResource.find(1, User.class);
			}
		});

		assertRaises(UnauthorizedAccessException.class, new Proc() {
			@Override
			public void call(Object... args) {
				User.setSite("http://user:pas@localhost:4567/");
				RestResource.find(1, User.class);
			}
		});

		User.setSite("http://user:pass@localhost:4567/");
		RestResource.find(1, User.class);
	}
}
