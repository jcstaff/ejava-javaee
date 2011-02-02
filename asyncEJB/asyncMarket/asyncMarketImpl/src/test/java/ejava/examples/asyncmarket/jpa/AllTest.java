package ejava.examples.asyncmarket.jpa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.asyncmarket.JPAUtil;
import ejava.examples.asyncmarket.dao.AuctionItemDemo;
import ejava.examples.asyncmarket.dao.OrderDemo;
import ejava.examples.asyncmarket.dao.PersonDemo;

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
        tests.addTestSuite(PersonDemo.class);
        tests.addTestSuite(AuctionItemDemo.class);
        tests.addTestSuite(OrderDemo.class);
        
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
