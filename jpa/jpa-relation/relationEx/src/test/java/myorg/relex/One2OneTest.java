package myorg.relex;

import static org.junit.Assert.*;

import java.util.Arrays;
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
    
    /**
     * This test provides a demonstration of a simple one-to-one
     * FK-join. The ID of the parent and child are independent of one
     * another and the child maintains a separate FK to reference the
     * parent.
     */
    @Test
    public void testOne2OneUniFK() {
        log.info("*** testOne2OneUniFK ***");
        Person person = new Person();
        person.setName("Johnny Unitas");
        Player player = new Player();
        player.setPerson(person);
        player.setPosition(Player.Position.OFFENSE);
        em.persist(person);
        em.persist(player); //provider will propagate person.id to player.FK
        
        //clear the persistence context and get new instances
        em.flush(); em.clear();
        Player player2 = em.find(Player.class, player.getId());
        assertEquals("unexpected position", player.getPosition(), player2.getPosition());
        assertEquals("unexpected name", player.getPerson().getName(), player2.getPerson().getName());
        
        //verify the contents of the database tables, columns, and relationships
        Object[] cols = (Object[]) em.createNativeQuery(
        		"select person.id person_id, person.name, player.id player_id, person.id player_person_id " +
        		"from RELATIONEX_PLAYER player " +
                "join RELATIONEX_PERSON person on person.id = player.person_id " +
                "where player.id = ?1")
                .setParameter(1, player.getId())
                .getSingleResult();
        log.info("row=" + Arrays.toString(cols));
        assertEquals("unexpected person_id", person.getId(), ((Number)cols[0]).intValue());
        assertEquals("unexpected person_name", person.getName(), (String)cols[1]);
        assertEquals("unexpected player_id", player.getId(), ((Number)cols[2]).intValue());
        assertEquals("unexpected player_person_id", person.getId(), ((Number)cols[3]).intValue());
        
        //remove the objects and flush commands to the database
        em.remove(player2);
        em.remove(player2.getPerson());
        em.flush();
        assertNull("person not deleted", em.find(Person.class, player.getPerson().getId()));
        assertNull("player not deleted", em.find(Player.class, player.getId()));
    }

    /**
     * This test provides a demonstration of a one-to-one primary key join.
     * In this case, the child and parent tables using the same primary key value
     * and the child re-uses its PK-column as the FK-column. No separate FK-column
     * is created in the child -- unlike the one-to-one FK-join case above.  
     */
    @Test
    public void testOne2OneUniPKJ() {
        log.info("*** testOne2OneUniPKJ ***");
        Person person = new Person();
        person.setName("Ozzie Newsome");
        em.persist(person);
        em.flush(); //generate the PK for the person
        
        Employee employee = new Employee();
    	employee.setPerson(person); //set PK/FK -- provider will not auto propagate
    	employee.setHireDate(new GregorianCalendar(1996, Calendar.JANUARY, 1).getTime());
        em.persist(employee);
        
        //clear the persistence context and get new instances
        em.flush(); em.clear();
        Employee employee2 = em.find(Employee.class, employee.getPerson().getId());
        assertEquals("unexpected name", employee.getPerson().getName(), employee2.getPerson().getName());
        
        //verify the contents of the database tables, columns, and relationships
        Object[] cols = (Object[]) em.createNativeQuery(
        		"select person.id person_id, person.name, employee.id employee_id " +
        		"from RELATIONEX_EMPLOYEE employee " +
                "join RELATIONEX_PERSON person on person.id = employee.id " +
                "where employee.id = ?1")
                .setParameter(1, employee.getId())
                .getSingleResult();
        log.info("row=" + Arrays.toString(cols));
        assertEquals("unexpected person_id", person.getId(), ((Number)cols[0]).intValue());
        assertEquals("unexpected person_name", person.getName(), (String)cols[1]);
        assertEquals("unexpected employee_id", employee.getId(), ((Number)cols[2]).intValue());
        
        //remove the objects and flush commands to the database
        em.remove(employee2);
        em.remove(employee2.getPerson());
        em.flush();
        assertNull("person not deleted", em.find(Employee.class, employee.getPerson().getId()));
        assertNull("player not deleted", em.find(Player.class, employee.getPerson().getId()));
    }
    
    @Test
    public void testOne2OneUniMapsId() {
        log.info("*** testOne2OneUniMapsId ***");
        Person person = new Person();
        person.setName("John Harbaugh");
        Coach coach = new Coach();
        coach.setType(Coach.Type.HEAD);
        coach.setPerson(person);
        em.persist(person);
        em.persist(coach); //provider auto propagates person.id to coach.FK mapped to coach.PK 
        
        //flush commands to database, clear cache, and pull back new instance
        em.flush(); em.clear();
        Coach coach2 = em.find(Coach.class, coach.getId());
        assertEquals("unexpected name", coach.getPerson().getName(), coach2.getPerson().getName());

        //verify the contents of the database tables, columns, and relationships
        Object[] cols = (Object[]) em.createNativeQuery(
        		"select person.id person_id, person.name, coach.person_id coach_id " +
        		"from RELATIONEX_COACH coach " +
                "join RELATIONEX_PERSON person on person.id = coach.person_id " +
                "where coach.person_id = ?1")
                .setParameter(1, coach.getId())
                .getSingleResult();
        log.info("row=" + Arrays.toString(cols));
        assertEquals("unexpected person_id", person.getId(), ((Number)cols[0]).intValue());
        assertEquals("unexpected person_name", person.getName(), (String)cols[1]);
        assertEquals("unexpected coach_id", coach.getId(), ((Number)cols[2]).intValue());
        
        
        //remove the objects and flush commands to the database
        em.remove(coach2);
        em.remove(coach2.getPerson());
        em.flush();
        assertNull("person not deleted", em.find(Employee.class, coach.getPerson().getId()));
        assertNull("player not deleted", em.find(Player.class, coach.getPerson().getId()));
    }
}
