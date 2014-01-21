package restresource.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import restresource.RequestListener;
import restresource.RestResource;
import restresource.exceptions.ClientException;
import restresource.exceptions.ForbiddenAccessException;
import restresource.exceptions.ResourceInvalidException;
import restresource.exceptions.ResourceNotFoundException;
import restresource.exceptions.ServerException;
import restresource.exceptions.StatusException;
import restresource.exceptions.UnauthorizedAccessException;
import restresource.utils.Proc;

public class TestUtils {
	private static List<Request> requests = new ArrayList<Request>();

	public static void assertRaises(Class<? extends Exception> klass, Proc p,
			Object... args) {
		Exception exc = null;
		try {
			p.call(args);
		} catch(Exception e) {
			exc = e;
		}
		assertNotNull("Expected to catch an exception of " + klass
				+ ", but no exception was received.", exc);
		assertEquals(klass, exc.getClass());
	}

	public static void assertRaisesStatusException(Proc block) {
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
	
	public static Request assertLastRequest(String method, String path,
			String body) {
		Request req = lastRequest();
		assertNotNull("Last request should not be null.", req);
		assertEquals(method, req.method());
		assertEquals(path,   req.path()  );
		assertEquals(body,   req.body()  );
		return req;
	}
	public static Request assertLastRequest(String method, String path) {
		return assertLastRequest(method, path, null);
	}

	public static Request lastRequest() {
		if (requests.isEmpty())
			return null;
		return requests.get(requests.size() - 1);
	}

	public static void recordRequests() {
		requests.clear();
		RestResource.addRequestListener(new RequestListener() {
			@Override
			public void requestMade(String method, String path, String body) {
				requests.add(new Request(method, path, body));
			}
		});
	}
}
