package ejava.examples.orm.core.products;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.orm.core.mapped.Bike;

import junit.framework.TestCase;

/** 
 * This test case provides a demo of using a class that has been mapped to
 * the database using a separate orm.xml descripor referenced by
 * META-INF/persistence.xml. Only the basic properties were specific. All
 * remaining properties are the default of the Java Persistence provider.
 * 
 * @author jcstaff
 * $Id:$
 */
public class BasicMappingDemo extends TestCase {
    private static Log log = LogFactory.getLog(BasicMappingDemo.class);
    private EntityManager em = null;

    protected void setUp() throws Exception {
        EntityManagerFactory emf = ProductsTest.emf;   
        em = emf.createEntityManager();
        em.getTransaction().begin();
    }

    protected void tearDown() throws Exception {
        EntityTransaction tx = em.getTransaction();
        if (tx.isActive()) {
            if (tx.getRollbackOnly() == true) { tx.rollback(); }
            else                              { tx.commit(); }
        }
        em.close();
    }
    
    /**
     * This test demonstrates the ability to map a simple object to the 
     * database using only the minimal mapping. This only works if
     * we create the database and the names of the Java class are sufficient
     * for use in the database table. 
     * 
     * Note that there could be only one Entity named Bike. Therefore, the
     * descriptor named the entity MappedBike and then supplied a table name
     * that was also being used for the annotated Bike.
     * 
     * By looking at the Bike class and mapping file, we would expect to see a 
     * Bike table with columns id, make, model, and size. The data types for 
     * these columns are the defaults of the Java Persistence provider. If we
     * left the name off, the table would have been called MappedBike. 
     */
    public void testDefaultMapping() {
        log.info("testDefaultMapping");
        ejava.examples.orm.core.mapped.Bike bike = new Bike(2);
        bike.setMake("trek");
        bike.setModel("2200");
        bike.setSize(26);
        
        //insert a row in the database
        em.persist(bike);
        log.info("created bike:" + bike);
        
        //find the inserted object
        Bike bike2 = em.find(Bike.class, 2L); 
        //note that the literal value needed to be explicitly expressed as
        //a long so that the Java5 autoboxing didn't incorrectly pass it as
        //an Integer; the data type for the PK is long.
        log.info("found bike:" + bike2);
        assertNotNull(bike2);
        assertEquals(bike2.getSize(), bike2.getSize());
        
        //lets update the size
        bike.setSize(24);
        em.flush();
        //since the persistence context is still active, bike and bike2
        //are actually the same object 
        assertEquals(bike.getSize(), bike2.getSize());
        log.info("updated bike:" + bike2);
        
        //lets delete the object
        em.remove(bike);
        em.flush();
        log.info("removed bike:" + bike);
        
        //lets put a bike back in at end of test so we can see it in database 
        em.persist(bike2);
        log.info("created leftover bike:" + bike2);
    }        
}
