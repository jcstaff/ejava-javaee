package ejava.examples.dao.jpa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This class provides a centralized startup and shutdown functionality for 
 * the JBoss embedded container.
 * 
 * @author jcstaff
 */
public class AllTest extends TestCase {
    @SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(AllTest.class);
    
    public static Test suite() {
        TestSuite tests = new TestSuite();
        tests.addTestSuite(JPAExtendedOnlyDemo.class);
        
        TestSetup wrapper = new TestSetup(tests) {
            public void setUp() throws Exception {
            }
            public void tearDown() throws Exception {
                JPAUtil.close();
            }
        };
        
        return wrapper;
    }
}
