package ejava.examples.orm.core.products;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
            }
            public void tearDown() throws Exception {
                JPAUtil.close();
            }
        };
        
        return wrapper;
    }
}
