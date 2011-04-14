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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public abstract class DemoBase {
    protected Log log = LogFactory.getLog(getClass());
    private static final String PERSISTENCE_UNIT = "javaeeEx-test";
    protected PersonDAO personDAO = null;
    protected static EntityManagerFactory emf;
    protected EntityManager em;

    @BeforeClass
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    }
    
    @Before
    public void setUp() throws Exception {
        em = emf.createEntityManager();
        personDAO = new JPAPersonDAO();
        ((JPAPersonDAO)personDAO).setEntityManager(em);
        cleanup();
        em.getTransaction().begin();
    }

    @After
    public void tearDown() throws Exception {
        EntityTransaction tx = em.getTransaction();
        if (tx.isActive()) {
            if (tx.getRollbackOnly() == true) { tx.rollback(); }
            else                              { tx.commit(); }
        }
        em.close();
        emf.close();
    }
    
    @AfterClass
    public static void tearDownClass() {
    	if (emf != null) {
    		emf.close();
    		emf = null;
    	}
    }
    
    protected void cleanup() {
        log.info("cleaning up database");
        em.getTransaction().begin();
        @SuppressWarnings("unchecked")
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
