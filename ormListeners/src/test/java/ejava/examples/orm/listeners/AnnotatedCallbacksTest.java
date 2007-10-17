package ejava.examples.orm.listeners;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.orm.listeners.annotated.Person;
import ejava.examples.orm.listeners.annotated.Residence;

import junit.framework.TestCase;


public class AnnotatedCallbacksTest extends TestCase {
    private static final Log log = 
        LogFactory.getLog(AnnotatedCallbacksTest.class);
    private EntityManagerFactory emf;
    private EntityManager em;
    
    public void setUp() {
        emf = Persistence.createEntityManagerFactory("ormListeners");
        em = emf.createEntityManager();
    }
    
    public void tearDown() {
        em.close();
        emf.close();        
    }

    /**
     * This test will verify that entity callbacks can be used to propogate
     * a generated primary key value to related classes that are joined by
     * the generated primary key. 
     *
     */
    public void testPersist() {
        log.info("*** testPersist() ***");
        
        em.getTransaction().begin();

        Person person = new Person("george"); 
        
        Residence residence = new Residence();
        residence.setStreet("1600 Penna Ave.");
        residence.setCity("Washington");
        residence.setState("DC");
        residence.setZip("20500");
        person.setResidence(residence);

        assertEquals(0, person.getId());        
        em.persist(person);
        log.debug("persisted person:" + person);
        
        em.flush();
        log.debug("flushed person:" + person);
        
        em.getTransaction().commit();
        assertTrue(person.getId() != 0);
        assertEquals(person.getId(), residence.getId());
        
        em.remove(person);
        em.getTransaction().begin();
        em.getTransaction().commit();
    }
}
