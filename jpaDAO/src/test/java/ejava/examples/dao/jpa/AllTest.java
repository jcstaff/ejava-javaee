package ejava.examples.dao.jpa;

import org.junit.runner.RunWith;

import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This *optional* JUnit construct can be used to group tests. 
 * 
 * @author jcstaff
 */
@RunWith(Suite.class)
@SuiteClasses({
	JPAAuthorDAODemo.class,
	JPANoDAODemo.class,
	JPAExtendedOnlyDemo.class
})
public class AllTest {
}
