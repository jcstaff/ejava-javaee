package ejava.examples.orm.core.products;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.orm.core.mapped.Drill;
import ejava.examples.orm.core.mapped.EggBeater;
import ejava.examples.orm.core.mapped.Gadget;

import junit.framework.TestCase;

/**
 * This test case provides a demo of using automatically generated primary
 * keys setup using class annotations.
 * 
 * @author jcstaff
 * $Id:$
 */
public class PKGenMappingDemo extends TestCase {
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
     * This test provides a demo of using the AUTO GeneratedType. This value
     * is provided by the Java Persistance provider.
     */
    public void testAUTOGood() {
        log.info("testAUTOGood");
        //note that since PKs are generated, we must pass in an object that
        //has not yet been assigned a PK value.
        ejava.examples.orm.core.mapped.Drill drill = new Drill(0);
        drill.setMake("acme");
        
        //insert a row in the database
        em.persist(drill);
        log.info("created drill (before flush):" + drill);
        em.flush(); 
        log.info("created drill (after flush):" + drill);
        
        assertFalse(drill.getId() == 0L);        
    }
    
    /**
     * This test provides a demo of the error that can occure when passing an
     * object with the PK already assigned when using GeneratedValues.
     */
    public void testAUTOBad() {
        log.info("testAUTOBad");
        //he's not going to like they non-zero PK value here
        ejava.examples.orm.core.mapped.Drill drill = new Drill(25L);
        drill.setMake("BD");
        
        //insert a row in the database
        boolean exceptionThrown = false;
        try { 
            assertFalse(drill.getId() == 0L);        
            log.info("trying to create drill with pre-exist pk:" + drill);
            em.persist(drill);
        }
        catch (PersistenceException ex) {
            log.info("got expected exception: " + ex);
            exceptionThrown = true;
        }        
        assertTrue(exceptionThrown);
    }        

    public void testTABLE() {
        log.info("testTABLE");
        //note that since PKs are generated, we must pass in an object that
        //has not yet been assigned a PK value.
        ejava.examples.orm.core.mapped.EggBeater eggbeater = new EggBeater(0);
        eggbeater.setMake("done right");
        
        //insert a row in the database
        em.persist(eggbeater);
        log.info("created eggbeater (before flush):" + eggbeater);
        em.flush(); 
        log.info("created eggbeater (after flush):" + eggbeater);
        
        assertFalse(eggbeater.getId() == 0L);        
    }

    public void testSEQUENCE() {
        log.info("testSEQUENCE - see PKSequenceGenMappingDemo");
    }

    public void testIDENTITY() {
        log.info("testIDENTITY");
        ejava.examples.orm.core.mapped.Gadget gadget = new Gadget(0);
        gadget.setMake("gizmo 2");
        
        //insert a row in the database
        em.persist(gadget);
        log.info("created gadget (before flush):" + gadget);
        em.flush(); 
        log.info("created gadget (after flush):" + gadget);
        
        assertFalse(gadget.getId() == 0L);                
    }
}
