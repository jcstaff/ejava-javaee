package ejava.examples.orm.rel;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

public abstract class DemoBase extends TestCase {
    protected Log log = LogFactory.getLog(getClass());
    protected EntityManager em = null;

    protected void setUp() throws Exception {        
        EntityManagerFactory emf = AllRelationsTest.emf;   
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
}