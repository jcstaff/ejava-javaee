package ejava.examples.orm.listeners;

import javax.persistence.EntityManager;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

import ejava.examples.orm.listeners.annotated.Person;
import ejava.examples.orm.listeners.annotated.Residence;

public class AnnotatedCallbacksTest {
    private static final Log log = 
        LogFactory.getLog(AnnotatedCallbacksTest.class);
    private EntityManagerFactory emf;
    private EntityManager em;
    
    @Before
    public void setUp() {
        emf = Persistence.createEntityManagerFactory("ormListeners");
        em = emf.createEntityManager();
    }
    
    @After
    public void tearDown() {
        em.close();
        emf.close();        
    }

    /**
     * This test will verify that entity callbacks can be used to propogate
     * a generated primary key value to related classes that are joined by
     * the generated primary key. In this case the transaction is not 
     * started until after the persist() is called. 
     *
     */
    //@Ignore 
    @Test
    public void testActiveTransaction() {
        log.info("*** testActiveTransaction() ***");
        

        Person person = new Person("george"); 
        
        Residence residence = new Residence();
        residence.setStreet("1600 Penna Ave.");
        residence.setCity("Washington");
        residence.setState("DC");
        residence.setZip("20500");
        person.setResidence(residence);

        assertEquals(0, person.peekId());        
        assertEquals(0, residence.peekId());        
        log.debug("beginning transaction:" + person + ": " + residence);
        em.getTransaction().begin();
        log.debug("calling persist()");
        em.persist(person);
        log.debug("persisted person:" + person);
                
        log.debug("committing transaction:" + person + ": " + residence);
        em.getTransaction().commit();
        log.debug("committed transaction:" + person + ": " + residence);
        assertTrue(person.peekId() != 0);
        assertEquals(person.peekId(), residence.peekId());
        
        em.remove(person);
        em.getTransaction().begin();
        em.getTransaction().commit();
    }

    /**
     * This test will verify that entity callbacks can be used to propogate
     * a generated primary key value to related classes that are joined by
     * the generated primary key. In this case the transaction is not 
     * started until after the persist() is called. 
     *
     */
    //@Ignore
    @Test
    public void testDelayedTransaction() {
        log.info("*** testDelayedTransaction() ***");
        

        Person person = new Person("george"); 
        
        Residence residence = new Residence();
        residence.setStreet("1600 Penna Ave.");
        residence.setCity("Washington");
        residence.setState("DC");
        residence.setZip("20500");
        person.setResidence(residence);

        assertEquals(0, person.peekId());        
        assertEquals(0, residence.peekId());        
        log.debug("calling persist()");
        em.persist(person);
        log.debug("persisted person:" + person);
        
        log.debug("beginning transaction:" + person + ": " + residence);
        em.getTransaction().begin();
        log.debug("committing transaction:" + person + ": " + residence);
        em.getTransaction().commit();
        log.debug("committed transaction:" + person + ": " + residence);
        assertTrue(person.peekId() != 0);
        assertEquals(person.peekId(), residence.peekId());
    }
}
