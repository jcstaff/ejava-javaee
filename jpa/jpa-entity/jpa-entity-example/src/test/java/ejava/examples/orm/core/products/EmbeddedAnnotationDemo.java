package ejava.examples.orm.core.products;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.orm.core.annotated.Manufacturer;
import ejava.examples.orm.core.annotated.XRay;

import junit.framework.TestCase;

/**
 * This test case provides a demo of the XRay class, which embeds a 
 * Manufacturer into its table mapping.
 *  
 * @author jcstaff
 * $Id:$
 */
public class EmbeddedAnnotationDemo extends TestCase {
    private static Log log = LogFactory.getLog(EmbeddedAnnotationDemo.class);
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
     */
    public void testEmbedded() {
        log.info("testEmbedded");
        ejava.examples.orm.core.annotated.XRay xray = new XRay(1);
        xray.setModel("look-at-you");
        xray.setMaker(
                new Manufacturer("hi-tech", "low valley", "410-555-1212"));
        
        //if this works, it should store the single object in 3 tables
        em.persist(xray);
        log.info("created xray:" + xray);
        
        em.flush();
        em.clear();
        XRay xray2 = em.find(XRay.class, 1L);
        assertNotNull(xray2);
        log.info("found xray:" + xray2);
        assertEquals(xray.getModel(), xray2.getModel());
        assertEquals(xray.getMaker().getName(), xray2.getMaker().getName());
        assertEquals(xray.getMaker().getAddress(), xray2.getMaker().getAddress());
        assertEquals(xray.getMaker().getPhone(), xray2.getMaker().getPhone());
        
        em.remove(xray2);
        log.info("removed xray:" + xray);
        
        //leave an xray in DB to inspect
        XRay xray3 = new XRay(3);
        xray3.setModel("inside-counts");
        xray3.setMaker(
                new Manufacturer("hi-tech", "low valley", "410-555-1212"));
        em.persist(xray3);
        log.info("created leftover xray:" + xray3);
    }        
}
