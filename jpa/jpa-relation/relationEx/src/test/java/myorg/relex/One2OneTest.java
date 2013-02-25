package myorg.relex;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.GregorianCalendar;

import myorg.relex.one2one.Employee;
import myorg.relex.one2one.Person;
import myorg.relex.one2one.Player;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.*;

/**
 * Verifies several one-to-one relationships.
 */
public class One2OneTest extends JPATestBase {
    private static Log log = LogFactory.getLog(One2OneTest.class);
    
    @Test
    public void testOne2OneUniFK() {
        log.info("*** testOne2OneUniFK ***");
        Person person = new Person()
        	.setName("Johnny Unitas");
        Player player = new Player()
        	.setPerson(person)
        	.setPosition(Player.Position.OFFENSE);
        em.persist(person);
        em.persist(player);
        
        //clear the persistence context and get new instances
        em.flush(); em.clear();
        Player player2 = em.find(Player.class, player.getId());
        assertEquals("unexpected position", player.getPosition(), player2.getPosition());
        assertEquals("unexpected name", player.getPerson().getName(), player2.getPerson().getName());
        
        //remove the objects and flush commands to the database
        em.remove(player2);
        em.remove(player2.getPerson());
        em.flush();
        assertNull("person not deleted", em.find(Person.class, player.getPerson().getId()));
        assertNull("player not deleted", em.find(Player.class, player.getId()));
    }

    @Test
    public void testOne2OneUniPKJ() {
        log.info("*** testOne2OneUniPKJ ***");
        Person person = new Person()
        	.setName("Ozzie Newsome");
        Employee employee = new Employee()
        	.setPerson(person)
        	.setHireDate(new GregorianCalendar(1996, Calendar.JANUARY, 1).getTime());
        em.persist(person);
        em.persist(employee);
        
        //clear the persistence context and get new instances
        em.flush(); em.clear();
        Employee employee2 = em.find(Employee.class, employee.getId());
        assertEquals("unexpected name", employee.getPerson().getName(), employee2.getPerson().getName());
        
        //remove the objects and flush commands to the database
        em.remove(employee2);
        em.remove(employee2.getPerson());
        em.flush();
        assertNull("person not deleted", em.find(Employee.class, employee.getPerson().getId()));
        assertNull("player not deleted", em.find(Player.class, employee.getId()));
    }
}
