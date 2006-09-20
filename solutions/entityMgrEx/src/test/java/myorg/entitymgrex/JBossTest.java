package myorg.entitymgrex;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.ejb3.embedded.EJB3StandaloneBootstrap;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This class provides a centralized startup and shutdown functionality for 
 * the JBoss embedded container. This class is the entry point for JUnit.
 * It defines the tests included in the TestSuite and makes sure that the
 * embedded container is only started and stopped at the start/end of the JVM.
 * Since each JUnit test is within a single JVM, we cannot call this code
 * in the normal setUp() and tearDown() functions.<p/>
 * 
 * The class could be further generalized with the use of a property file, 
 * but for now, do the following and your test class will not have any 
 * dependency on JBoss.
 * <ul>
 * <li>write a normal test class, but hide it from search paths by leaving  
 * the word Test out of its name.</li>
 * <li>add you test class to the TestSuite here using tests.addTestSuite()</li>
 * <li>use JPAUtil.getEntityManagerFactory() and this class will make sure
 * the factories are closed</li>
 * </ul>
 * 
 * @author jcstaff
 */
public class JBossTest extends TestCase {
    private static Log log = LogFactory.getLog(JBossTest.class);
    
    public static Test suite() {
        TestSuite tests = new TestSuite();
        tests.addTestSuite(EntityMgrExercise.class);
        
        TestSetup wrapper = new TestSetup(tests) {
            public void setUp() throws Exception {
                startupEmbeddedJBoss();
            }
            public void tearDown() throws Exception {
                JPAUtil.close();
                shutdownEmbeddedJBoss();
            }
        };
        
        log.debug("returning test suite");
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

