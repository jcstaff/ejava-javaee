package myorg.entityex;

import static org.junit.Assert.*;


import java.util.GregorianCalendar;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import myorg.entityex.mapped.Animal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AnimalTest {
    private static Log log = LogFactory.getLog(Auto.class);
    private static final String PERSISTENCE_UNIT = "entityEx-test";
    private static EntityManagerFactory emf;
    private EntityManager em;    

    @BeforeClass
    public static void setUpClass() {
        log.debug("creating entity manager factory");
        emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    }
    
    @Before
    public void setUp() throws Exception {
        log.debug("creating entity manager");
        em = emf.createEntityManager();
        cleanup();
        em.getTransaction().begin();
    }

    @After
    public void tearDown() throws Exception {
        try {
            log.debug("tearDown() started, em=" + em);
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
                em.getTransaction().commit();            
            } else if (!em.getTransaction().getRollbackOnly()) {
                em.getTransaction().commit();                        	
            } else {
            	em.getTransaction().rollback();
            }
            em.close();
            log.debug("tearDown() complete, em=" + em);
        }
        catch (Exception ex) {
            log.fatal("tearDown failed", ex);
            throw ex;
        }
     }
    
    @AfterClass
    public static void tearDownClass() {
        log.debug("closing entity manager factory");
        if (emf!=null) { emf.close(); }
    }
    
    public void cleanup() {
        em.getTransaction().begin();
        //delete what we need to here
        em.getTransaction().commit();
    }

    @Test
    public void testCreateAnimal() {
        log.info("testCreateAnimal");
    	Animal animal = new Animal("bessie", 
    			new GregorianCalendar(1960, 1, 1).getTime(), 1400.2);
        em.persist(animal);        
        
        assertNotNull("animal not found", em.find(Animal.class,animal.getId()));
        
        em.flush(); //make sure all writes were issued to DB
        em.clear(); //purge the local entity manager entity cache to cause new instance
        assertNotNull("animal not found", em.find(Animal.class,animal.getId()));
    }

    @Test
    public void testCreateAnimalAnnotated() {
        log.info("testCreateAnimalAnnotated");
    	myorg.entityex.annotated.Animal2 animal = new myorg.entityex.annotated.Animal2("bessie", 
    			new GregorianCalendar(1960, 1, 1).getTime(), 1400.2);
        em.persist(animal);        
        
        assertNotNull("animal not found", em.find(myorg.entityex.annotated.Animal2.class,animal.getId()));
        
        em.flush(); //make sure all writes were issued to DB
        em.clear(); //purge the local entity manager entity cache to cause new instance
        assertNotNull("animal not found", em.find(myorg.entityex.annotated.Animal2.class,animal.getId()));
    }
}
