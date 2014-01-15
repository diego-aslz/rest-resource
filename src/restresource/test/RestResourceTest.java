package restresource.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import restresource.RestResource;
import restresource.exceptions.ClientException;
import restresource.exceptions.ForbiddenAccessException;
import restresource.exceptions.ResourceInvalidException;
import restresource.exceptions.ResourceNotFoundException;
import restresource.exceptions.ServerException;
import restresource.exceptions.StatusException;
import restresource.exceptions.UnauthorizedAccessException;
import restresource.test.support.Person;
import restresource.test.support.StatusCode;
import restresource.utils.Proc;

public class RestResourceTest {
	@Test
	public void testFind() {
		Person p = RestResource.find(1, Person.class);
		assertEquals("John", p.getName());
	}

	private void testRaiseStatusException(Proc block) {
		for (int i = 400; i < 600; i++) {
			StatusException ce = null;
			try {
				block.call(i);
			} catch(StatusException e) {
				ce = e;
			}
			if (ce == null)
				fail("Expected 'find' with response code " + i + " to raise " +
						StatusException.class);
			assertEquals(i, ce.getStatus());
			if (i < 500) {
				assertTrue("Exception raised should be ClientException",
						ce instanceof ClientException);
				switch (i) {
				case 401:
					assertTrue("Expected exception to be instance of UnauthorizedAccessException (401)",
							ce instanceof UnauthorizedAccessException);
					break;
				case 403:
					assertTrue("Expected exception to be instance of ForbiddenAccessException (403)",
							ce instanceof ForbiddenAccessException);
					break;
				case 404:
					assertTrue("Expected exception to be instance of ResourceNotFoundException (404)",
							ce instanceof ResourceNotFoundException);
					break;
				case 422:
					assertTrue("Expected exception to be instance of ResourceInvalidException (422)",
							ce instanceof ResourceInvalidException);
					break;
				}
			} else
				assertTrue("Exception raised should be ServerException",
						ce instanceof ServerException);
		}
	}

	@Test
	public void testFindRaisesCorrectException() {
		testRaiseStatusException(new Proc() {
			@Override
			public void call(Object... args) {
				RestResource.find(args[0], StatusCode.class);
			}
		});
	}

	@Test
	public void testAllRaisesCorrectException() {
		testRaiseStatusException(new Proc() {
			@Override
			public void call(Object... args) {
				RestResource.all(StatusCode.class, "status=" + args[0]);
			}
		});
	}

	@Test
	public void testCreateRaisesCorrectException() {
		testRaiseStatusException(new Proc() {
			@Override
			public void call(Object... args) {
				RestResource.save(new StatusCode((Integer) args[0]));
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
	public void testCreate() {
		Person p = new Person();
		p.setName("Test");
		p = RestResource.save(p);
		assertEquals(1, p.getId());
		assertEquals("Test", p.getName());
	}
}
