package ejava.examples.orm.core.products;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.orm.core.mapped.Fan;

import junit.framework.TestCase;

/**
 * This test case provides a demo of using automatically generated primary
 * keys setup using descriptors and DB sequences. It is separate from
 * the PKGen tests since not all databases suppose sequences.
 * 
 * @author jcstaff
 * $Id:$
 */
public class PKSequenceGenMappingDemo extends TestCase {
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
    
    public void testSEQUENCE() {
        log.info("testSEQUENCE");
        //note that since PKs are generated, we must pass in an object that
        //has not yet been assigned a PK value.
        ejava.examples.orm.core.mapped.Fan fan = new Fan(0);
        fan.setMake("cool runner 2");
        
        //insert a row in the database
        em.persist(fan);
        log.info("created fan (before flush):" + fan);
        em.flush(); 
        log.info("created fan (after flush):" + fan);
        
        assertFalse(fan.getId() == 0L);                
    }
}
