package ejava.examples.orm.core.products;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.orm.core.mapped.Tank;

import junit.framework.TestCase;

/**
 * This test case provides a demo of using transient properties through
 * annotations.
 * 
 * @author jcstaff
 * $Id:$
 */
public class TransientMappingDemo extends TestCase {
    private static Log log = LogFactory.getLog(BasicAnnotationDemo.class);
    private static final String PERSISTENCE_UNIT = "ormCore";
    private EntityManager em = null;

    protected void setUp() throws Exception {        
        EntityManagerFactory emf = 
            JPAUtil.getEntityManagerFactory(PERSISTENCE_UNIT);   
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
     * This test provides a demo of persisting a class that has mapped
     * a getMakeModel() getter as @Transient so that it can be ignored
     * when peristing to the database.
     */
    public void testTransient() {
        log.info("testTransient");
        ejava.examples.orm.core.mapped.Tank tank = new Tank(2);
        tank.setMake("acme");
        tank.setModel("great guns");

        //insert a row in the database
        em.persist(tank);
        log.info("created tank:" + tank);        
    }
    
}
