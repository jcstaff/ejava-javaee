package ejava.examples.dao.jpa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This optional JUnit construct can be used to manage shared resources 
 * you need across initividual JUnit TestCasts. This is quite helpful when
 * you are using a library that can only have a single initialization or 
 * shutdown per JVM. I have also seen it helpful in managing the number 
 * of connections to databases and other resources. If your JUnit tests can
 * stand along, it is NOT needed.
 * 
 * @author jcstaff
 */
public class AllTest extends TestCase {
    @SuppressWarnings("unused")
	private static Log log = LogFactory.getLog(AllTest.class);
    
    public static Test suite() {
        TestSuite tests = new TestSuite();
        tests.addTestSuite(JPAAuthorDAODemo.class);
        tests.addTestSuite(JPANoDAODemo.class);
        
        TestSetup wrapper = new TestSetup(tests) {
            public void setUp() throws Exception {
                //put any common startup here - we are about to get started
            }
            public void tearDown() throws Exception {
                //put any common cleanup here - we are totally done
                JPAUtil.close();
            }
        };
        
        return wrapper;
    }
}
