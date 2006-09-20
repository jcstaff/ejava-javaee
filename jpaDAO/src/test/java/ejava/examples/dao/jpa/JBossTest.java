package ejava.examples.dao.jpa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.embedded.EJB3StandaloneBootstrap;

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
public class JBossTest extends TestCase {
    private static Log log = LogFactory.getLog(JBossTest.class);
    
    public static Test suite() {
        TestSuite tests = new TestSuite();
        tests.addTestSuite(JPAExtendedOnlyDemo.class);
        tests.addTestSuite(JPAAuthorDAODemo.class);
        
        TestSetup wrapper = new TestSetup(tests) {
            public void setUp() throws Exception {
                startupEmbeddedJBoss();
            }
            public void tearDown() throws Exception {
                JPAUtil.close();
                shutdownEmbeddedJBoss();
            }
        };
        
        return wrapper;
    }

    public static void startupEmbeddedJBoss() throws Exception {
        try {
            log.debug("starting up embedded JBoss container");     
            EJB3StandaloneBootstrap.boot(null);
            EJB3StandaloneBootstrap.scanClasspath();
            log.debug("embedded JBoss container startup complete");
        }
        catch (Exception ex) {
            log.fatal("error on embbeded JBoss startup" + ex);
            fail("" + ex);
        }
    }
    public static void shutdownEmbeddedJBoss() throws Exception {
        try {
        log.debug("shutting down embedded JBoss container");     
        EJB3StandaloneBootstrap.shutdown();
        log.debug("embedded JBoss container shutdown complete");
        }
        catch (Exception ex) {
            log.fatal("error on embedded JBoss shutdown", ex);
            fail("" + ex);
        }
    }    
}
