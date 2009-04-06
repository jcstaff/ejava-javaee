package myorg.javaeeex.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import myorg.javaeeex.bo.Person;
import myorg.javaeeex.dao.PersonDAO;
import myorg.javaeeex.jpa.JPAPersonDAO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import junit.framework.TestCase;

public abstract class DemoBase extends TestCase {
    protected Log log = LogFactory.getLog(getClass());
    private static final String PERSISTENCE_UNIT = "javaeeEx";
    protected PersonDAO personDAO = null;
    protected EntityManagerFactory emf;
    protected EntityManager em;

    protected void setUp() throws Exception {
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        em = emf.createEntityManager();
        personDAO = new JPAPersonDAO();
        ((JPAPersonDAO)personDAO).setEntityManager(em);
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
        List<Person> people = 
            em.createQuery("select p from Person p").getResultList();
        for(Person p: people) {
            em.remove(p);
        }
        em.getTransaction().commit();
    }
    
    protected void populate() {
        log.info("populating database");
        //em.getTransaction().begin();
            //...
        //em.getTransaction().commit();
    }
}
