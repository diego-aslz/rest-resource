package restresource.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import restresource.utils.Proc;

public class TestUtils {
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
}
