package myorg.javaeeex.ejbclient;

import java.util.Collection;

import javax.naming.InitialContext;

import junit.framework.TestCase;

import myorg.javaeeex.bl.TestUtil;
import myorg.javaeeex.ejb.RegistrarRemote;
import myorg.javaeeex.bo.Person;
import myorg.javaeeex.bo.Address;
import myorg.javaeeex.dto.AddressDTO;
import myorg.javaeeex.dto.PersonDTO;
import myorg.javaeeex.ejb.TestUtilRemote;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hibernate.LazyInitializationException;

public class RegistrarTest extends TestCase {
    Log log = LogFactory.getLog(RegistrarTest.class);
    InitialContext jndi;
    String registrarJNDI = System.getProperty("jndi.name.registrar");
    String testUtilJNDI = System.getProperty("jndi.name.testUtil");
    RegistrarRemote registrar;
    TestUtil testUtil;
    
        
    public void setUp() throws Exception {
        assertNotNull("jndi.name.registrar not supplied", registrarJNDI);

        log.debug("getting jndi initial context");
        jndi = new InitialContext();    
        log.debug("jndi=" + jndi.getEnvironment());
        jndi.lookup("/"); //do a quick comms check of JNDI
        
        log.debug("jndi name:" + registrarJNDI);
        registrar = (RegistrarRemote)jndi.lookup(registrarJNDI);

        log.debug("jndi name:" + testUtilJNDI);
        testUtil = (TestUtilRemote)jndi.lookup(testUtilJNDI);
        
        cleanup();
    }

    protected void cleanup() throws Exception {
        log.info("calling testUtil.resetAll()");
        testUtil.resetAll();        
        log.info("testUtil.resetAll() complete");
    }

    public void testPing() {
        log.info("*** testPing ***");
        registrar.ping();
    }
    
    protected Person makePerson() {
        Person person = new Person();
        person.setFirstName("joe");
        person.setLastName("smith");
        person.setSsn("123");
        Address address = new Address(0,"street1","city1", "state1", "zip1");
        person.getAddresses().add(address);
        return person;
    }

    public void testCreatePerson() throws Exception {
        log.info("*** testCreatePerson ***");
        
        Person person = makePerson();
        Person person2 = registrar.createPerson(person);
            //note that our original Person does not have an ID
        assertEquals("unexpected id", 0, person.getId());
            //it is separate from the one returned
        assertFalse("unexpected id:" + person2.getId(), person2.getId()==0);
        
        Person person3 = registrar.getPersonById(person2.getId());
        assertEquals("unexpected name",
            person.getFirstName(), 
            person3.getFirstName());
    }

    /**
     * This test will verify the impacts of receiving a once-managed entity
     * that is now detached. When the object graph gets more than trivial and
     * the server-side just passes back the root of the result tree, the client
     * may end up with a LazyLoadExcption due to the existence of references
     * still not hydrated and the session/DB connection being closed. 
     */
    public void testLazy() throws Exception {
        log.info("*** testLazys ***");

        for(int i=0; i<10; i++) {
            Person person = makePerson();
            person.setLastName("smith" + i);
            registrar.createPerson(person);
        }
        

            //the first time we are going to get people straight from the DAO, 
            //without cleaning the managed object or creating a new DTO.
        Collection<Person> people = registrar.getPeopleByName("joe", "%");
        assertEquals("unexpected number of lazy people",10, people.size());
        try {
            for (Person p: people) {
                p.getAddresses().iterator().next().getZip();
            }
            fail("no lazy instantiation exception thrown");
        }
        catch (LazyInitializationException expected) {
            log.info("got expected lazy instantiation exception:" + expected);
        }
        
            //this time, the EJB will be asked to walk the graph returned
        people = registrar.getPeopleByNameHydrated("joe", "%");
        assertEquals("unexpected number of loaded people",10, people.size());
        for (Person p: people) {
            p.getAddresses().iterator().next().getZip();
        }
    }

    /**
     * This test demonstrates how the client will need the persistence 
     * provider .jar for in its classpath since residue still remains in the
     * once-managed entity that is returned.
     */
    public void testPOJO() throws Exception {
        log.info("*** testPOJO ***");

        for(int i=0; i<10; i++) {
            Person person = makePerson();
            person.setLastName("smith" + i);
            registrar.createPerson(person);
        }
        //the objects returned will be fully loaded, but...
        Collection<Person> people = 
            registrar.getPeopleByNameHydrated("joe", "%");
        assertEquals("unexpected number of managed people",10, people.size());
        

            //the collection class requires hibernate to be in the path
        Class<?> clazz = people.iterator().next().getAddresses().getClass();
        log.debug("collection class=" + clazz);
        assertTrue("unexpected collection class", 
                clazz.getPackage().getName().contains("hibernate"));

            //now get a graph of objects that contain pure POJO classes. The 
            //server will create fresh POJOs for DTOs and pass information from
            //the business object POJO to the data transfer object POJO.
        people = registrar.getPeopleByNameCleaned("joe", "%");
        assertEquals("unexpected number of clean people",10, people.size());
        for (Person p: people) {
            p.getAddresses().iterator().next().getZip();
        }
        
            //the POJOs are cleansed of their hibernate types
        clazz = people.iterator().next().getAddresses().getClass();
        log.debug("collection class=" + clazz);
        assertFalse("unexpected collection class", 
                clazz.getPackage().getName().contains("hibernate"));
    }

    /**
     * This test will demonstrate the usage of a separate class to provide
     * the DTO functionality.
     */
    public void testDTOs() throws Exception {
        log.info("*** testDTOs ***");
        
        for(int i=0; i<10; i++) {
            Person person = makePerson();
            person.setLastName("smith" + i);
            registrar.createPerson(person);
        }

        //now get a graph of objects that contain pure DTOs versus BOs
        Collection<PersonDTO>peopleDTO = 
            registrar.getPeopleByNameDTO("joe", "%");
        assertEquals("unexpected number of DTO people",10, peopleDTO.size());
        for (PersonDTO p: peopleDTO) {
        	Collection<AddressDTO> a = p.getAddresses();
            a.iterator().next().getZip();
        }
        //the DTOs are POJOs that are designed to only contain what the 
        //clients needs to see. It contains no server-side behavior and 
        //could be represented as an XML document. In this example, we
        //have excluded the SSN from the PersonDTO.                
    }
}
