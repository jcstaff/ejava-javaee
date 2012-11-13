package myorg.javaeeex.blimpl;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import myorg.javaeeex.bl.Registrar;
import myorg.javaeeex.bl.RegistrarException;
import myorg.javaeeex.bo.Address;
import myorg.javaeeex.bo.Person;
import myorg.javaeeex.jpa.DemoBase;


public class RegistrarImplTest extends DemoBase {
    private static final Log log = LogFactory.getLog(RegistrarImplTest.class);
    private Registrar registrar;
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        registrar = new RegistrarImpl();
        ((RegistrarImpl)registrar).setDAO(personDAO);
    }
    
    protected Person makePerson() {
        Person person = new Person();
        person.setFirstName("joe");
        person.setLastName("smith");
        person.setSsn("123");
        person.getAddresses().add(
                new Address(0,"city1", "street1", "state1", "zip1"));
        return person;
    }

    @Test
    public void testCreatePerson() throws Exception {
        log.info("*** testCreatePerson() ***");
        
        String firstName = "joe";
        String lastName = "smith";
        String street = "1 main street";
        String city = "hometown";
        String state = "MD";
        String zip = "10101";
        
        Person person = new Person();
        person.setFirstName(firstName);
        person.setLastName(lastName);
        
        try {
            registrar.createPerson(person);
            fail("address constraint not enforced");
        }
        catch (RegistrarException expected) {}
        
        Address address = new Address();
        address.setStreet(street);
        address.setCity(city);
        address.setState(state);
        address.setZip(zip);
        person.getAddresses().add(address);
        
        registrar.createPerson(person);
        em.getTransaction().commit();
        log.info("registrar successfully created person:" + person);
        em.clear();

        Person person2 = registrar.getPersonById(person.getId());
        assertEquals("unexpected ID", person.getId(), person2.getId());
        assertEquals("unexpected first name", 
                person.getFirstName(), person2.getFirstName());
        assertEquals("unexpected last name", 
                person.getLastName(), person2.getLastName());
        assertEquals("unexpected address count", 
                1, person2.getAddresses().size());
        Address address2 = person.getAddresses().iterator().next();
        assertEquals("unexpected street", 
                address.getStreet(), address2.getStreet()); 
        assertEquals("unexpected city", 
                address.getCity(), address2.getCity()); 
        assertEquals("unexpected state", 
                address.getState(), address2.getState());
        assertEquals("unexpected zip", 
                address.getZip(), address2.getZip()); 
        log.info("registrar returned expected person:" + person);
    }
    
    @Test
    public void testChangeAddress() throws Exception {
        log.info("*** testChangeAddress() ***");
        Person person = makePerson();
        registrar.createPerson(person);
        
        //simulate a new transaction in between create and update
        em.getTransaction().commit();
        em.clear();
        em.getTransaction().begin();

        //actually change address
        person = personDAO.getPerson(person.getId());
        Address address2 = 
            new Address(0,"city2", "street2", "state2", "zip2");
        registrar.changeAddress(person, address2);
        em.getTransaction().commit();
        em.clear();

        //verify what was written to DB
        Person person2 = registrar.getPersonById(person.getId());
        assertEquals("unexpected address count", 
                1, person2.getAddresses().size());
        Address address2a = person2.getAddresses().iterator().next();
        assertEquals("unexpected street", 
                address2.getStreet(), address2a.getStreet()); 
        assertEquals("unexpected city", 
                address2.getCity(), address2a.getCity()); 
        assertEquals("unexpected state", 
                address2.getState(), address2a.getState());
        assertEquals("unexpected zip", 
                address2.getZip(), address2a.getZip()); 
        log.info("registrar returned expected person after address change:" + 
                person);
    }
}
