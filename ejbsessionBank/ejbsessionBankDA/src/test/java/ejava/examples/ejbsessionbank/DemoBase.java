package ejava.examples.ejbsessionbank;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.ejbsessionbank.bo.Account;
import ejava.examples.ejbsessionbank.da.AccountDAO;
import ejava.examples.ejbsessionbank.jpa.JPAAccountDAO;
import ejava.examples.ejbsessionbank.jpa.JPAUtil;

import junit.framework.TestCase;

public abstract class DemoBase extends TestCase {
    protected Log log = LogFactory.getLog(getClass());
    private static final String PERSISTENCE_UNIT = "ejbsessionbank";
    protected AccountDAO acctDAO = null;
    protected EntityManager em;

    protected void setUp() throws Exception {
        em = JPAUtil.getEntityManager(PERSISTENCE_UNIT);
        acctDAO = new JPAAccountDAO();
        cleanup();
        em.getTransaction().begin();
    }

    protected void tearDown() throws Exception {
        EntityTransaction tx = JPAUtil.getEntityManager().getTransaction();
        if (tx.isActive()) {
            if (tx.getRollbackOnly() == true) { tx.rollback(); }
            else                              { tx.commit(); }
        }
        JPAUtil.closeEntityManager();
    }
    
    @SuppressWarnings("unchecked")
    protected void cleanup() {
        log.info("cleaning up database");
        EntityManager em = JPAUtil.getEntityManager();
        em.getTransaction().begin();
        List<Account> accounts = 
            em.createQuery("select a from Account a").getResultList();
        for(Account a: accounts) {
            em.remove(a);
        }
        em.getTransaction().commit();
    }
    
    protected void populate() {
        log.info("populating database");
        //EntityManager em = JPAUtil.getEntityManager();
        //em.getTransaction().begin();
        
        //em.getTransaction().commit();
    }
}
