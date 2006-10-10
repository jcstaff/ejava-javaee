package ejava.examples.orm.ejbql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.embedded.EJB3StandaloneBootstrap;

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
public class JBossTest extends TestCase {
    private static Log log_ = LogFactory.getLog(JBossTest.class);

    public static Test suite() {
        log_.debug("creating test suite");
        TestSuite tests = new TestSuite();
        tests.addTestSuite(QueryDemo.class);
        tests.addTestSuite(EJBQLDemo.class);
        tests.addTestSuite(BulkDemo.class);
        tests.addTestSuite(NativeQueryDemo.class);
        tests.addTestSuite(NamedQueryDemo.class);
        
        TestSetup wrapper = new TestSetup(tests) {
            public void setUp() throws Exception {
                startupEmbeddedJBoss();
            }
            public void tearDown() throws Exception {
                shutdownEmbeddedJBoss();
                JPAUtil.close();
            }
        };
        
        return wrapper;
    }

    /**
     * This method performs the one-time JBoss embbedded container startup
     * calls. It is invoked by the TestSetup that was returned as part of 
     * the TestSuite.
     */
    public static void startupEmbeddedJBoss() throws Exception {
        try {
            log_.debug("starting up embedded JBoss container");     
            EJB3StandaloneBootstrap.boot(null);
            EJB3StandaloneBootstrap.scanClasspath();
            log_.debug("embedded JBoss container startup complete");
        }
        catch (Exception ex) {
            log_.fatal("error on embbeded JBoss startup" + ex);
            fail("" + ex);
        }
    }
    
    /**
     * This method performs the one-time JBoss embedded container shutdown
     * calls. It is invoked by the TestSetup that was returned as part of the 
     * TestSuite.
     */
    public static void shutdownEmbeddedJBoss() throws Exception {
        try {
        log_.debug("shutting down embedded JBoss container");     
        EJB3StandaloneBootstrap.shutdown();
        log_.debug("embedded JBoss container shutdown complete");
        }
        catch (Exception ex) {
            log_.fatal("error on embedded JBoss shutdown", ex);
            fail("" + ex);
        }
    }    
}
