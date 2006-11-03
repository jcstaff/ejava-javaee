package myorg.sysarg;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import junit.framework.TestCase;

public class ArgsTest extends TestCase {
    private static final Log log = LogFactory.getLog(ArgsTest.class);
    private static final String fooBar = System.getProperty("foo.bar");
    private static final String fooBaz = System.getProperty("foo.baz");

    public void testArgs() {
        log.info("foo.bar=" + fooBar);
        log.info("foo.baz=" + fooBaz);
    }
}
