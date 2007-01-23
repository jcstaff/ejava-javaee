package myorg.webtier.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

public class PersonTest extends TestCase {
    Log log = LogFactory.getLog(PersonTest.class);
    
    public void testPerson() {
        log.info("*** testPerson ***");
        
        Person person = new Person();
        
        assertEquals("unexpected id:" + person.getId(), 0, person.getId());
    }
}
