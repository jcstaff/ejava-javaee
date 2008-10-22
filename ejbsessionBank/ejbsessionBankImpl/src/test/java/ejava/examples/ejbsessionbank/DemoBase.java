package ejava.examples.ejbsessionbank;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.ejbsessionbank.bo.Account;
import ejava.examples.ejbsessionbank.bo.Owner;
import ejava.examples.ejbsessionbank.dao.AccountDAO;
import ejava.examples.ejbsessionbank.jpa.JPAAccountDAO;

import junit.framework.TestCase;

public abstract class DemoBase extends TestCase {
    protected Log log = LogFactory.getLog(getClass());
    private static final String PERSISTENCE_UNIT = "ejbsessionbank";
    protected AccountDAO acctDAO = null;
    protected static EntityManagerFactory emf =
        Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    protected EntityManager em;

    protected void setUp() throws Exception {
        em = emf.createEntityManager();
        acctDAO = new JPAAccountDAO();
        cleanup();
        em.getTransaction().begin();
    }

    protected void tearDown() throws Exception {
        EntityTransaction tx = em.getTransaction();
        if (tx.isActive()) {
            if (tx.getRollbackOnly() == true) { tx.rollback(); }
            else                              { tx.commit(); }
        }
        em.close();
        emf.close();
    }
    
    @SuppressWarnings("unchecked")
    protected void cleanup() {
        log.info("cleaning up database");
        em.getTransaction().begin();
        
        List<Owner> owners = 
            em.createQuery("select o from Owner o").getResultList();
        log.debug("found " + owners.size() + " owners");
        for (Owner o : owners) {
            em.remove(o);
            em.flush();
        }
        
        //delete orphan accounts
        List<Account> accounts = 
            em.createQuery("select a from Account a " +
            		"where NOT EXISTS " +
            		"(select o from Owner o " +
            		"where a MEMBER OF o.accounts)").getResultList();
        log.debug("found " + accounts.size() + " accounts");
        for(Account a: accounts) {
            em.remove(a);
        }
        em.getTransaction().commit();
    }
    
    protected void populate() {
        log.info("populating database");
        //em.getTransaction().begin();
        
        //em.getTransaction().commit();
    }
}
