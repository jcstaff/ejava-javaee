package myorg.mypackage.ex1;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
    private static Log log = LogFactory.getLog(AppTest.class);

    @Test
    public void testApp() {
        //System.out.println("testApp");
        log.info("testApp");
        App app = new App();
        assertTrue("app didn't return 1", app.returnOne() == 1);
    }

    @Test @Ignore //enable this test to see a failure
    public void testFail() {
        System.out.println("testFail");
        App app = new App();
        assertTrue("app didn't return 0", app.returnOne() == 0);
    }
}

