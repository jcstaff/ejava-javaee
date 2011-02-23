package ejava.examples.orm.core.products;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.orm.core.mapped.Umbrella;

import junit.framework.TestCase;

/**
 * This test case provides a demo of using Lazy fetch type on properties 
 * through annotations. Since Lazy Fetch is just a hint and it has been 
 * stated that lazy fetch of Basic data types is of limited value, don't
 * expect much out of this demo. In watching it, all setters are called 
 * before inspecting the object.
 * 
 * @author jcstaff
 * $Id:$
 */
public class LazyFetchMappingDemo extends TestCase {
    private static Log log = LogFactory.getLog(BasicAnnotationDemo.class);
    private static final String PERSISTENCE_UNIT = "ormCore";
    private EntityManagerFactory emf;
    private EntityManager em = null;

    protected void setUp() throws Exception {        
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);   
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
     * This test provides a demo of persisting and getting a class that
     * has marked model as LAZY. trace statements have been added to the
     * setters/getters to track activity within object.
     */
    public void testLazyFetch() {
        log.info("testLazyFetch");
        ejava.examples.orm.core.mapped.Umbrella umbrella = new Umbrella(2);
        umbrella.setMake("acme".toCharArray());
        umbrella.setModel("protector");

        //insert a row in the database
        em.persist(umbrella);
        log.info("created umbrella:" + umbrella);
        
        em.flush();
        em.clear();        
        Umbrella umbrella2 = em.find(Umbrella.class, 2L);
        assertNotNull("umbrella not found:" + 2L, umbrella2);
        assertTrue("didn't get a new object", umbrella != umbrella2);
        
        log.info("here's model:" + umbrella2.getModel());
        log.info("here's make:" + new String(umbrella2.getMake()));
        log.info("check setters in log");        
    }
    
}
