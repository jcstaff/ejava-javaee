package myorg.javaeeex.jpa;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import myorg.javaeeex.bo.Address;
import myorg.javaeeex.bo.Person;
import myorg.javaeeex.dao.PersonDAO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;


public class JPAPersonDAOTest extends DemoBase {
    Log log = LogFactory.getLog(JPAPersonDAOTest.class);
    
    protected Person makePerson() {
        Person person = new Person();
        person.setFirstName("joe");
        person.setLastName("smith");
        person.setSsn("123");
        person.getAddresses().add(
                new Address(0,"city1", "street1", "state1", "zip1"));
        person.getAddresses().add(
                new Address(0,"city2", "street2", "state2", "zip2"));
        return person;
    }
    
    @Test
    public void testCreatePerson() {
        log.info("*** testCreatePerson ***");
        Person person = makePerson();
        
        //test create Person method
        personDAO.createPerson(person);
        assertTrue("person not managed", em.contains(person));
        
        assertTrue("unexpected id:" + person.getId(), person.getId() != 0);        
    }    
    
    @Test
    public void testRemovePerson() {
        log.info("*** testRemovePerson ***");

        int startCount = personDAO.findPeople(
                PersonDAO.GET_ALL_PEOPLE_QUERY, null, 0, 100).size();        

        Person person = personDAO.createPerson(makePerson());
        
        int createCount = personDAO.findPeople(
                PersonDAO.GET_ALL_PEOPLE_QUERY, null, 0, 100).size();
        assertEquals("unexpected number of people",startCount+1, createCount);
        
        int addr1Count = 
            em.createQuery("select a from Address a").getResultList().size();
        Address address = person.getAddresses().iterator().next();
        person.getAddresses().remove(address);
        personDAO.removeAddress(address);
        int addr2Count = 
            em.createQuery("select a from Address a").getResultList().size();
        assertEquals("unexpected number of addresses", 
                addr1Count - 1, addr2Count);
        
        personDAO.removePerson(person);
        int removeCount = personDAO.findPeople(
                PersonDAO.GET_ALL_PEOPLE_QUERY, null, 0, 100).size();
        assertEquals("unexpected number of people",startCount, removeCount);        
    }
    
    @Test
    public void testGetPerson() {
        log.info("*** testGetPerson ***");
        
        Person person = makePerson();
        personDAO.createPerson(person);
        
        //write to DB and clear cache
        em.getTransaction().commit();
        em.clear();
        assertFalse("person still managed", em.contains(person));
        em.getTransaction().begin();
        
        //test getPerson method
        Person person2 = personDAO.getPerson(person.getId());
        assertNotNull("person2 was null", person2);
        assertEquals("unexpected number of addresses", 
                person.getAddresses().size(), 
                person2.getAddresses().size());
    }
    
    @Test
    public void testFindPeopleLikeName() {
        log.info("*** testFindPeopleLikeName ***");
        
        Person people[] = new Person[] {
                makePerson(),
                makePerson(),
                makePerson()
        };
        people[0].setFirstName("joe123");
        people[1].setFirstName("joe456");
        people[2].setFirstName("mary123");
        for (Person p : people) {
            personDAO.createPerson(p);
        }
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("firstName", "joe%");
        params.put("lastName", people[0].getLastName());
        Collection<Person> matches = 
            personDAO.findPeople(PersonDAO.GET_PEOPLE_LIKE_NAME_QUERY, 
                    params, 0, 99);
        assertEquals("unexpected number of people", 2, matches.size());
        log.debug("found people with first name like joe123%:" + matches);
    }

    @Test
    public void testFindAllPeople() {
        log.info("*** testFindAllPeople ***");

        Person person = makePerson();
        personDAO.createPerson(person);

        person = new Person();
        person.setFirstName("mary");
        person.setLastName("jones");
        person.setSsn("456");
        personDAO.createPerson(person);

        //write to DB and clear cache
        em.getTransaction().commit();
        em.clear();
        assertFalse("person still managed", em.contains(person));
        em.getTransaction().begin();

        //test findPeople method
        List<Person> people = personDAO.findPeople(
                PersonDAO.GET_ALL_PEOPLE_QUERY, null, 0, 100);        
        assertEquals("unexpected number of people", 
                people.size(), 2, people.size());        
    }    
}
