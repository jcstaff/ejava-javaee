package ejava.examples.txhotel.ejbclient;

import static org.junit.Assert.*;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Binding;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ejava.examples.txhotel.bl.HotelReservationSession;
import ejava.examples.txhotel.bl.HotelReservationist;
import ejava.examples.txhotel.bl.InvalidParameterException;
import ejava.examples.txhotel.bo.Person;
import ejava.examples.txhotel.bo.Reservation;
import ejava.examples.txhotel.ejb.HotelRegistrationRemote;
import ejava.examples.txhotel.ejb.HotelReservationSessionRemote;
import ejava.util.ejb.EJBClient;

/**
 * This test case provides an example of conducting a failed usecase 
 * across a set of EJBs that have a mixture of transaction scope.
 * @author jcstaff
 *
 */
public class HotelReservationSessionIT {
    private static final Log log = LogFactory.getLog(HotelReservationSessionIT.class);
    static InitialContext jndi;
    static final String sessionJNDI = System.getProperty("jndi.name.hotelsession",
    	EJBClient.getEJBLookupName("txHotelEAR", "txHotelEJB", "", 
        	"HotelReservationSessionEJB", HotelReservationSessionRemote.class.getName())+"?stateful");
    static final String requiredJNDI = System.getProperty("jndi.name.hotelsession",
    	EJBClient.getEJBLookupName("txHotelEAR", "txHotelEJB", "", 
        	"RequiredSessionEJB", HotelReservationSessionRemote.class.getName())+"?stateful");
    static final String requiresNewJNDI = System.getProperty("jndi.name.hotelsession",
    	EJBClient.getEJBLookupName("txHotelEAR", "txHotelEJB", "", 
        	"RequiresNewSessionEJB", HotelReservationSessionRemote.class.getName())+"?stateful");
    
    
    static Map<String, HotelReservationSession> reservationSessions =
        new HashMap<String, HotelReservationSession>();
    static HotelReservationist reservationist;
    static Map<String, HotelReservationist> reservationists;
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        log.debug("getting jndi initial context");
        jndi = new InitialContext();    
        log.debug("jndi=" + jndi.getEnvironment());
        log.debug("jndi name:" + sessionJNDI);        
        
       	reservationSessions.put("registrar", (HotelReservationSession)jndi.lookup(sessionJNDI));
       	reservationSessions.put("required", (HotelReservationSession)jndi.lookup(requiredJNDI));
       	reservationSessions.put("requiresNew", (HotelReservationSession)jndi.lookup(requiresNewJNDI));
       	reservationists = HotelReservationIT.getReservationists();
       	reservationist = (HotelReservationist) jndi.lookup(HotelReservationIT.registrarJNDI);
    }
    
    @Before
    public void setUp() throws Exception {
        cleanup();
    }

	private void cleanup() throws Exception {
        List<Reservation> reservations = reservationist.getReservations(0, 100);
        while (reservations.size() > 0) {
            for(Reservation r: reservations) {
                try { reservationist.cancelReservation(r); }
                catch (Exception ex) {}
            }
            reservations = reservationist.getReservations(0, 100);
        }        
    }

	@Test
    public void testCreates() throws Exception {
        for(String key: reservationSessions.keySet()) {
            testCreates(key, reservationSessions.get(key));
        }
        reservationSessions.clear();
    }

    private void testCreates(String key, HotelReservationSession session) 
        throws Exception {
        log.info("*** testCreates:" + key + " ***");       
        
        Person person = new Person(0,0,"joe", "smith");
        List<Reservation> baseline = 
            reservationist.getReservationsForPerson(person, 0, 100);

        Calendar start = new GregorianCalendar();
        start.add(Calendar.DAY_OF_YEAR, 10);
        Calendar end = new GregorianCalendar();
        end.setTime(start.getTime());
        end.add(Calendar.DAY_OF_YEAR, 2);
        for(int i=0; i<10; i++) {
            session.createReservation(
                    person, start.getTime(), end.getTime());
        }
        
        List<Reservation> mine = 
            reservationist.getReservationsForPerson(person, 0, 100);
        assertEquals(baseline.size(),mine.size());
        
        List<Reservation> reservations = session.commit();
        assertEquals(10,reservations.size());
        
        mine = reservationist.getReservationsForPerson(person, 0, 100);
        assertEquals(baseline.size()+10,mine.size());
    }
    
    @Test
    public void testBadCreates() throws Exception {
        for(String key: reservationSessions.keySet()) {
            testBadCreates(key, reservationSessions.get(key));
        }
        reservationSessions.clear();
    }
    private void testBadCreates(String key, HotelReservationSession session) 
        throws Exception {
        log.info("*** testBadCreates:" + key + " ***");       
        
        Person person = new Person(0,0,"joe", "smith");
        List<Reservation> baseline = 
            reservationist.getReservationsForPerson(person, 0, 100);
        
        Calendar start = new GregorianCalendar();
        start.add(Calendar.DAY_OF_YEAR, 10);
        Calendar end = new GregorianCalendar();
        end.setTime(start.getTime());
        end.add(Calendar.DAY_OF_YEAR, 2);
        for(int i=0; i<9; i++) {
            session.createReservation(
                    person, start.getTime(), end.getTime());
        }
        //now create a bad one
        session.createReservation(
                person, end.getTime(),start.getTime());
        
        List<Reservation> mine = 
            reservationist.getReservationsForPerson(person, 0, 100);
        assertEquals(baseline.size(),mine.size());
        
        try {
            session.commit();
            fail("bad reservation wasn't detected");
        }
        catch (InvalidParameterException ex) {
            log.debug("got expected exception:" + ex);
        }
        
        //if everyone was a part of the tx, none should have been added
        mine = reservationist.getReservationsForPerson(person, 0, 100);
        if (mine.size() == baseline.size()) {
            log.info("all reservations were rolled back for: " + key);
        }
        else {
            log.info("" + (mine.size() - baseline.size()) + 
                    " reservations were not rolled back for: " + key);            
        }        
    }
}
