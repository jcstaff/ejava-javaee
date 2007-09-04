package ejava.examples.txagent.ejbclient;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.txagent.bl.AgentReservationException;
import ejava.examples.txagent.bl.AgentReservationSession;
import ejava.examples.txagent.bl.BookingAgent;
import ejava.examples.txagent.bo.Booking;
import ejava.examples.txagent.ejb.AgentReservationSessionRemote;
import ejava.examples.txagent.ejb.BookingAgentRemote;
import ejava.examples.txhotel.bl.HotelReservationist;
import ejava.examples.txhotel.bo.Person;
import ejava.examples.txhotel.bo.Reservation;
import ejava.examples.txhotel.ejb.HotelRegistrationRemote;
import junit.framework.TestCase;

public class AgentReservationSessionTest extends TestCase {
    Log log = LogFactory.getLog(AgentReservationSessionTest.class);
    InitialContext jndi;
    String agentJNDI = System.getProperty("jndi.name.agent");
    String agentsessionJNDI = System.getProperty("jndi.name.agentsession");
    AgentReservationSession agentSession;
    BookingAgent agent;    
    String hotelJNDI = System.getProperty("jndi.name.hotel");
    HotelReservationist hotel;
    
    public void setUp() throws Exception {
        log.debug("getting jndi initial context");
        jndi = new InitialContext();    
        log.debug("jndi=" + jndi.getEnvironment());
        
        log.info("looking up:" + agentJNDI);
        agent = (BookingAgentRemote)jndi.lookup(agentJNDI);
        log.info("found:" + agent);
        
        log.info("looking up:" + agentsessionJNDI);
        agentSession = (AgentReservationSessionRemote)
            jndi.lookup(agentsessionJNDI);
        log.info("found:" + agentSession);

        log.info("looking up:" + hotelJNDI);
        hotel = (HotelRegistrationRemote)jndi.lookup(hotelJNDI);
        log.info("found:" + hotel);
        
        cleanup();
    }
    
    private void cleanup() throws Exception {
        List<Booking> bookings = agent.getBookings(0, 100);
        while (bookings.size() > 0) {
            for(Booking b: bookings) {
                log.debug("cleaning up booking# " + b.getConfirmation());
                agent.cleanupBooking(b.getConfirmation());
            }
            bookings = agent.getBookings(0, 100);
        }        
    }

    public void testCreate() throws Exception {
        log.info("*** testCreate: ***");       

        Person person = new Person(0,0,"joe", "smith");
        List<Reservation> baseline = 
            hotel.getReservationsForPerson(person, 0, 100);
        log.debug("starting with " + baseline.size() +
                " reservations for " + person);

        agentSession.createBooking();


        Calendar start = new GregorianCalendar();
        start.add(Calendar.DAY_OF_YEAR, 10);
        Calendar end = new GregorianCalendar();
        end.setTime(start.getTime());
        end.add(Calendar.DAY_OF_YEAR, 2);
        for(int i=0; i<10; i++) {
            agentSession.addReservation(
                    person, start.getTime(), end.getTime());
        }
                
        List<Reservation> mine = 
            hotel.getReservationsForPerson(person, 0, 100);
        assertEquals(baseline.size(),mine.size());
        log.debug("successfully verified that uncommited agent session has " +
                "not posted reservations to hotel");
        
        Booking booking = agentSession.commit();
        assertEquals("unexpected number of confirmations in booking",
                10,booking.getHotelConfirmations().size());
        assertEquals("unexpected number of reservations in booking",
                10,booking.getHotelReservations().size());
                
        mine = hotel.getReservationsForPerson(person, 0, 100);
        assertEquals(baseline.size()+10,mine.size());
        log.debug("successfully verified that commited agent session has " +
            "posted reservations to hotel");
    }
    
    public void testBadCreate() throws Exception {
        log.info("*** testBadCreate: ***");       
        
        Person person = new Person(0,0,"joe", "smith");
        List<Reservation> baseline = 
            hotel.getReservationsForPerson(person, 0, 100);
        log.debug("starting with " + baseline.size() +
                " reservations for " + person);

        agentSession.createBooking();


        Calendar start = new GregorianCalendar();
        start.add(Calendar.DAY_OF_YEAR, 10);
        Calendar end = new GregorianCalendar();
        end.setTime(start.getTime());
        end.add(Calendar.DAY_OF_YEAR, 2);
        for(int i=0; i<9; i++) {
            agentSession.addReservation(
                    person, start.getTime(), end.getTime());
        }
        //now create a bad one
        agentSession.addReservation(
                person, end.getTime(),start.getTime());
        
        List<Reservation> mine = 
            hotel.getReservationsForPerson(person, 0, 100);
        assertEquals(baseline.size(),mine.size());
        log.debug("successfully verified that uncommited agent session has " +
                "not posted reservations to hotel");
        
        try {
            agentSession.commit();
            fail("bad reservation wasn't detected");
        }
        catch (AgentReservationException ex) {
            log.debug("got expected exception:" + ex);
        }
        
        //if everyone was a part of the tx, none should have been added
        mine = hotel.getReservationsForPerson(person, 0, 100);
        if (mine.size() == baseline.size()) {
            log.info("all reservations were rolled back");
        }
        else {
            log.info("" + (mine.size() - baseline.size()) + 
                    " reservations were not rolled back");            
        }        
    }
}
