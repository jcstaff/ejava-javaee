package ejava.examples.txhotel.ejbclient;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.naming.Binding;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ejava.examples.txhotel.bl.HotelReservationist;
import ejava.examples.txhotel.bl.InvalidParameterException;
import ejava.examples.txhotel.bo.Person;
import ejava.examples.txhotel.bo.Reservation;
import ejava.examples.txhotel.ejb.HotelRegistrationRemote;

public class HotelReservationTest extends TestCase {
    Log log = LogFactory.getLog(HotelReservationTest.class);
    InitialContext jndi;
    String registrarJNDI = System.getProperty("jndi.name.hotel");
    Map<String,HotelReservationist> reservationists = 
        new HashMap<String, HotelReservationist>();
    
    public void setUp() throws Exception {
        log.debug("getting jndi initial context");
        jndi = new InitialContext();    
        log.debug("jndi=" + jndi.getEnvironment());
        log.debug("jndi name:" + registrarJNDI);
        
        for (NamingEnumeration<Binding> e=jndi.listBindings(registrarJNDI);
             e.hasMore(); ) {
            Binding b = e.next();
            StringBuilder name = new StringBuilder(registrarJNDI);
            name.append("/" + b.getName());
            name.append("/remote");
            try {
                Object object = jndi.lookup(name.toString());
                if (object instanceof HotelRegistrationRemote) {
                    reservationists.put(b.getName(), 
                                       (HotelReservationist)object);
                    log.info("found:" + name);
                }
            }
            catch (NameNotFoundException ex) {}
        }                
    }

    public void testCreateReservation() throws Exception {
        for(String key: reservationists.keySet()) {
            HotelReservationist reservationist = reservationists.get(key);
            doTestCreateReservation(key, reservationist);
        }
        reservationists.clear();
    }        
    public void doTestCreateReservation(
            String name, 
            HotelReservationist reservationist) throws Exception {
        log.info("*** testCreateReservation:" + name + " ***");
        
        Person person = new Person(0,0,"joe", "smith");
        Calendar start = new GregorianCalendar();
        start.add(Calendar.DAY_OF_YEAR, 10);

        Calendar end = new GregorianCalendar();
        end.setTime(start.getTime());
        end.add(Calendar.DAY_OF_YEAR, 2);
        
        Reservation reservation = reservationist.createReservation(
                person, start.getTime(), end.getTime());
        log.debug("created reservation:" + reservation);
        assertNotNull("unexpected confirmation id", 
                reservation.getConfirmation());
    }
    
    public void testGetReservation() throws Exception {
        for(String key: reservationists.keySet()) {
            HotelReservationist reservationist = reservationists.get(key);
            doTestGetReservation(key, reservationist);
        }
        reservationists.clear();
    }        
    public void doTestGetReservation(
            String name, 
            HotelReservationist reservationist) throws Exception {
        log.info("*** testGetReservation:" + name + " ***");
        
        Person person = new Person(0,0,"joe", "smith");
        Calendar start = new GregorianCalendar();
        start.add(Calendar.DAY_OF_YEAR, 10);

        Calendar end = new GregorianCalendar();
        end.setTime(start.getTime());
        end.add(Calendar.DAY_OF_YEAR, 2);
        
        Reservation reservation = reservationist.createReservation(
                person, start.getTime(), end.getTime());
        log.debug("created reservation:" + reservation);
        assertNotNull("unexpected confirmation id", 
                reservation.getConfirmation());
        
        Reservation reservation2 = 
            reservationist.getReservationByConfirmation(
                    reservation.getConfirmation());
        assertNotNull("reservation wasn't found", reservation2);
    }


    public void testBadCreateReservation() throws Exception {
        for(String key: reservationists.keySet()) {
            HotelReservationist reservationist = reservationists.get(key);
            doTestBadCreateReservation(key, reservationist);
        }
        reservationists.clear();
    }        
    public void doTestBadCreateReservation(
            String name, 
            HotelReservationist reservationist) throws Exception {
        log.info("*** testBadCreateReservation:" + name + " ***");
        
        Person person = new Person(0,0,"joe", "smith");
        Calendar start = new GregorianCalendar();
        start.add(Calendar.DAY_OF_YEAR, 10);

        Calendar end = new GregorianCalendar();
        end.setTime(start.getTime());
        end.add(Calendar.DAY_OF_YEAR, -2);
        
        @SuppressWarnings("unused")
        Reservation reservation = null;
        try {
            reservation = reservationist.createReservation(
                person, null, end.getTime());
            fail("didn't catch null start date");
        }
        catch (InvalidParameterException ex) {
            log.debug("got expected exception:" + ex);
        }

        try {
            reservation = reservationist.createReservation(
                person, start.getTime() , null);
            fail("didn't catch null end date");
        }
        catch (InvalidParameterException ex) {
            log.debug("got expected exception:" + ex);
        }

        try {
            reservation = reservationist.createReservation(
                person, start.getTime() , end.getTime());
            fail("didn't catch start date before end date");
        }
        catch (InvalidParameterException ex) {
            log.debug("got expected exception:" + ex);
        }
    }

}
