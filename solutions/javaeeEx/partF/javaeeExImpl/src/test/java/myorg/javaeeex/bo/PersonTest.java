package myorg.javaeeex.bo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;


/**
 * This class provides a test case for a very trivial business object.
 * @author jcstaff
 *
 */
public class PersonTest extends TestCase {
    private static Log log = LogFactory.getLog(PersonTest.class);
    
    public void testPerson() {
        log.info("*** testPerson ***");
        String firstName = "joe";
        String lastName = "smith";
        String ssn = "123";                
        
        Person p = new Person();
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setSsn(ssn);
        
        assertEquals("unexpected firstName", firstName, p.getFirstName());
        assertEquals("unexpected lastName", lastName, p.getLastName());
        assertEquals("unexpected ssn", ssn, p.getSsn());
        
        String street = "400 Spruce Street";
        String city = "Arnold";
        String state = "MD";
        String zip = "12345";
        
        Address address = new Address();
        address.setStreet(street);
        address.setCity(city);
        address.setState(state);
        address.setZip(zip);
        
        p.getAddresses().add(address);
        assertEquals("unexepcted number of addresses", 1, p.getAddresses().size());
        Address a2 = p.getAddresses().iterator().next();
        assertEquals("unexpected street", street, a2.getStreet());
        assertEquals("unexpected city", city, a2.getCity());
        assertEquals("unexpected state", state, a2.getState());
        assertEquals("unexpected zip", zip, a2.getZip());
    }
}
