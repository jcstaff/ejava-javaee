package ejava.projects.eleague.bo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.projects.eleague.bo.Address;

import junit.framework.TestCase;

/**
 * This test case provides an example framework for how business objects 
 * could be tested. The functionality of the business objects shoudl be
 * tested at this level prior to adding more complex scenarios, like 
 * persistence and server-side logic.
 * 
 * @author jcstaff
 *
 */
public class ELeagueBOTest extends TestCase {
	Log log = LogFactory.getLog(ELeagueBOTest.class);
	
    public void testVenuet() {
    	log.info("*** testVenue ***");
    	
    	String name = "myVenue";
    	String city = "Laurel";
    	
    	Venue venue = new Venue();
    	venue.setName(name);
    	
    	venue.setAddress(new Address(0, city));
    	
    	log.debug("created Venue:" + venue);
    	
    	assertEquals("unexpected name", name, venue.getName());
    	assertEquals("unexpected city", city, venue.getAddress().getCity());    	
    }
}
