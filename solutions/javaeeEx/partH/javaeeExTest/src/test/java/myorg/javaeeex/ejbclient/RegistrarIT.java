package myorg.javaeeex.ejbclient;

import java.util.Collection;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LazyInitializationException;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import myorg.javaeeex.ejb.RegistrarRemote;
import myorg.javaeeex.bo.Person;
import myorg.javaeeex.bo.Address;
import myorg.javaeeex.bl.TestUtil;
import myorg.javaeeex.ejb.TestUtilRemote;
import myorg.javaeeex.dto.AddressDTO;
import myorg.javaeeex.dto.PersonDTO;

public class RegistrarIT {
    private static final Log log = LogFactory.getLog(RegistrarIT.class);

    private static final String registrarJNDI = System.getProperty("jndi.name.registrar",
        "javaeeExEAR/javaeeExEJB/RegistrarEJB!myorg.javaeeex.ejb.RegistrarRemote");
    private RegistrarRemote registrar;

    private static final String testUtilJNDI = System.getProperty("jndi.name.testUtil",
        "javaeeExEAR/javaeeExEJB/TestUtilEJB!myorg.javaeeex.ejb.TestUtilRemote");
    private static final String knownUser = System.getProperty("known.user", "known");
    private static final String knownPassword = System.getProperty("known.password", "password");
    private static final String userUser = System.getProperty("user.user","user1");
    private static final String userPassword = System.getProperty("user.password", "password");
    private static final String adminUser = System.getProperty("admin.user", "admin1");
    private static final String adminPassword = System.getProperty("admin.password", "password");
    private Context jndi;

    @Before
    public void setUp() throws Exception {
        assertNotNull("jndi.name.registrar not supplied", registrarJNDI);

        log.debug("getting jndi initial context");
        jndi = runAs();
        log.debug("jndi=" + jndi.getEnvironment());
        jndi.lookup("/"); //do a quick comms check of JNDI

        log.debug("jndi name:" + registrarJNDI);
        registrar = (RegistrarRemote)jndi.lookup(registrarJNDI);
        log.debug("registrar=" + registrar);

        log.debug("jndi name:" + testUtilJNDI);
        TestUtilRemote testUtil = (TestUtilRemote)jndi.lookup(testUtilJNDI);
        log.debug("testUtil=" + testUtil);

        log.debug(String.format("admin= %s/%s", adminUser, adminPassword));
        
        cleanup();
        //run the tests as user
        Context context=runAs(userUser, userPassword);
        registrar=(RegistrarRemote)context.lookup(registrarJNDI);
    }
    
    /**
     * It is important to close the JNDI context in between tests
     * @throws NamingException
     */
    @After
    public void tearDown() throws NamingException {
    	if (jndi != null) {
    		jndi.close();
    		jndi=null;
    	}
    }

    protected void cleanup() throws Exception {
        log.info("calling testUtil.resetAll()");
        ((TestUtilRemote)runAs(adminUser, adminPassword).lookup(testUtilJNDI)).resetAll();
        log.info("testUtil.resetAll() complete");
    }

    private Context runAs(String username, String password) throws NamingException {
        if (jndi!=null) {
        	jndi.close();
        }
        Properties env = new Properties();
        if (username != null) {
            env.put(Context.SECURITY_PRINCIPAL, username);
            env.put(Context.SECURITY_CREDENTIALS, password);
        }
        log.debug(String.format("%s env=%s", username==null?"anonymous":username, env));
        jndi=new InitialContext(env);
        return jndi;
    }

    private Context runAs() throws NamingException {
    	return runAs(knownUser, knownPassword);
    }

    @Test
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

    @Test
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

    @Test
    public void testLazy() throws Exception {
        log.info("*** testLazy ***");

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

    @Test
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

    @Test
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

    @Test
    public void testWebUseCase() throws Exception {
        log.info("*** testWebUserCase ***");
        final int PAGE_SIZE=24;
        final int PAGES=4;
        final int TOTAL = PAGES*PAGE_SIZE;
        for(int i=0; i<TOTAL; i++) {
            Person person = makePerson();
            person.setFirstName("first" + i);
            person.setLastName("last" + i);
            registrar.createPerson(person);
        }
        
        int index = 0;
        int count = 0;
        int pages = 0;
        int loops = 0;
        Collection<Person> people = null;
        do {
            //people = registrar.getAllPeople(index, PAGE_SIZE);
            people = registrar.getAllPeopleHydrated(index, PAGE_SIZE);
            pages = (!people.isEmpty()) ? pages + 1 : pages;
            count += people.size();
            index += people.size();
            loops += 1;
            for (Person person : people) {
                log.debug(String.format("person: (%d) %s %s", 
                    person.getId(), person.getFirstName(), person.getLastName()));
            }
            log.debug("people=" + people);
        } while (!people.isEmpty() && loops <= PAGES + 1);
        
        assertEquals("unexpected # of pages", PAGES, pages);
        assertEquals("unexpected number of people", TOTAL, count);

        //view a specific user
        long id = registrar.getAllPeople(0, PAGE_SIZE).iterator().next().getId();
        //Person person = registrar.getPersonById(id);
        Person person = registrar.getPersonByIdHydrated(id);
        log.debug(String.format("person: (%d) %s %s", 
            person.getId(), person.getFirstName(), person.getLastName()));
        Address address = person.getAddresses().iterator().next();
        log.debug(String.format("address: (%d) %s %s, %s %s", 
            address.getId(), 
            address.getStreet(),
            address.getCity(), address.getState(), address.getZip()));

        //update the address of a specific user
        Address address2 = new Address();
        address2.setStreet(address.getStreet() + 2);
        address2.setCity(address.getCity() + 2);
        address2.setState(address.getState() + 2);
        address2.setZip(address.getZip() + 2);
        Person p3 = registrar.changeAddress(person, address2);
        Address a3 = p3.getAddresses().iterator().next();
        assertEquals("unexpected street" , address2.getStreet(), a3.getStreet());
        assertEquals("unexpected city" , address2.getCity(), a3.getCity());
        assertEquals("unexpected state" , address2.getState(), a3.getState());
        assertEquals("unexpected zip" , address2.getZip(), a3.getZip());

        //view user again
        person = registrar.getPersonByIdHydrated(id);
        log.debug(String.format("person: (%d) %s %s", 
            person.getId(), person.getFirstName(), person.getLastName()));
        address = person.getAddresses().iterator().next();
        log.debug(String.format("address: (%d) %s %s, %s %s", 
            address.getId(), 
            address.getStreet(),
            address.getCity(), address.getState(), address.getZip()));
    }
}
