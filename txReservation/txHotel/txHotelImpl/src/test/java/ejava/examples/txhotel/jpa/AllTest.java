package ejava.examples.txhotel.jpa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.txhotel.jpa.HotelRegistrationDemo;
import ejava.examples.txhotel.jpa.HotelReservationSessionDemo;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This class provides the main entry point for each of the individual test
 * cases. It provides one-time setUp and tearDown functionality needed by 
 * all individual test cases.
 * 
 * @author jcstaff
 * $Id:$
 */
public class AllTest extends TestCase {
    private static Log log_ = LogFactory.getLog(AllTest.class);

    public static Test suite() {
        log_.debug("creating test suite");
        TestSuite tests = new TestSuite();
        tests.addTestSuite(HotelRegistrationDemo.class);
        tests.addTestSuite(HotelReservationSessionDemo.class);
        
        TestSetup wrapper = new TestSetup(tests) {
            public void setUp() throws Exception {
            }
            public void tearDown() throws Exception {
            }
        };
        
        return wrapper;
    }
}
