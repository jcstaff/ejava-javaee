package ejava.examples.orm.core.products;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.orm.core.annotated.Bike;

import junit.framework.TestCase;

/**
 * This test case provides a demo of using a class that has been mapped
 * to the database with only basic class annotations. All defaults will
 * be determined by the Java Persistence provider.
 * 
 * @author jcstaff
 * $Id:$
 */
public class BasicAnnotationDemo extends TestCase {
    private static Log log = LogFactory.getLog(BasicAnnotationDemo.class);
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
     * database using only the minimal annotations. This only works if
     * we create the database and the names of the Java class are sufficient
     * for use in the database table.
     * 
     * By looking at the Bike class, we would expect to see a Bike table
     * with columns id, make, model, and size. The data types for these 
     * columns are the defaults of the Java Persistence provider. 
     */
    public void testDefaultMapping() {
        log.info("testDefaultMapping");
        ejava.examples.orm.core.annotated.Bike bike = new Bike(1);
        bike.setMake("trek");
        bike.setModel("2200");
        bike.setSize(26);
        
        //insert a row in the database
        em.persist(bike);
        log.info("created bike:" + bike);
        
        //find the inserted object
        Bike bike2 = em.find(Bike.class, 1L); 
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
