package myorg.mypackage.ex1;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
    private static Log log = LogFactory.getLog(AppTest.class);

    public AppTest( String testName ) {
        super( testName );
    }

    public static Test suite() {
        return new TestSuite( AppTest.class );
    }

    public void testApp() {
        //System.out.println("testApp");
        log.info("testApp");
        App app = new App();
        assertTrue("app didn't return 1", app.returnOne() == 1);
    }

/* uncomment this test to see a failure
    public void testFail() {
        System.out.println("testFail");
        App app = new App();
        assertTrue("app didn't return 0", app.returnOne() == 0);
    }
*/
}

