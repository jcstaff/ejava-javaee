package ejava.examples.orm.core.products;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.orm.core.annotated.MakeModelPK;
import ejava.examples.orm.core.annotated.Mower;
import ejava.examples.orm.core.annotated.Napsack;
import ejava.examples.orm.core.annotated.NapsackPK;
import ejava.examples.orm.core.annotated.Pen;
import ejava.examples.orm.core.MowerPK;

import junit.framework.TestCase;

/**
 * This test case provides a demo of using customer primary key classes
 * specified by annotations.
 * 
 * @author jcstaff
 * $Id:$
 */
public class PKClassAnnotationDemo extends TestCase {
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
    public void testIdClass() {
        log.info("testIdClass");
        ejava.examples.orm.core.annotated.Mower mower = 
            new Mower("acme", "power devil2");
        mower.setSize(21);
        
        //insert a row in the database
        em.persist(mower);
        log.info("created mower:" + mower);
        
        Mower mower2 = 
            em.find(Mower.class, new MowerPK("acme", "power devil2"));
        assertNotNull(mower2);
        log.info("found mower:" + mower2);
        assertEquals(mower.getSize(), mower2.getSize());
        
        mower.setSize(30);
        assertEquals(mower.getSize(), mower2.getSize());
        log.info("updated mower:" + mower2);        
        
        em.remove(mower);
        log.info("removed mower:" + mower2);

        //leave a mower around for inspection
        em.flush();
        Mower mower3 = new Mower("wack attack","bladerunner2"); 
        mower3.setSize(19);
        em.persist(mower3);
        log.info("created leftover mower:" + mower3);
    }
    
    /**
     * The Napsack class hosts an embedded primary key class, called
     * NapsackPK that is specific to the Napsack. All database mappings
     * are provided within the NapsackPK class.
     *
     */
    public void testEmbeddedId() {
        log.info("testEmbedded");
        ejava.examples.orm.core.annotated.Napsack napsack = 
            new Napsack("acme", "hold all2");
        napsack.setSize(3);
        
        //insert a row in the database
        em.persist(napsack);
        log.info("created napsack:" + napsack);
        
        Napsack napsack2 = 
            em.find(Napsack.class, new NapsackPK("acme", "hold all2"));
        assertNotNull(napsack2);
        log.info("found napsack:" + napsack2);
        assertEquals(napsack.getSize(), napsack2.getSize());
        
        napsack.setSize(30);
        assertEquals(napsack.getSize(), napsack2.getSize());
        log.info("updated napsack:" + napsack2);        
        
        em.remove(napsack);
        log.info("removed napsack:" + napsack2);

        //leave a object around for inspection
        em.flush();
        Napsack napsack3 = new Napsack("pack attack","getta round2"); 
        napsack3.setSize(19);
        em.persist(napsack3);
        log.info("created leftover napsack:" + napsack3);
    }        

    /**
     * The Pen class use a generic MakeModelPK class. All database mappings
     * are made within the Pen class and nothing is directly associated
     * with the generic PK class.
     *
     */
    public void testEmbeddedIdOverrides() {
        log.info("testEmbeddedOverrides");
        ejava.examples.orm.core.annotated.Pen pen = 
            new Pen("acme", "quick write2");
        pen.setSize(3);
        
        //insert a row in the database
        em.persist(pen);
        log.info("created pen:" + pen);
        
        Pen pen2 = 
            em.find(Pen.class, new MakeModelPK("acme", "quick write2"));
        assertNotNull(pen2);
        log.info("found pen:" + pen2);
        assertEquals(pen.getSize(), pen2.getSize());
        
        pen.setSize(30);
        assertEquals(pen.getSize(), pen2.getSize());
        log.info("updated pen:" + pen2);        
        
        em.remove(pen);
        log.info("removed pen:" + pen2);

        //leave a object around for inspection
        em.flush();
        Pen pen3 = new Pen("write attack","jotter2"); 
        pen3.setSize(19);
        em.persist(pen3);
        log.info("created leftover pen:" + pen3);
    }        
}
