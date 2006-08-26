package ejava.examples.orm.core.products;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

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
public class ProductsTest extends TestCase {
    private static Log log_ = LogFactory.getLog(ProductsTest.class);
    private static final String PERSISTENCE_UNIT = "ormCore";
    static EntityManagerFactory emf; 

    public static Test suite() {
        log_.debug("creating test suite");
        TestSuite tests = new TestSuite();
        tests.addTestSuite(BasicAnnotationDemo.class);
        tests.addTestSuite(BasicMappingDemo.class);
        tests.addTestSuite(TableColumnAnnotationDemo.class);
        tests.addTestSuite(TableColumnMappingDemo.class);
        tests.addTestSuite(PKGenAnnotationDemo.class);
        tests.addTestSuite(PKGenMappingDemo.class);
        tests.addTestSuite(PKClassAnnotationDemo.class);
        tests.addTestSuite(PKClassMappingDemo.class);
        tests.addTestSuite(TransientAnnotationDemo.class);
        tests.addTestSuite(TransientMappingDemo.class);
        tests.addTestSuite(LazyFetchAnnotationDemo.class);
        tests.addTestSuite(LazyFetchMappingDemo.class);
        tests.addTestSuite(TypesAnnotationDemo.class);
        tests.addTestSuite(TypesMappingDemo.class);
        tests.addTestSuite(MultiTableAnnotationDemo.class);
        tests.addTestSuite(MultiTableMappingDemo.class);
        tests.addTestSuite(EmbeddedAnnotationDemo.class);
        tests.addTestSuite(EmbeddedMappingDemo.class);
        
        TestSetup wrapper = new TestSetup(tests) {
            public void setUp() throws Exception {
                startupEmbeddedJBoss();
                initEntityManagerFactory();
            }
            public void tearDown() throws Exception {
                closeEntityManagerFactory();
                shutdownEmbeddedJBoss();
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
    
    public static void initEntityManagerFactory() throws Exception {
        log_.info("creating EntityManager Factory (" + PERSISTENCE_UNIT + ")");
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    }

    public static void closeEntityManagerFactory() throws Exception {
        log_.info("closing EntityManagerFactory");
        emf.close();
        log_.info("EntityManagerFactory closed");        
    }        
}
