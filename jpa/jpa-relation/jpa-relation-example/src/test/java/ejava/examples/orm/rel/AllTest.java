package ejava.examples.orm.rel;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This class provides an example of grouping a set of classes within a 
 * suite so they can be more easily managed as a suite. One might do this
 * if you had a single Impl approach with unit tests for each level of the
 * architecture (i.e., BO, DAO, and BLImpl) or types of technology (e.g.,
 * JDBC and JPA). Coincidentally, each of the classes under test also inherit 
 * from a common base test class that provides the ability to share resources.
 *  
 * @author jcstaff
 * $Id:$
 */
@RunWith(Suite.class)
@SuiteClasses({
    OneToOneDemo.class,
    OneToManyDemo.class,
    ManyToOneUnidirectionalDemo.class,
    OneToManyJoinTableDemo.class,
    ManyToManyDemo.class,
    OneToManyMapDemo.class,
    RelationshipOwnershipDemo.class
})
public class AllTest {
}
