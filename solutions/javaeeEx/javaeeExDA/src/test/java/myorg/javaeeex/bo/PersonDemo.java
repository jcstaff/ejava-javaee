package myorg.javaeeex.bo;

import java.util.List;

import myorg.javaeeex.da.PersonDAO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class PersonDemo extends DemoBase {
    Log log = LogFactory.getLog(PersonDemo.class);
    
    public void testCreatePerson() {
        log.info("*** testCreatePerson ***");
        
        Person person = new Person();
        person.setFirstName("joe");
        person.setLastName("smith");
        
        em.persist(person);
        assertTrue("person not managed", em.contains(person));
        assertTrue("unexpected id:" + person.getId(), person.getId() != 0);        
    }    

    public void testFindAllPeople() {
        log.info("*** testFindAllPeople ***");

        List<Person> people = personDAO.findPeople(
                PersonDAO.GET_ALL_PEOPLE_QUERY, null, 0, 100);
        
        assertEquals("unexpected number of people", 
                people.size(), 0, people.size());
    }    
}
