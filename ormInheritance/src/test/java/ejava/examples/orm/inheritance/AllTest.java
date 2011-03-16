package ejava.examples.orm.inheritance;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import junit.framework.TestCase;

/**
 * This class provides the main entry point for each of the individual test
 * cases. It provides one-time setUp and tearDown functionality needed by 
 * all individual test cases.
 * 
 * @author jcstaff
 * $Id:$
 */

@RunWith(Suite.class)
@SuiteClasses({
    SingleTablePerHierarchyDemo.class,
    TablePerConcreteClassDemo.class,
    TablePerClassDemo.class,
    NonEntityBaseDemo.class,
    MixedDemo.class
})
public class AllTest extends TestCase {
}
