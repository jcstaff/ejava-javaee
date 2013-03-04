package myorg.relex;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import javax.persistence.TemporalType;

import myorg.relex.one2one.Applicant;
import myorg.relex.one2one.Application;
import myorg.relex.one2one.Auto;
import myorg.relex.one2one.Coach;
import myorg.relex.one2one.Driver;
import myorg.relex.one2one.Employee;
import myorg.relex.one2one.Member;
import myorg.relex.one2one.Person;
import myorg.relex.one2one.Player;
import myorg.relex.one2one.ShowEvent;
import myorg.relex.one2one.ShowEventPK;
import myorg.relex.one2one.ShowTickets;
import myorg.relex.one2one.BoxOffice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.*;

/**
 * Verifies several one-to-one relationships.
 */
public class One2OneTest extends JPATestBase {
    private static Log log = LogFactory.getLog(One2OneTest.class);
    
    /**
     * This test provides a demonstration of a simple one-to-one, uni-directional
     * relationship realized through a foreign key join (FK-join).
     * The ID of the parent and dependent classes are independent of one
     * another and the dependent maintains a separate FK to reference the
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
        		"select person.id person_id, person.name, " +
        		       "player.id player_id, player.person_id player_person_id " +
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
        assertNull("person not deleted", em.find(Person.class, person.getId()));
        assertNull("player not deleted", em.find(Player.class, player.getId()));
    }

    /**
     * This test provides a demonstration of a simple one-to-one, uni-directional
     * relationship realized through a separate join table. The ID of the parent and 
     * dependent are independent of one another and these values are kept in a separate
     * table to realize the relationship.
     */
    @Test
    public void testOne2OneUniJoinTable() {
        log.info("*** testOne2OneUniJoinTable ***");
        Person person = new Person();
        person.setName("Joe Smith");
        Member member = new Member(person);
        member.setRole(Member.Role.SECONDARY);
        em.persist(person);
        em.persist(member); //provider will propagate person.id to player.FK
        
        //clear the persistence context and get new instances
        em.flush(); em.clear();
        Member member2 = em.find(Member.class, member.getId());
        assertEquals("unexpected role", member.getRole(), member2.getRole());
        assertEquals("unexpected name", member.getPerson().getName(), member2.getPerson().getName());
        
        //verify the contents of the database tables, columns, and relationships
        Object[] cols = (Object[]) em.createNativeQuery(
        		"select person.id person_id, person.name, " +
        		       "member.id member_id, member.role member_role, " +
        		       "link.member_id link_member, link.person_id link_person " +
        		"from RELATIONEX_MEMBER member " +
        		"join RELATIONEX_MEMBER_PERSON link on link.member_id = member.id " +
                "join RELATIONEX_PERSON person      on link.person_id = person.id " +
                "where member.id = ?1")
                .setParameter(1, member.getId())
                .getSingleResult();
        log.info("row=" + Arrays.toString(cols));
        assertEquals("unexpected person_id", person.getId(), ((Number)cols[0]).intValue());
        assertEquals("unexpected person_name", person.getName(), (String)cols[1]);
        assertEquals("unexpected member_id", member.getId(), ((Number)cols[2]).intValue());
        assertEquals("unexpected member_role", member.getRole().name(), (String)cols[3]);
        assertEquals("unexpected link_member_id", member.getId(), ((Number)cols[4]).intValue());
        assertEquals("unexpected link_person_id", person.getId(), ((Number)cols[5]).intValue());
        
        //remove the objects and flush commands to the database
        em.remove(member2);
        em.remove(member2.getPerson());
        em.flush();
        assertNull("person not deleted", em.find(Person.class, person.getId()));
        assertNull("member not deleted", em.find(Member.class, member.getId()));
    }
    
    
    
    /**
     * This test provides a demonstration of a one-to-one primary key join.
     * In this case, the child and parent tables using the same primary key value
     * and the child re-uses its PK-column as the FK-column. No separate FK-column
     * is created in the child -- unlike the one-to-one FK-join case above. However,
     * the provider does not automatically propagate the parent.PK to the dependent.PK
     * during the persist().  
     */
    @Test
    public void testOne2OneUniPKJ() {
        log.info("*** testOne2OneUniPKJ ***");
        Person person = new Person();
        person.setName("Ozzie Newsome");
        em.persist(person);
        em.flush(); //generate the PK for the person
        
        Employee employee = new Employee(person);//set PK/FK -- provider will not auto propagate
    	employee.setHireDate(new GregorianCalendar(1996, Calendar.JANUARY, 1).getTime());
        //em.persist(person);
        em.persist(employee);
        
        //clear the persistence context and get new instances
        em.flush(); em.clear();
        Employee employee2 = em.find(Employee.class, employee.getPerson().getId());
        log.info("calling person...");
        assertEquals("unexpected name", employee.getPerson().getName(), employee2.getPerson().getName());
        
        //verify the contents of the database tables, columns, and relationships
        Object[] cols = (Object[]) em.createNativeQuery(
        		"select person.id person_id, person.name, " +
        		       "employee.id employee_id " +
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
        assertNull("person not deleted", em.find(Person.class, person.getId()));
        assertNull("employee not deleted", em.find(Employee.class, employee.getId()));
    }
    
    /**
     * This test provides a demonstration of a one-to-on primary key join. 
     * In this case, the entity is configured to use the foreign key mapping as the
     * primary key using @MapsId. This works much easier than the case where the PK is used
     * as the FK.  The provider automatically propagates the parent.PK to the dependent.FK,
     * which is re-used to be the dependent.PK.
     */
    @Test
    public void testOne2OneUniMapsId() {
        log.info("*** testOne2OneUniMapsId ***");
        Person person = new Person();
        person.setName("John Harbaugh");
        Coach coach = new Coach(person);
        coach.setType(Coach.Type.HEAD);
        em.persist(person);
        em.persist(coach); //provider auto propagates person.id to coach.FK mapped to coach.PK 
        
        //flush commands to database, clear cache, and pull back new instance
        em.flush(); em.clear();
        Coach coach2 = em.find(Coach.class, coach.getId());
        log.info("calling person...");
        assertEquals("unexpected name", coach.getPerson().getName(), coach2.getPerson().getName());

        //verify the contents of the database tables, columns, and relationships
        Object[] cols = (Object[]) em.createNativeQuery(
        		"select person.id person_id, person.name, " +
        		       "coach.person_id coach_id " +
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
        assertNull("person not deleted", em.find(Person.class, person.getId()));
        assertNull("coach not deleted", em.find(Coach.class, coach.getId()));
    }
    
    /**
     * This test provides a demonstration of creating a one-to-one, uni-directional
     * relationship to a parent class that uses a composite primary key mapped thru an @IdClass
     */
    @Test
    public void testOne2OneUniIdClass() {
        log.info("*** testOne2OneUniIdClass ***");
        Date showDate = new GregorianCalendar(1975+new Random().nextInt(100),
        		Calendar.JANUARY, 1).getTime();
        Date showTime = new GregorianCalendar(0, 0, 0, 0, 0, 0).getTime();
        ShowEvent show = new ShowEvent(showDate, showTime);
        show.setName("Rocky Horror");
        ShowTickets tickets = new ShowTickets(show); //parent already has natural PK by this point
        tickets.setTicketsLeft(300);
        em.persist(show);
        em.persist(tickets);  
        
        //flush commands to database, clear cache, and pull back new instance
        em.flush(); em.clear();
        ShowTickets tickets2 = em.find(ShowTickets.class, new ShowEventPK(tickets.getDate(), tickets.getTime()));
        log.info("calling parent...");
        assertEquals("unexpected name", tickets.getShow().getName(), tickets2.getShow().getName());

        //verify the contents of the database tables, columns, and relationships
        Object[] cols = (Object[]) em.createNativeQuery(
        		"select show.date show_date, show.time show_time, " +
        		       "tickets.ticket_date ticket_date, tickets.ticket_time ticket_time, tickets.tickets " +
        		"from RELATIONEX_SHOWEVENT show " +
                "join RELATIONEX_SHOWTICKETS tickets on show.date = tickets.ticket_date and show.time = tickets.ticket_time " +
                "where tickets.ticket_date = ?1 and tickets.ticket_time = ?2")
                .setParameter(1, tickets.getShow().getDate(), TemporalType.DATE)
                .setParameter(2, tickets.getShow().getTime(), TemporalType.TIME)
                .getSingleResult();
        log.info("row=" + Arrays.toString(cols));
        assertEquals("unexpected show_date", tickets2.getShow().getDate(), (Date)cols[0]);
        assertEquals("unexpected show_time", tickets2.getShow().getTime(), (Date)cols[1]);
        assertEquals("unexpected ticket_date", tickets2.getDate(), (Date)cols[2]);
        assertEquals("unexpected ticket_time", tickets2.getTime(), (Date)cols[3]);
        assertEquals("unexpected ticketsLeft", tickets2.getTicketsLeft(), ((Number)cols[4]).intValue());
        
        
        //remove the objects and flush commands to the database
        em.remove(tickets2);
        em.remove(tickets2.getShow());
        em.flush();
        assertNull("tickets not deleted", em.find(ShowEvent.class, 
        		new ShowEventPK(show.getDate(), show.getTime())));
        assertNull("show not deleted", em.find(ShowTickets.class, 
        		new ShowEventPK(tickets.getDate(), tickets.getTime())));
    }
    

    /**
     * This test provides a demonstration of creating a one-to-one, uni-directional
     * relationship to a parent class that uses a composite primary key mapped thru and 
     * @EmbeddedId
     */
    @Test
    public void testOne2OneUniEmbeddedId() {
        log.info("*** testOne2OneUniEmbedded ***");
        Date showDate = new GregorianCalendar(1975+new Random().nextInt(100),
        		Calendar.JANUARY, 1).getTime();
        Date showTime = new GregorianCalendar(0, 0, 0, 0, 0, 0).getTime();
        ShowEvent show = new ShowEvent(showDate, showTime);
        show.setName("Rocky Horror");
        BoxOffice boxOffice = new BoxOffice(show);
        boxOffice.setTicketsLeft(500);
        em.persist(show);
        em.persist(boxOffice); //provider auto propagates parent.cid to dependent.FK mapped to dependent.cid 
        
        //flush commands to database, clear cache, and pull back new instance
        em.flush(); em.clear();
        BoxOffice boxOffice2 = em.find(BoxOffice.class, new ShowEventPK(boxOffice.getDate(), boxOffice.getTime()));
        log.info("calling parent...");
        assertEquals("unexpected name", boxOffice.getShow().getName(), boxOffice2.getShow().getName());

        //verify the contents of the database tables, columns, and relationships
        Object[] cols = (Object[]) em.createNativeQuery(
        		"select show.date show_date, show.time show_time, " +
        		       "tickets.show_date ticket_date, tickets.show_time ticket_time, tickets.tickets " +
        		"from RELATIONEX_SHOWEVENT show " +
                "join RELATIONEX_BOXOFFICE tickets on show.date = tickets.show_date and show.time = tickets.show_time " +
                "where tickets.show_date = ?1 and tickets.show_time = ?2")
                .setParameter(1, boxOffice.getShow().getDate(), TemporalType.DATE)
                .setParameter(2, boxOffice.getShow().getTime(), TemporalType.TIME)
                .getSingleResult();
        log.info("row=" + Arrays.toString(cols));
        assertEquals("unexpected show_date", boxOffice2.getShow().getDate(), (Date)cols[0]);
        assertEquals("unexpected show_time", boxOffice2.getShow().getTime(), (Date)cols[1]);
        assertEquals("unexpected ticket_date", boxOffice2.getDate(), (Date)cols[2]);
        assertEquals("unexpected ticket_time", boxOffice2.getTime(), (Date)cols[3]);
        assertEquals("unexpected ticketsLeft", boxOffice2.getTicketsLeft(), ((Number)cols[4]).intValue());        
        
        //remove the objects and flush commands to the database
        em.remove(boxOffice2);
        em.remove(boxOffice2.getShow());
        em.flush();
        assertNull("tickets not deleted", em.find(ShowEvent.class, 
        		new ShowEventPK(show.getDate(), show.getTime())));
        assertNull("show not deleted", em.find(BoxOffice.class, 
        		new ShowEventPK(boxOffice.getDate(), boxOffice.getTime())));
    }
    
    
    /**
     * This test provides a demonstration of creating a one-to-one
     * bi-directional relationship using entities that will share
     * a generated primary key value and be joined by their common
     * primary key value.
     */
    @Test
    public void testOne2OneBiPKJ() {
        log.info("*** testOne2OneBiPKJ() ***");
        Applicant applicant = new Applicant();
        applicant.setName("Jason Garret");
        Application application = new Application(applicant);
        application.setDesiredStartDate(new GregorianCalendar(2008, Calendar.JANUARY, 1).getTime());
        em.persist(applicant);   //provider will generate a PK
        em.persist(application); //provider will propogate parent.PK to dependent.FK/PK
        
        //clear the persistence context and get new instances from the owning side
        em.flush(); em.clear();
        log.info("finding dependent...");
        Application application2 = em.find(Application.class, application.getId());
        log.info("found dependent...");
        assertTrue("unexpected startDate", 
        		application.getDesiredStartDate().equals(
        		application2.getDesiredStartDate()));
        log.info("calling parent...");
        assertEquals("unexpected name", application.getApplicant().getName(), application2.getApplicant().getName());
        
        //clear the persistence context and get new instances from the inverse side
        em.flush(); em.clear();
        log.info("finding parent...");
        Applicant applicant2 = em.find(Applicant.class, applicant.getId());
        log.info("found parent...");
        assertEquals("unexpected name", applicant.getName(), applicant2.getName());
        log.info("calling dependent...");
        assertTrue("unexpected startDate", 
        		applicant.getApplication().getDesiredStartDate().equals(
        		applicant2.getApplication().getDesiredStartDate()));
        
        //remove the objects and flush commands to the database
        em.remove(applicant2.getApplication());
        em.remove(applicant2);
        em.flush();
        assertNull("applicant not deleted", em.find(Applicant.class, applicant2.getId()));
        assertNull("application not deleted", em.find(Application.class, applicant2.getApplication().getId()));
    }
    
    /**
     * This test provides a demonstration of creating a one-to-one bi-directional
     * relationship where the inverse/parent side of the relation has a 0..1 with 
     * the owning/dependent side.
     */
    @Test
    public void testOne2OneBiOwningOptional() {
        log.info("*** testOne2OneBiOwningOptional() ***");
        Auto auto = new Auto();           //auto is inverse/parent side
        auto.setType(Auto.Type.CAR);
        Driver driver = new Driver(auto); //driver is owning/dependent side
        driver.setName("Danica Patrick");
        auto.setDriver(driver); //application must maintain inverse side
        em.persist(auto);
        em.persist(driver);

        //clear the persistence context and get new instances from the owning side
        em.flush(); em.clear();
        log.info("finding dependent...");
        Driver driver2 = em.find(Driver.class, driver.getId());
        log.info("found dependent...");
        assertEquals("unexpected name", driver.getName(), driver2.getName());
        log.info("calling parent...");
        assertEquals("unexpected name", driver.getAuto().getType(), driver2.getAuto().getType());

        //clear the persistence context and get new instances from the inverse side
        em.flush(); em.clear();
        log.info("finding parent...");
        Auto auto2 = em.find(Auto.class, auto.getId());
        log.info("found parent...");
        assertEquals("unexpected type", auto.getType(), auto.getType());
        log.info("calling dependent...");
        assertEquals("unexpected name", auto.getDriver().getName(), auto2.getDriver().getName());
        
        //remove the driver from the auto
        em.remove(auto2.getDriver()); //driver must be deleted since requires auto
        em.flush(); em.clear();
        Auto auto3 = em.find(Auto.class, auto.getId());
        assertNull("driver not removed from auto", auto3.getDriver());
        
        //remove remaining object(s) from database
        em.remove(auto3);
        em.flush();
        assertNull("driver not deleted", em.find(Driver.class, auto2.getDriver().getId()));
        assertNull("auto not deleted", em.find(Auto.class, auto2.getId()));
    }
    
    @Test
    public void testOne2OneOrphan() {
        log.info("*** testOne2OneOrphan ***");
    }
    @Test
    public void testOne2OneCascade() {
        log.info("*** testOne2OneCascade ***");
    }
    
    
    
    
    
    
    
    
    
    
    /** SAVE FOR MANY-TO-ONE
    @Test @Ignore
    public void testOne2OneUniIdClass() {
        log.info("*** testOne2OneUniIdClass ***");
        Person person = new Person();
        person.setName("Michael Jackson");
        LifePhase age = new LifePhase(person, LifePhase.Phase.TEEN);
        em.persist(person);
        em.persist(age); //provider auto propagates person.id to lifephase.FK/lifephase.PK 
        
        //flush commands to database, clear cache, and pull back new instance
        em.flush(); em.clear();
        Coach age2 = em.find(Coach.class, new LifePhasePK(age.getPerson().getId(), age.getPhase()));
        assertEquals("unexpected name", age.getPerson().getName(), age2.getPerson().getName());

        //verify the contents of the database tables, columns, and relationships
        Object[] cols = (Object[]) em.createNativeQuery(
        		"select person.id person_id, person.name, " +
        		       "coach.person_id coach_id " +
        		"from RELATIONEX_LIFEPHASE phase " +
                "join RELATIONEX_PERSON person on person.id = phase.person_id " +
                "where phase.person_id = ?1")
                .setParameter(1, age.getPerson().getId())
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

    @Test @Ignore
    public void testOne2OneUniEmbeddedId() {}
	*/
    

}
